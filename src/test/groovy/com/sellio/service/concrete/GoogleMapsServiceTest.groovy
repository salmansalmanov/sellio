package com.sellio.service.concrete

import com.sellio.exception.custom.GoogleMapsException
import com.sellio.mapper.AddressMapper
import com.sellio.model.dto.response.client.GoogleMapsPlaceResponse
import com.sellio.model.dto.response.core.AddressResponse
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

import java.util.function.Function

class GoogleMapsServiceTest extends Specification {
    def webClient = Mock(WebClient)
    def addressMapper = Mock(AddressMapper)
    def requestHeadersUriSpec = Mock(WebClient.RequestHeadersUriSpec)
    def requestHeadersSpec = Mock(WebClient.RequestHeadersSpec)
    def responseSpec = Mock(WebClient.ResponseSpec)

    @Subject
    def googleMapsService = new GoogleMapsService(webClient, addressMapper)

    def setup() {
        googleMapsService.apiKey = "test-api-key"
    }

    def "should return AddressResponse when placeId is valid"() {
        given: "Setup data"
        def placeId = "ChIJN1t_tDeuEmsRUsoyG83frY4"
        def googleResponse = new GoogleMapsPlaceResponse(id: placeId, formattedAddress: "Baku, Azerbaijan")
        def expectedAddress = AddressResponse.builder().placeId(placeId).fullAddress("Baku, Azerbaijan").build()

        when: "Service method is called"
        def result = googleMapsService.getAddressByPlaceId(placeId)

        then: "WebClient chain is mocked"
        1 * webClient.get() >> requestHeadersUriSpec
        1 * requestHeadersUriSpec.uri(_ as Function) >> requestHeadersSpec
        1 * requestHeadersSpec.accept(MediaType.APPLICATION_JSON) >> requestHeadersSpec
        1 * requestHeadersSpec.retrieve() >> responseSpec
        1 * responseSpec.bodyToMono(GoogleMapsPlaceResponse.class) >> Mono.just(googleResponse)

        and: "Mapper is called"
        1 * addressMapper.toResponse(googleResponse) >> expectedAddress

        expect:
        result.placeId == placeId
        result.fullAddress == "Baku, Azerbaijan"
    }

    def "should throw GoogleMapsException when response is null"() {
        given:
        def placeId = "invalid_id"

        when:
        googleMapsService.getAddressByPlaceId(placeId)

        then:
        1 * webClient.get() >> requestHeadersUriSpec
        1 * requestHeadersUriSpec.uri(_ as Function) >> requestHeadersSpec
        1 * requestHeadersSpec.accept(_) >> requestHeadersSpec
        1 * requestHeadersSpec.retrieve() >> responseSpec
        1 * responseSpec.bodyToMono(GoogleMapsPlaceResponse.class) >> Mono.empty()

        thrown(GoogleMapsException)
    }

    def "should throw GoogleMapsException when WebClient fails"() {
        given:
        def placeId = "error_id"

        when:
        googleMapsService.getAddressByPlaceId(placeId)

        then:
        1 * webClient.get() >> { throw new RuntimeException("Network error") }

        thrown(GoogleMapsException)
    }
}