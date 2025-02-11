package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.JwtToken;
import hongmumuk.hongmumuk.dto.SignInDto;
import hongmumuk.hongmumuk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {

        String email = signInDto.getEmail();
        String password = signInDto.getPassword();

        JwtToken token = userService.signIn(email, password);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.CREATED, token));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody SignInDto signInDto) {
        return userService.joinService(signInDto);
    }
}
