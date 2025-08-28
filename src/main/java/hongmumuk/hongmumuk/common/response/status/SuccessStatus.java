package hongmumuk.hongmumuk.common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    OK(HttpStatus.OK, "OK200", "요청에 성공하였습니다."),
    CREATED(HttpStatus.CREATED, "CREATED201", "요청에 성공하였습니다."),
    AVAILABLE(HttpStatus.OK, "OK200", "리뷰 작성 가능한 식당입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
