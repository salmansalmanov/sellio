package com.sellio.service.impl

import com.sellio.mapper.AddressMapper
import com.sellio.model.dto.response.core.AddressResponse
import com.sellio.model.entity.AddressEntity
import com.sellio.repository.AddressRepository
import com.sellio.service.concrete.GoogleMapsService
import spock.lang.Specification
import spock.lang.Subject

class AddressServiceImplTest extends Specification {

    def addressRepository = Mock(AddressRepository)
    def googleMapsService = Mock(GoogleMapsService)
    def addressMapper = Mock(AddressMapper)

    @Subject
    def addressService = new AddressServiceImpl(
            addressRepository,
            googleMapsService,
            addressMapper
    )

    def "save should return existing address from repository when placeId exists"() {
        given: "A placeId that already exists in DB"
        def placeId = "existing-place-id"
        def existingAddress = AddressEntity.builder()
                .id(UUID.randomUUID())
                .placeId(placeId)
                .city("Baku")
                .build()

        when:
        def result = addressService.save(placeId)

        then: "Repository is checked"
        1 * addressRepository.findByPlaceId(placeId) >> Optional.of(existingAddress)

        and: "Google Maps service is NOT called"
        0 * googleMapsService.getAddressByPlaceId(_)
        0 * addressMapper.toEntity(_)

        expect:
        result.placeId == placeId
        result.city == "Baku"
    }

    def "save should fetch from google maps and map to entity when address does not exist in DB"() {
        given: "A new placeId not in DB"
        def placeId = "new-place-id"
        def googleResponse = AddressResponse.builder()
                .placeId(placeId)
                .city("Ganja")
                .fullAddress("Heydar Aliyev Ave")
                .build()
        def mappedEntity = AddressEntity.builder()
                .placeId(placeId)
                .city("Ganja")
                .build()

        when:
        def result = addressService.save(placeId)

        then: "Repository returns empty"
        1 * addressRepository.findByPlaceId(placeId) >> Optional.empty()

        and: "Google Maps service is called to fetch data"
        1 * googleMapsService.getAddressByPlaceId(placeId) >> googleResponse

        and: "Mapper converts response to entity"
        1 * addressMapper.toEntity(googleResponse) >> mappedEntity

        expect:
        result.placeId == placeId
        result.city == "Ganja"
    }
}