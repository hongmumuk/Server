package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
    private final String category;
    private final Integer page;
    private final String sort;
}
