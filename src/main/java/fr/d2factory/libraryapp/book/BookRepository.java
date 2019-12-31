package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {

    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public void addBooks(List<Book> books) {
        books.forEach(book -> availableBooks.put(book.getIsbn(), book));
    }

    public Optional<Book> findBook(long isbnCode) {
        return Optional.ofNullable(availableBooks.get(new ISBN(isbnCode)));
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt) {
        borrowedBooks.put(availableBooks.remove(book.getIsbn()), borrowedAt);
    }

    public LocalDate findBorrowedBookDate(Book book) {
        return borrowedBooks.get(book);
    }
}
