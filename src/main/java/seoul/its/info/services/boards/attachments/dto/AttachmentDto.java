package seoul.its.info.services.boards.attachments.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttachmentDto {
   private Long id;
   private Long postId;
   private Long commentId;
   private Integer uploadFrom;
   private Long userId;
   private String loginId;
   private String originalName;
   private String savedName;
   private Integer isImage; // 이미지 파일 여부 0 = false, 1 = true
   private String filePath;
   private String fileSize; // 1.3GB, 5.3MBbytes, 8KByes, 3Bytes 같이 MB, GB는 소수점 한 자리, 나머지는 정수만 표시함
   private String fileType; // 파일 확장자
   private String uploaderIpAddress;
   private Integer isDeleted; // 업로드 할 때는 0이지만 게시글 삭제, 파일 삭제 할 때는 1로 변경
   private Integer uploadByAnonymous; // 익명 업로드 여부, 현재는 회원 게시판만 만들거라 0으로 고정
   private LocalDateTime createdAt; // 업로드 시간. Default 있지만 명시적으로 표시
} 