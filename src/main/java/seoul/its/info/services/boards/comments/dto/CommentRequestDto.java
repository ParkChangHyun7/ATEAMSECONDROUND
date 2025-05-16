package seoul.its.info.services.boards.comments.dto;

import lombok.Data;

@Data
public class CommentRequestDto {
    private Long postId; // 게시글 고유번호
    private String content; // 댓글 내용
    private Integer isParent; // 댓글인지 대댓글인지 여부 0 = 댓글, 1 = 대댓글
    private Long parentCommentId; // 부모 댓글 고유번호, 대댓글일때만 값 있음
    private Integer isAnonymous; // 익명 댓글 여부 0 = 익명 댓글 아님, 1 = 익명 댓글
    // 원글이 익명 게시판이여야만 익명 댓글 가능
    private Integer imageIncluded; // 이미지 포함 여부 0 = 이미지 없음, 1 = 이미지 있음
} 