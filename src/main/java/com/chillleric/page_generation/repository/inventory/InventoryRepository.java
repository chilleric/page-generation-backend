package com.chillleric.page_generation.repository.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InventoryRepository {
    void create(Inventory inventory);

    Optional<List<Inventory>> getInventories(Map<String, String> allParams, String keySort,
            int page, int pageSize, String sortField);

    long getTotalPage(Map<String, String> allParams);

    void delete(Map<String, String> allParams);
}
