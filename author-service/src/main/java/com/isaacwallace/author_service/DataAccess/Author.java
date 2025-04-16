package com.isaacwallace.author_service.DataAccess;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    public Author(@NotNull String firstName, @NotNull String lastName, String pseudonym) {
    }
}
