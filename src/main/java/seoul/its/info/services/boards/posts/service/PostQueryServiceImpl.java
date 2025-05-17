package seoul.its.info.services.boards.posts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import seoul.its.info.services.boards.BoardMapper;
import seoul.its.info.services.boards.dto.BoardsDto;
import seoul.its.info.services.boards.posts.PostMapper;
import seoul.its.info.services.boards.posts.dto.PostListDto;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostQueryServiceImpl implements PostQueryService {

    private final PostMapper postMapper;
    private final BoardMapper boardMapper;

    @Autowired
    public PostQueryServiceImpl(PostMapper postMapper, BoardMapper boardMapper) {
        this.postMapper = postMapper;
        this.boardMapper = boardMapper;
    }

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
        
        Integer userRole = 0;
        if (userDetails instanceof UserDetailsImpl) {
            UserDetailsImpl customUserDetails = (UserDetailsImpl) userDetails;
            if (customUserDetails.getRole() != null) {
                userRole = customUserDetails.getRole();
            }
        } else if (userDetails != null) {
            String authority = userDetails.getAuthorities().stream()
                                   .findFirst()
                                   .map(auth -> auth.getAuthority())
                                   .orElse("0");
            try {
                userRole = Integer.parseInt(authority);
            } catch (NumberFormatException e) {
                // 권한 문자열이 숫자가 아닐 경우 기본값(0) 유지 또는 로깅
            }
        }

        if (userRole < board.getReadRole()) {
            throw new AccessDeniedException("게시판을 읽을 권한이 없습니다. 필요한 권한: " + board.getReadRole() + ", 현재 권한: " + userRole);
        }

        boolean canWrite = (userRole >= board.getWriteRole());

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

        // IP 주소 마스킹 처리
        for (PostListDto post : combinedList) {
            String ip = post.getIpAddress();
            if (ip != null && !ip.isEmpty() && ip.contains(".")) {
                String[] ipParts = ip.split("\\.");
                if (ipParts.length == 4) {
                    post.setIpAddress(ipParts[0] + "." + ipParts[1] + ".*.*");
                } else {
                    post.setIpAddress(ipParts[0] + ".*.*.*");
                }
            } else if (ip != null && !ip.isEmpty()) {
                post.setIpAddress("*.*.*.*");
            }
            else {
                 post.setIpAddress("*.*");
            }
        }

        // 결과 Map 생성 및 반환
        Map<String, Object> result = new HashMap<>();
        result.put("posts", combinedList);
        result.put("totalRegularPosts", totalRegularPosts);
        result.put("canWrite", canWrite);
        result.put("boardName", board.getName());

        return result;
    }
} 