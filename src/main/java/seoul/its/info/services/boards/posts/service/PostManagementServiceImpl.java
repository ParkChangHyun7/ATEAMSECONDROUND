package seoul.its.info.services.boards.posts.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import seoul.its.info.domain.User; // MyBatis 사용으로 주석 처리
// import seoul.its.info.domain.post.Post; // MyBatis 사용으로 주석 처리
// import seoul.its.info.domain.post.PostRepository; // MyBatis 사용으로 주석 처리
import seoul.its.info.services.boards.posts.dto.PostRequestDto;
import seoul.its.info.services.boards.posts.dto.PostResponseDto;
import seoul.its.info.services.boards.posts.dto.PostsDto;
import seoul.its.info.services.boards.posts.PostMapper;
import seoul.its.info.services.users.login.detail.UserDetailsImpl; // 임포트 경로 수정
import seoul.its.info.common.exception.SystemException; // SystemException 임포트
import seoul.its.info.common.exception.ErrorCode;     // ErrorCode 임포트

@Service
@RequiredArgsConstructor
public class PostManagementServiceImpl implements PostManagementService {

    private final int ADMIN_ROLE_THRESHOLD = 100; // 관리자 역할 임계값 정의 (설정 파일 등에서 관리 가능)
    private final PostMapper postMapper;
    private final PostQueryService postQueryService;

    @Override
    @Transactional
    public PostResponseDto createPost(Long boardId, PostRequestDto requestDto, UserDetails userDetails) {
        UserDetailsImpl principal = (UserDetailsImpl) userDetails;
        Long currentUserId = principal.getId(); // Long 타입 userId 가져오기
        String writerName = principal.getNickname();

        PostsDto newPost = new PostsDto();
        newPost.setBoardId(boardId);
        newPost.setUserId(currentUserId); // Long 타입 userId 설정
        newPost.setWriter(writerName);
        newPost.setTitle(requestDto.getTitle());
        newPost.setContent(requestDto.getContent());
        // Integer 비교 수정: 0 또는 1과 같은 특정 값과 비교
        // 공지사항 등록은 관리자만 가능하도록 체크
        if (requestDto.getIsNotice() != null && requestDto.getIsNotice() == 1) {
            boolean isAdmin = principal.getRole() != null && principal.getRole() >= ADMIN_ROLE_THRESHOLD;
            if (!isAdmin) {
                throw new SystemException(ErrorCode.AUTHORIZATION_FAILED.getStatus().name(), "공지사항 등록은 관리자 권한이 필요합니다.");
            }
        }
        newPost.setIsNotice(requestDto.getIsNotice() != null && requestDto.getIsNotice() == 1 ? 1 : 0);
        newPost.setIsAnonymous(requestDto.getIsAnonymous() != null && requestDto.getIsAnonymous() == 1 ? 1 : 0);
        // TODO: fileIncluded, imageIncluded 설정 (PostRequestDto에 필드 추가 필요)
        // TODO: writerRole 설정 (principal.getRole() 사용)
        newPost.setWriterRole(principal.getRole());
        // TODO: ipAddress 설정 (Controller에서 HttpServletRequest 주입받아 처리 또는 AOP 활용)

        postMapper.createPost(newPost);

        if (newPost.getId() == null) {
            throw new SystemException(ErrorCode.POST_CREATION_FAILED.getStatus().name(), ErrorCode.POST_CREATION_FAILED.getMessage());
        }
        return postQueryService.getPostDetail(boardId, newPost.getId(), userDetails);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long boardId, Long postId, PostRequestDto requestDto, UserDetails userDetails) {
        UserDetailsImpl principal = (UserDetailsImpl) userDetails;
        Long currentUserId = principal.getId();

        // 1. 수정 대상 게시글 정보 조회 (작성자 ID, 게시판 ID 확인 목적)
        PostsDto existingPost = postMapper.findPostByIdForUpdate(postId);

        if (existingPost == null || !existingPost.getBoardId().equals(boardId)) {
            throw new SystemException(ErrorCode.POST_NOT_FOUND.getStatus().name(), ErrorCode.POST_NOT_FOUND.getMessage());
        }

        boolean isAdmin = principal.getRole() != null && principal.getRole() >= ADMIN_ROLE_THRESHOLD;
        if (!existingPost.getUserId().equals(currentUserId) && !isAdmin) {
            throw new SystemException(ErrorCode.AUTHORIZATION_FAILED.getStatus().name(), ErrorCode.AUTHORIZATION_FAILED.getMessage());
        }

        PostsDto postToUpdate = new PostsDto();
        postToUpdate.setTitle(requestDto.getTitle());
        postToUpdate.setContent(requestDto.getContent());
        postToUpdate.setIsNotice(requestDto.getIsNotice() != null && requestDto.getIsNotice() == 1 ? 1 : 0);
        postToUpdate.setIsAnonymous(requestDto.getIsAnonymous() != null && requestDto.getIsAnonymous() == 1 ? 1 : 0);
        // TODO: fileIncluded, imageIncluded 등 변경 가능한 다른 필드 설정

        int updatedRows = postMapper.updatePost(boardId, postId, postToUpdate);

        if (updatedRows == 0) {
            // 게시글이 존재했으나 업데이트가 되지 않은 경우, 동시성 문제 또는 다른 이유일 수 있음
            throw new SystemException(ErrorCode.POST_UPDATE_FAILED.getStatus().name(), ErrorCode.POST_UPDATE_FAILED.getMessage());
        }
        return postQueryService.getPostDetail(boardId, postId, userDetails);
    }

    @Override
    @Transactional
    public void deletePost(Long boardId, Long postId, UserDetails userDetails) {
        UserDetailsImpl principal = (UserDetailsImpl) userDetails;
        Long currentUserId = principal.getId();

        // 1. 삭제 대상 게시글 정보 조회 (작성자 ID, 게시판 ID 확인 목적)
        PostsDto postToDelete = postMapper.findPostByIdForUpdate(postId);

        if (postToDelete == null || !postToDelete.getBoardId().equals(boardId)) {
            throw new SystemException(ErrorCode.POST_NOT_FOUND.getStatus().name(), ErrorCode.POST_NOT_FOUND.getMessage());
        }

        boolean isAdmin = principal.getRole() != null && principal.getRole() >= ADMIN_ROLE_THRESHOLD;
        if (!postToDelete.getUserId().equals(currentUserId) && !isAdmin) {
            throw new SystemException(ErrorCode.AUTHORIZATION_FAILED.getStatus().name(), ErrorCode.AUTHORIZATION_FAILED.getMessage());
        }

        int deletedRows = postMapper.deletePost(boardId, postId);

        if (deletedRows == 0) {
            // 게시글이 존재했으나 삭제가 되지 않은 경우
            throw new SystemException(ErrorCode.POST_DELETE_FAILED.getStatus().name(), ErrorCode.POST_DELETE_FAILED.getMessage());
        }
        // TODO: 관련된 데이터(댓글, 좋아요 등) 처리 로직 (CASCADE 설정이 없다면 Mapper에서, 아니면 여기서 서비스 호출)
        // commentService.deleteCommentsByPostId(postId);
        // likeService.deleteLikesByPostId(postId);
    }
} 