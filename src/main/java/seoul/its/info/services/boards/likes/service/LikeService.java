package seoul.its.info.services.boards.likes.service;

import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.services.boards.likes.dto.LikePostDto;
import seoul.its.info.services.boards.likes.dto.LikeCommentDto;

public interface LikeService {
    LikePostDto toggleLike(Long postId, UserDetailsImpl userDetails);

    LikeCommentDto toggleCommentLike(Long commentId, UserDetailsImpl userDetails);

    boolean isPostLikedByUser(Long postId, UserDetailsImpl userDetails);
}