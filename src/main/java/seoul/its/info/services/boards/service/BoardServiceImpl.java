package seoul.its.info.services.boards.service;

import org.springframework.stereotype.Service;

import seoul.its.info.services.boards.BoardMapper;
import seoul.its.info.services.boards.dto.BoardRequestDto;
import seoul.its.info.services.boards.dto.BoardsDto;
import seoul.its.info.common.exception.BusinessException;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    public List<BoardsDto> getBoardList() {
        return boardMapper.getBoardList();
    }

    @Override
    public BoardsDto getBoardDetail(Long boardId) {
        return boardMapper.getBoardDetail(boardId);
    }

    @Override
    public void createBoard(BoardRequestDto boardRequestDto) {
        boardMapper.createBoard(boardRequestDto);
    }

    @Override
    public int updateBoard(Long boardId, BoardRequestDto boardRequestDto) {
        // 1. 현재 게시판 정보 조회
        BoardsDto existingBoard = boardMapper.getBoardDetail(boardId);
        if (existingBoard == null) {
            // 해당 ID의 게시판이 존재하지 않는 경우
            throw new BusinessException("BOARD_NOT_FOUND", "수정하려는 게시판을 찾을 수 없습니다.");
        }

        // 2. 요청받은 정보와 현재 정보 비교
        boolean isChanged =
            !existingBoard.getName().equals(boardRequestDto.getName()) ||
            !existingBoard.getDescription().equals(boardRequestDto.getDescription()) ||
            !existingBoard.getCategoryCode().equals(boardRequestDto.getCategoryCode()) ||
            !existingBoard.getIsActive().equals(boardRequestDto.getIsActive()) ||
            !existingBoard.getWriteRole().equals(boardRequestDto.getWriteRole()) ||
            !existingBoard.getReadRole().equals(boardRequestDto.getReadRole()) ||
            !existingBoard.getIsAnonymous().equals(boardRequestDto.getIsAnonymous());
            // updatedBy는 업데이트 시점에 변경될 수 있으므로 비교 대상에서 제외

        if (!isChanged) {
            // 3. 변경 사항이 없을 경우 예외 발생
            throw new BusinessException("NO_CHANGES", "게시판 정보에 변경 사항이 없습니다.");
        }

        // 4. 변경 사항이 있을 경우 업데이트 수행
        int affectedRows = boardMapper.updateBoard(boardId, boardRequestDto);

        // affectedRows가 0인 경우는 매퍼 쿼리 자체 문제일 가능성이 높으므로 기존 로직 유지
        if (affectedRows == 0) {
             throw new BusinessException("BOARD_UPDATE_FAILED_DB", "게시판 정보 업데이트에 실패했습니다. (DB 오류)");
        }

        return affectedRows;
    }

    @Override
    public void deleteBoard(Long boardId) {
        boardMapper.deleteBoard(boardId);
    }
} 