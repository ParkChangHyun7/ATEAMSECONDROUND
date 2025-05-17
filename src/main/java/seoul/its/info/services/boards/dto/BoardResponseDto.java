package seoul.its.info.services.boards.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardResponseDto {
   private Long id; // 게시판 고유 번호
   private String name; // 게시판 이름
   private String description; // 게시판 설명
   private Integer categoryCode; // 게시판 카테고리 코드
   private Integer isActive; // 게시판 사용 여부 0 = 사용 안함, 1 = 사용
   private Integer writeRole; // 글쓰기 가능 레벨 기준 (공지사항은 어드민 레벨 100부터 된다던지 등)
   private Integer readRole; // 읽기 가능 레벨 기준 (0 = 미회원, 1 = 회원, above 100 = 관리자 게시판 등)
   private Integer isAnonymous; // 익명 게시판 여부 0 = 익명 게시판 아님, 1 = 익명 게시판
   private LocalDateTime createdAt; // 게시판 생성 시간
   private LocalDateTime updatedAt; // 게시판 정보 업데이트 시간
   private Long updatedBy; // 게시판 정보 업데이트한 사용자 idx (추후 관리자 페이지에서 게시판 생성 가능하기 전 까지는 값이 없을 듯)
}