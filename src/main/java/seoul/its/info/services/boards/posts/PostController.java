package seoul.its.info.services.boards.posts;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import seoul.its.info.services.boards.posts.service.PostQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Collections;
// import java.util.List; // 더 이상 직접 사용하지 않음
import java.util.Map;

@Controller
@RequestMapping("/boards/{boardId}/posts") // 게시판 ID를 포함한 기본 경로 설정
public class PostController {

    private final PostQueryService postQueryService;

    public PostController(PostQueryService postQueryService) {
        this.postQueryService = postQueryService;
    }

    // 게시글 목록 조회 (JSP 뷰 반환)
    @GetMapping
    // @ResponseBody // JSP 뷰를 반환하므로 제거
    public String getPostList(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        // PostQueryService를 사용하여 게시글 목록, 총 개수, 쓰기 권한 등 조회
        Map<String, Object> result = postQueryService.getPostList(boardId, page, pageSize, userDetails);

        // 서비스에서 가져온 게시판 이름으로 pageTitle 설정
        model.addAttribute("pageTitle", result.getOrDefault("boardName", "게시판"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 날짜를 타임스탬프(숫자 배열)가 아닌 표준 ISO 문자열로 출력하도록 명시적으로 설정
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            // posts 데이터를 JSON 문자열로 변환
            model.addAttribute("postsJson", objectMapper.writeValueAsString(result.get("posts")));
        } catch (Exception e) {
            // JSON 변환 실패 시 빈 배열 또는 에러 메시지 전달 (혹은 로깅)
            model.addAttribute("postsJson", "[]");
            // 로그 추가: e.printStackTrace(); 또는 로거 사용
        }

        model.addAttribute("totalRegularPosts", result.get("totalRegularPosts").toString());
        model.addAttribute("currentPage", String.valueOf(page));
        model.addAttribute("pageSize", String.valueOf(pageSize));
        model.addAttribute("boardId", String.valueOf(boardId));
        model.addAttribute("canWrite", result.get("canWrite").toString());

        model.addAttribute("contentPage", "content_pages/boards/list.jsp");
        model.addAttribute("scriptsPage", "include/boards/list/scripts.jsp");
        model.addAttribute("resourcesPage", "include/boards/list/resources.jsp");

        return "base";
    }

    // 게시글 목록 데이터 조회 (API 반환 - 페이지네이션용)
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPostListData(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // PostQueryService를 사용하여 게시글 목록 및 총 개수 조회
        Map<String, Object> result = postQueryService.getPostList(boardId, page, pageSize, userDetails);

        // 데이터만 JSON 형태로 반환
        return ResponseEntity.ok(result);
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> getPostDetail(@PathVariable Long boardId, @PathVariable Long postId) {
        // TODO: 주어진 boardId와 postId에 대한 게시글 상세 정보 조회 로직 구현 예정
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }

    // 게시글 생성 (POST 요청은 API로 처리)
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createPost(@PathVariable Long boardId, @RequestBody Object postRequestDto) {
        // TODO: 새 게시글 생성 로직 구현 예정
        // postRequestDto 대신 실제 DTO 클래스를 사용해야 합니다.
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }

    // 게시글 수정 (PUT 요청은 API로 처리)
    @PutMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> updatePost(@PathVariable Long boardId, @PathVariable Long postId, @RequestBody Object postRequestDto) {
        // TODO: 게시글 수정 로직 구현 예정
        // postRequestDto 대신 실제 DTO 클래스를 사용해야 합니다.
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }

    // 게시글 삭제 (DELETE 요청은 API로 처리)
    @DeleteMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> deletePost(@PathVariable Long boardId, @PathVariable Long postId) {
        // TODO: 게시글 삭제 로직 구현 예정
        return ResponseEntity.ok(Collections.emptyMap()); // 임시 응답
    }
} 