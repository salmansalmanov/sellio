package com.sellio.service.impl

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.SubcategoryMapper
import com.sellio.model.dto.request.SubcategoryCreateRequest
import com.sellio.model.dto.request.SubcategoryUpdateRequest
import com.sellio.model.dto.response.core.SubcategoryDetailsResponse
import com.sellio.model.entity.CategoryEntity
import com.sellio.model.entity.SubcategoryEntity
import com.sellio.repository.CategoryRepository
import com.sellio.repository.SubcategoryRepository
import org.springframework.data.domain.*
import spock.lang.Specification
import spock.lang.Subject

class SubcategoryServiceImplTest extends Specification {
    def categoryRepository = Mock(CategoryRepository)
    def subcategoryMapper = Mock(SubcategoryMapper)
    def subcategoryRepository = Mock(SubcategoryRepository)

    @Subject
    def service = new SubcategoryServiceImpl(categoryRepository, subcategoryMapper, subcategoryRepository)

    def "save should successfully create subcategory"() {
        given:
        def categoryId = UUID.randomUUID()
        def request = new SubcategoryCreateRequest(name: "Laptops", categoryId: categoryId)

        def category = new CategoryEntity(name: "Electronics")
        def subcategoryEntity = new SubcategoryEntity(name: "Laptops")
        def savedEntity = new SubcategoryEntity(id: UUID.randomUUID(), name: "Laptops")
        def response = SubcategoryDetailsResponse.builder().name("Laptops").build()

        when:
        def result = service.save(request)

        then:
        1 * categoryRepository.findById(categoryId) >> Optional.of(category)
        1 * subcategoryMapper.createRequestToEntity(request) >> subcategoryEntity
        1 * subcategoryRepository.save(subcategoryEntity) >> savedEntity
        1 * subcategoryMapper.toDetailsResponse(savedEntity) >> response

        expect:
        result.success
        result.data.name == "Laptops"
    }

    def "getAll should return paged subcategories by category when categoryId is provided"() {
        given:
        def categoryId = UUID.randomUUID()
        def category = new CategoryEntity(name: "Mobile")
        def pageable = PageRequest.of(0, 10, Sort.by("createdAt"))
        def page = new PageImpl([new SubcategoryEntity()], pageable, 1)

        when:
        def result = service.getAll(0, 10, categoryId)

        then:
        1 * categoryRepository.findById(categoryId) >> Optional.of(category)
        1 * subcategoryRepository.findAllByCategory(category, pageable) >> page
        1 * subcategoryMapper.toResponses(_) >> []

        expect:
        result.success
        result.message.contains("Mobile")
    }

    def "getAll should return all subcategories when categoryId is null"() {
        given:
        def pageable = PageRequest.of(0, 10, Sort.by("createdAt"))
        def page = new PageImpl([], pageable, 0)

        when:
        def result = service.getAll(0, 10, null)

        then:
        1 * subcategoryRepository.findAll(pageable) >> page
        1 * subcategoryMapper.toResponses(_) >> []

        expect:
        result.success
        result.message == "Subcategories found successfully"
    }

    def "update should update parent category when categoryId in request is not null"() {
        given:
        def subcategoryId = UUID.randomUUID()
        def newCategoryId = UUID.randomUUID()
        def request = new SubcategoryUpdateRequest(categoryId: newCategoryId)

        def existingSubcategory = new SubcategoryEntity(id: subcategoryId)
        def newCategory = new CategoryEntity(name: "New Category")
        def response = SubcategoryDetailsResponse.builder().build()

        when:
        def result = service.update(subcategoryId, request)

        then:
        1 * subcategoryRepository.findById(subcategoryId) >> Optional.of(existingSubcategory)
        1 * categoryRepository.findById(newCategoryId) >> Optional.of(newCategory)
        1 * subcategoryMapper.updateRequestToEntity(request, existingSubcategory)
        1 * subcategoryMapper.toDetailsResponse(existingSubcategory) >> response

        expect:
        result.success
        existingSubcategory.category == newCategory
    }

    def "delete should successfully remove subcategory"() {
        given:
        def id = UUID.randomUUID()
        def entity = new SubcategoryEntity()

        when:
        def result = service.delete(id)

        then:
        1 * subcategoryRepository.findById(id) >> Optional.of(entity)
        1 * subcategoryRepository.delete(entity)

        expect:
        result.success
        result.message == "Subcategory deleted successfully"
    }

    def "any operation should throw ResourceNotFoundException when subcategory not found"() {
        given:
        def id = UUID.randomUUID()

        when:
        service.getById(id)

        then:
        1 * subcategoryRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }
}