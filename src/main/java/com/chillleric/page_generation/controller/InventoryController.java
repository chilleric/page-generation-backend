package com.chillleric.page_generation.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.CommonResponse;
import com.chillleric.page_generation.dto.common.ListWrapperResponse;
import com.chillleric.page_generation.dto.inventory.InventoryRequest;
import com.chillleric.page_generation.dto.inventory.InventoryResponse;
import com.chillleric.page_generation.service.inventory.InventoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "inventory")
public class InventoryController extends AbstractController<InventoryService> {
        @SecurityRequirement(name = "Bearer Authentication")
        @PostMapping
        public ResponseEntity<CommonResponse<String>> createInventory(@RequestBody InventoryRequest inventoryRequest,
                        HttpServletRequest request) {
                validateToken(request);

                service.createInventory(inventoryRequest);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, LanguageMessageKey.INVENTORY_CREATE_SUCCESS,
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @GetMapping(value = "detailed-inventory")
        public ResponseEntity<CommonResponse<InventoryResponse>> getIventoryById(@RequestParam String inventoryId,
                        HttpServletRequest request) {
                validateToken(request);

                return response(service.findOneById(inventoryId), LanguageMessageKey.SUCCESS);
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @GetMapping
        public ResponseEntity<CommonResponse<ListWrapperResponse<InventoryResponse>>> getAllInventories(
                        @RequestParam(required = false, defaultValue = "1") int page,
                        @RequestParam(required = false, defaultValue = "10") int pageSize,
                        @RequestParam Map<String, String> allParams,
                        @RequestParam(defaultValue = "asc") String keySort,
                        @RequestParam(defaultValue = "type") String sortField,
                        HttpServletRequest request) {
                validateToken(request);

                return response(service.findAll(allParams, keySort, page, pageSize,
                                sortField), LanguageMessageKey.SUCCESS);
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @DeleteMapping
        public ResponseEntity<CommonResponse<String>> deleteInventory(@RequestParam String inventoryId,
                        HttpServletRequest request) {
                validateToken(request);

                service.deleteById(inventoryId);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, LanguageMessageKey.SUCCESS,
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }
}
