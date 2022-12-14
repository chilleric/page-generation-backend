package com.chillleric.page_generation.repository.invenData;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "inven_datas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvenData {
    private ObjectId _id;
    private String title;
    private String link;
}
