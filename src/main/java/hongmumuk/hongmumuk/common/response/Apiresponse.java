package hongmumuk.hongmumuk.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "data"}) // 변수 순서를 지정
public class Apiresponse<T> {

    private Boolean isSuccess;
    private String code;
    private String message;
    private T data;

    // 성공 시 응답, 데이터 포함
    public static <T> Apiresponse<T> isSuccess(SuccessStatus status, T result){
        return new Apiresponse<>(true, status.getCode(), status.getMessage(), result);
    }

    // 성공 시 응답, 데이터 미포함
    public static <T> Apiresponse<T> isSuccess(SuccessStatus status) {
        return new Apiresponse<>(true, status.getCode(), status.getMessage(), null);
    }

    // 실패 시 응답, 데이터 포함
    public static <T> Apiresponse<T> isFailed(ErrorStatus status, T result) {
        return new Apiresponse<>(false, status.getCode(), status.getMessage(), result);
    }

    // 실패 시 응답, 데이터 미포함
    public static <T> Apiresponse<T> onFailure(ErrorStatus status) {
        return new Apiresponse<>(false, status.getCode(), status.getMessage(), null);
    }
}
