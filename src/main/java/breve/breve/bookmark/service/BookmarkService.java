package breve.breve.bookmark.service;

import breve.breve.board.model.Board;
import breve.breve.board.repository.BoardRepository;
import breve.breve.bookmark.model.Bookmark;
import breve.breve.bookmark.repository.BookmarkRepository;
import breve.breve.users.model.Users;
import breve.breve.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    //== entity -> map id & title ==//
    public Map<String, Object> entityToMap(List<Bookmark> bookmarkList) {
        Map<String, Object> map = new HashMap<>();
        List<Long> boardId = new ArrayList<>();
        List<String> boardTitle = new ArrayList<>();

        for (Bookmark bookmark : bookmarkList) {
            boardId.add(bookmark.getBoard().getId());
            boardTitle.add(bookmark.getBoard().getTitle());
        }

        map.put("boardId", boardId);
        map.put("boardTitle", boardTitle);

        return map;
    }

    public Map<String, Object> getBookmarkList(String email) {
        return entityToMap(bookmarkRepository.findByUserEmail(email));
    }

    @Transactional
    public void saveBookmark(String email, Long boardId) {
        Users users = userRepository.findByEmail(email);
        Board board = boardRepository.findOneById(boardId);

        Bookmark bookmark = Bookmark.builder()
                .users(users)
                .board(board)
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void bookmarkCancel(String email, Long boardId) {
        Users users = userRepository.findByEmail(email);
        Board board = boardRepository.findOneById(boardId);

        Bookmark bookmark = bookmarkRepository.findUsersAndBoard(users, board);
        bookmarkRepository.deleteById(bookmark.getId());
    }
}
