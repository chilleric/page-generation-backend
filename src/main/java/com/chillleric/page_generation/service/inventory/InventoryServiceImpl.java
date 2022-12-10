package com.chillleric.page_generation.service.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.inventory.InventoryRequest;
import com.chillleric.page_generation.dto.inventory.InventoryResponse;
import com.chillleric.page_generation.exception.InvalidRequestException;
import com.chillleric.page_generation.exception.ResourceNotFoundException;
import com.chillleric.page_generation.repository.inventory.Inventory;
import com.chillleric.page_generation.repository.inventory.InventoryRepository;
import com.chillleric.page_generation.service.AbstractService;

@Service
public class InventoryServiceImpl extends AbstractService<InventoryRepository> implements InventoryService {

    @Override
    public void createInventory(InventoryRequest inventoryRequest) {
        validate(inventoryRequest);

        Map<String, String> error = generateError(InventoryRequest.class);
        repository.getInventories(Map.ofEntries(Map.entry("name", inventoryRequest.getName())), "", 0, 0,
                "").orElseThrow(() -> {
                    throw new InvalidRequestException(error, LanguageMessageKey.INVENTORY_NAME_EXISTED);
                });

        // Inventory inventory = new Inventory();
        Inventory inventory = objectMapper.convertValue(inventoryRequest, Inventory.class);
        ObjectId inventoryId = new ObjectId();
        inventory.set_id(inventoryId);

        repository.create(inventory);
    }

    @Override
    public Optional<InventoryResponse> findOneById(String inventoryId) {
        Optional<List<Inventory>> inventories = repository.getInventories(Map.ofEntries(Map.entry("_id", inventoryId)),
                "", 0, 0, "");
        inventories.orElseThrow(() -> {
            throw new ResourceNotFoundException(LanguageMessageKey.INVENTORY_NOT_FOUND);
        });

        Inventory inventory = inventories.get().get(0);
        return Optional.of(new InventoryResponse().generateInventoryResponse(inventory));
    }

    @Override
    public Optional<ListWrapperResponse<InventoryResponse>> findAll(Map<String, String> allParams, String keySort,
            int page, int pageSize,
            String sortField) {
        List<Inventory> inventories = repository.getInventories(allParams,
                "", page, pageSize, sortField).get();
        if (inventories.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(new ListWrapperResponse<InventoryResponse>(
                inventories.stream().map(inventory -> new InventoryResponse().generateInventoryResponse(inventory))
                        .collect(Collectors.toList()),
                page,
                pageSize,
                repository.getTotalPage(allParams)));
    }

    @Override
    public void deleteById(String inventoryId) {
        Optional<List<Inventory>> inventories = repository.getInventories(Map.ofEntries(Map.entry("_id", inventoryId)),
                "", 0, 0, "");
        inventories.orElseThrow(() -> {
            throw new ResourceNotFoundException(LanguageMessageKey.INVENTORY_NOT_FOUND);
        });
        // #### TODO: validate if any Template use

        repository.delete(Map.ofEntries(Map.entry("_id", inventoryId)));
    }

}
