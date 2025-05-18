package seoul.its.info.services.boards.posts.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostResponseDto {
   private Long id; // 게시글 고유 번호
   private Long boardId; // 게시판 고유 번호
   private String writer; // 게시글 작성자 이름 (회원 게시판은 회원 닉네임 그대로, 익명 게시판 or 익명 글은 닉네임 표시 안 함)
   private String title; // 게시글 제목
   private String content; // 게시글 내용
   private Integer viewCount; // 조회수
   private Integer commentCount; // 댓글 수
   private Integer likeCount; // 좋아요 수
   private Integer isNotice; // 공지 여부 0 = 공지 아님, 1 = 공지
   private Integer isAnonymous; // 익명 게시판 여부 0 = 익명 게시판 아님, 1 = 익명 게시판
   private Integer isBlinded; // 블라인드 여부 0 = 블라인드 아님, 1 = 블라인드
   private Integer isDeleted; // 삭제 여부 0 = 삭제 아님, 1 = 삭제, 1인 경우 보통은 deleted_posts 테이블로 옮겨져 있어야됨
   private Integer fileIncluded; // 파일 첨부 여부 0 = 파일 첨부 아님, 1 = 파일 첨부
   private Integer imageIncluded; // 이미지 첨부 여부 0 = 이미지 첨부 아님, 1 = 이미지 첨부
   // 게시글 상세 보기에서 파일 첨부 표시에 따라 response값이 더 필요하긴 할 건데 다운로드도 모듈화 이후 추가할 거라 현재는 여기까지만 보냄.
   private Integer writerRole; // 작성 당시 사용자의 role, role 변경 되더라도 글 수정 전 변경 안함
   private String thumbnailPath; // 썸네일 경로
   // 썸네일 제작 모듈도 추후 만들어야돼서 현재는 미입력으로 둠.
   private String ipAddress; // 작성자 ip 주소 앞 두 자리만 표시 (123.456.789.123 -> 123.45)
   private LocalDateTime createdAt; // 작성일
   private LocalDateTime updatedAt; // 수정일. 수정 한 일 없으면 값 없음
   // 목록에서는 수정한 경우 작성일 옆에 *을 붙였지만, 글 상세보기에서는 작성 시간과 수정 시간을 모두 보여줌
   // YYYY.MM.DD HH:MM:SS 형식으로 표시함 (24시간 기준)
   // 작성 2025.05.18 12:32:16
   // 2025.05.19 11:53:16 (수정됨)
   // 이후 수정 이력 관리 테이블도 운영할 거지만 현재는 이렇게만 표시함.
} 