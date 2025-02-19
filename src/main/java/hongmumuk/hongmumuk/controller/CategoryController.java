package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.dto.CategoryDto;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import hongmumuk.hongmumuk.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public ResponseEntity<?> findCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.findCategory(categoryDto);
    }

}
