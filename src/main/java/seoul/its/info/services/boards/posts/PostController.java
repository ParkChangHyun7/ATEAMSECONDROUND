package seoul.its.info.services.boards.posts;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/boards/{boardId}/posts") // 게시판 ID를 포함한 기본 경로 설정
public class PostController {

    // 게시글 목록 조회
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getPostList(@PathVariable Long boardId) {
        // TODO: Implement logic to fetch post list for the given boardId
        return ResponseEntity.ok(Collections.emptyList()); // 임시 응답
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> getPostDetail(@PathVariable Long boardId, @PathVariable Long postId) {
        // TODO: Implement logic to fetch post detail for the given boardId and postId
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }

    // 게시글 생성 (POST 요청은 API로 처리)
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createPost(@PathVariable Long boardId, @RequestBody Object postRequestDto) {
        // TODO: Implement logic to create a new post
        // postRequestDto 대신 실제 DTO 클래스를 사용해야 합니다.
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }

    // 게시글 수정 (PUT 요청은 API로 처리)
    @PutMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> updatePost(@PathVariable Long boardId, @PathVariable Long postId, @RequestBody Object postRequestDto) {
        // TODO: Implement logic to update the post
        // postRequestDto 대신 실제 DTO 클래스를 사용해야 합니다.
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }

    // 게시글 삭제 (DELETE 요청은 API로 처리)
    @DeleteMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> deletePost(@PathVariable Long boardId, @PathVariable Long postId) {
        // TODO: Implement logic to delete the post
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }
} 