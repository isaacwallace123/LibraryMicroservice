package com.isaacwallace.author_service.DataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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
}