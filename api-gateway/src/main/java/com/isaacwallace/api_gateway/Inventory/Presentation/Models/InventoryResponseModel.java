package com.isaacwallace.api_gateway.Inventory.Presentation.Models;

import com.isaacwallace.api_gateway.Author.Presentation.Models.AuthorResponseModel;
import com.isaacwallace.api_gateway.Inventory.DataAccess.Availability;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
public class InventoryResponseModel extends RepresentationModel<InventoryResponseModel> {
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
