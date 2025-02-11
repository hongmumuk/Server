package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.JwtToken;
import hongmumuk.hongmumuk.dto.JwtTokenProvider;
import hongmumuk.hongmumuk.dto.SignInDto;
import hongmumuk.hongmumuk.entity.User;
import hongmumuk.hongmumuk.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtToken signIn(String email, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성

        return jwtTokenProvider.generateToken(authentication);
    }

    @Transactional
    public ResponseEntity<?> joinService(SignInDto signInDto) {

        if(userRepository.existsByEmail(signInDto.getEmail())){
            return ResponseEntity.ok(Apiresponse.onFailure(ErrorStatus.BAD_REQUEST));
        }

        User user = User.builder()
                .email(signInDto.getEmail())
                .password(passwordEncoder.encode(signInDto.getPassword()))
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }
}
