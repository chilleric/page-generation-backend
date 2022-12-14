package com.chillleric.page_generation.repository.inventory;

import java.util.List;
import java.util.Map;
import com.chillleric.page_generation.repository.inventory.Inventory.InventoryEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryData {
    private String targetId;
    private InventoryEnum type;
    private Map<String, String> css;
    private Map<String, Object> special;
    private List<InventoryData> children;
    private List<InventoryData> typeList;
    private int number;
}
