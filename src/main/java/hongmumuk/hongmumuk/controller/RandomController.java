package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.dto.CategoryDto;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import hongmumuk.hongmumuk.service.CategoryService;
import hongmumuk.hongmumuk.service.RandomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RandomController {

    private final RandomService randomService;

    @PostMapping("/random")
    public ResponseEntity<?> random() {
        return randomService.random();
    }

}
