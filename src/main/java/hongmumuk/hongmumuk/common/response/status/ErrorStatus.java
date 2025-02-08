package hongmumuk.hongmumuk.common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {

    USER_EXISTS(HttpStatus.CONFLICT, "CONFLICT409_1", "이미 존재하는 회원입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD400_0", "잘못된 요청입니다."),
    CERT_ERROR(HttpStatus.BAD_REQUEST, "BAD400_1", "올바른 인증번호가 아닙니다."),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "COMMON401", "해당 리소스에 유효한 인증 자격 증명이 필요합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
