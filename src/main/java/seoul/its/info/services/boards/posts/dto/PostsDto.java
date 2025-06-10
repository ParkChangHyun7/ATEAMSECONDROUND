package seoul.its.info.services.boards.posts.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostsDto {
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
   private Integer isDeleted; // 삭제 여부 0 = 삭제 아님, 1 = 삭제, 1인 경우 보통은 deleted_posts 테이블로 옮겨져 있어야됨
   private Integer fileIncluded; // 파일 첨부 여부 0 = 파일 첨부 아님, 1 = 파일 첨부
   private Integer imageIncluded; // 이미지 첨부 여부 0 = 이미지 첨부 아님, 1 = 이미지 첨부
   private Integer writerRole; // 작성 당시 사용자의 role, role 변경 되더라 글 수정 전 변경 안함
   private String thumbnailPath; // 썸네일 경로
   private Integer reportStatus; // 신고 상태 0 = 미신고, 1 = 신고됨 2 = 처리중 3 = 처리완료(변화 없음) 4 = 처리완료(카테고리 이동) 5 = 처리완료(삭제) 등등
   private String ipAddress; // 작성자 ip 주소 앞 두 자리만 표시 (123.456.789.123 -> 123.45)
   private LocalDateTime createdAt; // 작성일
   private LocalDateTime updatedAt; // 수정일. 수정 한 일 없으면 값 없음
   private Integer noReply; // 댓글 금지 여부 (0: 허용, 1: 금지)
} 