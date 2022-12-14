package com.chillleric.page_generation.dto.inventory;

import java.util.List;
import java.util.Map;
import com.chillleric.page_generation.repository.inventory.Inventory.InventoryEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDataResponse {
    public String targetId;
    public InventoryEnum type;
    public Map<String, String> css;
    public Map<String, Object> special;
    public List<InventoryDataResponse> children;
    public List<InventoryDataRequest> typeList;
    public int number;

    public String title;
    public String link;
}
