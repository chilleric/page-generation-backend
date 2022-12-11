package com.chillleric.page_generation.dto.inventory;

import java.util.List;
import java.util.Map;

import com.chillleric.page_generation.repository.inventory.Inventory;
import com.chillleric.page_generation.repository.inventory.Inventory.InventoryEnum;

import lombok.Data;

@Data
public class InventoryResponse {
    public String inventoryId;
    public String name;
    public String targetId;
    public InventoryEnum type;
    public Map<String, String> css;
    public Map<String, Object> special;
    public List<Inventory> children;
    public Inventory typeList;
    public int number;

    public InventoryResponse(Inventory inventory) {
        this.inventoryId = inventory.get_id().toString();
        this.name = inventory.getName();
        this.targetId = inventory.getTargetId();
        this.type = inventory.getType();
        this.css = inventory.getCss();
        this.special = inventory.getSpecial();
        this.children = inventory.getChildren();
        this.type = inventory.getType();
        this.number = inventory.getNumber();
    }
}
