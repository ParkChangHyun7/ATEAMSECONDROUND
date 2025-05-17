package seoul.its.info.services.boards.dto;

import lombok.Data;

@Data
public class BoardRequestDto {
   private Long id; // 게시판 ID (생성 후 반환 값을 받기 위해 추가)
   private String name; // 게시판 이름
   private String description; // 게시판 설명
   private Integer categoryCode; // 게시판 카테고리 코드
   private Integer isActive; // 게시판 사용 여부 0 = 사용 안함, 1 = 사용
   private Integer writeRole; // 글쓰기 가능 레벨 기준 (공지사항은 어드민 레벨 100부터 된다던지 등)
   private Integer readRole; // 읽기 가능 레벨 기준 (0 = 미회원, 1 = 회원, above 100 = 관리자 게시판 등)
   private Integer isAnonymous; // 익명 게시판 여부 0 = 익명 게시판 아님, 1 = 익명 게시판
   private Long updatedBy; // 최종 수정/생성 사용자 ID
} 