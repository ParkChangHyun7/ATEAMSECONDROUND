package seoul.its.info.services.boards.likes;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Optional;
import seoul.its.info.services.boards.likes.dto.LikePostDto;
import seoul.its.info.services.boards.likes.dto.LikeCommentDto;

@Mapper
public interface LikeMapper {
    @Select("SELECT id, post_id, user_id, nickname, created_at FROM post_likes WHERE post_id = #{postId} AND user_id = #{userId}")
    Optional<LikePostDto> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Insert("INSERT INTO post_likes (post_id, user_id, login_id, nickname) VALUES (#{postId}, #{userId}, #{loginId}, #{nickname})")
    void save(LikePostDto likePostDto);

    @Delete("DELETE FROM post_likes WHERE post_id = #{postId} AND user_id = #{userId}")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Update("UPDATE posts SET like_count = like_count + #{amount} WHERE id = #{postId}")
    void updatePostLikeCount(@Param("postId") Long postId, @Param("amount") int amount);

    @Select("SELECT id, comment_id, user_id, nickname, created_at FROM comment_likes WHERE comment_id = #{commentId} AND user_id = #{userId}")
    Optional<LikeCommentDto> findByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Insert("INSERT INTO comment_likes (comment_id, user_id, login_id, nickname) VALUES (#{commentId}, #{userId}, #{loginId}, #{nickname})")
    void saveCommentLike(LikeCommentDto likeCommentDto);

    @Delete("DELETE FROM comment_likes WHERE comment_id = #{commentId} AND user_id = #{userId}")
    void deleteCommentLikeByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Update("UPDATE comments SET like_count = like_count + #{amount} WHERE id = #{commentId}")
    void updateCommentLikeCount(@Param("commentId") Long commentId, @Param("amount") int amount);
}