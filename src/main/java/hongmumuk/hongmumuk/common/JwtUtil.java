package hongmumuk.hongmumuk.common;

import hongmumuk.hongmumuk.entity.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtUtil {

    private JwtUtil() {};

    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
    public static String getCurrentUserEmail(){
        final CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetail.getUser() == null || userDetail.getUsername() == null)
            throw new RuntimeException();

        return userDetail.getUsername();
    }
}
