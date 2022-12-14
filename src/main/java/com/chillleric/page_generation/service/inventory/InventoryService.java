package com.chillleric.page_generation.service.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.inventory.InventoryDataResponse;
import com.chillleric.page_generation.dto.inventory.InventoryRequest;
import com.chillleric.page_generation.dto.inventory.InventoryResponse;
import com.chillleric.page_generation.repository.inventory.InventoryData;

public interface InventoryService {
    void createInventory(InventoryRequest inventoryRequest);

    Optional<InventoryResponse> findOneById(String inventoryId);

    Optional<ListWrapperResponse<InventoryResponse>> findAll(Map<String, String> allParams, String keySort, int page,
            int pageSize, String sortField);

    // List<InventoryRequest> preprocessInventoryResquest(List<InventoryRequest>
    // inventoryRequest);

    void deleteById(String inventoryId);

    InventoryDataResponse preprocessInventoryDataResponse(InventoryData inventoryData);

    List<InventoryDataResponse> mappingListData(InventoryData inventoryData);

    InventoryDataResponse mappingDataForImageOrTitle(InventoryData inventoryData);
}
