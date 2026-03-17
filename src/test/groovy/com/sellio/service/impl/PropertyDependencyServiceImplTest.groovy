package com.sellio.service.impl

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.PropertyDependencyMapper
import com.sellio.mapper.PropertyValueMapper
import com.sellio.model.dto.request.PropertyDependencyCreateRequest
import com.sellio.model.dto.response.core.PropertyValueResponse
import com.sellio.model.entity.PropertyDependencyEntity
import com.sellio.model.entity.PropertyValueEntity
import com.sellio.repository.PropertyDependencyRepository
import com.sellio.repository.PropertyValueRepository
import org.springframework.data.domain.*
import spock.lang.Specification
import spock.lang.Subject

class PropertyDependencyServiceImplTest extends Specification {
    def propertyValueRepository = Mock(PropertyValueRepository)
    def propertyDependencyRepository = Mock(PropertyDependencyRepository)
    def propertyValueMapper = Mock(PropertyValueMapper)
    def propertyDependencyMapper = Mock(PropertyDependencyMapper)

    @Subject
    def service = new PropertyDependencyServiceImpl(
            propertyValueRepository,
            propertyDependencyRepository,
            propertyValueMapper,
            propertyDependencyMapper
    )

    def "save should successfully create dependencies for new child values"() {
        given:
        def parentId = UUID.randomUUID()
        def childId = UUID.randomUUID()
        def request = new PropertyDependencyCreateRequest(parentId, [childId])

        def parentEntity = new PropertyValueEntity(value: "Apple")
        parentEntity.id = parentId

        def childEntity = new PropertyValueEntity(value: "iPhone 17")
        childEntity.id = childId

        def childResponse = PropertyValueResponse.builder().propertyValue("iPhone 17").build()

        when:
        def result = service.save(request)

        then:
        1 * propertyValueRepository.findById(parentId) >> Optional.of(parentEntity)
        1 * propertyValueRepository.findAllById([childId]) >> [childEntity]
        1 * propertyDependencyRepository.existsByParentIdAndChildId(parentId, childId) >> false
        1 * propertyDependencyRepository.save(_ as PropertyDependencyEntity)
        1 * propertyValueMapper.toResponse(childEntity) >> childResponse

        expect:
        result.success
        result.data.parentPropertyValue == "Apple"
        result.data.childPropertyValues.size() == 1
        result.data.childPropertyValues[0].propertyValue == "iPhone 17"
    }

    def "save should skip if child value is same as parent or already exists"() {
        given:
        def parentId = UUID.randomUUID()
        def request = new PropertyDependencyCreateRequest(parentId, [parentId]) // Eyni ID gonderirik

        def parentEntity = new PropertyValueEntity(value: "Apple")
        parentEntity.id = parentId

        when:
        def result = service.save(request)

        then:
        1 * propertyValueRepository.findById(parentId) >> Optional.of(parentEntity)
        1 * propertyValueRepository.findAllById([parentId]) >> [parentEntity]
        0 * propertyDependencyRepository.existsByParentIdAndChildId(_, _)
        0 * propertyDependencyRepository.save(_)

        expect:
        result.success
        result.data.childPropertyValues.isEmpty()
    }

    def "save should throw ResourceNotFoundException when parent not found"() {
        given:
        def parentId = UUID.randomUUID()
        def request = new PropertyDependencyCreateRequest(parentId, [UUID.randomUUID()])

        when:
        service.save(request)

        then:
        1 * propertyValueRepository.findById(parentId) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }

    def "getById should return dependency when it exists"() {
        given:
        def id = UUID.randomUUID()
        def entity = new PropertyDependencyEntity()

        when:
        def result = service.getById(id)

        then:
        1 * propertyDependencyRepository.findById(id) >> Optional.of(entity)
        1 * propertyDependencyMapper.toResponse(entity) >> Mock(com.sellio.model.dto.response.core.PropertyDependencyResponse)

        expect:
        result.success
    }

    def "getAll should return paged data for specific parent"() {
        given:
        def parentId = UUID.randomUUID()
        def parentEntity = new PropertyValueEntity(value: "Brand")
        def pageable = PageRequest.of(0, 10, Sort.by("createdAt"))
        def pageContent = [new PropertyDependencyEntity()]
        def page = new PageImpl(pageContent, pageable, 1)

        when:
        def result = service.getAll(parentId, 0, 10)

        then:
        1 * propertyValueRepository.findById(parentId) >> Optional.of(parentEntity)
        1 * propertyDependencyRepository.findAllByParent(parentEntity, pageable) >> page
        1 * propertyDependencyMapper.toResponses(pageContent) >> []

        expect:
        result.success
        result.message.contains("Brand")
    }

    def "deleteById should delete when dependency exists"() {
        given:
        def id = UUID.randomUUID()
        def entity = new PropertyDependencyEntity()

        when:
        def result = service.deleteById(id)

        then:
        1 * propertyDependencyRepository.findById(id) >> Optional.of(entity)
        1 * propertyDependencyRepository.delete(entity)

        expect:
        result.success
    }
}