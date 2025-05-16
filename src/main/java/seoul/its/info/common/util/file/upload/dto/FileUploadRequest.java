package seoul.its.info.common.util.file.upload.dto;

import java.util.Set;

import lombok.Data;

@Data
public class FileUploadRequest {
    private Long userId;
    private String loginId;
    private String uploadContext;
    private Long serviceId;
    private int uploadFrom;
    private Integer uploadByAnonymous;
    private Set<String> allowedExtensions;
    private long maxSizeBytes;
    private int role;
    private String clientIpAddress;
}