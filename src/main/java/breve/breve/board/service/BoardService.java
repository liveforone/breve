package breve.breve.board.service;

import breve.breve.board.model.Board;
import breve.breve.board.model.BoardResponse;
import breve.breve.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    //== entity ->  dto 편의메소드1 - 페이징 형식 ==//
    public Page<BoardResponse> entityToDtoPage(Page<Board> boardList) {
        return boardList.map(m -> BoardResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .content(m.getContent())
                .hashTag(m.getHashTag())
                .saveFileName(m.getSaveFileName())
                .view(m.getView())
                .good(m.getGood())
                .createdDate(m.getCreatedDate())
                .build()
        );
    }

    //== entity -> dto 편의메소드2 - 엔티티 하나 ==//
    public BoardResponse entityToDtoDetail(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hashTag(board.getHashTag())
                .saveFileName(board.getSaveFileName())
                .view(board.getView())
                .good(board.getGood())
                .createdDate(board.getCreatedDate())
                .build();
    }

    public Page<BoardResponse> getBoardPaging(Pageable pageable) {
        return entityToDtoPage(boardRepository.findAllBoard(pageable));
    }

    public Page<BoardResponse> getTodayBoard(Pageable pageable) {
        LocalDate now = LocalDate.now();

        return entityToDtoPage(boardRepository.findBoardByCreatedDate(now, pageable));
    }

    public Page<BoardResponse> getSearchBoard(String keyword, Pageable pageable) {
        return entityToDtoPage(boardRepository.findSearchByTitle(keyword, pageable));
    }

    public Page<BoardResponse> getBoardByHashTag(String hashTag, Pageable pageable) {
        return entityToDtoPage(boardRepository.findBoardByHashTag(hashTag, pageable));
    }
}
