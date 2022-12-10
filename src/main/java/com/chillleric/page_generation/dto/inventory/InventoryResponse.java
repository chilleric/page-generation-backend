package com.chillleric.page_generation.dto.inventory;

import java.util.List;
import java.util.Map;

import com.chillleric.page_generation.repository.inventory.Inventory;
import com.chillleric.page_generation.repository.inventory.Inventory.InventoryEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    public String inventoryId;
    public String name;
    public String targetId;
    public InventoryEnum type;
    public Map<String, Object> css;
    public Map<String, Object> special;
    public List<Inventory> children;
    public Inventory typeList;
    public int number;

    public InventoryResponse generateInventoryResponse(Inventory inventory) {
        InventoryResponse inventoryResponse = new InventoryResponse();

        inventoryResponse.setInventoryId(inventory.get_id().toString());
        inventoryResponse.setName(inventory.getName());
        inventoryResponse.setTargetId(inventory.getTargetId());
        inventoryResponse.setType(inventory.getType());
        inventoryResponse.setCss(inventory.getCss());
        inventoryResponse.setSpecial(inventory.getSpecial());
        inventoryResponse.setChildren(inventory.getChildren());
        inventoryResponse.setTypeList(inventory.getTypeList());
        inventoryResponse.setNumber(inventory.getNumber());

        return inventoryResponse;

    }
}
