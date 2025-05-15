package com.isaacwallace.inventory_service.DataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Book findBookByBookIdentifier_Bookid(String bookid);

    boolean existsByTitleIgnoreCaseAndGenreIgnoreCaseAndPublisherIgnoreCase(String title, String genre, String publisher);

    List<Book> findBooksByAuthorid(String authorid);
}
