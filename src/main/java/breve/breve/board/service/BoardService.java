package breve.breve.board.service;

import breve.breve.board.model.Board;
import breve.breve.board.model.BoardRequest;
import breve.breve.board.model.BoardResponse;
import breve.breve.board.repository.BoardRepository;
import breve.breve.users.model.Users;
import breve.breve.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

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

    public String fileSave(MultipartFile uploadFile) throws IOException {
        UUID uuid = UUID.randomUUID();
        String saveFileName = uuid + "_" + uploadFile.getOriginalFilename();
        uploadFile.transferTo(new File(saveFileName));

        return saveFileName;
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

    public Board getBoardEntity(Long id) {
        return boardRepository.findOneById(id);
    }

    public BoardResponse getBoardDetail(Long id) {
        return entityToDtoDetail(boardRepository.findOneById(id));
    }

    @Transactional
    public Long saveBoardNoFile(BoardRequest boardRequest, String writer) {
        Users users = userRepository.findByEmail(writer);

        boardRequest.setUsers(users);

        return boardRepository.save(boardRequest.toEntity()).getId();
    }

    @Transactional
    public Long saveBoardFile(BoardRequest boardRequest, MultipartFile uploadFile, String writer) throws IOException {
        Users users = userRepository.findByEmail(writer);

        String saveFileName = fileSave(uploadFile);
        boardRequest.setUsers(users);
        boardRequest.setSaveFileName(saveFileName);

        return boardRepository.save(boardRequest.toEntity()).getId();
    }

    @Transactional
    public void updateView(Long id) {
        boardRepository.updateView(id);
    }

    @Transactional
    public void updateGood(Long id) {
        boardRepository.updateGood(id);
    }

    @Transactional
    public void editBoardFile(Long id, MultipartFile uploadFile, BoardRequest boardRequest) throws IOException {
        Board board = boardRepository.findOneById(id);
        String saveFileName = fileSave(uploadFile);

        boardRequest.setId(board.getId());
        boardRequest.setUsers(board.getUsers());
        boardRequest.setGood(board.getGood());
        boardRequest.setView(board.getView());
        boardRequest.setSaveFileName(saveFileName);

        boardRepository.save(boardRequest.toEntity());
    }

    @Transactional
    public void editBoardNoFile(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findOneById(id);

        boardRequest.setId(board.getId());
        boardRequest.setUsers(board.getUsers());
        boardRequest.setGood(board.getGood());
        boardRequest.setView(board.getView());
        boardRequest.setSaveFileName(board.getSaveFileName());  //파일이 원래 없던지, 파일을 수정 안헀던지

        boardRepository.save(boardRequest.toEntity());
    }
}
