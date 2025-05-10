package com.isaacwallace.inventory_service.Inventory.Business;

import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryResponseModel;

import java.util.List;

public interface InventoryService {
    public List<InventoryResponseModel> getInventory();
    public InventoryResponseModel getInventoryById(String inventoryid);
    public InventoryResponseModel addInventory(InventoryRequestModel inventoryRequestModel);
    public InventoryResponseModel updateInventory(String inventoryid, InventoryRequestModel inventoryRequestModel);
    public void deleteInventory(String inventoryid);
}
