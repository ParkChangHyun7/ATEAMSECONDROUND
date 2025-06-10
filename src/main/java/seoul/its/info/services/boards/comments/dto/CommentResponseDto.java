package seoul.its.info.services.boards.comments.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponseDto {
    private Long id; // 댓글 고유 번호
    private Long postId; // 댓글이 달린 게시글 고유번호
    private String writer; // 댓글 작성자 이름 (회원 nickname) 익명 게시판은 닉네임 표시 안함
    // 익명 게시판은 response에도 값 안 담아서 보냄
    private String content; // 댓글 내용
    private Integer isParent; // 부모댓글 여부 (대댓글 아니면 모두 1)
    private Long parentCommentId; // 부모 댓글 고유 번호
    private Integer isParentSecret; // 부모 댓글 비밀 여부 (0: 공개, 1: 비밀)
    private Integer postWriterOnly; // 게시글 작성자에게만 보여주는 댓글
    private Integer isAnonymous; // 익명 댓글 여부
    private Integer isBlinded; // 블라인드 여부
    private Integer isDeleted; // 댓글 삭제 여부 0 = 삭제 아님, 1 = 삭제 (CommentDto에 존재하지만 ResponseDto에 없었음)
    private Integer deletedByPost; // 게시글 삭제로 인해 삭제된 댓글(자발적 삭제X)은 1, 이외에는 모두 0 고정
    private Integer imageIncluded; // 이미지 포함 여부
    private String writerRole; // 댓글 작성자 권한 레벨 0=일반 회원, 100 이상 = 관리자
    private Integer reportStatus; // 신고된 댓글은 1부터 시작, 신고되지 않은 댓글은 0 고정
    private String ipAddress; // IP정보 응답은 전체 IP가 아니라 앞 두 자리만 응답함 (예: 123.45.678.910 -> 123.45)
    private LocalDateTime createdAt; // 댓글 작성 시간
    private LocalDateTime updatedAt; // 댓글 수정 시간 (있으면 수정된 거고 수정 시간 표시, 없으면 작성시간 표시)
    private Integer likeCount; // 좋아요 수
    private List<CommentResponseDto> replies; // 대댓글 목록
}