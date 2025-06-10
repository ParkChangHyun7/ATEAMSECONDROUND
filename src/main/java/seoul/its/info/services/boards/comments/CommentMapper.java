package seoul.its.info.services.boards.comments;

import org.apache.ibatis.annotations.*;
import seoul.its.info.services.boards.comments.dto.CommentDto;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {
    
    // 게시글 ID로 댓글 목록 조회 (대댓글 포함)
    @Select("SELECT " +
            "id, post_id, user_id, login_id, writer, content, " +
            "is_parent, parent_comment_id, is_parent_secret, " +
            "post_writer_only, post_writer_id, is_anonymous, " +
            "is_blinded, is_deleted, deleted_by_post, image_included, " +
            "writer_role, report_status, ip_address, " +
            "created_at, updated_at, like_count " +
            "FROM comments WHERE post_id = #{postId} AND is_deleted = 0 ORDER BY created_at ASC")
    List<CommentDto> findByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    // 댓글 ID로 조회
    @Select("SELECT " +
            "id, post_id, user_id, login_id, writer, content, " +
            "is_parent, parent_comment_id, is_parent_secret, " +
            "post_writer_only, post_writer_id, is_anonymous, " +
            "is_blinded, is_deleted, deleted_by_post, image_included, " +
            "writer_role, report_status, ip_address, " +
            "created_at, updated_at, like_count " +
            "FROM comments WHERE id = #{id} AND is_deleted = 0")
    Optional<CommentDto> findById(@Param("id") Long id);
    
    // 댓글 생성
    @Insert("INSERT INTO comments (post_id, user_id, login_id, writer, content, is_parent, parent_comment_id, " +
            "is_parent_secret, post_writer_only, post_writer_id, is_anonymous, is_blinded, is_deleted, deleted_by_post, " +
            "image_included, writer_role, report_status, ip_address, created_at, like_count) " +
            "VALUES (#{postId}, #{userId}, #{loginId}, #{writer}, #{content}, #{isParent}, #{parentCommentId}, " +
            "#{isParentSecret}, #{postWriterOnly}, #{postWriterId}, #{isAnonymous}, #{isBlinded}, #{isDeleted}, #{deletedByPost}, " +
            "#{imageIncluded}, #{writerRole}, #{reportStatus}, #{ipAddress}, NOW(), #{likeCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(CommentDto commentDto);
    
    // 댓글 수정
    @Update("UPDATE comments SET content = #{content}, updated_at = NOW() " +
            "WHERE id = #{id} AND is_deleted = 0")
    void update(CommentDto commentDto);
    
    // 댓글 삭제 (논리적 삭제)
    @Update("UPDATE comments SET is_deleted = 1, updated_at = NOW() WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
    
    // 부모 댓글 ID로 대댓글 개수 조회
    @Select("SELECT COUNT(*) FROM comments WHERE parent_comment_id = #{parentId} AND is_deleted = 0")
    int countByParentId(@Param("parentId") Long parentId);
    
    // 게시글의 댓글 개수 조회
    @Select("SELECT COUNT(*) FROM comments WHERE post_id = #{postId} AND is_deleted = 0")
    int countByPostId(@Param("postId") Long postId);
}