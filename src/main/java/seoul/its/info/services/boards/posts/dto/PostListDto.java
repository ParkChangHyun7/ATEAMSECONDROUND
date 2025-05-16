package seoul.its.info.services.boards.posts.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostListDto {
   private Long id; // 게시글 고유 번호
   private Long boardId; // 게시판 고유 번호
   private Long userId; // 사용자 고유 번호
   private String loginId; // 사용자 로그인 아이디
   private String writer; // 게시글 작성자 이름 (회원 게시판은 회원 닉네임 그대로, 익명 게시판 or 익명 글은 닉네임 표시 안 함)
   private String title; // 게시글 제목
   private String content; // 게시글 내용
   private Integer viewCount; // 조회수
   private Integer commentCount; // 댓글 수
   private Integer likeCount; // 좋아요 수
   private Integer reportCount; // 신고 당한 수
   private Integer isNotice; // 공지 여부 0 = 공지 아님, 1 = 공지
   private Integer isAnonymous; // 익명 게시판 여부 0 = 익명 게시판 아님, 1 = 익명 게시판
   private Integer isBlinded; // 블라인드 여부 0 = 블라인드 아님, 1 = 블라인드
   private Integer isDeleted;
   private Integer fileIncluded;
   private Integer imageIncluded;
   private Integer writerRole;
   private String thumbnailPath;
   private Integer reportStatus;
   private String ipAddress;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
} 