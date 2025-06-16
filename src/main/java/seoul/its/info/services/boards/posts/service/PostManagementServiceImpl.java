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
import seoul.its.info.common.exception.ErrorCode; // ErrorCode 임포트
import seoul.its.info.common.util.file.imageupload.ImageUploadService; // ImageUploadService 임포트 추가

// OWASP Antisamy 임포트
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

// PostConstruct 임포트
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class PostManagementServiceImpl implements PostManagementService {

    private final int ADMIN_ROLE_THRESHOLD = 100; // 관리자 역할 임계값 정의 (설정 파일 등에서 관리 가능)
    private final PostMapper postMapper;
    private final PostQueryService postQueryService;
    private final ImageUploadService imageUploadService; // ImageUploadService 주입 추가

    // OWASP Antisamy 정책 로드
    private Policy policy;

    // @PostConstruct 어노테이션을 사용하여 의존성 주입 완료 후 정책 로드
    @PostConstruct
    private void loadAntisamyPolicy() {
        try {
            policy = Policy.getInstance(getClass().getClassLoader().getResourceAsStream("antisamy.xml"));
        } catch (PolicyException e) {
            System.err.println("Critical: Failed to load Antisamy policy file. " + e.getMessage());
            throw new RuntimeException("Failed to load Antisamy policy file", e);
        }
    }

    @Override
    @Transactional
    public PostResponseDto createPost(Long boardId, PostRequestDto requestDto, UserDetails userDetails,
            String clientIp) {
        UserDetailsImpl principal = (UserDetailsImpl) userDetails;
        if (principal == null) {
            throw new SystemException(ErrorCode.AUTHENTICATION_FAILED.getStatus().name(), "사용자 인증 정보를 찾을 수 없습니다.");
        }

        Long currentUserId = principal.getId();
        String writerName = principal.getNickname();
        if (writerName == null) {
            writerName = "Unknown";
        }

        PostsDto newPost = new PostsDto();
        newPost.setBoardId(boardId);
        newPost.setUserId(currentUserId);
        newPost.setWriter(writerName);

        if (requestDto == null) {
            throw new SystemException(ErrorCode.INVALID_INPUT_VALUE.getStatus().name(), "요청 데이터가 비어있습니다.");
        }
        newPost.setTitle(requestDto.getTitle());

        String cleanedContent = sanitizeHtml(requestDto.getContent());
        newPost.setContent(cleanedContent);

        if (requestDto.getIsNotice() != null && requestDto.getIsNotice() == 1) {
            boolean isAdmin = principal.getRole() != null && principal.getRole() >= ADMIN_ROLE_THRESHOLD;
            if (!isAdmin) {
                throw new SystemException(ErrorCode.AUTHORIZATION_FAILED.getStatus().name(), "공지사항 등록은 관리자 권한이 필요합니다.");
            }
        }
        newPost.setIsNotice(requestDto.getIsNotice() != null && requestDto.getIsNotice() == 1 ? 1 : 0);
        newPost.setIsAnonymous(requestDto.getIsAnonymous() != null && requestDto.getIsAnonymous() == 1 ? 1 : 0);
        newPost.setWriterRole(principal.getRole());
        newPost.setIpAddress(clientIp);

        postMapper.createPost(newPost);

        if (newPost.getId() == null) {
            throw new SystemException(ErrorCode.POST_CREATION_FAILED.getStatus().name(),
                    ErrorCode.POST_CREATION_FAILED.getMessage());
        }

        imageUploadService.assignPostIdToTemporaryImages(currentUserId, 0, boardId, newPost.getId());

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
            throw new SystemException(ErrorCode.POST_NOT_FOUND.getStatus().name(),
                    ErrorCode.POST_NOT_FOUND.getMessage());
        }

        boolean isAdmin = principal.getRole() != null && principal.getRole() >= ADMIN_ROLE_THRESHOLD;
        // 수정 권한 확인: 본인 게시물이거나, 공지사항(isNotice == 1)이면서 관리자 권한이 있는 경우만 허용
        if (!existingPost.getUserId().equals(currentUserId) && !(isAdmin && existingPost.getIsNotice() == 1)) {
            throw new SystemException(ErrorCode.AUTHORIZATION_FAILED.getStatus().name(),
                    ErrorCode.AUTHORIZATION_FAILED.getMessage());
        }

        PostsDto postToUpdate = new PostsDto();
        postToUpdate.setTitle(requestDto.getTitle());

        // XSS 클린징 적용
        String cleanedContent = sanitizeHtml(requestDto.getContent());
        postToUpdate.setContent(cleanedContent);

        postToUpdate.setIsNotice(requestDto.getIsNotice() != null && requestDto.getIsNotice() == 1 ? 1 : 0);
        postToUpdate.setIsAnonymous(requestDto.getIsAnonymous() != null && requestDto.getIsAnonymous() == 1 ? 1 : 0);
        // TODO: fileIncluded, imageIncluded 등 변경 가능한 다른 필드 설정

        int updatedRows = postMapper.updatePost(boardId, postId, postToUpdate);

        if (updatedRows == 0) {
            // 게시글이 존재했으나 업데이트가 되지 않은 경우, 동시성 문제 또는 다른 이유일 수 있음
            throw new SystemException(ErrorCode.POST_UPDATE_FAILED.getStatus().name(),
                    ErrorCode.POST_UPDATE_FAILED.getMessage());
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
            throw new SystemException(ErrorCode.POST_NOT_FOUND.getStatus().name(),
                    ErrorCode.POST_NOT_FOUND.getMessage());
        }

        boolean isAdmin = principal.getRole() != null && principal.getRole() >= ADMIN_ROLE_THRESHOLD;
        if (!postToDelete.getUserId().equals(currentUserId) && !isAdmin) {
            throw new SystemException(ErrorCode.AUTHORIZATION_FAILED.getStatus().name(),
                    ErrorCode.AUTHORIZATION_FAILED.getMessage());
        }

        int deletedRows = postMapper.deletePost(boardId, postId);

        if (deletedRows == 0) {
            // 게시글이 존재했으나 삭제가 되지 않은 경우
            throw new SystemException(ErrorCode.POST_DELETE_FAILED.getStatus().name(),
                    ErrorCode.POST_DELETE_FAILED.getMessage());
        }
        // TODO: 관련된 데이터(댓글, 좋아요 등) 처리 로직 (CASCADE 설정이 없다면 Mapper에서, 아니면 여기서 서비스 호출)
        // commentService.deleteCommentsByPostId(postId);
        // likeService.deleteLikesByPostId(postId);
    }

    // HTML 내용을 XSS 공격으로부터 안전하게 클린징하는 헬퍼 메서드
    private String sanitizeHtml(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        try {
            AntiSamy as = new AntiSamy();
            return as.scan(html, policy).getCleanHTML();
        } catch (ScanException | PolicyException e) {
            System.err.println("Error sanitizing HTML: " + e.getMessage());
            // e.printStackTrace(); // 스택 트레이스 로깅은 필요에 따라 추가함.
            return "";
        }
    }
}