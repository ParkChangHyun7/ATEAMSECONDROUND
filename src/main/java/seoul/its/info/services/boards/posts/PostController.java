package seoul.its.info.services.boards.posts;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import seoul.its.info.services.boards.posts.service.PostQueryService;
import seoul.its.info.services.boards.posts.service.PostManagementService;
import seoul.its.info.services.boards.posts.dto.PostRequestDto;
import seoul.its.info.services.boards.posts.dto.PostResponseDto;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;
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
    private final PostManagementService postManagementService;

    public PostController(PostQueryService postQueryService, PostManagementService postManagementService) {
        this.postQueryService = postQueryService;
        this.postManagementService = postManagementService;
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

    // 게시글 상세 조회 (API 반환)
    @GetMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> getPostDetailApi(@PathVariable Long boardId, @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postQueryService.getPostDetail(boardId, postId, userDetails));
    }

    // 게시글 상세 조회 (JSP 뷰 반환)
    @GetMapping("/read/{postId}")
    public String showPostReadPage(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails, // UserDetailsImpl 직접 사용
            Model model
    ) {
        PostResponseDto postResponseDto = postQueryService.getPostDetail(boardId, postId, userDetails);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            model.addAttribute("postJson", objectMapper.writeValueAsString(postResponseDto));
            
            Map<String, Object> boardConfig = Map.of(
                "id", postResponseDto.getBoardId(),
                "name", postResponseDto.getBoardName() != null ? postResponseDto.getBoardName() : "게시판"
            );
            model.addAttribute("boardConfigJson", objectMapper.writeValueAsString(boardConfig));

            // UserDetailsImpl이 null일 수 있으므로 방어 코드 추가
            if (userDetails != null) {
                model.addAttribute("currentUserJson", objectMapper.writeValueAsString(Map.of(
                    "username", userDetails.getUsername(),
                    "nickname", userDetails.getNickname(),
                    "role", userDetails.getRole() // UserDetailsImpl에 getRole()이 숫자나 Enum을 반환한다고 가정
                )));
            } else {
                model.addAttribute("currentUserJson", objectMapper.writeValueAsString(Collections.emptyMap()));
            }

        } catch (Exception e) {
            model.addAttribute("postJson", "{}");
        }

        model.addAttribute("pageTitle", (postResponseDto.getBoardName() != null ? postResponseDto.getBoardName() : "") + " - " + postResponseDto.getTitle());
        model.addAttribute("contentPage", "content_pages/boards/read.jsp");
        model.addAttribute("scriptsPage", "include/boards/read/scripts.jsp");
        model.addAttribute("resourcesPage", "include/boards/read/resources.jsp");

        return "base";
    }

    // 게시글 생성 (POST 요청은 API로 처리)
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createPost(@PathVariable Long boardId, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postManagementService.createPost(boardId, requestDto, userDetails));
    }

    // 게시글 수정 (PUT 요청은 API로 처리)
    @PutMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> updatePost(@PathVariable Long boardId, @PathVariable Long postId, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postManagementService.updatePost(boardId, postId, requestDto, userDetails));
    }

    // 게시글 삭제 (DELETE 요청은 API로 처리)
    @DeleteMapping("/delete/{postId}")
    @ResponseBody
    public ResponseEntity<?> deletePost(@PathVariable Long boardId, @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postManagementService.deletePost(boardId, postId, userDetails);
        return ResponseEntity.ok().build(); // 성공 시 200 OK 와 빈 body 반환
    }
} 