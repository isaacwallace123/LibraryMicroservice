package com.isaacwallace.inventory_service.Presentation.Models;

import com.isaacwallace.inventory_service.DataAccess.Availability;
import com.isaacwallace.inventory_service.DomainClient.Author.Models.AuthorResponseModel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookResponseModel {
    private String bookid;
    private String authorid;

    private String title;
    private String genre;
    private String publisher;

    private LocalDateTime released;

    private Integer stock;

    private Availability availability;

    private AuthorResponseModel author;
}
