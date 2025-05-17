package seoul.its.info.services.boards;

import org.apache.ibatis.annotations.*;

import seoul.its.info.services.boards.dto.BoardRequestDto;
import seoul.its.info.services.boards.dto.BoardsDto;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("SELECT id, name, description, category_code, is_active, write_role, read_role, is_anonymous, created_at, updated_at, updated_by FROM boards")
    List<BoardsDto> getBoardList();

    @Select("SELECT id, name, description, category_code, is_active, write_role, read_role, is_anonymous, created_at, updated_at, updated_by FROM boards WHERE id = #{boardId}")
    BoardsDto getBoardDetail(@Param("boardId") Long boardId);

    @Insert("INSERT INTO boards (name, description, category_code, is_active, write_role, read_role, is_anonymous, updated_by) " +
            "VALUES (#{boardRequestDto.name}, #{boardRequestDto.description}, #{boardRequestDto.categoryCode}, #{boardRequestDto.isActive}, #{boardRequestDto.writeRole}, #{boardRequestDto.readRole}, #{boardRequestDto.isAnonymous}, #{boardRequestDto.updatedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createBoard(@Param("boardRequestDto") BoardRequestDto boardRequestDto);

    @Update("UPDATE boards SET name = #{boardRequestDto.name}, description = #{boardRequestDto.description}, category_code = #{boardRequestDto.categoryCode}, " +
            "is_active = #{boardRequestDto.isActive}, write_role = #{boardRequestDto.writeRole}, read_role = #{boardRequestDto.readRole}, " +
            "is_anonymous = #{boardRequestDto.isAnonymous}, updated_by = #{boardRequestDto.updatedBy} WHERE id = #{boardId}")
    int updateBoard(@Param("boardId") Long boardId, @Param("boardRequestDto") BoardRequestDto boardRequestDto);

    @Delete("DELETE FROM boards WHERE id = #{boardId}")
    void deleteBoard(@Param("boardId") Long boardId);
} 