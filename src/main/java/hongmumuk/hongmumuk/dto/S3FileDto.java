package hongmumuk.hongmumuk.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class S3FileDto {

    private String originalFileName;
    private String uploadFileName;
    private String uploadFilePath;
    private String uploadFileUrl;
}
