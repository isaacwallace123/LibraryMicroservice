package com.isaacwallace.inventory_service.Inventory.Mapper;

import com.isaacwallace.inventory_service.Inventory.DataAccess.Inventory;
import com.isaacwallace.inventory_service.Inventory.DataAccess.InventoryStatus;
import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryResponseModel;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryResponseMapper {
    @Mapping(expression = "java(inventory.getInventoryIdentifier().getInventoryid())", target = "inventoryid")

    InventoryResponseModel entityToResponseModel(Inventory inventory);
    List<InventoryResponseModel> entityListToResponseModelList(List<Inventory> inventoryList);

    @AfterMapping
    default void mapResponseFields(@MappingTarget InventoryResponseModel inventoryResponseModel, Inventory inventory) {
        inventoryResponseModel.setAvailability(inventoryResponseModel.getQuantity() > 0 ? InventoryStatus.AVAILABLE : InventoryStatus.UNAVAILABLE);
    }
}
