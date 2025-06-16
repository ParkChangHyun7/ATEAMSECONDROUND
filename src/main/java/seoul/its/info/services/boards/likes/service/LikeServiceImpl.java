package seoul.its.info.services.boards.likes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seoul.its.info.services.boards.likes.LikeMapper;
import seoul.its.info.services.boards.likes.dto.LikePostDto;
import seoul.its.info.services.boards.likes.dto.LikeCommentDto;
import seoul.its.info.services.boards.posts.PostMapper;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;

    @Override
    @Transactional
    public LikePostDto toggleLike(Long postId, UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }

        Optional<LikePostDto> existingLike = likeMapper.findByPostIdAndUserId(postId, userDetails.getId());

        if (existingLike.isPresent()) {
            // 좋아요가 이미 존재하면 삭제하고 like_count 감소
            likeMapper.deleteByPostIdAndUserId(postId, userDetails.getId());
            likeMapper.updatePostLikeCount(postId, -1);
            log.info("좋아요 취소 - 게시글 ID: {}, 사용자 ID: {}", postId, userDetails.getId());
            return null;
        } else {
            // 좋아요가 없으면 추가하고 like_count 증가
            LikePostDto newLike = new LikePostDto();
            newLike.setPostId(postId);
            newLike.setUserId(userDetails.getId());
            newLike.setLoginId(userDetails.getUsername());
            newLike.setNickname(userDetails.getNickname());
            likeMapper.save(newLike);
            likeMapper.updatePostLikeCount(postId, 1);
            log.info("좋아요 추가 - 게시글 ID: {}, 사용자 ID: {}", postId, userDetails.getId());
            return newLike;
        }
    }

    @Override
    @Transactional
    public LikeCommentDto toggleCommentLike(Long commentId, UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }

        Optional<LikeCommentDto> existingCommentLike = likeMapper.findByCommentIdAndUserId(commentId,
                userDetails.getId());

        if (existingCommentLike.isPresent()) {
            // 댓글 좋아요가 이미 존재하면 삭제하고 like_count 감소
            likeMapper.deleteCommentLikeByCommentIdAndUserId(commentId, userDetails.getId());
            likeMapper.updateCommentLikeCount(commentId, -1);
            log.info("댓글 좋아요 취소 - 댓글 ID: {}, 사용자 ID: {}", commentId, userDetails.getId());
            return null;
        } else {
            // 댓글 좋아요가 없으면 추가하고 like_count 증가
            LikeCommentDto newCommentLike = new LikeCommentDto();
            newCommentLike.setCommentId(commentId);
            newCommentLike.setUserId(userDetails.getId());
            newCommentLike.setLoginId(userDetails.getUsername());
            newCommentLike.setNickname(userDetails.getNickname());
            likeMapper.saveCommentLike(newCommentLike);
            likeMapper.updateCommentLikeCount(commentId, 1);
            log.info("댓글 좋아요 추가 - 댓글 ID: {}, 사용자 ID: {}", commentId, userDetails.getId());
            return newCommentLike;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLikedByUser(Long postId, UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return false; // 로그인하지 않은 사용자는 좋아요를 누를 수 없음
        }
        return likeMapper.findByPostIdAndUserId(postId, userDetails.getId()).isPresent();
    }
}