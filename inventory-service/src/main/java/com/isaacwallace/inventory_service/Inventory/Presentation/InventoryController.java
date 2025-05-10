package com.isaacwallace.inventory_service.Inventory.Presentation;

import com.isaacwallace.inventory_service.Inventory.Business.InventoryService;
import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.inventory_service.Inventory.Presentation.Models.InventoryResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InventoryResponseModel>> getInventory() {
        return ResponseEntity.status(HttpStatus.OK).body(this.inventoryService.getInventory());
    }

    @GetMapping(value = "{inventoryid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponseModel> getInventoryById(@PathVariable String inventoryid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.inventoryService.getInventoryById(inventoryid));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponseModel> addInventory(@RequestBody InventoryRequestModel inventoryRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.inventoryService.addInventory(inventoryRequestModel));
    }

    @PutMapping(value = "{inventoryid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponseModel> updateInventory(@PathVariable String inventoryid, @RequestBody InventoryRequestModel inventoryRequestModel) {
        return ResponseEntity.status(HttpStatus.OK).body(this.inventoryService.updateInventory(inventoryid, inventoryRequestModel));
    }

    @DeleteMapping(value = "{inventoryid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponseModel> deleteInventory(@PathVariable String inventoryid) {
        this.inventoryService.deleteInventory(inventoryid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
