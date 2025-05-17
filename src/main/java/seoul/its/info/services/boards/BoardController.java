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
@RequestMapping("/manage") // 게시판 관련 기본 경로 설정
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/boards")
    public String getBoardList(Model model) {
        model.addAttribute("pageTitle", "게시판 관리");
        model.addAttribute("contentPage", "content_pages/boards/boards.jsp");
        model.addAttribute("scriptsPage", "include/boards/scripts.jsp");
        model.addAttribute("resourcesPage", "include/boards/resources.jsp");
        return "base";
    }

    // 게시판 목록 JSON 데이터를 반환하는 API 엔드포인트
    @GetMapping("/boards/list")
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
    @PostMapping("/create/board")
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
} 