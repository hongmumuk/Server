package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.dto.AdminDto;
import hongmumuk.hongmumuk.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/add/restaurant")
    public ResponseEntity<?> addRestaurant(@RequestBody AdminDto.addNewOneDto addNewOneDto){
        return adminService.addRestaurant(addNewOneDto);
    }

    @PatchMapping("/modify/restaurant")
    public ResponseEntity<?> modifyRestaurant(@RequestBody AdminDto.modifyRestaurantDto modifyRestaurantDto){

        return adminService.crudRestaurant(modifyRestaurantDto);
    }

    @PatchMapping("/modify/rname")
    public ResponseEntity<?> changeRestaurantName(@RequestBody AdminDto.modifyResNameDto modifyResNameDto){

        return adminService.changeResName(modifyResNameDto);
    }

    @PatchMapping("/modify/allRname")
    public ResponseEntity<?> changeAllRestaurantName(@RequestBody AdminDto.modifyAllResNameDto modifyAllResNameDto){

        return adminService.changeAllResName(modifyAllResNameDto);
    }

    @DeleteMapping("/delete/restaurant")
    public ResponseEntity<?> deleteRestaurant(@RequestBody AdminDto.deleteRestaurantDto deleteRestaurantDto){
        return adminService.deleteRestaurant(deleteRestaurantDto);
    }

    @PatchMapping(value = "/add/thumbnail/{rid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addThumbnail(@RequestPart MultipartFile multipartFile,
                                          @PathVariable Long rid
    ){
        return adminService.addThumbnail(rid, multipartFile);
    }

    @DeleteMapping("/delete/thumbnail/{rid}")
    public ResponseEntity<?> deleteThumbnail(@PathVariable Long rid) {

        return adminService.deleteThumbnail(rid);
    }

}
