package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.common.JwtUtil;
import hongmumuk.hongmumuk.dto.PageDto;
import hongmumuk.hongmumuk.dto.ProfileDto;
import hongmumuk.hongmumuk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> findProfile(){
        String email = JwtUtil.getCurrentUserEmail();
        return userService.findProfile(email);
    }

    @GetMapping("/liked")
    public ResponseEntity<?> likedRestaurant(){
        String email = JwtUtil.getCurrentUserEmail();
        return userService.getLikedR(email);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> newPassword(@RequestBody ProfileDto.passwordDto newPassword){
        String email = JwtUtil.getCurrentUserEmail();
        return userService.modifyPassword(email, newPassword.getPassword());
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkPassword(@RequestBody ProfileDto.passwordDto password){
        String email = JwtUtil.getCurrentUserEmail();
        return userService.checkPassword(email, password);
    }

    @PatchMapping("/nickname/{nickname}")
    public ResponseEntity<?> nickName(@PathVariable String nickname){
        String email = JwtUtil.getCurrentUserEmail();
        return userService.modifyNickname(email, nickname);
    }

    @DeleteMapping("/quit")
    public ResponseEntity<?> deleteUser(){
        String email = JwtUtil.getCurrentUserEmail();
        return userService.deleteUser(email);
    }

    @GetMapping("/review")
    public ResponseEntity<?> getMyReviews(@RequestParam int page, @RequestParam String sort){
        String email = JwtUtil.getCurrentUserEmail();
        return userService.getMyReviews(email, page, sort);
    }
}
