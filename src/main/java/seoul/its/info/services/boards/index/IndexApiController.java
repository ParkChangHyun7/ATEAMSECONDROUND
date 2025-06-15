package seoul.its.info.services.boards.index;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seoul.its.info.services.boards.posts.dto.IndexPostListDto;
import seoul.its.info.services.boards.posts.service.PostQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/index")
public class IndexApiController {

    private final PostQueryService postQueryService;

    public IndexApiController(PostQueryService postQueryService) {
        this.postQueryService = postQueryService;
    }

    @GetMapping("/notices")
    public ResponseEntity<List<IndexPostListDto>> getNoticeListForIndex() {
        List<IndexPostListDto> noticeList = postQueryService.getPostsForIndex();
        return ResponseEntity.ok(noticeList);
    }
}
