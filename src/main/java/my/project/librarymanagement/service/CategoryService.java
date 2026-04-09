package my.project.librarymanagement.service;

import my.project.librarymanagement.dto.request.CreateCategoryRequest;
import my.project.librarymanagement.dto.request.UpdateCategoryRequest;
import my.project.librarymanagement.dto.response.CategoryResponse;
import my.project.librarymanagement.entity.Category;
import my.project.librarymanagement.exception.DuplicateResourceException;
import my.project.librarymanagement.exception.ResourceNotFoundException;
import my.project.librarymanagement.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories(){
        return this.categoryRepository.findAll().stream()
                .map(x->toResponse(x))
                .toList();
    }

    public CategoryResponse getCategoryById(Long id){
        return toResponse(findCategory(id));
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        if(categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return toResponse(this.categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        Category category= findCategory(id);
        boolean categoryExists = categoryRepository.findAll().stream()
                .anyMatch(x->x.getName().equals(request.getName())
                && !x.getId().equals(id));

        if(categoryExists) {
            throw new DuplicateResourceException("Category already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category){
        return new CategoryResponse(category.getId(),category.getName(), category.getDescription());
    }

    private Category findCategory(Long id){
        return this.categoryRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Category not found"));

    }
}
