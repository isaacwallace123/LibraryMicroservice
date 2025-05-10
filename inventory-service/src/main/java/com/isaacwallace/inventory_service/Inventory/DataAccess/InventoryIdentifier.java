package com.isaacwallace.inventory_service.Inventory.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class InventoryIdentifier {
    @Column(name = "inventoryid")
    private String inventoryid;

    public InventoryIdentifier() {
        this.inventoryid = UUID.randomUUID().toString();
    }

    public InventoryIdentifier(String inventoryid) {
        this.inventoryid = inventoryid;
    }
}
