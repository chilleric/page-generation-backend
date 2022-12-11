package com.chillleric.page_generation.dto.inventory;

import java.util.List;
import java.util.Map;

import com.chillleric.page_generation.repository.inventory.Inventory;
import com.chillleric.page_generation.repository.inventory.Inventory.InventoryEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    public String _id;
    public String name;
    public String targetId;
    public InventoryEnum type;
    public Map<String, String> css;
    public Map<String, Object> special;
    public List<InventoryRequest> children;
    public Inventory typeList;
    public int number;
}
