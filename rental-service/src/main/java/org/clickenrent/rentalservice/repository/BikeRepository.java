package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Bike entity.
 */
@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {
    Optional<Bike> findByExternalId(String externalId);
    Optional<Bike> findByCode(String code);

    /**
     * Find bikes within a specified radius of a given location using PostGIS.
     * Returns bikes with their distance from the center point.
     * 
     * @param latitude Center point latitude
     * @param longitude Center point longitude
     * @param radiusMeters Search radius in meters
     * @param limit Maximum number of results
     * @return List of Object arrays containing bike data and distance
     */
    @Query(value = """
        SELECT 
            b.id,
            b.external_id,
            b.code,
            b.name,
            bs.id as bike_status_id,
            bs.name as bike_status_name,
            bcs.id as battery_level_id,
            bcs.name as battery_level_name,
            c.latitude,
            c.longitude,
            ST_Distance(c.geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography) as distance_meters,
            h.external_id as hub_external_id,
            h.name as hub_name
        FROM product b
        INNER JOIN coordinates c ON b.coordinates_id = c.id
        LEFT JOIN bike_status bs ON b.bike_status_id = bs.id
        LEFT JOIN battery_charge_status bcs ON b.battery_charge_status_id = bcs.id
        LEFT JOIN hub h ON b.hub_id = h.id
        WHERE b.dtype = 'BIKE'
            AND b.is_deleted = false
            AND c.geom IS NOT NULL
            AND ST_DWithin(
                c.geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
        ORDER BY distance_meters ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNearbyBikes(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters,
            @Param("limit") Integer limit
    );

    /**
     * Find bikes within a specified radius with bike status filter.
     * 
     * @param latitude Center point latitude
     * @param longitude Center point longitude
     * @param radiusMeters Search radius in meters
     * @param bikeStatusId Filter by bike status ID
     * @param limit Maximum number of results
     * @return List of Object arrays containing bike data and distance
     */
    @Query(value = """
        SELECT 
            b.id,
            b.external_id,
            b.code,
            b.name,
            bs.id as bike_status_id,
            bs.name as bike_status_name,
            bcs.id as battery_level_id,
            bcs.name as battery_level_name,
            c.latitude,
            c.longitude,
            ST_Distance(c.geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography) as distance_meters,
            h.external_id as hub_external_id,
            h.name as hub_name
        FROM product b
        INNER JOIN coordinates c ON b.coordinates_id = c.id
        LEFT JOIN bike_status bs ON b.bike_status_id = bs.id
        LEFT JOIN battery_charge_status bcs ON b.battery_charge_status_id = bcs.id
        LEFT JOIN hub h ON b.hub_id = h.id
        WHERE b.dtype = 'BIKE'
            AND b.is_deleted = false
            AND c.geom IS NOT NULL
            AND bs.id = :bikeStatusId
            AND ST_DWithin(
                c.geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
        ORDER BY distance_meters ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNearbyBikesByStatus(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters,
            @Param("bikeStatusId") Long bikeStatusId,
            @Param("limit") Integer limit
    );

    /**
     * Count bikes within a specified radius.
     * 
     * @param latitude Center point latitude
     * @param longitude Center point longitude
     * @param radiusMeters Search radius in meters
     * @return Count of bikes within radius
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM product b
        INNER JOIN coordinates c ON b.coordinates_id = c.id
        WHERE b.dtype = 'BIKE'
            AND b.is_deleted = false
            AND c.geom IS NOT NULL
            AND ST_DWithin(
                c.geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusMeters
            )
        """, nativeQuery = true)
    Long countNearbyBikes(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );
}








