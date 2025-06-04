package seoul.its.info.services.boards.comments;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seoul.its.info.common.util.ClientIpGetHelper;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.services.boards.comments.dto.CommentRequestDto;
import seoul.its.info.services.boards.comments.dto.CommentResponseDto;
import seoul.its.info.services.boards.comments.service.CommentService;

import java.util.List;

/**
 * 댓글 관리를 위한 REST 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/posts/{postId}/comments")
public class CommentController {
    
    private final CommentService commentService;
    private final ClientIpGetHelper clientIpGetHelper;

    /**
     * 게시글의 댓글 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    /**
     * 댓글 작성
     */
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest request) {
        if (userDetails == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        String clientIp = clientIpGetHelper.getClientIpAddress(request);
        return ResponseEntity.ok(commentService.createComment(postId, requestDto, userDetails, clientIp));
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        return ResponseEntity.ok(commentService.updateComment(commentId, requestDto, userDetails));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        commentService.deleteComment(commentId, userDetails);
        return ResponseEntity.noContent().build();
    }
}