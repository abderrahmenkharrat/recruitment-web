package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

public class LibraryImpl implements Library {

    private final BookRepository bookRepository;

    // dependency injection
    public LibraryImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt)
            throws HasLateBooksException, CannotFindBookException {

        if (this.isMemberLate(member))
            throw new HasLateBooksException();

        Optional<Book> bookToBorrow = bookRepository.findBook(isbnCode);

        if (bookToBorrow.isPresent()) {
            bookRepository.saveBookBorrow(bookToBorrow.get(), borrowedAt);
            member.getBorrowedBooks().add(bookToBorrow.get());
        } else
            throw new CannotFindBookException();

        return bookToBorrow.get();
    }

    @Override
    public void returnBook(Book book, Member member) {
        Long numberOfDays = DAYS.between(bookRepository.findBorrowedBookDate(book), LocalDate.now());
        member.payBook(numberOfDays.intValue());
    }

    @Override
    public boolean isMemberLate(Member member) {
        return member.getBorrowedBooks().stream()
                .anyMatch(book -> {
                    LocalDate borrowedBookDate = bookRepository.findBorrowedBookDate(book);
                    LocalDate now = LocalDate.now();
                    if (member instanceof Resident) {
                        return DAYS.between(borrowedBookDate, now) > 60;
                    } else
                        return DAYS.between(borrowedBookDate, now) > 30;
                });
    }
}
