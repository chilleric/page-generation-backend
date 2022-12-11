package com.chillleric.page_generation.service.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.inventory.InventoryRequest;
import com.chillleric.page_generation.dto.inventory.InventoryResponse;
import com.chillleric.page_generation.exception.InvalidRequestException;
import com.chillleric.page_generation.exception.ResourceNotFoundException;
import com.chillleric.page_generation.inventory.inventory.InvenInventory;
import com.chillleric.page_generation.repository.inventory.Inventory;
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

        Inventory inventory = objectMapper.convertValue(inventoryRequest, Inventory.class);
        ObjectId inventoryId = new ObjectId();
        inventory.set_id(inventoryId);

        repository.create(inventory);
    }

    @Override
    public Optional<InventoryResponse> findOneById(String inventoryId) {
        Optional<Inventory> inventories = invenInventory.findInventoryById(inventoryId);
        inventories.orElseThrow(() -> {
            throw new ResourceNotFoundException(LanguageMessageKey.INVENTORY_NOT_FOUND);
        });

        Inventory inventory = inventories.get();
        return Optional.of(
                preprocessResponse(new InventoryResponse(inventory.get_id().toString(), inventory.getName(),
                        inventory.getTargetId(), inventory.getType(), inventory.getCss(),
                        inventory.getSpecial(), inventory.getChildren(), inventory.getTypeList(),
                        inventory.getNumber())));
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
                        .map(inventory -> {
                            return preprocessResponse(
                                    new InventoryResponse(inventory.get_id().toString(), inventory.getName(),
                                            inventory.getTargetId(), inventory.getType(), inventory.getCss(),
                                            inventory.getSpecial(), inventory.getChildren(), inventory.getTypeList(),
                                            inventory.getNumber()));
                        })
                        .collect(Collectors.toList()),
                page,
                pageSize,
                repository.getTotalPage(allParams)));
    }

    @Override
    public InventoryResponse preprocessResponse(InventoryResponse inventoryResponse) {
        InventoryResponse result = inventoryResponse;
        switch (inventoryResponse.getType()) {
            case IMAGE:
                // # TODO: mapping data into this
                break;
            case TITLE:
                // # TODO: mapping data into this
                break;
            case SLIDER: {
                result.setChildren(preprocessInventoryResquest(inventoryResponse.getTypeList().getChildren()));
                break;
            }
            case LIST: {
                result.setChildren(preprocessInventoryResquest(inventoryResponse.getTypeList().getChildren()));
                break;
            }
            case COMPONENTS: {
                result.setChildren(preprocessInventoryResquest(inventoryResponse.getChildren()));
                break;
            }
        }
        return result;
    }

    @Override
    public List<InventoryRequest> preprocessInventoryResquest(List<InventoryRequest> inventoryRequest) {
        List<InventoryRequest> result = inventoryRequest;

        for (int i = 0; i < inventoryRequest.size(); i++) {
            System.out.println(i);
            switch (inventoryRequest.get(i).getType()) {
                case COMPONENTS: {
                    result.get(i).setChildren(preprocessInventoryResquest(inventoryRequest.get(i).getChildren()));
                    break;
                }
                case LIST: {
                    result.get(i).setChildren(preprocessInventoryResquest(
                            inventoryRequest.get(i).getTypeList().getChildren()));
                    break;
                }
                case SLIDER: {
                    result.get(i).setChildren(preprocessInventoryResquest(
                            inventoryRequest.get(i).getTypeList().getChildren()));
                    break;
                }
                case TITLE: {
                    // # TODO: mapping data into this
                    break;
                }
                case IMAGE: {
                    // # TODO: mapping data into this
                    break;
                }
                default:
                    break;
            }
        }
        return result;
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
