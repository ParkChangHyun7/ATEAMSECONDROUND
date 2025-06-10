package seoul.its.info.services.boards.comments.service;

import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.services.boards.comments.dto.CommentRequestDto;
import seoul.its.info.services.boards.comments.dto.CommentResponseDto;

import java.util.List;

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface CommentService {
    
    /**
     * 게시글 ID로 댓글 목록 조회
     * @param postId 게시글 ID
     * @return 댓글 목록 (대댓글 포함)
     */
    List<CommentResponseDto> getCommentsByPostId(Long postId);
    
    /**
     * 댓글 생성
     * @param postId 게시글 ID
     * @param requestDto 댓글 생성 요청 DTO (내용, 부모 댓글 ID 등)
     * @param userDetails 현재 인증된 사용자 정보
     * @return 생성된 댓글 정보
     * @throws SecurityException 인증되지 않은 사용자인 경우
     */
    CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, UserDetailsImpl userDetails);
    
    /**
     * 댓글 작성
     * @param boardId 게시판 ID
     * @param postId 게시글 ID
     * @param requestDto 댓글 작성 요청 DTO
     * @param userDetails 사용자 정보
     * @param ipAddress 클라이언트 IP 주소
     * @return 생성된 댓글 응답 DTO
     */
    CommentResponseDto createComment(Long boardId, Long postId, CommentRequestDto requestDto, UserDetailsImpl userDetails, String ipAddress);
    
    /**
     * 댓글 수정
     * @param commentId 수정할 댓글 ID
     * @param requestDto 수정할 댓글 내용
     * @param userDetails 현재 인증된 사용자 정보 (작성자 확인용)
     * @return 수정된 댓글 정보
     * @throws SecurityException 작성자 또는 관리자가 아닌 경우
     */
    CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails);
    
    /**
     * 댓글 삭제 (실제 삭제가 아닌 상태 변경)
     * @param commentId 삭제할 댓글 ID
     * @param userDetails 현재 인증된 사용자 정보 (작성자 확인용)
     * @throws SecurityException 작성자 또는 관리자가 아닌 경우
     */
    void deleteComment(Long commentId, UserDetailsImpl userDetails);
}