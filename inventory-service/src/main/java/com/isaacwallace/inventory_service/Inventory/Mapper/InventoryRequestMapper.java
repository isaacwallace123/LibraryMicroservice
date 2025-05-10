package com.isaacwallace.inventory_service.Inventory.Mapper;

import com.isaacwallace.inventory_service.Inventory.DataAccess.Inventory;
import com.isaacwallace.inventory_service.Inventory.DataAccess.InventoryIdentifier;
import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryRequestMapper {
    @Mapping(target = "id", ignore = true)
    Inventory requestModelToEntity(InventoryRequestModel inventoryRequestModel, InventoryIdentifier inventoryIdentifier);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(InventoryRequestModel inventoryRequestModel, @MappingTarget Inventory inventory);
}
