package my.project.libraryManagement.controller;

import jakarta.validation.Valid;
import my.project.libraryManagement.dto.request.CreateCategoryRequest;
import my.project.libraryManagement.dto.request.UpdateCategoryRequest;
import my.project.libraryManagement.dto.response.CategoryResponse;
import my.project.libraryManagement.dto.response.common.ApiResponse;
import my.project.libraryManagement.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategorys() {return ResponseEntity.ok(success("Get all categories", categoryService.getAllCategories()));}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable("id") Long id){return ResponseEntity.ok(success("Get category by id",categoryService.getCategoryById(id)));}

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(success("Create category", categoryService.createCategory(createCategoryRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@Valid @RequestBody UpdateCategoryRequest updateCategoryRequest, @PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(success("Update category", categoryService.updateCategory(id, updateCategoryRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") Long id){
        this.categoryService.deleteCategory(id);
        return ResponseEntity.ok(success("Delete a category", null));
    }

    private<T> ApiResponse<T> success(String message, T data){
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(true);
        res.setMessage(message);
        res.setData(data);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }
}
