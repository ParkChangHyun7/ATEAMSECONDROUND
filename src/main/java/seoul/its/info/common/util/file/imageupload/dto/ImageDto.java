package seoul.its.info.common.util.file.imageupload.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageDto {
    private Long id;
    private Long post_id;
    private Long comment_id;
    private Long contact_id;
    private Long service_id;
    private int upload_from;
    private Long user_id;
    private String login_id;
    private String original_name;
    private String saved_name;
    private int is_image;
    private String file_path;
    private String file_size;
    private String file_type; // 파일 확장자
    private int risk_level;
    private String uploader_ip_address;
    private int is_deleted;
    private int upload_by_anonymous;
    private int count; // 다운로드 횟수
    private LocalDateTime created_at;
} 