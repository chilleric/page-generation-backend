package com.chillleric.page_generation.inventory.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.chillleric.page_generation.inventory.AbstractInventory;
import com.chillleric.page_generation.repository.inventory.Inventory;
import com.chillleric.page_generation.repository.inventory.InventoryRepository;

@Service
public class InvenInventoryImpl extends AbstractInventory<InventoryRepository>
        implements InvenInventory {

    @Override
    public Optional<Inventory> findInventoryById(String inventoryId) {
        List<Inventory> inventories = repository
                .getInventories(Map.ofEntries(Map.entry("_id", inventoryId)), "", 0, 0, "").get();
        if (inventories.size() != 0) {
            return Optional.of(inventories.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Inventory>> findInventoriesByName(String inventoryName) {
        List<Inventory> inventories = repository
                .getInventories(Map.ofEntries(Map.entry("name", inventoryName)), "", 0, 0, "")
                .get();
        if (inventories.size() != 0) {
            return Optional.of(inventories);
        }
        return Optional.empty();
    }

}
