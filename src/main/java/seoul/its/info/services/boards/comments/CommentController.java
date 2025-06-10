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
import seoul.its.info.services.boards.comments.exception.CommentPermissionDeniedException;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;

import java.util.List;

/**
 * 댓글 관리를 위한 REST 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/posts/{postId}/comments")
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
    public ResponseEntity<?> createComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest request) {
        if (userDetails == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        String clientIp = clientIpGetHelper.getClientIpAddress(request);
        try {
            CommentResponseDto createdComment = commentService.createComment(boardId, postId, requestDto, userDetails, clientIp);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("data", createdComment);
            successResponse.put("message", "댓글 작성 성공");
            return ResponseEntity.ok(successResponse);
        } catch (CommentPermissionDeniedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "댓글 작성 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        try {
            CommentResponseDto updatedComment = commentService.updateComment(commentId, requestDto, userDetails);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("data", updatedComment);
            successResponse.put("message", "댓글 수정 성공");
            return ResponseEntity.ok(successResponse);
        } catch (CommentPermissionDeniedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "댓글 수정 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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