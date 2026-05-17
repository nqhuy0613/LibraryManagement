package my.project.librarymanagement.controller;

import jakarta.validation.Valid;
import my.project.librarymanagement.dto.request.category.CreateCategoryRequest;
import my.project.librarymanagement.dto.request.category.UpdateCategoryRequest;
import my.project.librarymanagement.dto.response.CategoryResponse;
import my.project.librarymanagement.dto.response.common.ApiResponse;
import my.project.librarymanagement.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {return ResponseEntity.ok(ApiResponse.success("Get all categories", categoryService.getAllCategories()));}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable("id") Long id){return ResponseEntity.ok(ApiResponse.success("Get category by id",categoryService.getCategoryById(id)));}

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Create category", categoryService.createCategory(createCategoryRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@Valid @RequestBody UpdateCategoryRequest updateCategoryRequest, @PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Update category", categoryService.updateCategory(id, updateCategoryRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") Long id){
        this.categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Delete a category", null));
    }

    
}
