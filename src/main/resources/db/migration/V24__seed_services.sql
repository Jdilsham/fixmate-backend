INSERT INTO service (title, category_id)
VALUES

-- OUTDOOR / LANDSCAPING
('Landscaping',
 (SELECT category_id FROM service_category WHERE name = 'Outdoor')),

-- ELECTRICAL
('Electrical',
 (SELECT category_id FROM service_category WHERE name = 'Electrical')),

-- CLEANING
('Cleaners',
 (SELECT category_id FROM service_category WHERE name = 'Cleaning')),

-- PLUMBING
('Plumbing',
 (SELECT category_id FROM service_category WHERE name = 'Plumbing')),

-- PAINTING / WASHING
('Color Washing',
 (SELECT category_id FROM service_category WHERE name = 'Painting')),

-- MASONRY / CONSTRUCTION
('Masonry',
 (SELECT category_id FROM service_category WHERE name = 'General')),

-- VEHICLE
('Vehicle Repair',
 (SELECT category_id FROM service_category WHERE name = 'General')),

-- TILE WORK
('Tile Work',
 (SELECT category_id FROM service_category WHERE name = 'General')),

-- CUSHION / UPHOLSTERY
('Cushion Works',
 (SELECT category_id FROM service_category WHERE name = 'General')),

-- CARPENTRY
('Carpentry',
 (SELECT category_id FROM service_category WHERE name = 'Carpentry')),

-- WELDING
('Welding',
 (SELECT category_id FROM service_category WHERE name = 'General')),

-- TV REPAIR
('TV Repair',
 (SELECT category_id FROM service_category WHERE name = 'Home Appliances')),

-- EQUIPMENT
('Equipment Repairing',
 (SELECT category_id FROM service_category WHERE name = 'General')),

-- ROOFING
('Roofing',
 (SELECT category_id FROM service_category WHERE name = 'General')),

-- CONTRACTORS
('Contractors',
 (SELECT category_id FROM service_category WHERE name = 'General'));
