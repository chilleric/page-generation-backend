package com.chillleric.page_generation.repository.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.chillleric.page_generation.repository.AbstractMongoRepo;

@Repository
public class InventoryRepositoryImpl extends AbstractMongoRepo implements InventoryRepository {

    @Override
    public void create(Inventory inventory) {
        authenticationTemplate.save(inventory);
    }

    @Override
    public Optional<List<Inventory>> getInventories(Map<String, String> allParams, String keySort, int page,
            int pageSize, String sortField) {
        Query query = generateQueryMongoDB(allParams, Inventory.class, keySort, sortField, page, pageSize);

        Optional<List<Inventory>> inventories = replaceFind(query, Inventory.class);
        return inventories;
    }

    @Override
    public void delete(Map<String, String> allParams) {
        Query query = generateQueryMongoDB(allParams, Inventory.class, "", "", 1, 1);
        // Optional<Inventory> inventory = replaceFindOne(query, Inventory.class);
        authenticationTemplate.remove(query, Inventory.class);

    }

    @Override
    public long getTotalPage(Map<String, String> allParams) {
        Query query = generateQueryMongoDB(allParams, Inventory.class, "", "", 0, 0);
        long total = authenticationTemplate.count(query, Inventory.class);
        return total;
    }

}
