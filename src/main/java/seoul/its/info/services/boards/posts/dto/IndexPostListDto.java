package seoul.its.info.services.boards.posts.dto;

import lombok.Data;

@Data
public class IndexPostListDto {
   private Long id; // 게시글 고유 번호
   private Long boardId; // 게시판 고유 번호
   private String title; // 게시글 내용
}
