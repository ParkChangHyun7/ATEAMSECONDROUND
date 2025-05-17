package seoul.its.info.services.boards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import seoul.its.info.services.boards.service.BoardService; // Assuming BoardService exists
import seoul.its.info.services.boards.dto.BoardRequestDto; // Import BoardRequestDto

import java.util.Collections;

@Controller
@RequestMapping("/admin/boards") // 게시판 관련 기본 경로 설정
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/")
    public String getBoardList(Model model) {
        model.addAttribute("pageTitle", "게시판 관리");
        model.addAttribute("contentPage", "content_pages/admin/boards/boards.jsp");
        model.addAttribute("scriptsPage", "include/admin/boards/scripts.jsp");
        model.addAttribute("resourcesPage", "include/admin/boards/resources.jsp");
        return "base";
    }

    // 게시판 목록 JSON 데이터를 반환하는 API 엔드포인트
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<?> getBoardListJson() {
        return ResponseEntity.ok(boardService.getBoardList());
    }

    // 특정 게시판 상세 조회
    @GetMapping("/detail/{boardId}")
    @ResponseBody
    public ResponseEntity<?> getBoardDetail(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoardDetail(boardId));
    }

    // 게시판 생성
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createBoard(@RequestBody BoardRequestDto boardRequestDto) {
        boardService.createBoard(boardRequestDto); // 🔥 확인 필요: createBoard 메서드의 반환 타입 및 예외 처리
        return ResponseEntity.ok().build(); // 200 OK 응답
    }

    // 게시판 수정
    @PutMapping("/modify/{boardId}")
    @ResponseBody
    public ResponseEntity<?> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequestDto boardRequestDto) {
        int affectedRows = boardService.updateBoard(boardId, boardRequestDto); // 서비스에서 반환 값 받음
        if (affectedRows > 0) {
             // 성공 시 JSON 응답 반환
             return ResponseEntity.ok(Collections.singletonMap("message", "게시판이 성공적으로 수정되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "게시판 정보 수정에 실패했습니다."));
        }
    }

    // 게시판 삭제
    @DeleteMapping("/delete/{boardId}")
    @ResponseBody
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId); // 🔥 확인 필요: deleteBoard 메서드의 반환 타입 및 예외 처리
        return ResponseEntity.ok().build(); // 200 OK 응답
    }

    // TODO: 향후 Spring Security 표현식 기반 접근 제어 (EL) 도입 고려
    // 필요성:
    // 1. 선언적 권한 관리: 서비스 계층의 비즈니스 로직과 권한 검사 로직을 분리하여 코드 가독성 및 유지보수성 향상.
    // 2. 일관된 권한 정책 적용: 컨트롤러 메소드 진입 전에 권한을 검사하므로, 서비스 계층까지 비인가 요청이 도달하는 것을 방지.
    // 3. 유연한 권한 표현: 사용자 역할(Role) 뿐만 아니라, 특정 객체에 대한 특정 행위(Permission) 기반의 세밀한 권한 제어 가능.
    // 상세 내용:
    // - @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter 등의 어노테이션 활용.
    // - 예시: @PreAuthorize("hasRole('ROLE_ADMIN')") 또는 @PreAuthorize("hasPermission(#boardId, 'seoul.its.info.services.boards.BoardsDto', 'EDIT')")
    // - 사용자 정의 권한 평가 로직을 위해 `PermissionEvaluator` 인터페이스 구현 필요.
    //   - `PermissionEvaluator`는 `hasPermission(Authentication authentication, Object targetDomainObject, Object permission)` 와
    //     `hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)` 메소드를 가짐.
    //   - 여기서 `targetId` (예: boardId), `targetType` (예: "BoardsDto" 또는 클래스명), `permission` (예: "READ", "WRITE", "DELETE")을 받아
    //     DB에서 해당 객체의 권한 정보(예: Boards.readRole, Boards.writeRole)와 사용자의 역할을 비교하여 true/false 반환.
    // - Spring Expression Language (SpEL)을 사용하여 다양한 조건 조합 가능.
    // 참고 자료: Spring Security 공식 문서의 Expression-Based Access Control 섹션
} 