-- Fix car_type enum values to uppercase to match Java CarType enum
UPDATE car_models SET car_type = UPPER(car_type) WHERE car_type != UPPER(car_type);
