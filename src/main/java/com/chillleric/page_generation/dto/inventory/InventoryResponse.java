package com.chillleric.page_generation.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    public String id;
    public String name;
    public InventoryDataResponse data;
}
