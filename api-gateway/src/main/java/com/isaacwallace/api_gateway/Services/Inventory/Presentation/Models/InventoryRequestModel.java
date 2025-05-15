package com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryRequestModel extends RepresentationModel<InventoryRequestModel> {
    String authorid;

    String title;
    String genre;
    String publisher;

    LocalDateTime released;

    Integer stock;
}
