package seoul.its.info.services.boards.comments.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDto {
    private final Long id; // 댓글 고유 번호
    private final Long postId; // 게시글 고유 번호
    private final Long userId; // 사용자 고유 번호
    private final String loginId; // 사용자 로그인 아이디
    private final String writer; // 댓글 작성자 이름 (회원 게시판은 회원 닉네임 그대로, 익명 게시판은 닉네임 표시 안 함)
    private final String content; // 댓글 내용
    private final Integer isParent; // 댓글인지 대댓글인지 여부 0 = 댓글, 1 = 대댓글
    private final Long parentCommentId; // 부모 댓글 고유 번호
    private final Integer isParentSecret; // 부모 댓글 비밀 여부 (0: 공개, 1: 비밀)
    private final Integer postWriterOnly; // 원글 작성자(post_writer)만 볼 수 있는지 여부. 대댓글로는 불가
    private final Long postWriterId; // 원글(포스트) 작성자의 idx값.
    private final Integer isAnonymous; // 익명 댓글 여부 0 = 익명 댓글 아님, 1 = 익명 댓글
    private final Integer isBlinded; // 블라인드 여부 0 = 블라인드 아님, 1 = 블라인드
    private final Integer isDeleted; // 댓글 삭제 여부 0 = 삭제 아님, 1 = 삭제
    private final Integer deletedByPost; // 게시글 삭제로 인해 삭제된 댓글(자발적 삭제X)은 1, 이외에는 모두 0 고정
    private final Integer imageIncluded; // 첨부된 이미지 여부 0 = 이미지 없음, 1 = 이미지 있음
    private final String writerRole; // 댓글 작성자 권한 레벨 0=일반 회원, 100 이상 = 관리자
    private final Integer reportStatus; // 신고된 댓글은 1부터 시작, 신고되지 않은 댓글은 0 고정
    // 신고 누적은 reports 테이블에 관리되고 신고 누적에 따라, 관리자의 결정에 따라 status 변경 됨.
    private final String ipAddress; // 댓글 작성자의 IP 주소 common/util/ClientIpGetHelper.java 참조
    private final LocalDateTime createdAt; // 댓글 작성 시간 명시적 표시
    private final LocalDateTime updatedAt; // 댓글 수정 시간 수정하기 전에는 값 없음. 수정 된 경우 값 있으며 수정 시간으로 업데이트 됨.
    // 참고로 수정 시간 및 내용 등은 추후에 history 테이블로 누적되며 관리 될 예정
    private final Integer likeCount; // 좋아요 수
}