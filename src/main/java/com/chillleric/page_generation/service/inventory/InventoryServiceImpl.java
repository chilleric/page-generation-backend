package com.chillleric.page_generation.service.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.inventory.InventoryDataRequest;
import com.chillleric.page_generation.dto.inventory.InventoryDataResponse;
import com.chillleric.page_generation.dto.inventory.InventoryRequest;
import com.chillleric.page_generation.dto.inventory.InventoryResponse;
import com.chillleric.page_generation.exception.InvalidRequestException;
import com.chillleric.page_generation.exception.ResourceNotFoundException;
import com.chillleric.page_generation.inventory.inventory.InvenInventory;
import com.chillleric.page_generation.repository.invenData.InvenData;
import com.chillleric.page_generation.repository.inventory.Inventory;
import com.chillleric.page_generation.repository.inventory.InventoryData;
import com.chillleric.page_generation.repository.inventory.InventoryRepository;
import com.chillleric.page_generation.service.AbstractService;

@Service
public class InventoryServiceImpl extends AbstractService<InventoryRepository> implements InventoryService {

    @Autowired
    private InvenInventory invenInventory;

    @Override
    public void createInventory(InventoryRequest inventoryRequest) {
        validate(inventoryRequest);

        Map<String, String> error = generateError(InventoryRequest.class);
        invenInventory.findInventoriesByName(inventoryRequest.getName()).ifPresent(name -> {
            throw new InvalidRequestException(error, LanguageMessageKey.INVENTORY_NAME_EXISTED);
        });

        InventoryData inventoryData = objectMapper.convertValue(inventoryRequest.getData(), InventoryData.class);
        Inventory inventory = new Inventory();

        ObjectId inventoryId = new ObjectId();
        inventory.set_id(inventoryId);

        inventory.setName(inventoryRequest.getName());
        inventory.setData(inventoryData);

        repository.create(inventory);
    }

    @Override
    public Optional<InventoryResponse> findOneById(String inventoryId) {
        Optional<Inventory> inventories = invenInventory.findInventoryById(inventoryId);
        inventories.orElseThrow(() -> {
            throw new ResourceNotFoundException(LanguageMessageKey.INVENTORY_NOT_FOUND);
        });

        Inventory inventory = inventories.get();
        // return null;
        return Optional.of(new InventoryResponse(inventory.get_id().toString(),
                inventory.getName(), preprocessInventoryDataResponse(inventory.getData())));
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
                inventories.stream()
                        .map(inventory -> new InventoryResponse(inventory.get_id().toString(), inventory.getName(),
                                preprocessInventoryDataResponse(inventory.getData())))
                        .collect(Collectors.toList()),
                page,
                pageSize,
                repository.getTotalPage(allParams)));
    }

    @Override
    public InventoryDataResponse preprocessInventoryDataResponse(InventoryData inventoryData) {
        InventoryDataResponse result = new InventoryDataResponse();
        switch (inventoryData.getType()) {
            case IMAGE: {
                // # TODO: mapping data into this
                result = mappingDataForImageOrTitle(inventoryData);
                break;
            }
            case TITLE: {
                // # TODO: mapping data into this
                result = mappingDataForImageOrTitle(inventoryData);
                break;
            }
            case SLIDER: {
                result = this.objectMapper.convertValue(inventoryData, InventoryDataResponse.class);
                result.setChildren(mappingListData(inventoryData.getTypeList().get(0)));
                break;
            }
            case LIST: {
                result = this.objectMapper.convertValue(inventoryData, InventoryDataResponse.class);
                result.setChildren(mappingListData(inventoryData.getTypeList().get(0)));
                break;
            }
            case COMPONENTS: {
                result = this.objectMapper.convertValue(inventoryData, InventoryDataResponse.class);
                result.setChildren(
                        inventoryData.getChildren().stream().map(element -> preprocessInventoryDataResponse(element))
                                .collect(Collectors.toList()));
                break;
            }
        }
        return result;
    }

    public List<InventoryDataResponse> mappingListData(InventoryData inventoryData) {
        // # This is the fake invenData array:
        List<InvenData> invenDatas = new ArrayList<InvenData>();

        InvenData invenData1 = new InvenData(new ObjectId("6398a77c2677e9166be8064c"), "This is test invenData",
                "link for invenData");
        InvenData invenData2 = new InvenData(new ObjectId("6398a97237df9f3ae32da8d4"), "This is test invenData2",
                "link for invenDat2a");
        invenDatas.add(invenData1);
        invenDatas.add(invenData2);
        if (invenDatas.size() == 0) {
            return new ArrayList<InventoryDataResponse>();
        }

        List<InventoryDataRequest> typeList = new ArrayList<InventoryDataRequest>();
        InventoryDataRequest inventoryDataRequest = objectMapper.convertValue(inventoryData.getTypeList().get(0),
                InventoryDataRequest.class);
        typeList.add(inventoryDataRequest);

        List<InventoryDataResponse> children = inventoryData.getChildren().stream()
                .map(element -> preprocessInventoryDataResponse(element))
                .collect(Collectors.toList());

        return invenDatas.stream()
                .map(invenData -> new InventoryDataResponse(
                        inventoryData.getTargetId(),
                        inventoryData.getType(),
                        inventoryData.getCss(),
                        inventoryData.getSpecial(),
                        children,
                        typeList,
                        // new ArrayList<InventoryDataRequest>().add(inventoryDataRequest),
                        inventoryData.getNumber(),
                        invenData.getTitle(),
                        invenData.getLink()))
                .collect(Collectors.toList());

    }

    public InventoryDataResponse mappingDataForImageOrTitle(InventoryData inventoryData) {
        // # Get data with targetId
        InvenData invenData1 = new InvenData(new ObjectId("6398a77c2677e9166be8064c"), "This is test invenData",
                "link for invenData");

        return new InventoryDataResponse(
                inventoryData.getTargetId().toString(),
                inventoryData.getType(),
                inventoryData.getCss(),
                inventoryData.getSpecial(),
                new ArrayList<InventoryDataResponse>(),
                new ArrayList<InventoryDataRequest>(),
                inventoryData.getNumber(),
                invenData1.getTitle(),
                invenData1.getLink());
    }

    @Override
    public void deleteById(String inventoryId) {
        invenInventory.findInventoryById(inventoryId).orElseThrow(() -> {
            throw new ResourceNotFoundException(LanguageMessageKey.INVENTORY_NOT_FOUND);
        });

        // #### TODO: validate if any Template use

        repository.delete(Map.ofEntries(Map.entry("_id", inventoryId)));
    }
}
