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
   private Integer isNotice; // 공지 여부 0 = 공지 아님, 1 = 공지
   private Integer isAnonymous; // 익명 게시글 여부 0 = 익명 게시글 아님, 1 = 익명 게시글 / 기본값 0
   private Integer isBlinded; // 블라인드 여부 0 = 블라인드 아님, 1 = 블라인드 / 기본값 0
   private Integer isDeleted; // 삭제 여부 0 = 삭제 아님, 1 = 삭제 / 기본값 0
   private Integer fileIncluded; // 파일 포함 여부 0 = 파일 포함 아님, 1 = 파일 포함 / 기본값 0
   // 1인 경우 제목 옆에 <span class="material-symbols-outlined">attach_file</span> 표시 (구글 아이콘 사용)
   private Integer imageIncluded; // 이미지 포함 여부 0 = 이미지 포함 아님, 1 = 이미지 포함 / 기본값 0
   // 1인 경우 제목 옆에 <span class="material-symbols-outlined">image</span> 표시 (구글 아이콘 사용)
   private Integer writerRole; // 작성자 역할 1 = 일반 사용자, 100 = 관리자 / 설계 할 때 기본 값은 게시글이 아닌 게시판의 writerRole이랑 같거나 더 커야함.
   private String thumbnailPath; // 썸네일 경로 / 일단 미입력으로 둠. TODO: 썸네일 로직 구현 후 입력 필요
   private Integer reportStatus; // 신고 로직 작업 이후 처리 예정. 현재는 미입력으로 둠.
   // 신고 상태 0 미신고, 1신고됨 2처리중 3처리완료(변화 없음) 4처리완료(카테고리 이동) 5처리완료(삭제) 등등으로 표시 예정
   private LocalDateTime createdAt; // 게시글 생성 일시
   private LocalDateTime updatedAt; // 게시글 수정 일시. 리스트에는 생성일만 보여주는데 수정일이 있는 경우 작성 시간에 *을 붙임. 상세 페이지에서 수정일 보여줌.
   // 오늘 작성된 글은 24시간 기준 HH:MM (17:27 형식) 으로 표시, 하루 지난 글은 MM-DD(05-18 형식) 로 표시, 1년 경과 글은 YY-MM-DD로 표시 (24-05-17 형식)
} 