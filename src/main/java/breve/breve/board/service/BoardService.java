package breve.breve.board.service;

import breve.breve.board.model.Board;
import breve.breve.board.dto.BoardRequest;
import breve.breve.board.dto.BoardResponse;
import breve.breve.board.repository.BoardRepository;
import breve.breve.users.model.Users;
import breve.breve.users.repository.UserRepository;
import breve.breve.utility.CommonUtils;
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

    //== BoardResponse builder method ==//
    public BoardResponse dtoBuilder(Board board) {
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

    //== dto -> entity ==//
    public Board dtoToEntity(BoardRequest board) {
        return Board.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .users(board.getUsers())
                    .hashTag(board.getHashTag())
                    .saveFileName(board.getSaveFileName())
                    .view(board.getView())
                    .good(board.getGood())
                    .build();
    }

    //== entity ->  dto 편의메소드1 - 페이징 형식 ==//
    public Page<BoardResponse> entityToDtoPage(Page<Board> boardList) {
        return boardList.map(this::dtoBuilder);
    }

    //== entity -> dto 편의메소드2 - 엔티티 하나 ==//
    public BoardResponse entityToDtoDetail(Board board) {

        if (CommonUtils.isNull(board)) {
            return null;
        }
        return dtoBuilder(board);
    }

    public String fileSave(MultipartFile uploadFile) throws IOException {
        UUID uuid = UUID.randomUUID();
        String saveFileName = uuid + "_" + uploadFile.getOriginalFilename();
        uploadFile.transferTo(new File(saveFileName));

        return saveFileName;
    }

    public Page<BoardResponse> getBoardPaging(Pageable pageable) {
        return entityToDtoPage(
                boardRepository.findAllBoard(pageable)
        );
    }

    public Page<BoardResponse> getTodayBoard(Pageable pageable) {
        LocalDate now = LocalDate.now();

        return entityToDtoPage(
                boardRepository.findBoardByCreatedDate(
                        now,
                        pageable
                )
        );
    }

    public Page<BoardResponse> getSearchBoard(String keyword, Pageable pageable) {
        return entityToDtoPage(
                boardRepository.searchByTitle(
                        keyword,
                        pageable
                )
        );
    }

    public Page<BoardResponse> getBoardByHashTag(String hashTag, Pageable pageable) {
        return entityToDtoPage(
                boardRepository.findBoardByHashTag(
                        hashTag,
                        pageable
                )
        );
    }

    public Page<BoardResponse> getBoardByUser(String writer, Pageable pageable) {
        return entityToDtoPage(
                boardRepository.findBoardByWriter(
                        writer,
                        pageable
                )
        );
    }

    public Page<BoardResponse> getBoardByNickname(String nickname, Pageable pageable) {
        return entityToDtoPage(
                boardRepository.findBoardByNickname(
                        nickname,
                        pageable
                )
        );
    }

    public Board getBoardEntity(Long id) {
        return boardRepository.findOneById(id);
    }

    @Transactional
    public Long saveBoardNoFile(BoardRequest boardRequest, String writer) {
        Users users = userRepository.findByEmail(writer);

        boardRequest.setUsers(users);

        return boardRepository.save(
                dtoToEntity(boardRequest)).getId();
    }

    @Transactional
    public Long saveBoardFile(
            BoardRequest boardRequest,
            MultipartFile uploadFile,
            String writer
    ) throws IOException {
        Users users = userRepository.findByEmail(writer);

        String saveFileName = fileSave(uploadFile);
        boardRequest.setUsers(users);
        boardRequest.setSaveFileName(saveFileName);

        return boardRepository.save(
                dtoToEntity(boardRequest)).getId();
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
    public void editBoardFile(
            Long id,
            MultipartFile uploadFile,
            BoardRequest boardRequest
    ) throws IOException {
        Board board = boardRepository.findOneById(id);
        String saveFileName = fileSave(uploadFile);

        boardRequest.setId(board.getId());
        boardRequest.setUsers(board.getUsers());
        boardRequest.setGood(board.getGood());
        boardRequest.setView(board.getView());
        boardRequest.setSaveFileName(saveFileName);
        boardRequest.setHashTag(board.getHashTag());

        boardRepository.save(
                dtoToEntity(boardRequest)
        );
    }

    @Transactional
    public void editBoardNoFile(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findOneById(id);

        boardRequest.setId(board.getId());
        boardRequest.setUsers(board.getUsers());
        boardRequest.setGood(board.getGood());
        boardRequest.setView(board.getView());
        boardRequest.setSaveFileName(board.getSaveFileName());  //파일이 원래 없던지, 파일을 수정 안헀던지
        boardRequest.setHashTag(board.getHashTag());

        boardRepository.save(
                dtoToEntity(boardRequest)
        );
    }

    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
