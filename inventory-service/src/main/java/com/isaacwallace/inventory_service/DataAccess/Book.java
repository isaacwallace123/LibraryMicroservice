package com.isaacwallace.inventory_service.DataAccess;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private BookIdentifier bookIdentifier;

    private String authorid;

    private String title;
    private String genre;
    private String publisher;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime released;

    private Integer stock;

    public Book(String authorid, String title, String genre, String publisher, LocalDateTime now, int stock) {
        this.authorid = authorid;
        this.title = title;
        this.genre = genre;
        this.publisher = publisher;
        this.released = now;
        this.stock = stock;
    }
}
