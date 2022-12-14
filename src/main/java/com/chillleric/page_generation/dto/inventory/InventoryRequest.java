package com.chillleric.page_generation.dto.inventory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.chillleric.page_generation.constant.LanguageMessageKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    @NotBlank(message = LanguageMessageKey.INVENTORY_NAME_REQUIRED)
    @NotNull(message = LanguageMessageKey.INVENTORY_NAME_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.INVENTORY_NAME_REQUIRED)
    private String name;

    @NotNull(message = LanguageMessageKey.INVENTORY_DATA_REQUIRED)
    private InventoryDataRequest data;

}
