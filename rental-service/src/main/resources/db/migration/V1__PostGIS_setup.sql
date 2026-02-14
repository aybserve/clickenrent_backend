-- =====================================================================================================================
-- RENTAL SERVICE - POSTGIS SETUP (Flyway Migration V1)
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL + PostGIS
-- Description: Enable PostGIS extension and set up geometry column, trigger, and spatial index
--              for the coordinates table. Required for nearby bikes feature.
--
--              NOTE: The PostGIS extension is also installed by PostGISExtensionConfig.java
--              before Hibernate DDL, so this is a safety net.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Enable PostGIS extension for spatial operations (idempotent)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Guard all table-dependent operations: only run if coordinates table exists
-- (Hibernate ddl-auto=update creates the table before Flyway runs via ApplicationRunner)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'coordinates') THEN

        -- Add geom column if not already present (Hibernate may create it from entity definition)
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'coordinates' AND column_name = 'geom'
        ) THEN
            ALTER TABLE coordinates ADD COLUMN geom GEOGRAPHY(POINT, 4326);
        END IF;

        -- Create spatial index on geometry column for fast proximity queries
        IF NOT EXISTS (
            SELECT 1 FROM pg_indexes WHERE indexname = 'idx_coordinates_geom'
        ) THEN
            CREATE INDEX idx_coordinates_geom ON coordinates USING GIST (geom);
        END IF;

        -- Trigger function to automatically update geometry from latitude/longitude
        CREATE OR REPLACE FUNCTION update_coordinates_geom() RETURNS TRIGGER AS $fn$
        BEGIN
            NEW.geom = ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326)::geography;
            RETURN NEW;
        END;
        $fn$ LANGUAGE plpgsql;

        -- Drop trigger if exists, then recreate (safe for repeated runs)
        DROP TRIGGER IF EXISTS trg_coordinates_geom_update ON coordinates;
        CREATE TRIGGER trg_coordinates_geom_update
            BEFORE INSERT OR UPDATE OF latitude, longitude ON coordinates
            FOR EACH ROW EXECUTE FUNCTION update_coordinates_geom();

        -- Populate geom for any existing coordinates rows that have NULL geom
        UPDATE coordinates SET geom = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
            WHERE geom IS NULL AND latitude IS NOT NULL AND longitude IS NOT NULL;

    END IF;
END $$;

-- =====================================================================================================================
-- END OF POSTGIS SETUP
-- =====================================================================================================================
