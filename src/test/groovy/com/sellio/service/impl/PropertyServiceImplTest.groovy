package com.sellio.service.impl

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.PropertyMapper
import com.sellio.model.dto.request.PropertyCreateRequest
import com.sellio.model.dto.request.PropertyUpdateRequest
import com.sellio.model.dto.response.core.PropertyDetailsResponse
import com.sellio.model.entity.PropertyEntity
import com.sellio.model.entity.SubcategoryEntity
import com.sellio.repository.PropertyRepository
import com.sellio.repository.SubcategoryRepository
import org.springframework.data.domain.*
import spock.lang.Specification
import spock.lang.Subject

class PropertyServiceImplTest extends Specification {
    def subcategoryRepository = Mock(SubcategoryRepository)
    def propertyMapper = Mock(PropertyMapper)
    def propertyRepository = Mock(PropertyRepository)

    @Subject
    def service = new PropertyServiceImpl(subcategoryRepository, propertyMapper, propertyRepository)

    def "save should successfully create property"() {
        given:
        def subcategoryId = UUID.randomUUID()
        def request = new PropertyCreateRequest(name: "RAM", subcategoryId: subcategoryId)

        def subcategory = new SubcategoryEntity(name: "Electronics")
        def propertyEntity = new PropertyEntity(name: "RAM")
        def savedEntity = new PropertyEntity(id: UUID.randomUUID(), name: "RAM")
        def response = PropertyDetailsResponse.builder().name("RAM").build()

        when:
        def result = service.save(request)

        then:
        1 * subcategoryRepository.findById(subcategoryId) >> Optional.of(subcategory)
        1 * propertyMapper.createRequestToEntity(request) >> propertyEntity
        1 * propertyRepository.save(propertyEntity) >> savedEntity
        1 * propertyMapper.toDetailsResponse(savedEntity) >> response

        expect:
        result.success
        result.data.name == "RAM"
    }

    def "update should update subcategory when subcategoryId is provided"() {
        given:
        def propertyId = UUID.randomUUID()
        def newSubcategoryId = UUID.randomUUID()
        def request = new PropertyUpdateRequest(subcategoryId: newSubcategoryId)

        def existingProperty = new PropertyEntity(id: propertyId)
        def newSubcategory = new SubcategoryEntity(name: "New Sub")
        def response = PropertyDetailsResponse.builder().subcategoryName("New Sub").build()

        when:
        def result = service.update(propertyId, request)

        then:
        1 * propertyRepository.findById(propertyId) >> Optional.of(existingProperty)
        1 * subcategoryRepository.findById(newSubcategoryId) >> Optional.of(newSubcategory)
        1 * propertyMapper.updateRequestToEntity(request, existingProperty)
        1 * propertyMapper.toDetailsResponse(existingProperty) >> response

        expect:
        result.success
        existingProperty.subcategory == newSubcategory
    }

    def "getAll should return all properties when subcategoryId is null"() {
        given:
        def pageable = PageRequest.of(0, 10, Sort.by("createdAt"))
        def page = new PageImpl([new PropertyEntity()], pageable, 1)

        when:
        def result = service.getAll(null, 0, 10)

        then:
        1 * propertyRepository.findAll(pageable) >> page
        1 * propertyMapper.toResponses(_) >> []

        expect:
        result.success
        result.message == "Properties found successfully"
    }

    def "getAll should return properties by subcategory when subcategoryId is provided"() {
        given:
        def subcategoryId = UUID.randomUUID()
        def subcategory = new SubcategoryEntity(name: "Laptops")
        def pageable = PageRequest.of(0, 10, Sort.by("createdAt"))
        def page = new PageImpl([], pageable, 0)

        when:
        def result = service.getAll(subcategoryId, 0, 10)

        then:
        1 * subcategoryRepository.findById(subcategoryId) >> Optional.of(subcategory)
        1 * propertyRepository.findAllBySubcategory(subcategory, pageable) >> page
        1 * propertyMapper.toResponses(_) >> []

        expect:
        result.success
        result.message.contains("Laptops")
    }

    def "delete should throw exception when property not found"() {
        given:
        def id = UUID.randomUUID()

        when:
        service.delete(id)

        then:
        1 * propertyRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }
}