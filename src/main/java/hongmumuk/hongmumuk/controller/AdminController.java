package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.dto.AdminDto;
import hongmumuk.hongmumuk.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/modify/restaurant")
    public ResponseEntity<?> modifyRestaurant(@RequestBody AdminDto.modifyRestaurantDto modifyRestaurantDto){

        return adminService.crudRestaurant(modifyRestaurantDto);
    }

    @DeleteMapping("/delete/restaurant")
    public ResponseEntity<?> deleteRestaurant(@RequestBody AdminDto.deleteRestaurantDto deleteRestaurantDto){
        return adminService.deleteRestaurant(deleteRestaurantDto);
    }
}
