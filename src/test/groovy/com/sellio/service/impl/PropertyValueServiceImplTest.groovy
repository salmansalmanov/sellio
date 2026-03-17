package com.sellio.service.impl

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.PropertyValueMapper
import com.sellio.model.dto.request.PropertyValueAddRequest
import com.sellio.model.dto.request.PropertyValueUpdateRequest
import com.sellio.model.dto.response.core.PropertyValueGetResponse
import com.sellio.model.dto.response.core.PropertyValueSaveResponse
import com.sellio.model.entity.PropertyEntity
import com.sellio.model.entity.PropertyValueEntity
import com.sellio.repository.PropertyRepository
import com.sellio.repository.PropertyValueRepository
import org.springframework.data.domain.*
import spock.lang.Specification
import spock.lang.Subject

class PropertyValueServiceImplTest extends Specification {
    def propertyValueMapper = Mock(PropertyValueMapper)
    def propertyRepository = Mock(PropertyRepository)
    def propertyValueRepository = Mock(PropertyValueRepository)

    @Subject
    def service = new PropertyValueServiceImpl(propertyValueMapper, propertyRepository, propertyValueRepository)

    def "save should successfully create multiple property values"() {
        given:
        def propertyId = UUID.randomUUID()
        def request = new PropertyValueAddRequest(propertyId, ["OLED", "AMOLED"])
        def propertyEntity = new PropertyEntity(name: "Screen Type")

        def savedEntities = [
                new PropertyValueEntity(value: "OLED"),
                new PropertyValueEntity(value: "AMOLED")
        ]
        def response = PropertyValueSaveResponse.builder().propertyName("Screen Type").build()

        when:
        def result = service.save(request)

        then:
        1 * propertyRepository.findById(propertyId) >> Optional.of(propertyEntity)
        1 * propertyValueRepository.saveAll({ List<PropertyValueEntity> list -> list.size() == 2 }) >> savedEntities
        1 * propertyValueMapper.toSaveResponse(savedEntities) >> response

        expect:
        result.success
        result.data.propertyName == "Screen Type"
    }

    def "getById should return response when entity exists"() {
        given:
        def id = UUID.randomUUID()
        def entity = new PropertyValueEntity(value: "16GB")
        def response = PropertyValueGetResponse.builder().propertyValueName("16GB").build()

        when:
        def result = service.getById(id)

        then:
        1 * propertyValueRepository.findById(id) >> Optional.of(entity)
        1 * propertyValueMapper.toGetResponse(entity) >> response

        expect:
        result.success
        result.data.propertyValueName == "16GB"
    }

    def "getAll should return paged values for a property"() {
        given:
        def propertyId = UUID.randomUUID()
        def propertyEntity = new PropertyEntity(name: "Color")
        def pageable = PageRequest.of(0, 10, Sort.by("createdAt"))
        def page = new PageImpl([new PropertyValueEntity(value: "Black")], pageable, 1)

        when:
        def result = service.getAll(propertyId, 0, 10)

        then:
        1 * propertyRepository.findById(propertyId) >> Optional.of(propertyEntity)
        1 * propertyValueRepository.findAllByProperty(propertyEntity, pageable) >> page
        1 * propertyValueMapper.toResponses(_) >> []

        expect:
        result.success
        result.message.contains("Color")
    }

    def "update should change property parent if propertyId is provided"() {
        given:
        def valueId = UUID.randomUUID()
        def newPropertyId = UUID.randomUUID()
        def request = new PropertyValueUpdateRequest(value: "New Value", propertyId: newPropertyId)

        def existingValue = new PropertyValueEntity(value: "Old Value")
        def newProperty = new PropertyEntity(name: "New Category")
        def response = PropertyValueGetResponse.builder().propertyValueName("New Value").build()

        when:
        def result = service.update(valueId, request)

        then:
        1 * propertyValueRepository.findById(valueId) >> Optional.of(existingValue)
        1 * propertyRepository.findById(newPropertyId) >> Optional.of(newProperty)
        1 * propertyValueMapper.updateRequestToEntity(request, existingValue) >> existingValue
        1 * propertyValueMapper.toGetResponse(existingValue) >> response

        expect:
        result.success
        existingValue.property == newProperty
    }

    def "delete should remove entity when found"() {
        given:
        def id = UUID.randomUUID()
        def entity = new PropertyValueEntity()

        when:
        def result = service.delete(id)

        then:
        1 * propertyValueRepository.findById(id) >> Optional.of(entity)
        1 * propertyValueRepository.delete(entity)

        expect:
        result.success
        result.message == "Property value deleted successfully"
    }

    def "any method should throw ResourceNotFoundException when property not found"() {
        given:
        def id = UUID.randomUUID()

        when:
        service.getById(id)

        then:
        1 * propertyValueRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }
}