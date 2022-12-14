package com.chillleric.page_generation.dto.inventory;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.repository.inventory.Inventory.InventoryEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// This annotation helps mapping data in nested object
@JsonDeserialize(as = InventoryDataRequest.class)
public class InventoryDataRequest {
    @NotBlank(message = LanguageMessageKey.INVENTORY_TARGET_ID_REQUIRED)
    @NotNull(message = LanguageMessageKey.INVENTORY_TARGET_ID_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.INVENTORY_TARGET_ID_REQUIRED)
    private String targetId;

    private InventoryEnum type;

    @NotNull(message = LanguageMessageKey.INVENTORY_CSS_REQUIRED)
    private Map<String, String> css;

    @NotNull(message = LanguageMessageKey.INVENTORY_SPECIAL_REQUIRED)
    private Map<String, Object> special;

    @NotNull(message = LanguageMessageKey.INVENTORY_CHILDREN_REQUIRED)
    private List<InventoryDataRequest> children;

    @NotNull(message = LanguageMessageKey.INVENTORY_TYPE_LIST_REQUIRED)
    private List<InventoryDataRequest> typeList;

    private int number;
}