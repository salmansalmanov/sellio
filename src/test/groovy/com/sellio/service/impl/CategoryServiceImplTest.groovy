package com.sellio.service.impl

import com.sellio.exception.custom.ResourceNotFoundException
import com.sellio.mapper.CategoryMapper
import com.sellio.model.dto.request.CategoryCreateRequest
import com.sellio.model.dto.request.CategoryUpdateRequest
import com.sellio.model.dto.response.core.CategoryDetailsResponse
import com.sellio.model.dto.response.core.CategoryResponse
import com.sellio.model.entity.CategoryEntity
import com.sellio.repository.CategoryRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification
import spock.lang.Subject

class CategoryServiceImplTest extends Specification {

    def categoryMapper = Mock(CategoryMapper)
    def categoryRepository = Mock(CategoryRepository)

    @Subject
    def categoryService = new CategoryServiceImpl(categoryMapper, categoryRepository)

    def "save should create category successfully"() {
        given:
        def request = new CategoryCreateRequest(name: "Electronics")
        def entity = new CategoryEntity(name: "Electronics")
        def savedEntity = new CategoryEntity(id: UUID.randomUUID(), name: "Electronics")
        def response = CategoryDetailsResponse.builder().name("Electronics").build()

        when:
        def result = categoryService.save(request)

        then:
        1 * categoryMapper.createRequestToEntity(request) >> entity
        1 * categoryRepository.save(entity) >> savedEntity
        1 * categoryMapper.toDetailsResponse(savedEntity) >> response

        expect:
        result.success
        result.data.name == "Electronics"
    }

    def "getAll should return paginated categories"() {
        given:
        def page = 0
        def size = 10
        def category = new CategoryEntity(id: UUID.randomUUID(), name: "Fashion")
        def categories = [category]
        def categoryPage = new PageImpl<CategoryEntity>(categories)
        def responses = [CategoryResponse.builder().name("Fashion").build()]

        when:
        def result = categoryService.getAll(page, size)

        then:
        1 * categoryRepository.findAll(_ as Pageable) >> categoryPage
        1 * categoryMapper.toResponses(categories) >> responses

        expect:
        result.success
        result.data.content.size() == 1
        result.data.content[0].name == "Fashion"
    }

    def "getById should throw ResourceNotFoundException when category not found"() {
        given:
        def id = UUID.randomUUID()

        when:
        categoryService.getById(id)

        then:
        1 * categoryRepository.findById(id) >> Optional.empty()
        thrown(ResourceNotFoundException)
    }

    def "updateById should update name when category exists"() {
        given:
        def id = UUID.randomUUID()
        def request = new CategoryUpdateRequest(name: "Home & Garden")
        def existingEntity = new CategoryEntity(id: id, name: "Old Name")
        def updatedEntity = new CategoryEntity(id: id, name: "Home & Garden")
        def response = CategoryDetailsResponse.builder().name("Home & Garden").build()

        when:
        def result = categoryService.updateById(id, request)

        then:
        1 * categoryRepository.findById(id) >> Optional.of(existingEntity)
        1 * categoryMapper.updateRequestToEntity(request, existingEntity) >> updatedEntity
        1 * categoryRepository.save(updatedEntity) >> updatedEntity
        1 * categoryMapper.toDetailsResponse(updatedEntity) >> response

        expect:
        result.success
        result.data.name == "Home & Garden"
    }

    def "deleteById should call delete when category exists"() {
        given:
        def id = UUID.randomUUID()
        def entity = new CategoryEntity(id: id)

        when:
        def result = categoryService.deleteById(id)

        then:
        1 * categoryRepository.findById(id) >> Optional.of(entity)
        1 * categoryRepository.delete(entity)

        expect:
        result.success
        result.message == "Category deleted successfully"
    }
}