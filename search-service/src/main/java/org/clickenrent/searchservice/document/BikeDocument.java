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
 * Elasticsearch document for Bike entity.
 * 
 * Indexed fields include bike identification, status, and location information.
 * Multi-tenant via companyExternalId field.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bikes")
public class BikeDocument {

    @Id
    private String id; // Uses externalId as document ID
    
    @Field(type = FieldType.Keyword)
    private String externalId;
    
    @Field(type = FieldType.Keyword)
    private String companyExternalId;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String code;
    
    @Field(type = FieldType.Keyword)
    private String qrCodeUrl;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String frameNumber;
    
    @Field(type = FieldType.Long)
    private Long bikeStatusId;
    
    @Field(type = FieldType.Integer)
    private Integer batteryLevel;
    
    @Field(type = FieldType.Long)
    private Long bikeTypeId;
    
    @Field(type = FieldType.Long)
    private Long bikeModelId;
    
    @Field(type = FieldType.Long)
    private Long hubId;
    
    // Combined searchable text field for full-text search
    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchableText;
}
