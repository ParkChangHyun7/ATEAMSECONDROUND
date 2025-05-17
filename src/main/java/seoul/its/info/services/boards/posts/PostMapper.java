package seoul.its.info.services.boards.posts;

import org.apache.ibatis.annotations.*;
import seoul.its.info.services.boards.posts.dto.PostRequestDto;
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
    @Select("SELECT id, board_id, user_id, writer, title, content, view_count, comment_count, like_count, report_count, is_notice, is_anonymous, is_blinded, is_deleted, file_included, image_included, writer_role, thumbnail_path, report_status, ip_address, created_at, updated_at FROM posts WHERE id = #{postId} AND board_id = #{boardId} AND is_deleted = 0")
    PostResponseDto getPostDetail(@Param("boardId") Long boardId, @Param("postId") Long postId);

    // 게시글 생성
    @Insert("INSERT INTO posts (board_id, user_id, writer, title, content, is_anonymous, is_notice, file_included, image_included, writer_role, ip_address) " +
            "VALUES (#{postRequestDto.boardId}, #{postRequestDto.userId}, #{postRequestDto.writer}, #{postRequestDto.title}, #{postRequestDto.content}, #{postRequestDto.isAnonymous}, #{postRequestDto.isNotice}, #{postRequestDto.fileIncluded}, #{postRequestDto.imageIncluded}, #{postRequestDto.writerRole}, #{postRequestDto.ipAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createPost(@Param("postRequestDto") PostRequestDto postRequestDto);

    // 게시글 수정
    @Update("UPDATE posts SET title = #{postRequestDto.title}, content = #{postRequestDto.content}, is_anonymous = #{postRequestDto.isAnonymous}, is_notice = #{postRequestDto.isNotice}, " +
            "file_included = #{postRequestDto.fileIncluded}, image_included = #{postRequestDto.imageIncluded}, updated_at = NOW() WHERE id = #{postId} AND board_id = #{boardId} AND is_deleted = 0")
    int updatePost(@Param("boardId") Long boardId, @Param("postId") Long postId, @Param("postRequestDto") PostRequestDto postRequestDto);

    // 게시글 삭제 (논리적 삭제)
    @Update("UPDATE posts SET is_deleted = 1, updated_at = NOW() WHERE id = #{postId} AND board_id = #{boardId} AND is_deleted = 0")
    int deletePost(@Param("boardId") Long boardId, @Param("postId") Long postId);

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
} 