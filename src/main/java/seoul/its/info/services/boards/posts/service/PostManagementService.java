package seoul.its.info.services.boards.posts.service;

import org.springframework.security.core.userdetails.UserDetails;
import seoul.its.info.services.boards.posts.dto.PostRequestDto;
import seoul.its.info.services.boards.posts.dto.PostResponseDto;

public interface PostManagementService {

    /**
     * 새 게시글을 생성합니다.
     *
     * @param boardId      게시판 ID
     * @param requestDto   게시글 생성 요청 데이터
     * @param userDetails  현재 인증된 사용자 정보
     * @return 생성된 게시글 정보
     */
    PostResponseDto createPost(Long boardId, PostRequestDto requestDto, UserDetails userDetails);

    /**
     * 특정 게시글을 수정합니다.
     *
     * @param boardId      게시판 ID
     * @param postId       게시글 ID
     * @param requestDto   게시글 수정 요청 데이터
     * @param userDetails  현재 인증된 사용자 정보 (수정 권한 확인용)
     * @return 수정된 게시글 정보
     */
    PostResponseDto updatePost(Long boardId, Long postId, PostRequestDto requestDto, UserDetails userDetails);

    /**
     * 특정 게시글을 삭제합니다.
     *
     * @param boardId      게시판 ID
     * @param postId       게시글 ID
     * @param userDetails  현재 인증된 사용자 정보 (삭제 권한 확인용)
     */
    void deletePost(Long boardId, Long postId, UserDetails userDetails);
} 