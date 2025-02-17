package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.dto.EmailDto;
import hongmumuk.hongmumuk.dto.SignInDto;
import hongmumuk.hongmumuk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody EmailDto.VerifyDto verifyDto) throws IOException {
        return userService.verifyService(verifyDto);
    }

    @Operation(
            summary = "토큰 재발급 API",
            description = "리프레쉬 토큰을 검증한 후 액세스 토큰을 재발급합니다."
//            parameters = {
//                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Access Token"),
//                    @Parameter(name = "refreshToken", in = ParameterIn.HEADER, required = true, description = "Refresh Token")
//            }
            //security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/token")
    public ResponseEntity<?> reissue(@RequestParam(value = "accessToken") String accessToken,
                                     @RequestParam(value = "refreshToken") String refreshToken){
        //Bearer 접두사 삭제
        accessToken = accessToken.substring(7);

        return userService.reissue(accessToken, refreshToken);
    }
}
