package hongmumuk.hongmumuk.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageDto {
    private Integer page;
    private String sort;
}
