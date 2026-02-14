package org.clickenrent.searchservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Elasticsearch document for User entity.
 * 
 * Indexed fields include user identification, contact information, and searchable text.
 * Multi-tenant via companyExternalIds field.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "users")
public class UserDocument {

    @Id
    private String id; // Uses externalId as document ID
    
    @Field(type = FieldType.Keyword)
    private String externalId;
    
    @Field(type = FieldType.Keyword)
    private List<String> companyExternalIds;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String userName;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String email;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String firstName;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String lastName;
    
    @Field(type = FieldType.Keyword)
    private String phone;
    
    @Field(type = FieldType.Keyword)
    private String imageUrl;
    
    @Field(type = FieldType.Boolean)
    private Boolean isActive;
    
    // Combined searchable text field for full-text search
    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchableText;
}
