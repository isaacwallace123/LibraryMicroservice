package com.isaacwallace.api_gateway.Inventory.Presentation.Models;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
public class InventoryResponseModel extends RepresentationModel<InventoryResponseModel> {
    private String inventoryid;
    private String authorid;

    private String title;
    private String genre;
    private String publisher;

    private LocalDateTime released;
}
