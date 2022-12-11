package com.chillleric.page_generation.repository.inventory;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.chillleric.page_generation.dto.inventory.InventoryRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
	private ObjectId _id;
	private String name;
	private String targetId;
	private InventoryEnum type;
	private Map<String, String> css;
	private Map<String, Object> special;
	private List<InventoryRequest> children;
	private Inventory typeList;
	private int number;

	public enum InventoryEnum {
		IMAGE,
		TITLE,
		LIST,
		COMPONENTS,
		SLIDER,
	}
}
