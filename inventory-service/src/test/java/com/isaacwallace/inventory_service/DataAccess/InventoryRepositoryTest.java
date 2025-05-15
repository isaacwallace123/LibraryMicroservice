package com.isaacwallace.inventory_service.DataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        bookRepository.deleteAll();
    }

    @Test
    void testInventoryConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);

        assertEquals(book.getAuthorid(), "authorid");
        assertEquals(book.getTitle(), "title");
        assertEquals(book.getGenre(), "genre");
        assertEquals(book.getPublisher(), "publisher");
        assertEquals(book.getReleased(), now);
        assertEquals(book.getStock(), 10);
    }

    @Test
    void testSaveInventory() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);

        bookRepository.save(book);

        assertEquals(1, bookRepository.count());
    }

    @Test
    void toStringContainsAllFields() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);

        bookRepository.save(book);

        String toString = book.toString();
        assertTrue(toString.contains(book.getAuthorid()));
        assertTrue(toString.contains(book.getTitle()));
        assertTrue(toString.contains(book.getGenre()));
        assertTrue(toString.contains(book.getPublisher()));
        assertTrue(toString.contains(book.getReleased().toString()));
        assertTrue(toString.contains(book.getStock().toString()));
    }

    @Test
    void testEqualsSameObject() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);

        assertEquals(book, book);
    }

    @Test
    void testEqualsDifferentClass() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);

        assertNotEquals(book, "not a book");
    }

    @Test
    void testEqualsNull() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);

        assertNotEquals(book, null);
    }

    @Test
    void testNotEqualsDifferentFields() {
        LocalDateTime now = LocalDateTime.now();

        Book book1 = new Book("authorid", "title", "genre", "publisher", now, 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher", now, 11);

        assertNotEquals(book1, book2);
        assertNotEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void testEqualsDifferentId() {
        LocalDateTime now = LocalDateTime.now();

        Book book1 = new Book("authorid", "title", "genre", "publisher", now, 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher", now, 10);

        book1.setBookIdentifier(new BookIdentifier());
        book2.setBookIdentifier(new BookIdentifier());

        assertNotEquals(book1, book2);
        assertNotEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void testHashCodeDifferentObjects() {
        LocalDateTime now = LocalDateTime.now();

        Book book1 = new Book("authorid", "title", "genre", "publisher", now, 10);
        Book book2 = new Book("authori", "title", "genre", "publisher", now, 10);

        assertNotEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);

        book.setBookIdentifier(new BookIdentifier("abc-123"));

        int hashCode = book.hashCode();

        assertEquals(hashCode, book.hashCode());

        book.setTitle("new title");

        assertNotEquals(hashCode, book.hashCode());
    }

    @Test
    void whenBookExists_thenReturnAllBooks() {
        bookRepository.save(new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10));
        bookRepository.save(new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10));
        bookRepository.save(new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10));

        List<Book> books = bookRepository.findAll();

        assertNotNull(books);
        assertNotEquals(0, books.size());
        assertEquals(3, books.size());
        assertEquals(books.size(), this.bookRepository.count());
    }

    @Test
    void testEquals_DifferentTitle() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title2", "genre", "publisher", LocalDateTime.now(), 10);
        assertNotEquals(book1, book2);
    }

    @Test
    void testEquals_DifferentAuthorId() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid2", "title", "genre", "publisher", LocalDateTime.now(), 10);
        assertNotEquals(book1, book2);
    }

    @Test
    void testEquals_DifferentGenre() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title", "genre2", "publisher", LocalDateTime.now(), 10);
        assertNotEquals(book1, book2);
    }

    @Test
    void testEquals_DifferentPublisher() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher2", LocalDateTime.now(), 10);
        assertNotEquals(book1, book2);
    }

    @Test
    void testEquals_DifferentReleased() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now().plusDays(1), 10);
        assertNotEquals(book1, book2);
    }

    @Test
    void testEquals_DifferentStock() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 11);
        assertNotEquals(book1, book2);
    }

    @Test
    void testEquals_DifferentInventoryIdentifier() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book1.setBookIdentifier(new BookIdentifier("abc-123"));
        book2.setBookIdentifier(new BookIdentifier("abc-456"));
        assertNotEquals(book1, book2);
    }

    @Test
    void testEquals_MixedNullFields() {
        Book book1 = new Book("authorid", "title", "genre", null, LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher", null, 1);
        assertNotEquals(book1, book2);
    }

    @Test
    void whenBookExists_thenReturnBookByBookId() {
        LocalDateTime now = LocalDateTime.now();

        Book book = new Book("authorid", "title", "genre", "publisher", now, 10);
        book.setBookIdentifier(new BookIdentifier("abc-123"));
        bookRepository.save(book);

        Book foundBook = bookRepository.findBookByBookIdentifier_Bookid("abc-123");

        assertNotNull(foundBook);

        assertEquals(book, foundBook);

        assertEquals("abc-123", foundBook.getBookIdentifier().getBookid());
        assertEquals("authorid", foundBook.getAuthorid());
        assertEquals("title", foundBook.getTitle());
        assertEquals("genre", foundBook.getGenre());
        assertEquals("publisher", foundBook.getPublisher());
        assertEquals(now, foundBook.getReleased());
        assertEquals(10, foundBook.getStock());
    }

    @Test
    void whenBookDoesNotExist_thenReturnNull() {
        Book foundBook = bookRepository.findBookByBookIdentifier_Bookid("abc-123");
        assertNull(foundBook);
    }

    @Test
    void whenValidEntitySaved_thenPersistentAndReturn() {
        Book book = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book.setBookIdentifier(new BookIdentifier());

        Book savedBook = bookRepository.save(book);

        assertNotNull(savedBook);
        assertNotNull(savedBook.getBookIdentifier().getBookid());

        assertEquals(book.getTitle(), savedBook.getTitle());
        assertEquals(book.getAuthorid(), savedBook.getAuthorid());
        assertEquals(book.getGenre(), savedBook.getGenre());
        assertEquals(book.getPublisher(), savedBook.getPublisher());
        assertEquals(book.getReleased(), savedBook.getReleased());
        assertEquals(book.getStock(), savedBook.getStock());
    }

    @Test
    void whenValidEntityDeleted_thenPersistentAndReturn() {
        Book book = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book.setBookIdentifier(new BookIdentifier());

        Book savedBook = bookRepository.save(book);

        bookRepository.delete(savedBook);

        assertNull(bookRepository.findBookByBookIdentifier_Bookid(savedBook.getBookIdentifier().getBookid()));
    }

    @Test
    void testExistsByPublisherAndTitleAndGenreIgnoreCase() {
        Book book = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book.setBookIdentifier(new BookIdentifier("abc-123"));
        bookRepository.save(book);

        assertTrue(bookRepository.existsByTitleIgnoreCaseAndGenreIgnoreCaseAndPublisherIgnoreCase("title", "genre", "publisher"));
        assertFalse(bookRepository.existsByTitleIgnoreCaseAndGenreIgnoreCaseAndPublisherIgnoreCase("title", "genre", "publisher2"));
    }

    @Test
    void testSavingDuplicateBooks() {
        Book book = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book.setBookIdentifier(new BookIdentifier("abc-123"));

        Book book2 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book2.setBookIdentifier(new BookIdentifier("abc-123"));

        bookRepository.save(book);
        bookRepository.save(book2);

        List<Book> books = bookRepository.findAll();

        assertEquals(2, books.size());
        assertEquals(2, bookRepository.count());
    }

    @Test
    void testDeleteAllBooks() {
        Book book = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book.setBookIdentifier(new BookIdentifier("abc-123"));

        Book book2 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book2.setBookIdentifier(new BookIdentifier("abc-123"));

        bookRepository.save(book);
        bookRepository.save(book2);

        assertEquals(2, bookRepository.count());

        bookRepository.deleteAll();

        List<Book> books = bookRepository.findAll();

        assertEquals(0, books.size());
        assertEquals(0, bookRepository.count());
    }

    @Test
    void testDefaultConstructorSetsIdCorrectly() {
        String testId = "abc-123";
        BookIdentifier bookIdentifier = new BookIdentifier(testId);

        assertNotNull(bookIdentifier);
        assertEquals(testId, bookIdentifier.getBookid());
    }

    @Test
    void testTwoDefaultConstructorGenerateDifferentIds() {
        BookIdentifier bookIdentifier1 = new BookIdentifier();
        BookIdentifier bookIdentifier2 = new BookIdentifier();

        assertNotEquals(bookIdentifier1.getBookid(), bookIdentifier2.getBookid());
    }

    @Test
    void testCustomIdCanBeUuidString() {
        String uuid = UUID.randomUUID().toString();
        BookIdentifier bookIdentifier = new BookIdentifier(uuid);

        assertDoesNotThrow(() -> UUID.fromString(bookIdentifier.getBookid()));
        assertEquals(uuid, bookIdentifier.getBookid());
    }

    @Test
    void testEqualsIdenticalValuesDifferentInstances() {
        BookIdentifier bookId = new BookIdentifier("abc-123");

        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book1.setBookIdentifier(bookId);

        Book book2 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book2.setBookIdentifier(bookId);

        assertEquals(book1, book2);
    }

    @Test
    void testEqualsDifferentBookIdentifier() {
        Book book1 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        Book book2 = new Book("authorid", "title", "genre", "publisher", LocalDateTime.now(), 10);
        book1.setBookIdentifier(new BookIdentifier("abc-123"));
        book2.setBookIdentifier(new BookIdentifier("abc-456"));
        assertNotEquals(book1, book2);
    }

    @Test
    void testEqualsWithNullFields() {
        Book book1 = new Book();
        Book book2 = new Book();

        assertEquals(book1, book2);
    }

    @Test
    void testCanEqual() {
        Book book = new Book();

        assertTrue(book.canEqual(new Book()));
        assertFalse(book.canEqual(new Object()));
    }
}