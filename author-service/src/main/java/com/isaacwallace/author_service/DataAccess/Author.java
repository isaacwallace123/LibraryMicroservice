package com.isaacwallace.author_service.DataAccess;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "authors")
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private AuthorIdentifier authorIdentifier;

    private String firstName;
    private String lastName;

    private String pseudonym;
}
