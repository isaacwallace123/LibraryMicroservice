package com.isaacwallace.inventory_service.Inventory.Presentation.Models;

import com.isaacwallace.inventory_service.Inventory.DataAccess.InventoryStatus;
import lombok.Data;

@Data
public class InventoryResponseModel {
    private String inventoryid;

    private String bookid;
    private Integer quantity;

    private InventoryStatus availability;
}