package fr.d2factory.libraryapp.library;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Do not forget to consult the README.md :)
 */
public class LibraryTest {
    private Library library;
    private BookRepository bookRepository;
    private static List<Book> books;

    @BeforeEach
    void setup() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        File booksJson = new File("src/test/resources/books.json");
        books = mapper.readValue(booksJson, new TypeReference<List<Book>>() {
        });
        bookRepository = new BookRepository();
        bookRepository.addBooks(books);
        library = new LibraryImpl(bookRepository);
    }

    @Test
    void member_can_borrow_a_book_if_book_is_available() {
        // Given
        Member member = new Student();
        member.setEntryDate(LocalDate.parse("2019-05-01"));
        // When
        library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.now());
        // Then
        assertThat(bookRepository.findBorrowedBookDate(books.get(0))).isNotNull();
    }

    @Test
    void borrowed_book_is_no_longer_available() {
        // Given
        Member member = new Student();
        member.setEntryDate(LocalDate.parse("2019-05-01"));
        // When
        Assertions.assertThrows(CannotFindBookException.class, () -> {
            // first borrow
            library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.now());
            // second borrow
            library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.now());
        });
    }

    @Test
    void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
        // Given
        Member member = new Resident();
        member.setEntryDate(LocalDate.parse("2019-05-01"));
        member.setWallet(100);
        library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.now().minusDays(30));
        // When
        library.returnBook(books.get(0), member);
        // Then
        assertThat(member.getWallet()).isEqualTo(97);
    }

    @Test
    void students_pay_10_cents_the_first_30days() {
        // Given
        Member member = new Student();
        member.setEntryDate(LocalDate.parse("2018-05-01"));
        member.setWallet(100);
        library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.now().minusDays(50));
        // When
        library.returnBook(books.get(0), member);
        // Then
        assertThat(member.getWallet()).isEqualTo(95);
    }

    @Test
    void students_in_1st_year_are_not_taxed_for_the_first_15days() {
        // Given
        Member member = new Student();
        member.setEntryDate(LocalDate.parse("2019-05-01"));
        member.setWallet(100);
        library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.now().minusDays(45));
        // When
        library.returnBook(books.get(0), member);
        // Then
        assertThat(member.getWallet()).isEqualTo(97);
    }

    @Test
    void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {
        // Given
        Member member = new Resident();
        member.setEntryDate(LocalDate.parse("2019-05-01"));
        member.setWallet(100);
        library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.now().minusDays(90));
        // When
        library.returnBook(books.get(0), member);
        // Then
        assertThat(member.getWallet()).isEqualTo(88);
    }

    @Test
    void members_cannot_borrow_book_if_they_have_late_books() {
        // Given
        Member member = new Student();
        member.setEntryDate(LocalDate.parse("2019-05-01"));
        // When
        Assertions.assertThrows(HasLateBooksException.class, () -> {
            // first borrow
            library.borrowBook(books.get(0).getIsbn().getIsbnCode(), member, LocalDate.parse("2019-11-01"));
            // second borrow
            library.borrowBook(books.get(1).getIsbn().getIsbnCode(), member, LocalDate.now());
        });
    }
}
