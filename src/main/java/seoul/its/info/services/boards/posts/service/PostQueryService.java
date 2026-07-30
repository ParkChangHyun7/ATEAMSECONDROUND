package seoul.its.info.services.boards.posts.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import seoul.its.info.services.boards.posts.dto.PostResponseDto;

public interface PostQueryService {

    /**
     * 특정 게시글의 상세 정보를 조회합니다.
     * TODO: 댓글/대댓글 정보도 함께 조회하는 로직 추가 예정 (PostQueryService 와 역할 분담 고려)
     *
     * @param boardId      게시판 ID
     * @param postId       게시글 ID
     * @param userDetails  현재 인증된 사용자 정보 (필요시 열람 권한 확인용)
     * @return 게시글 상세 정보
     */
    PostResponseDto getPostDetail(Long boardId, Long postId, UserDetails userDetails);

    // 특정 게시판의 게시글 목록 조회 (공지 포함, 페이징 처리) 및 총 개수 반환
    Map<String, Object> getPostList(Long boardId, int page, int pageSize, UserDetails userDetails);
} 