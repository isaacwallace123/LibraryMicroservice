package com.isaacwallace.api_gateway.Services.Inventory.Business;

import com.isaacwallace.api_gateway.DomainClient.InventoryServiceClient;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.InventoryController;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InventoryServiceClient inventoryServiceClient;

    public InventoryServiceImpl(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public List<InventoryResponseModel> getAllInventory() {
        return this.inventoryServiceClient.getInventorys().stream().map(this::addLinks).toList();
    }

    public InventoryResponseModel getInventoryById(String inventoryid) {
        return this.addLinks(this.inventoryServiceClient.getInventoryByInventoryId(inventoryid));
    }

    public InventoryResponseModel addInventory(InventoryRequestModel inventoryRequestModel) {
        return this.addLinks(this.inventoryServiceClient.addInventory(inventoryRequestModel));
    }

    public InventoryResponseModel updateInventory(String inventoryid, InventoryRequestModel inventoryRequestModel) {
        return this.addLinks(this.inventoryServiceClient.updateInventory(inventoryid, inventoryRequestModel));
    }

    public void deleteInventory(String inventoryid) {
        this.inventoryServiceClient.deleteInventory(inventoryid);
    }

    private InventoryResponseModel addLinks(InventoryResponseModel inventoryResponseModel) {
        Link selfLink = linkTo(methodOn(InventoryController.class)
                .getInventoryById(inventoryResponseModel.getBookid()))
                .withSelfRel();
        inventoryResponseModel.add(selfLink);

        Link allLink = linkTo(methodOn(InventoryController.class)
                .getInventory())
                .withRel("inventories");
        inventoryResponseModel.add(allLink);

        return inventoryResponseModel;
    }
}
