package seoul.its.info.services.boards.posts;

import org.apache.ibatis.annotations.*;
import seoul.its.info.services.boards.posts.dto.PostListDto;
import seoul.its.info.services.boards.posts.dto.PostResponseDto;
import seoul.its.info.services.boards.posts.dto.PostsDto;

import java.util.List;

@Mapper
public interface PostMapper {

    // 특정 게시판의 게시글 목록 조회
    @Select("SELECT id, board_id, user_id, writer, title, content, view_count, comment_count, like_count, report_count, is_notice, is_anonymous, is_blinded, is_deleted, file_included, image_included, writer_role, thumbnail_path, report_status, ip_address, created_at, updated_at FROM posts WHERE board_id = #{boardId} AND is_deleted = 0")
    List<PostListDto> getPostListByBoardId(@Param("boardId") Long boardId);

    // 게시글 상세 조회
    @Select("SELECT p.id, p.board_id, b.name AS board_name, p.user_id, p.writer, p.title, p.content, " +
            "p.view_count, p.comment_count, p.like_count, p.report_count, " +
            "p.is_notice, p.is_anonymous, p.is_blinded, p.is_deleted, " +
            "p.file_included, p.image_included, p.writer_role, p.thumbnail_path, " +
            "p.report_status, p.ip_address, p.created_at, p.updated_at " +
            "FROM posts p LEFT JOIN boards b ON p.board_id = b.id " +
            "WHERE p.id = #{postId} AND p.board_id = #{boardId} AND p.is_deleted = 0")
    PostResponseDto getPostDetail(@Param("boardId") Long boardId, @Param("postId") Long postId);

    // 게시글 생성
    @Insert("INSERT INTO posts (board_id, user_id, writer, title, content, is_anonymous, is_notice, file_included, image_included, writer_role, ip_address, created_at, updated_at) " +
            "VALUES (#{post.boardId}, #{post.userId}, #{post.writer}, #{post.title}, #{post.content}, #{post.isAnonymous}, #{post.isNotice}, #{post.fileIncluded}, #{post.imageIncluded}, #{post.writerRole}, #{post.ipAddress}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "post.id")
    int createPost(@Param("post") PostsDto post);

    // 게시글 수정
    @Update("UPDATE posts SET title = #{postDto.title}, content = #{postDto.content}, is_anonymous = #{postDto.isAnonymous}, is_notice = #{postDto.isNotice}, " +
            "file_included = #{postDto.fileIncluded}, image_included = #{postDto.imageIncluded}, updated_at = NOW() WHERE id = #{postId} AND board_id = #{boardId} AND is_deleted = 0")
    int updatePost(@Param("boardId") Long boardId, @Param("postId") Long postId, @Param("postDto") PostsDto postDto);

    // 게시글 삭제 (논리적 삭제)
    @Update("UPDATE posts SET is_deleted = 1, updated_at = NOW() WHERE id = #{postId} AND board_id = #{boardId} AND is_deleted = 0")
    int deletePost(@Param("boardId") Long boardId, @Param("postId") Long postId);

    // 조회수 증가
    @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{postId}")
    int incrementViewCount(@Param("postId") Long postId);

    // 삭제된 게시글 정보 deleted_posts 테이블에 삽입
    @Insert("INSERT INTO deleted_posts (id, board_id, user_id, login_id, writer, title, content, view_count, comment_count, like_count, report_count, is_notice, is_anonymous, is_blinded, is_deleted, file_included, writer_role, thumbnail_path, report_status, ip_address, created_at, updated_at, deleted_at, deleted_by_user_id) " +
            "VALUES (#{post.id}, #{post.boardId}, #{post.userId}, #{post.loginId}, #{post.writer}, #{post.title}, #{post.content}, #{post.viewCount}, #{post.commentCount}, #{post.likeCount}, #{post.reportCount}, #{post.isNotice}, #{post.isAnonymous}, #{post.isBlinded}, #{post.isDeleted}, #{post.fileIncluded}, #{post.writerRole}, #{post.thumbnailPath}, #{post.reportStatus}, #{post.ipAddress}, #{post.createdAt}, #{post.updatedAt}, NOW(), #{deletedByUserId})")
    int insertDeletedPost(@Param("post") PostsDto post, @Param("deletedByUserId") Long deletedByUserId);

    // 게시글 물리적 삭제
    @Delete("DELETE FROM posts WHERE id = #{postId} AND board_id = #{boardId}")
    int hardDeletePost(@Param("boardId") Long boardId, @Param("postId") Long postId);

    // 특정 게시판의 공지사항 게시글 목록 조회
    @Select("SELECT id, board_id, user_id, login_id, writer, title, content, view_count, comment_count, like_count, report_count, is_notice, is_anonymous, is_blinded, is_deleted, file_included, image_included, writer_role, thumbnail_path, report_status, ip_address, created_at, updated_at FROM posts WHERE board_id = #{boardId} AND is_deleted = 0 AND is_notice = 1 ORDER BY id DESC")
    List<PostListDto> getNoticePostListByBoardId(@Param("boardId") Long boardId);

    // 특정 게시판의 페이징 처리된 일반 게시글 목록 조회
    @Select("SELECT id, board_id, user_id, login_id, writer, title, content, view_count, comment_count, like_count, report_count, is_notice, is_anonymous, is_blinded, is_deleted, file_included, image_included, writer_role, thumbnail_path, report_status, ip_address, created_at, updated_at FROM posts WHERE board_id = #{boardId} AND is_deleted = 0 AND is_notice = 0 ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    List<PostListDto> getRegularPostListByBoardId(@Param("boardId") Long boardId, @Param("offset") int offset, @Param("limit") int limit);

    // 특정 게시판의 일반 게시글 총 개수 조회
    @Select("SELECT COUNT(*) FROM posts WHERE board_id = #{boardId} AND is_deleted = 0 AND is_notice = 0")
    int countRegularPostsByBoardId(@Param("boardId") Long boardId);

    // 게시글 수정을 위한 정보 조회 (userId, boardId 등 최소 정보)
    // PostManagementServiceImpl의 updatePost, deletePost에서 사용
    @Select("SELECT id, user_id, board_id FROM posts WHERE id = #{postId} AND is_deleted = 0")
    PostsDto findPostByIdForUpdate(@Param("postId") Long postId);
} 