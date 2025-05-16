package seoul.its.info.common.util.file.upload.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FileUploadDto {
   private Long id;
   private Long postId;
   private Long commentId;
   private Long contactId;
   private Long serviceId;
   private Integer uploadFrom;
   private Long userId;
   private String loginId;
   private String originalName;
   private String savedName;
   private String filePath;
   private Long fileSize;
   private String fileType;
   private Integer risklevel;
   private Integer uploadByAnonymous;
   private String uploaderIpAddress;
   private Integer isDeleted;
   private Integer count;
   private LocalDateTime createdAt;
}
