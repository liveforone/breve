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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

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
