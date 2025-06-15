package seoul.its.info.services.boards.posts.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seoul.its.info.services.boards.BoardMapper;
import seoul.its.info.services.boards.dto.BoardsDto;
import seoul.its.info.services.boards.posts.PostMapper;
import seoul.its.info.services.boards.posts.dto.PostListDto;
import seoul.its.info.services.boards.posts.dto.PostResponseDto;
import seoul.its.info.common.exception.SystemException;
import seoul.its.info.common.exception.ErrorCode;
import seoul.its.info.services.boards.service.BoardHelperService;
import seoul.its.info.services.boards.posts.dto.IndexPostListDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostQueryServiceImpl implements PostQueryService {

    private final PostMapper postMapper;
    private final BoardMapper boardMapper;
    private final ViewCountManager viewCountManager;
    private final BoardHelperService boardHelperService;

    public PostQueryServiceImpl(PostMapper postMapper, BoardMapper boardMapper, ViewCountManager viewCountManager, BoardHelperService boardHelperService) {
        this.postMapper = postMapper;
        this.boardMapper = boardMapper;
        this.viewCountManager = viewCountManager;
        this.boardHelperService = boardHelperService;
    }

    // 특정 게시판의 게시글 목록 조회 (공지 포함, 페이징 처리) 및 총 개수 반환
    @Override
    public Map<String, Object> getPostList(Long boardId, int page, int pageSize, UserDetails userDetails) {
        BoardsDto board = boardMapper.getBoardDetail(boardId);

        if (board == null || board.getIsActive() == 0) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("posts", new ArrayList<>());
            emptyResult.put("totalRegularPosts", 0);
            emptyResult.put("canWrite", false);
            emptyResult.put("boardName", "알 수 없는 게시판");
            return emptyResult;
        }
        
        // BoardHelperService를 통해 읽기 권한 확인
        if (!boardHelperService.canReadBoard(boardId, userDetails)) {
             // 게시판이 존재하지 않거나 비활성화되었거나 권한이 없는 경우
            throw new AccessDeniedException("게시판을 읽을 권한이 없습니다."); // 상세 메시지는 BoardHelperService에서 이미 처리되므로 간결하게
        }

        // BoardHelperService를 통해 쓰기 권한 확인
        boolean canWrite = boardHelperService.canWriteBoard(boardId, userDetails);

        // 공지 게시글 목록 조회
        List<PostListDto> noticePosts = postMapper.getNoticePostListByBoardId(boardId);

        // 일반 게시글 총 개수 조회
        int totalRegularPosts = postMapper.countRegularPostsByBoardId(boardId);

        // 일반 게시글 페이징 계산
        int offset = (page - 1) * pageSize;

        // 일반 게시글 목록 조회
        List<PostListDto> regularPosts = postMapper.getRegularPostListByBoardId(boardId, offset, pageSize);

        // 공지 게시글과 일반 게시글 합치기 (공지가 상위에 위치)
        List<PostListDto> combinedList = new ArrayList<>();
        combinedList.addAll(noticePosts);
        combinedList.addAll(regularPosts);

        // 결과 Map 생성 및 반환
        Map<String, Object> result = new HashMap<>();
        result.put("posts", combinedList);
        result.put("totalRegularPosts", totalRegularPosts);
        result.put("canWrite", canWrite);
        result.put("boardName", board.getName());

        return result;
    }

    // 특정 게시글의 상세 정보 조회
    @Override
    @Transactional
    public PostResponseDto getPostDetail(Long boardId, Long postId, UserDetails userDetails) {
        // BoardHelperService를 통해 읽기 권한 확인
        if (!boardHelperService.canReadBoard(boardId, userDetails)) {
            throw new AccessDeniedException("게시글을 읽을 권한이 없습니다."); // 상세 메시지는 BoardHelperService에서 이미 처리되므로 간결하게
        }

        // ViewCountManager를 통해 조회수 무한 증가 방지 (3시간에 1회 가능)
        if (viewCountManager.shouldIncrementViewCount(postId)) {
            postMapper.incrementViewCount(postId);
        }

        PostResponseDto postResponse = postMapper.getPostDetail(boardId, postId);
        // IP 주소 마스킹 처리
        if (postResponse != null) {
            String ip = postResponse.getIpAddress();
            if (ip != null && !ip.isEmpty() && ip.contains(".")) {
                String[] ipParts = ip.split("\\.");
                if (ipParts.length == 4) {
                    postResponse.setIpAddress(ipParts[0] + "." + ipParts[1] + ".*.*");
                } else {
                    postResponse.setIpAddress("*.*.*.*");
                }
            } else {
                postResponse.setIpAddress("*.*.*.*");
            }
        }                
        if (postResponse == null) {
            throw new SystemException(ErrorCode.POST_NOT_FOUND.getStatus().name(), ErrorCode.POST_NOT_FOUND.getMessage());
        }

        // TODO: fileIncluded, imageIncluded, thumbnailPath가 1일 때는 프론트에서
        // 파일 및 이미지 링크 처리 할 수 있도록 file 테이블 정보 일부도 같이 보내주도록 수정해야됨
        // 현재는 파일 첨부 미구현으로 둠

        // TODO: 댓글/대댓글 정보 조회 및 PostResponseDto에 설정
        // List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        // postResponse.setComments(comments); // PostResponseDto에 댓글 목록 필드 추가 가정

        return postResponse;
    }

    // 인덱스 페이지용 공지사항 게시글 목록 조회
    @Override
    public List<IndexPostListDto> getPostsForIndex() {
        // 공지사항 게시판(boardId=3)의 최신 게시글 5개를 가져옴
        long noticeBoardId = 3L;
        int limit = 5;
        return postMapper.findPostsForIndex(noticeBoardId, limit);
    }
} 