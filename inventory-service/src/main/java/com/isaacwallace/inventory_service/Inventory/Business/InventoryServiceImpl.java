package com.isaacwallace.inventory_service.Inventory.Business;

import com.isaacwallace.inventory_service.Inventory.DataAccess.Inventory;
import com.isaacwallace.inventory_service.Inventory.DataAccess.InventoryIdentifier;
import com.isaacwallace.inventory_service.Inventory.DataAccess.InventoryRepository;
import com.isaacwallace.inventory_service.Inventory.Mapper.InventoryRequestMapper;
import com.isaacwallace.inventory_service.Inventory.Mapper.InventoryResponseMapper;
import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryResponseModel;
import com.isaacwallace.inventory_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.inventory_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryResponseMapper inventoryResponseMapper;
    private final InventoryRequestMapper inventoryRequestMapper;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryResponseMapper inventoryResponseMapper, InventoryRequestMapper inventoryRequestMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryResponseMapper = inventoryResponseMapper;
        this.inventoryRequestMapper = inventoryRequestMapper;
    }

    private void validateInventoryInvariant(Inventory inventory) {
        if (inventory.getQuantity() < 0) {
            throw new InvalidInputException("Inventory quantity cannot be negative");
        }
    }

    public List<InventoryResponseModel> getInventory() {
        return this.inventoryResponseMapper.entityListToResponseModelList(this.inventoryRepository.findAll());
    }

    public InventoryResponseModel getInventoryById(String inventoryid) {
        Inventory inventory = this.inventoryRepository.findInventoryByInventoryIdentifier_Inventoryid(inventoryid);

        if (inventory == null) {
            throw new NotFoundException("Unknown inventoryid: " + inventoryid);
        }

        return this.inventoryResponseMapper.entityToResponseModel(inventory);
    }

    public InventoryResponseModel addInventory(InventoryRequestModel inventoryRequestModel) {
        Inventory inventory = this.inventoryRequestMapper.requestModelToEntity(inventoryRequestModel, new InventoryIdentifier());

        this.validateInventoryInvariant(inventory);

        return this.inventoryResponseMapper.entityToResponseModel(this.inventoryRepository.save(inventory));
    }

    public InventoryResponseModel updateInventory(String inventoryid, InventoryRequestModel inventoryRequestModel) {
        Inventory inventory = this.inventoryRepository.findInventoryByInventoryIdentifier_Inventoryid(inventoryid);

        if (inventory == null) {
            throw new NotFoundException("Unknown inventoryid: " + inventoryid);
        }

        this.inventoryRequestMapper.updateEntityFromRequest(inventoryRequestModel, inventory);

        this.validateInventoryInvariant(inventory);

        log.info("Updated inventory with inventoryid: {}", inventoryid);

        return this.inventoryResponseMapper.entityToResponseModel(this.inventoryRepository.save(inventory));
    }

    public void deleteInventory(String inventoryid) {
        Inventory inventory = this.inventoryRepository.findInventoryByInventoryIdentifier_Inventoryid(inventoryid);

        if (inventory == null) {
            throw new NotFoundException("Unknown inventoryid: " + inventoryid);
        }

        this.inventoryRepository.delete(inventory);
    }

}
