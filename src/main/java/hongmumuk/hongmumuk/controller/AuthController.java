package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.EmailDto;
import hongmumuk.hongmumuk.dto.JwtToken;
import hongmumuk.hongmumuk.dto.SignInDto;
import hongmumuk.hongmumuk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {

        String email = signInDto.getEmail();
        String password = signInDto.getPassword();

        return userService.logIn(email, password);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody SignInDto signInDto) {
        return userService.joinService(signInDto);
    }


    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody EmailDto emailDto) throws IOException {
        return userService.sendService(emailDto);
    }

    @PostMapping("verify")
    public ResponseEntity<?> verify(@RequestBody EmailDto.VerifyDto verifyDto) throws IOException {
        return userService.verifyService(verifyDto);
    }

    @PostMapping("clear")
    public ResponseEntity<?> clear() throws IOException {
        userService.clear();
        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }
}
