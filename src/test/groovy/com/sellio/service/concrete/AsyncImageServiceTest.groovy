package com.sellio.service.concrete

import com.sellio.event.ImageUploadEvent
import com.sellio.mapper.ImageMapper
import com.sellio.model.dto.response.core.ImageResponse
import com.sellio.model.entity.ImageEntity
import com.sellio.model.entity.ListingEntity
import com.sellio.model.enums.DomainType
import com.sellio.model.enums.ImageType
import com.sellio.repository.ImageRepository
import com.sellio.repository.ListingRepository
import com.sellio.repository.ShopRepository
import spock.lang.Specification
import spock.lang.Subject

class AsyncImageServiceTest extends Specification {
    def shopRepository = Mock(ShopRepository)
    def cloudinaryService = Mock(CloudinaryService)
    def imageMapper = Mock(ImageMapper)
    def imageRepository = Mock(ImageRepository)
    def listingRepository = Mock(ListingRepository)

    @Subject
    def asyncImageService = new AsyncImageService(
            shopRepository,
            cloudinaryService,
            imageMapper,
            imageRepository,
            listingRepository
    )

    def "should upload shop logo successfully"() {
        given: "Initial data setup"
        def referenceId = UUID.randomUUID()
        def bytes = "test-image".bytes
        def event = new ImageUploadEvent(referenceId, bytes, ImageType.LOGO, DomainType.SHOP)

        def cloudinaryMap = [
                "public_id" : "shops/logo_123",
                "secure_url": "https://res.cloudinary.com/demo/image/upload/logo_123",
                "format"    : "png",
                "bytes"     : 1024
        ]

        def imageResponse = ImageResponse.builder()
                .publicId("shops/logo_123")
                .secureUrl("https://res.cloudinary.com/demo/image/upload/logo_123")
                .format("png")
                .bytes(1024)
                .build()

        def imageEntity = ImageEntity.builder()
                .id(UUID.randomUUID())
                .publicId("shops/logo_123")
                .build()

        when: "Method is called"
        asyncImageService.uploadImagesAsync(event)

        then: "Verify interactions"
        1 * cloudinaryService.upload(bytes, "shops/$referenceId", "LOGO") >> cloudinaryMap
        1 * imageMapper.toResponse(cloudinaryMap) >> imageResponse
        1 * imageMapper.toEntity(imageResponse, ImageType.LOGO) >> imageEntity
        1 * imageRepository.save(imageEntity) >> imageEntity
        1 * shopRepository.updateLogo(referenceId, imageEntity)
        0 * listingRepository.findById(_)
    }

    def "should upload listing thumbnail and link to listing entity"() {
        given:
        def referenceId = UUID.randomUUID()
        def event = new ImageUploadEvent(referenceId, "image-content".bytes, ImageType.LISTING_THUMBNAIL, DomainType.LISTING)

        def imageEntity = ImageEntity.builder().id(UUID.randomUUID()).publicId("listing/thumb").build()
        def listingEntity = ListingEntity.builder().id(referenceId).build()

        when:
        asyncImageService.uploadImagesAsync(event)

        then:
        1 * cloudinaryService.upload(_, _, { it.startsWith("listing_") }) >> [:]
        1 * imageMapper.toResponse(_) >> ImageResponse.builder().build()
        1 * imageMapper.toEntity(_, _) >> imageEntity
        1 * imageRepository.save(imageEntity) >> imageEntity

        1 * listingRepository.findById(referenceId) >> Optional.of(listingEntity)
        1 * listingRepository.save(listingEntity)
        1 * imageRepository.save(imageEntity)

        expect:
        listingEntity.thumbnail == imageEntity
        imageEntity.listing == listingEntity
    }

    def "should not throw exception when listing is not found"() {
        given:
        def referenceId = UUID.randomUUID()
        def event = new ImageUploadEvent(referenceId, "bytes".bytes, ImageType.LISTING_THUMBNAIL, DomainType.LISTING)
        def imageEntity = ImageEntity.builder().id(UUID.randomUUID()).build()

        when:
        asyncImageService.uploadImagesAsync(event)

        then:
        1 * listingRepository.findById(referenceId) >> Optional.empty()
        0 * listingRepository.save(_)
        notThrown(Exception)
    }

    def "should handle cloudinary exception gracefully"() {
        given:
        def event = new ImageUploadEvent(UUID.randomUUID(), "bytes".bytes, ImageType.LOGO, DomainType.SHOP)

        when:
        asyncImageService.uploadImagesAsync(event)

        then:
        1 * cloudinaryService.upload(_, _, _) >> { throw new RuntimeException("Cloudinary Down") }
        0 * imageRepository.save(_)
        notThrown(Exception)
    }

    def "should upload shop banner successfully"() {
        given:
        def referenceId = UUID.randomUUID()
        def event = new ImageUploadEvent(referenceId, "banner-bytes".bytes, ImageType.BANNER, DomainType.SHOP)
        def imageEntity = ImageEntity.builder().id(UUID.randomUUID()).build()

        when:
        asyncImageService.uploadImagesAsync(event)

        then:
        1 * cloudinaryService.upload(_, _, "BANNER") >> [:]
        1 * imageMapper.toResponse(_) >> ImageResponse.builder().build()
        1 * imageMapper.toEntity(_, _) >> imageEntity
        1 * imageRepository.save(imageEntity) >> imageEntity
        1 * shopRepository.updateBanner(referenceId, imageEntity)
    }
}