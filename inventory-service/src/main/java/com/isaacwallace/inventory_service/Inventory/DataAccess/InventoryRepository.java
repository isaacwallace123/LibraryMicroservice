package com.isaacwallace.inventory_service.Inventory.DataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory,Integer> {
    Inventory findInventoryByInventoryIdentifier_Inventoryid(String inventoryid);
}
