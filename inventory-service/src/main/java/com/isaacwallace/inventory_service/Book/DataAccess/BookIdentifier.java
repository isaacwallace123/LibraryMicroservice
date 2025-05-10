package com.isaacwallace.inventory_service.Book.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class BookIdentifier {
    @Column(name = "bookid")
    private String bookid;

    public BookIdentifier() {
        this.bookid = UUID.randomUUID().toString();
    }

    public BookIdentifier(String bookid) {
        this.bookid = bookid;
    }
}