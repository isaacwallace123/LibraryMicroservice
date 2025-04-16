package com.isaacwallace.author_service.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class AuthorIdentifier {
    @Column(name = "authorid")
    private String authorid;

    public AuthorIdentifier() {
        this.authorid = UUID.randomUUID().toString();
    }

    public AuthorIdentifier(String authorid) {
        this.authorid = authorid;
    }
}
