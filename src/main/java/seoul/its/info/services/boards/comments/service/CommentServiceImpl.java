package seoul.its.info.services.boards.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seoul.its.info.services.boards.comments.CommentMapper;
import seoul.its.info.services.boards.comments.dto.CommentDto;
import seoul.its.info.services.boards.comments.dto.CommentRequestDto;
import seoul.its.info.services.boards.comments.dto.CommentResponseDto;
import seoul.its.info.services.boards.comments.exception.CommentNotFoundException;
import seoul.its.info.services.boards.comments.exception.CommentPermissionDeniedException;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.services.boards.posts.service.PostQueryService;
import seoul.its.info.services.boards.posts.dto.PostResponseDto;
import seoul.its.info.common.util.ProfanityFilter;
import seoul.its.info.services.boards.posts.PostMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostQueryService postQueryService;
    private final ProfanityFilter profanityFilter;
    private final PostMapper postMapper;
    private static final int ADMIN_ROLE_THRESHOLD = 100; // 관리자 역할 임계값

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        List<CommentDto> comments = commentMapper.findByPostIdOrderByCreatedAtAsc(postId);
        return comments.stream()
                .filter(comment -> comment.getIsParent() != null && comment.getIsParent() == 0) // 부모 댓글만 필터링
                .map(comment -> convertToResponseDto(comment, comments))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        // 기본 IP 주소로 네 개의 매개변수를 가진 메서드 호출
        return createComment(null, postId, requestDto, userDetails, "0.0.0.0"); // boardId는 여기서 알 수 없으므로 null 전달
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long boardId, Long postId, CommentRequestDto requestDto, UserDetailsImpl userDetails, String ipAddress) {
        validateUser(userDetails);

        // 게시글의 댓글 금지 여부 확인 (no_reply)
        PostResponseDto post = postQueryService.getPostDetail(boardId, postId, userDetails); // boardId를 정확히 전달
        if (post != null && post.getNoReply() != null && post.getNoReply() != 0) {
            // 댓글 금지 게시글이고, 사용자가 관리자가 아닌 경우
            if (userDetails.getRole() == null || userDetails.getRole() < ADMIN_ROLE_THRESHOLD) {
                throw new CommentPermissionDeniedException("댓글 작성이 금지된 게시글입니다.");
            }
        }

        // 금칙어 검사 추가
        String forbiddenWord = profanityFilter.getFirstProfanity(requestDto.getContent());
        if (forbiddenWord != null) {
            throw new CommentPermissionDeniedException("댓글 내용에 금칙어 '" + forbiddenWord + "'(이)가 포함되어 있습니다. 해당 단어는 입력할 수 없습니다.");
        }

        // 대댓글인 경우 부모 댓글 존재 여부 확인
        if (requestDto.getParentCommentId() != null) {
            commentMapper.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException("상위 댓글을 찾을 수 없습니다."));
        }

        CommentDto commentDto = CommentDto.builder()
                .postId(postId)
                .userId(userDetails.getId())
                .loginId(userDetails.getUsername())
                .writer(userDetails.getNickname())
                .content(requestDto.getContent())
                .isParent(requestDto.getParentCommentId() == null ? 0 : 1)  // 부모 댓글이면 0, 대댓글이면 1 (CommentDto 정의에 맞춤)
                .parentCommentId(requestDto.getParentCommentId())
                .isParentSecret(0) // 기본값 0으로 설정. (추후 기능 추가 시 requestDto에서 받도록 수정 가능)
                .postWriterOnly(0)  // 기본값
                .postWriterId(null) // 원글 작성자 ID는 서비스에서 설정
                .isAnonymous(requestDto.getIsAnonymous() != null ? requestDto.getIsAnonymous() : 0)
                .isBlinded(0)  // 기본값
                .isDeleted(0)   // 삭제되지 않음
                .deletedByPost(0)  // 게시글 상태로 인한 삭제 아님 (필드명 변경)
                .imageIncluded(0)  // 기본값
                .writerRole(userDetails.getRole() != null ? userDetails.getRole().toString() : "USER")
                .reportStatus(0)  // 신고 상태 없음
                .ipAddress(ipAddress)  // 클라이언트 IP 주소 설정
                .likeCount(0) // 기본값 0으로 설정
                .build();

        commentMapper.save(commentDto);
        postMapper.updateCommentCount(postId, 1);
        log.info("새 댓글 생성 - 댓글 ID: {}, 작성자: {}, IP: {}", commentDto.getId(), userDetails.getNickname(), ipAddress);
        
        return convertToResponseDto(commentDto, List.of());
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        validateUser(userDetails);
        
        CommentDto existingComment = commentMapper.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("수정할 댓글을 찾을 수 없습니다."));
        
        // 작성자 본인만 수정 가능
        if (!isOwner(existingComment, userDetails)) {
            throw new CommentPermissionDeniedException("댓글 수정 권한이 없습니다.");
        }

        // 금칙어 검사 추가 (수정 시에도 적용)
        String forbiddenWord = profanityFilter.getFirstProfanity(requestDto.getContent());
        if (forbiddenWord != null) {
            throw new CommentPermissionDeniedException("댓글 내용에 금칙어 '" + forbiddenWord + "'(이)가 포함되어 있습니다. 해당 단어는 입력할 수 없습니다.");
        }

        // 기존 댓글 정보로 새 DTO 생성 (내용만 업데이트)
        CommentDto updatedComment = CommentDto.builder()
                .id(existingComment.getId())
                .postId(existingComment.getPostId())
                .userId(existingComment.getUserId())
                .loginId(existingComment.getLoginId())
                .writer(existingComment.getWriter())
                .content(requestDto.getContent()) // 내용만 업데이트
                .isParent(existingComment.getIsParent())
                .parentCommentId(existingComment.getParentCommentId())
                .isParentSecret(existingComment.getIsParentSecret())
                .postWriterOnly(existingComment.getPostWriterOnly())
                .postWriterId(existingComment.getPostWriterId())
                .isAnonymous(existingComment.getIsAnonymous())
                .isBlinded(existingComment.getIsBlinded())
                .isDeleted(existingComment.getIsDeleted())
                .deletedByPost(existingComment.getDeletedByPost()) // 필드명 변경
                .imageIncluded(existingComment.getImageIncluded())
                .writerRole(existingComment.getWriterRole())
                .reportStatus(existingComment.getReportStatus())
                .ipAddress(existingComment.getIpAddress())
                .createdAt(existingComment.getCreatedAt())
                .updatedAt(LocalDateTime.now()) // 수정 시간 업데이트
                .likeCount(existingComment.getLikeCount())
                .build();

        commentMapper.update(updatedComment);
        log.info("댓글 수정 완료 - 댓글 ID: {}", commentId);
        
        return convertToResponseDto(updatedComment, commentMapper.findByPostIdOrderByCreatedAtAsc(updatedComment.getPostId()));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {
        validateUser(userDetails);
        
        CommentDto existingComment = commentMapper.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("삭제할 댓글을 찾을 수 없습니다."));
        
        // 작성자 또는 관리자만 삭제 가능
        if (!canUserDeleteComment(existingComment, userDetails)) {
            throw new CommentPermissionDeniedException("댓글 삭제 권한이 없습니다.");
        }

        // 대댓글이 있는 경우 내용만 삭제 처리
        if (hasReplies(commentId)) {
            // 삭제된 댓글을 나타내는 새 DTO 생성
            CommentDto deletedComment = CommentDto.builder()
                    .id(existingComment.getId())
                    .postId(existingComment.getPostId())
                    .userId(existingComment.getUserId())
                    .loginId(existingComment.getLoginId())
                    .writer(existingComment.getWriter())
                    .content("삭제된 댓글입니다.")
                    .isParent(existingComment.getIsParent())
                    .parentCommentId(existingComment.getParentCommentId())
                    .isParentSecret(existingComment.getIsParentSecret())
                    .postWriterOnly(existingComment.getPostWriterOnly())
                    .postWriterId(existingComment.getPostWriterId())
                    .isAnonymous(existingComment.getIsAnonymous())
                    .isBlinded(existingComment.getIsBlinded())
                    .isDeleted(1) // 삭제 상태로 설정
                    .deletedByPost(existingComment.getDeletedByPost()) // 필드명 변경
                    .imageIncluded(0) // 이미지 제거
                    .writerRole(existingComment.getWriterRole())
                    .reportStatus(0) // 신고 상태 초기화
                    .ipAddress(existingComment.getIpAddress())
                    .createdAt(existingComment.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .likeCount(existingComment.getLikeCount())
                    .build();
            
            commentMapper.update(deletedComment);
            log.info("댓글 삭제 처리 (내용만 삭제) - 댓글 ID: {}", commentId);
        } else {
            // 대댓글이 없는 경우 완전 삭제
            commentMapper.deleteById(commentId);
            log.info("댓글 완전 삭제 - 댓글 ID: {}", commentId);
        }
        postMapper.updateCommentCount(existingComment.getPostId(), -1);
    }

    // ====== 내부 유틸리티 메서드 ======

    private void validateUser(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
    }

    private boolean isOwner(CommentDto comment, UserDetailsImpl userDetails) {
        return comment.getUserId().equals(userDetails.getId());
    }

    private boolean hasReplies(Long parentId) {
        return commentMapper.countByParentId(parentId) > 0;
    }

    private boolean canUserDeleteComment(CommentDto comment, UserDetailsImpl userDetails) {
        // 본인 댓글은 항상 삭제 가능함.
        if (comment.getUserId().equals(userDetails.getId())) {
            return true;
        }

        // 관리자 권한 확인 (레벨 100 이상인 경우).
        if (userDetails.getRole() != null && userDetails.getRole() >= ADMIN_ROLE_THRESHOLD) {
            try {
                int commentWriterRole = 0; // USER 역할인 경우 기본값 0으로 처리함.
                if (comment.getWriterRole() != null && !comment.getWriterRole().equals("USER")) {
                    commentWriterRole = Integer.parseInt(comment.getWriterRole());
                }
                // 현재 관리자 레벨이 댓글 작성자의 레벨보다 같거나 높은 경우 삭제 가능함.
                return userDetails.getRole() >= commentWriterRole;
            } catch (NumberFormatException e) {
                log.warn("댓글 작성자 역할 파싱 오류: {}", comment.getWriterRole());
                return false; // 파싱 오류 시 삭제 불가 처리함.
            }
        }
        return false; // 본인도 아니고 관리자도 아닌 경우 삭제 불가함.
    }

    private CommentResponseDto convertToResponseDto(CommentDto comment, List<CommentDto> allComments) {
        List<CommentResponseDto> replies = allComments.stream()
                .filter(c -> comment.getId().equals(c.getParentCommentId()))
                .map(c -> convertToResponseDto(c, allComments))
                .collect(Collectors.toList());

        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .isParent(comment.getIsParent())
                .parentCommentId(comment.getParentCommentId())
                .isParentSecret(comment.getIsParentSecret())
                .postWriterOnly(comment.getPostWriterOnly()) // 기존값 유지
                .isAnonymous(comment.getIsAnonymous())
                .isBlinded(comment.getIsBlinded())
                .isDeleted(comment.getIsDeleted()) // isDeleted 추가
                .deletedByPost(comment.getDeletedByPost()) // deletedByPost 추가
                .imageIncluded(comment.getImageIncluded())
                .writerRole(comment.getWriterRole())
                .reportStatus(comment.getReportStatus()) // reportStatus 추가
                .ipAddress(comment.getIpAddress()) // IP는 마스킹해서 반환 (기존값 유지)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likeCount(comment.getLikeCount()) // likeCount 추가
                .replies(replies)
                .build();
    }
}