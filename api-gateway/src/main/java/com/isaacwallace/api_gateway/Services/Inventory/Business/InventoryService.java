package com.isaacwallace.api_gateway.Services.Inventory.Business;

import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryResponseModel;

import java.util.List;

public interface InventoryService {
    public List<InventoryResponseModel> getAllInventory();
    public InventoryResponseModel getInventoryById(String inventoryid);
    public InventoryResponseModel addInventory(InventoryRequestModel inventoryRequestModel);
    public InventoryResponseModel updateInventory(String inventoryid, InventoryRequestModel inventoryRequestModel);
    public void deleteInventory(String inventoryid);
}
