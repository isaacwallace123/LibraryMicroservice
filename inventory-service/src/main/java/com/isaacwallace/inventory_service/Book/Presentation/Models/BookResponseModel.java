package com.isaacwallace.inventory_service.Book.Presentation.Models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookResponseModel {
    private String bookid;
    private String authorid;

    private String title;
    private String genre;
    private String publisher;

    private LocalDateTime released;
}
