package my.project.libraryManagement.service;

import my.project.libraryManagement.dto.request.CreateCategoryRequest;
import my.project.libraryManagement.dto.request.UpdateCategoryRequest;
import my.project.libraryManagement.dto.response.CategoryResponse;
import my.project.libraryManagement.entity.Category;
import my.project.libraryManagement.exception.DuplicateResourceException;
import my.project.libraryManagement.exception.ResourceNotFoundException;
import my.project.libraryManagement.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        if(categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return toResponse(this.categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category= findCategory(id);
        boolean categoryExists = categoryRepository.findAll().stream()
                .anyMatch(x->x.getName().equals(request.getName())
                && !category.getId().equals(id));

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
        return new CategoryResponse(category.getName(), category.getDescription());
    }

    private Category findCategory(Long id){
        return this.categoryRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Category not found"));

    }
}
