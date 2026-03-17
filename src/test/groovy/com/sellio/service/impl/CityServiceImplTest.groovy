package com.sellio.service.impl

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.CityMapper
import com.sellio.model.dto.request.CityCreateRequest
import com.sellio.model.dto.request.CityUpdateRequest
import com.sellio.model.dto.response.core.CityResponse
import com.sellio.model.entity.CityEntity
import com.sellio.repository.CityRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification
import spock.lang.Subject

class CityServiceImplTest extends Specification {

    def cityMapper = Mock(CityMapper)
    def cityRepository = Mock(CityRepository)

    @Subject
    def cityService = new CityServiceImpl(cityMapper, cityRepository)

    def "save should create city and return response"() {
        given:
        def request = new CityCreateRequest(name: "Baku")
        def entity = new CityEntity(name: "Baku")
        def savedEntity = new CityEntity(id: UUID.randomUUID(), name: "Baku")
        def response = CityResponse.builder().id(savedEntity.id).name("Baku").build()

        when:
        def result = cityService.save(request)

        then:
        1 * cityMapper.createRequestToEntity(request) >> entity
        1 * cityRepository.save(entity) >> savedEntity
        1 * cityMapper.toResponse(savedEntity) >> response

        expect:
        result.success
        result.data.name == "Baku"
    }

    def "getById should return city when it exists"() {
        given:
        def id = UUID.randomUUID()
        def entity = new CityEntity(id: id, name: "Sumqayit")
        def response = CityResponse.builder().id(id).name("Sumqayit").build()

        when:
        def result = cityService.getById(id)

        then:
        1 * cityRepository.findById(id) >> Optional.of(entity)
        1 * cityMapper.toResponse(entity) >> response

        expect:
        result.success
        result.data.name == "Sumqayit"
    }

    def "getById should throw ResourceNotFoundException when city not found"() {
        given:
        def id = UUID.randomUUID()

        when:
        cityService.getById(id)

        then:
        1 * cityRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }

    def "getAll should return paginated cities"() {
        given:
        def page = 0
        def size = 5
        def city = new CityEntity(id: UUID.randomUUID(), name: "Ganja")
        def cityPage = new PageImpl<CityEntity>([city])
        def responses = [CityResponse.builder().name("Ganja").build()]

        when:
        def result = cityService.getAll(page, size)

        then:
        1 * cityRepository.findAll(_ as Pageable) >> cityPage
        1 * cityMapper.toResponses([city]) >> responses

        expect:
        result.success
        result.data.content.size() == 1
        result.data.content[0].name == "Ganja"
    }

    def "update should update city details when found"() {
        given:
        def id = UUID.randomUUID()
        def request = new CityUpdateRequest(name: "Lankaran")
        def existingEntity = new CityEntity(id: id, name: "Old Name")
        def updatedEntity = new CityEntity(id: id, name: "Lankaran")
        def response = CityResponse.builder().id(id).name("Lankaran").build()

        when:
        def result = cityService.update(id, request)

        then:
        1 * cityRepository.findById(id) >> Optional.of(existingEntity)
        1 * cityMapper.updateRequestToEntity(request, existingEntity) >> updatedEntity
        1 * cityMapper.toResponse(updatedEntity) >> response
        0 * cityRepository.save(_)

        expect:
        result.success
        result.data.name == "Lankaran"
    }

    def "delete should remove city from repository"() {
        given:
        def id = UUID.randomUUID()
        def entity = new CityEntity(id: id)

        when:
        def result = cityService.delete(id)

        then:
        1 * cityRepository.findById(id) >> Optional.of(entity)
        1 * cityRepository.delete(entity)

        expect:
        result.success
        result.message == "City deleted successfully"
    }
}