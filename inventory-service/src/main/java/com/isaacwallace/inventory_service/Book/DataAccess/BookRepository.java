package com.isaacwallace.inventory_service.Book.DataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Book findBookByBookIdentifier_Bookid(String bookid);
}
