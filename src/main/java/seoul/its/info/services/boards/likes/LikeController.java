package seoul.its.info.services.boards.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seoul.its.info.services.boards.likes.dto.LikePostDto;
import seoul.its.info.services.boards.likes.dto.LikeCommentDto;
import seoul.its.info.services.boards.likes.service.LikeService;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/posts/{postId}/likes")
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean liked = likeService.isPostLikedByUser(postId, userDetails);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> toggleLike(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            LikePostDto result = likeService.toggleLike(postId, userDetails);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            if (result != null) {
                successResponse.put("message", "좋아요가 추가되었습니다.");
            } else {
                successResponse.put("message", "좋아요가 취소되었습니다.");
            }
            return ResponseEntity.ok(successResponse);
        } catch (SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "좋아요 처리 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<?> toggleCommentLike(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            LikeCommentDto result = likeService.toggleCommentLike(commentId, userDetails);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            if (result != null) {
                successResponse.put("message", "댓글 좋아요가 추가되었습니다.");
            } else {
                successResponse.put("message", "댓글 좋아요가 취소되었습니다.");
            }
            return ResponseEntity.ok(successResponse);
        } catch (SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "댓글 좋아요 처리 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}