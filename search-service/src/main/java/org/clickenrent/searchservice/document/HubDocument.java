package org.clickenrent.searchservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch document for Hub entity.
 * 
 * Indexed fields include hub name, location, capacity, and status.
 * Multi-tenant via companyExternalId field.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "hubs")
public class HubDocument {

    @Id
    private String id; // Uses externalId as document ID
    
    @Field(type = FieldType.Keyword)
    private String externalId;
    
    @Field(type = FieldType.Keyword)
    private String companyExternalId;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;
    
    @Field(type = FieldType.Long)
    private Long locationId;
    
    @Field(type = FieldType.Integer)
    private Integer capacity;
    
    @Field(type = FieldType.Boolean)
    private Boolean isActive;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    // Combined searchable text field for full-text search
    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchableText;
}
