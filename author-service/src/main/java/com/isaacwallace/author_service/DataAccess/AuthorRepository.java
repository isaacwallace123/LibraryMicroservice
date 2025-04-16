package com.isaacwallace.author_service.DataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Author findAuthorByAuthorIdentifier_Authorid(String authorid);
}
