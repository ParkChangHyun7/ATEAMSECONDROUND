package seoul.its.info.services.boards.service;

import seoul.its.info.services.boards.dto.BoardRequestDto;
import seoul.its.info.services.boards.dto.BoardsDto;

import java.util.List;

public interface BoardService {

    List<BoardsDto> getBoardList();

    BoardsDto getBoardDetail(Long boardId);

    void createBoard(BoardRequestDto boardRequestDto);

    int updateBoard(Long boardId, BoardRequestDto boardRequestDto);

    void deleteBoard(Long boardId);
} 