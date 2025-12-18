package org.clickenrent.rentalservice.entity;

/**
 * Marker interface for entities that can be used as product model types.
 * Implemented by: BikeModel, BikeType, Part, ServiceProduct
 */
public interface ProductModelType {
    
    /**
     * Get the ID of the product model type entity.
     * @return the ID
     */
    Long getId();
    
    /**
     * Get the type name discriminator for this product model type.
     * @return the type name (e.g., "BIKE_MODEL", "BIKE_TYPE", "PART", "SERVICE_PRODUCT")
     */
    String getProductModelTypeName();
}

