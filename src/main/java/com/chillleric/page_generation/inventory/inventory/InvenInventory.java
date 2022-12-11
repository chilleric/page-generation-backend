package com.chillleric.page_generation.inventory.inventory;

import java.util.List;
import java.util.Optional;

import com.chillleric.page_generation.repository.inventory.Inventory;

public interface InvenInventory {
    Optional<Inventory> findInventoryById(String inventoryId);

    Optional<List<Inventory>> findInventoriesByName(String inventoryName);
}
