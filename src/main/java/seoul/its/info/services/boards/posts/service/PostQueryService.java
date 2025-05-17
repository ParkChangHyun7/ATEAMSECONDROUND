package seoul.its.info.services.boards.posts.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface PostQueryService {

    // 특정 게시판의 게시글 목록 조회 (공지 포함, 페이징 처리) 및 총 개수 반환
    Map<String, Object> getPostList(Long boardId, int page, int pageSize, UserDetails userDetails);
} 