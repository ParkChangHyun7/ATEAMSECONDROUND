package seoul.its.info.services.boards.likes.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LikeCommentDto {
   private Long id;
   private Long commentId;
   private Long userId;
   private String loginId;
   private String nickname;
   private LocalDateTime createdAt;
}
