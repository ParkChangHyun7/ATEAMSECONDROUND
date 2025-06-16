package seoul.its.info.services.boards.posts;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;
import seoul.its.info.services.boards.posts.service.PostQueryService;
import seoul.its.info.services.boards.posts.service.PostManagementService;
import seoul.its.info.services.boards.posts.dto.PostRequestDto;
import seoul.its.info.services.boards.posts.dto.PostResponseDto;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.common.util.ClientIpGetHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Collections;
// import java.util.List; // 더 이상 직접 사용하지 않음
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/boards/{boardId}/posts") // 게시판 ID를 포함한 기본 경로 설정
public class PostController {

    private final PostQueryService postQueryService;
    private final PostManagementService postManagementService;
    private final ClientIpGetHelper clientIpGetHelper;

    public PostController(PostQueryService postQueryService, PostManagementService postManagementService, ClientIpGetHelper clientIpGetHelper) {
        this.postQueryService = postQueryService;
        this.postManagementService = postManagementService;
        this.clientIpGetHelper = clientIpGetHelper;
    }

    // 글쓰기 페이지 표시
    @GetMapping("/write")
    public String showWritePage(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // 로그인 여부 확인
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요한 서비스입니다.");
            return "redirect:/boards/" + boardId + "/posts";
        }
        
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        
        // 사용자 역할 확인
        if (userDetailsImpl.getRole() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 역할이 설정되지 않았습니다. 관리자에게 문의해주세요.");
            return "redirect:/boards/" + boardId + "/posts";
        }
        
        // 게시판 설정 정보 조회 (예시로 간단히 구현)
        Map<String, Object> boardConfig = new HashMap<>();
        boardConfig.put("boardId", boardId);
        boardConfig.put("isAnonymous", 0); // 기본값, 실제로는 DB에서 조회 필요
        
        // 현재 사용자 정보 설정 (관리자 여부 확인용)
        Map<String, Object> currentUser = new HashMap<>();
        currentUser.put("username", userDetailsImpl.getUsername());
        currentUser.put("nickname", userDetailsImpl.getNickname());
        currentUser.put("role", userDetailsImpl.getRole());
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열로 변환하여 전달 및 HTML 이스케이프
            model.addAttribute("boardConfig", HtmlUtils.htmlEscape(objectMapper.writeValueAsString(boardConfig)));
            model.addAttribute("currentUser", HtmlUtils.htmlEscape(objectMapper.writeValueAsString(currentUser)));
        } catch (Exception e) {
            model.addAttribute("boardConfig", "{}");
            model.addAttribute("currentUser", "null");
        }
        
        model.addAttribute("pageTitle", "글쓰기");
        model.addAttribute("contentPage", "content_pages/boards/write.jsp");
        model.addAttribute("scriptsPage", "include/boards/write/scripts.jsp");
        model.addAttribute("resourcesPage", "include/boards/write/resources.jsp");
        
        return "base";
    }

    @GetMapping
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
            // posts 데이터를 JSON 문자열로 변환 및 HTML 이스케이프
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

    // // 게시글 상세 조회 (API 반환)
    // @GetMapping("/{postId}")
    // @ResponseBody
    // public ResponseEntity<?> getPostDetailApi(@PathVariable Long boardId, @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
    //     return ResponseEntity.ok(postQueryService.getPostDetail(boardId, postId, userDetails));
    // }

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
            postResponseDto.setNoReply(postResponseDto.getNoReply() != null ? postResponseDto.getNoReply() : 0); // null 값 방지
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
                    "role", userDetails.getRole(), // UserDetailsImpl에 getRole()이 숫자나 Enum을 반환한다고 가정
                    "userId", userDetails.getId() // userId 추가
                )));
            } else {
                model.addAttribute("currentUserJson", "null");
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

    // 게시글 수정 페이지 표시
    @GetMapping("/modify/{postId}")
    public String showModifyPage(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. 게시글 데이터 로드 및 권한 확인
        PostResponseDto postResponseDto = null;
        try {
            postResponseDto = postQueryService.getPostDetail(boardId, postId, userDetails);

            // 게시글 작성자 또는 관리자(공지사항인 경우)만 수정 페이지 접근 가능하도록 서버에서 최종 검증
            Long currentUserId = (userDetails != null) ? userDetails.getId() : null;
            boolean isAdmin = (userDetails != null && userDetails.getRole() != null && userDetails.getRole() >= 100);

            if (postResponseDto == null || !postResponseDto.getBoardId().equals(boardId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "게시글을 찾을 수 없습니다.");
                return "redirect:/boards/" + boardId + "/posts/read/" + postId;
            }

            if (!postResponseDto.getUserId().equals(currentUserId) && !(isAdmin && postResponseDto.getIsNotice() == 1)) {
                redirectAttributes.addFlashAttribute("errorMessage", "게시글 수정 권한이 없습니다.");
                return "redirect:/boards/" + boardId + "/posts/read/" + postId;
            }

        } catch (Exception e) {
            // 게시글 조회 중 오류 발생 시
            redirectAttributes.addFlashAttribute("errorMessage", "게시글을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/boards/" + boardId + "/posts";
        }

        // 2. JSON 데이터 준비 (read.jsp와 유사)
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

            if (userDetails != null) {
                model.addAttribute("currentUserJson", objectMapper.writeValueAsString(Map.of(
                    "username", userDetails.getUsername(),
                    "nickname", userDetails.getNickname(),
                    "role", userDetails.getRole()
                )));
            } else {
                model.addAttribute("currentUserJson", objectMapper.writeValueAsString(Collections.emptyMap()));
            }

        } catch (Exception e) {
            // JSON 변환 실패 시
            model.addAttribute("postJson", "{}");
            model.addAttribute("boardConfigJson", "{}");
            model.addAttribute("currentUserJson", "{}");
            System.err.println("Error preparing JSON for modify page: " + e.getMessage());
        }

        // 3. 뷰 정보 설정
        model.addAttribute("pageTitle", "글 수정");
        model.addAttribute("contentPage", "content_pages/boards/modify.jsp");
        model.addAttribute("scriptsPage", "include/boards/modify/scripts.jsp");
        model.addAttribute("resourcesPage", "include/boards/modify/resources.jsp");

        return "base";
    }

    // 게시글 생성 (POST 요청은 API로 처리)
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createPost(
            @PathVariable Long boardId,
            @RequestBody PostRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
            ) {
        
        // 클라이언트 IP 주소 가져오기
        String clientIp = clientIpGetHelper.getClientIpAddress(request);

        // PostManagementService를 통해 게시글 생성 (temporaryPostId 인자 제거)
        PostResponseDto createdPost = postManagementService.createPost(boardId, requestDto, userDetails, clientIp);
        return ResponseEntity.ok(createdPost);
    }

    // 게시글 수정 (PUT 요청은 API로 처리)
    @PutMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> updatePost(@PathVariable Long boardId, @PathVariable Long postId, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postManagementService.updatePost(boardId, postId, requestDto, userDetails));
    }

    // 게시글 삭제 (DELETE 요청은 API로 처리)
    @DeleteMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<?> deletePost(@PathVariable Long boardId, @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postManagementService.deletePost(boardId, postId, userDetails);
        return ResponseEntity.ok().build(); // 성공 시 200 OK 와 빈 body 반환
    }
} 