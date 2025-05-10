package com.isaacwallace.inventory_service.Inventory.Presentation.Models;

import lombok.*;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryRequestModel {
    String bookid;
    Integer quantity;
}
