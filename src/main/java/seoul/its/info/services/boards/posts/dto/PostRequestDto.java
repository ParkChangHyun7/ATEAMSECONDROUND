package seoul.its.info.services.boards.posts.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRequestDto {
    private Long boardId; // 게시판 고유 번호
    private Long postId; // 게시글 고유 번호
    // private Long userId; // 사용자 고유 번호, 서버 내부에서 세션으로 체크하도록 하는 게 좋을 듯.
    // 글 수정은 작성자 본인 이외에 그 누구도 수정 불가능하도록 해야함.(관리자도 본인 아니면 수정 절대 불가능)
    @NotNull(message = "제목은 필수 입력 사항입니다.")
    private String title; // 필수값
    @NotNull(message = "내용은 필수 입력 사항입니다.")
    private String content; // 필수값
    private Integer isAnonymous; // 게시판 설정(isAnonymous)에 따라 게시판이 익명 게시판이면 익명 글 체크박스가 보이고 체크 여부로 0, 1 값을 지정할 수 있음
    private Integer isNotice; // 관리자의 경우 isNotice에 해당하는 [공지사항] 체크박스가 보이고 체크 여부로 0, 1 값을 지정할 수 있음
    // 익명, 공지 모두 체크박스를 강제로 만들어서 0/1 값을 전달할 경우를 고려해서 백엔드에서 권한 및 가능한 게시판인지 여부 확인해야함
    private Integer fileIncluded; // 파일 첨부 여부 0 = 파일 첨부 아님, 1 = 파일 첨부
    private Integer imageIncluded; // 이미지 첨부 여부 0 = 이미지 첨부 아님, 1 = 이미지 첨부
    // 파일 첨부 기능은 나중에 구현 또는 연동 예정
    private Integer noReply; // 댓글 금지 여부 (0: 허용, 1: 금지)
} 