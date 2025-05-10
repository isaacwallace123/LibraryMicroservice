package com.isaacwallace.inventory_service.Inventory.DataAccess;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private InventoryIdentifier inventoryIdentifier;

    private String bookid;
    private Integer quantity;
}
