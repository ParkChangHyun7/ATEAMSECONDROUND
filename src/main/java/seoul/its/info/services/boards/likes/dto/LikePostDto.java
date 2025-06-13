package seoul.its.info.services.boards.likes.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LikePostDto {
   private Long id;
   private Long postId;
   private Long userId;
   private String loginId;
   private String nickname;
   private LocalDateTime createdAt;
}