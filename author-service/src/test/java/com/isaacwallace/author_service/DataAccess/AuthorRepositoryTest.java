package com.isaacwallace.author_service.DataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setup() {
        authorRepository.deleteAll();
    }

    @Test
    void testAuthorConstructorAndGetters() {
        Author author = new Author("Isaac", "Wallace", "Test");

        assertEquals("Isaac", author.getFirstName());
        assertEquals("Wallace", author.getLastName());
        assertEquals("Test", author.getPseudonym());
    }

    @Test
    void testToStringContainsAllFields() {
        Author author = new Author("Isaac", "Wallace", "Goose");
        author.setId(1);
        author.setAuthorIdentifier(new AuthorIdentifier());

        String str = author.toString();

        assertTrue(str.contains("Isaac"));
        assertTrue(str.contains("Wallace"));
        assertTrue(str.contains("Goose"));
        assertTrue(str.contains("1"));
    }

    @Test
    void testEqualsSameObject() {
        Author author = new Author("Isaac", "Wallace", "IW");
        author.setAuthorIdentifier(new AuthorIdentifier("abc-123"));
        author.setId(1);

        assertEquals(author, author); // same reference
    }

    @Test
    void testNotEqualsDifferentFields() {
        Author a1 = new Author("Isaac", "Wallace", "IW");
        a1.setAuthorIdentifier(new AuthorIdentifier("abc-123"));
        a1.setId(1);

        Author a2 = new Author("John", "Doe", "JD");
        a2.setAuthorIdentifier(new AuthorIdentifier("xyz-456"));
        a2.setId(2);

        assertNotEquals(a1, a2);
        assertNotEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testEqualsNullAndDifferentClass() {
        Author author = new Author("Isaac", "Wallace", "IW");
        author.setAuthorIdentifier(new AuthorIdentifier("abc-123"));
        author.setId(1);

        assertNotEquals(null, author);           // null check
        assertNotEquals("some string", author);  // different class
    }

    @Test
    void testHashCodeConsistency() {
        Author author = new Author("Isaac", "Wallace", "IW");
        author.setAuthorIdentifier(new AuthorIdentifier("abc-123"));
        author.setId(1);

        int hash1 = author.hashCode();
        int hash2 = author.hashCode();

        assertEquals(hash1, hash2); // same hash on repeated calls
    }

    @Test
    public void whenAuthorsExists_thenReturnAllAuthors() {
        Author author1 = new Author("Isaac", "Wallace", "Test");
        Author author2 = new Author("John", "Doe", "Test");

        this.authorRepository.save(author1);
        this.authorRepository.save(author2);

        long afterSizeDB = this.authorRepository.count();

        List<Author> authors = this.authorRepository.findAll();

        assertNotNull(authors);
        assertNotEquals(0, authors.size());
        assertEquals(afterSizeDB, authors.size());
    }

    @Test
    public void whenAuthorExists_thenReturnAuthorByAuthorId() {
        Author author = new Author("Isaac", "Wallace", "Test");

        author.setAuthorIdentifier(new AuthorIdentifier());

        this.authorRepository.save(author);

        Author foundAuthor = this.authorRepository.findAuthorByAuthorIdentifier_Authorid(author.getAuthorIdentifier().getAuthorid());

        assertNotNull(foundAuthor);

        assertEquals(author.getAuthorIdentifier().getAuthorid(), foundAuthor.getAuthorIdentifier().getAuthorid());
        assertEquals(author.getFirstName(), foundAuthor.getFirstName());
        assertEquals(author.getLastName(), foundAuthor.getLastName());
        assertEquals(author.getPseudonym(), foundAuthor.getPseudonym());
    }

    @Test
    public void whenAuthorDoesNotExist_thenReturnNull() {
        Author foundAuthor = this.authorRepository.findAuthorByAuthorIdentifier_Authorid("this-id-is-certainly-fake");

        assertNull(foundAuthor);
    }

    @Test
    public void whenValidEntitySaved_thenPersistentAndReturn() {
        Author author = new Author("Isaac", "Wallace", "Test");
        author.setAuthorIdentifier(new AuthorIdentifier());

        Author savedAuthor = this.authorRepository.save(author);

        assertNotNull(savedAuthor);
        assertNotNull(savedAuthor.getAuthorIdentifier().getAuthorid());

        assertEquals(author.getFirstName(), savedAuthor.getFirstName());
        assertEquals(author.getLastName(), savedAuthor.getLastName());
        assertEquals(author.getPseudonym(), savedAuthor.getPseudonym());
        assertEquals(author.getAuthorIdentifier().getAuthorid(), savedAuthor.getAuthorIdentifier().getAuthorid());
    }

    @Test
    public void whenValidEntityDeleted_thenPersistentAndReturn() {
        Author author = new Author("Isaac", "Wallace", "Test");
        author.setAuthorIdentifier(new AuthorIdentifier());

        this.authorRepository.save(author);
        this.authorRepository.delete(author);

        assertNull(this.authorRepository.findAuthorByAuthorIdentifier_Authorid(author.getAuthorIdentifier().getAuthorid()));
    }

    @Test
    void testExistsByFirstNameAndLastNameIgnoreCase() {
        Author author = new Author("Isaac", "Wallace", "Test");
        author.setAuthorIdentifier(new AuthorIdentifier());

        this.authorRepository.save(author);

        assertTrue(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(author.getFirstName(), author.getLastName()));
        assertFalse(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("johnny", "doe"));
    }

    @Test
    void testExistsByPseudonymIgnoreCase() {
        Author author = new Author("Isaac", "Wallace", "I.W.");
        author.setAuthorIdentifier(new AuthorIdentifier());

        authorRepository.save(author);

        assertTrue(authorRepository.existsByPseudonymIgnoreCase(author.getPseudonym()));
        assertFalse(authorRepository.existsByPseudonymIgnoreCase("unknown"));
    }

    @Test
    void testUpdateAuthor() {
        Author author = new Author("Isaac", "Wallace", "Test");
        author.setAuthorIdentifier(new AuthorIdentifier());

        Author savedAuthor = authorRepository.save(author);
        savedAuthor.setPseudonym("NewPseudonym");

        Author updatedAuthor = authorRepository.save(savedAuthor);

        assertEquals("NewPseudonym", updatedAuthor.getPseudonym());

        Author fetched = authorRepository.findAuthorByAuthorIdentifier_Authorid(savedAuthor.getAuthorIdentifier().getAuthorid());
        assertEquals("NewPseudonym", fetched.getPseudonym());
    }

    @Test
    void testSavingDuplicateAuthors() {
        Author author1 = new Author("Isaac", "Wallace", "Test");
        Author author2 = new Author("Isaac", "Wallace", "Test");

        author1.setAuthorIdentifier(new AuthorIdentifier());
        author2.setAuthorIdentifier(new AuthorIdentifier());

        authorRepository.save(author1);
        authorRepository.save(author2);

        List<Author> authors = authorRepository.findAll();
        assertEquals(2, authors.size());
    }

    @Test
    void testFindByPseudonymIgnoreCase() {
        Author author = new Author("Isaac", "Wallace", "IW");
        author.setAuthorIdentifier(new AuthorIdentifier());

        authorRepository.save(author);

        boolean exists = authorRepository.existsByPseudonymIgnoreCase("iw");
        assertTrue(exists);
    }

    @Test
    void testDeleteAllAuthors() {
        Author author1 = new Author("Isaac", "Wallace", "Test1");
        Author author2 = new Author("John", "Doe", "Test2");

        authorRepository.save(author1);
        authorRepository.save(author2);

        authorRepository.deleteAll();
        List<Author> authors = authorRepository.findAll();

        assertTrue(authors.isEmpty());
    }

    @Test
    void testRepositoryIsEmptyInitially() {
        List<Author> authors = authorRepository.findAll();
        assertTrue(authors.isEmpty());
    }

    @Test
    void testDefaultConstructorGeneratesValidUUID() {
        AuthorIdentifier identifier = new AuthorIdentifier();
        assertNotNull(identifier.getAuthorid());
        assertDoesNotThrow(() -> UUID.fromString(identifier.getAuthorid()));
    }

    @Test
    void testCustomConstructorSetsIdCorrectly() {
        String customId = "test-id-123";
        AuthorIdentifier identifier = new AuthorIdentifier(customId);

        assertEquals(customId, identifier.getAuthorid());
    }

    @Test
    void testTwoDefaultConstructorsGenerateDifferentIds() {
        AuthorIdentifier id1 = new AuthorIdentifier();
        AuthorIdentifier id2 = new AuthorIdentifier();

        assertNotEquals(id1.getAuthorid(), id2.getAuthorid());
    }

    @Test
    void testAuthorIdIsNotNullOrEmpty() {
        AuthorIdentifier identifier = new AuthorIdentifier();
        assertNotNull(identifier.getAuthorid());
        assertFalse(identifier.getAuthorid().isEmpty());
    }

    @Test
    void testCustomIdCanBeUuidString() {
        String uuidString = UUID.randomUUID().toString();
        AuthorIdentifier identifier = new AuthorIdentifier(uuidString);

        assertDoesNotThrow(() -> UUID.fromString(identifier.getAuthorid()));
    }
}