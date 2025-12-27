INSERT INTO users (id, first_name, last_name, email, phone, password, role, banned)
VALUES
-- PROVIDERS (1–15)
(1,'John','Electric','p1@fixmate.com','0700000001','pw','PROVIDER',false),
(2,'Jane','Plumber','p2@fixmate.com','0700000002','pw','PROVIDER',false),
(3,'Mike','Carpenter','p3@fixmate.com','0700000003','pw','PROVIDER',false),
(4,'Sara','Cleaner','p4@fixmate.com','0700000004','pw','PROVIDER',false),
(5,'Alex','Mechanic','p5@fixmate.com','0700000005','pw','PROVIDER',false),
(6,'Nimal','Landscaper','p6@fixmate.com','0700000006','pw','PROVIDER',false),
(7,'Sunil','Tiler','p7@fixmate.com','0700000007','pw','PROVIDER',false),
(8,'Ravi','Roofer','p8@fixmate.com','0700000008','pw','PROVIDER',false),
(9,'Kasun','Welder','p9@fixmate.com','0700000009','pw','PROVIDER',false),
(10,'Tharindu','TVTech','p10@fixmate.com','0700000010','pw','PROVIDER',false),
(11,'Chamath','Painter','p11@fixmate.com','0700000011','pw','PROVIDER',false),
(12,'Isuru','Mason','p12@fixmate.com','0700000012','pw','PROVIDER',false),
(13,'Saman','Cushion','p13@fixmate.com','0700000013','pw','PROVIDER',false),
(14,'Pradeep','Contractor','p14@fixmate.com','0700000014','pw','PROVIDER',false),
(15,'Dilan','Equipment','p15@fixmate.com','0700000015','pw','PROVIDER',false),

-- CUSTOMERS (16–20)
(16,'Customer','One','c1@fixmate.com','0710000001','pw','CUSTOMER',false),
(17,'Customer','Two','c2@fixmate.com','0710000002','pw','CUSTOMER',false),
(18,'Customer','Three','c3@fixmate.com','0710000003','pw','CUSTOMER',false),
(19,'Customer','Four','c4@fixmate.com','0710000004','pw','CUSTOMER',false),
(20,'Customer','Five','c5@fixmate.com','0710000005','pw','CUSTOMER',false);


INSERT INTO service_category (category_id, name, description)
VALUES
    (1,'Home Services','Household and property services'),
    (2,'Technical & Vehicle Services','Technical and vehicle related services');


INSERT INTO service (service_id, category_id, title, description, base_price)
VALUES
    (1,1,'Landscaping','Garden and outdoor landscaping',4000),
    (2,1,'Electrical','Electrical repair and installation',3500),
    (3,1,'Cleaners','House and office cleaning',2500),
    (4,1,'Plumbing','Plumbing services',3000),
    (5,1,'Color Washing','Painting and color washing',4500),
    (6,1,'Masonry','Brick and cement work',5000),
    (7,1,'Tile Work','Floor and wall tiling',5500),
    (8,1,'Cushion Works','Upholstery and cushion works',4000),
    (9,1,'Carpentry','Wood and furniture work',4800),
    (10,1,'Roofing','Roof installation and repair',6000),
    (11,1,'Contractors','General contracting',7000),
    (12,2,'Vehicle Repair','Vehicle maintenance and repair',5000),
    (13,2,'Welding','Metal welding services',4500),
    (14,2,'TV Repair','Television repair services',3000),
    (15,2,'Equipment Repairing','Equipment repair services',5500);


INSERT INTO service_provider
(service_provider_id, user_id, skill, rating, is_available, is_verified, experience)
VALUES
    (1,1,'Electrician specialist',4.8,true,true,'5 years'),
    (2,2,'Professional plumber',4.6,true,true,'4 years'),
    (3,3,'Furniture carpenter',4.7,true,true,'6 years'),
    (4,4,'Home & office cleaning',4.5,true,false,'3 years'),
    (5,5,'Vehicle mechanic',4.9,true,true,'7 years'),
    (6,6,'Garden landscaping',4.4,true,false,'4 years'),
    (7,7,'Tile installation',4.6,true,true,'5 years'),
    (8,8,'Roof repair expert',4.5,true,true,'6 years'),
    (9,9,'Metal welding',4.7,true,true,'5 years'),
    (10,10,'TV technician',4.3,true,false,'3 years'),
    (11,11,'Painting specialist',4.6,true,true,'5 years'),
    (12,12,'Masonry works',4.5,true,true,'6 years'),
    (13,13,'Cushion upholstery',4.4,true,false,'4 years'),
    (14,14,'General contractor',4.8,true,true,'8 years'),
    (15,15,'Equipment repair expert',4.6,true,true,'5 years');


INSERT INTO service_provider_service (service_id, service_provider_id)
VALUES
    (2,1),(4,2),(9,3),(3,4),(12,5),
    (1,6),(7,7),(10,8),(13,9),(14,10),
    (5,11),(6,12),(8,13),(11,14),(15,15);


INSERT INTO booking
(booking_id, user_id, service_provider_id, service_id, total_price, status)
VALUES
    (1,16,1,2,4500,'COMPLETED'),
    (2,17,2,4,3500,'COMPLETED'),
    (3,18,3,9,5000,'COMPLETED'),
    (4,19,4,3,3000,'COMPLETED'),
    (5,20,5,12,6000,'COMPLETED');


INSERT INTO address
(address_id, user_id, address, city, latitude, longitude, booking_id)
VALUES
    (1,16,'No 10, Main Street','Galle',6.0535,80.2210,1),
    (2,17,'No 20, Lake Road','Matara',5.9496,80.5350,2),
    (3,18,'No 30, Flower Rd','Colombo',6.9271,79.8612,3),
    (4,19,'No 40, Hill St','Kandy',7.2906,80.6337,4),
    (5,20,'No 50, Beach Rd','Negombo',7.2083,79.8358,5);


INSERT INTO payment
(booking_id, amount, payment_method, status)
VALUES
    (1,4500,'CARD','PAID'),
    (2,3500,'CASH','PAID'),
    (3,5000,'CARD','PAID'),
    (4,3000,'CARD','PAID'),
    (5,6000,'CASH','PAID');


INSERT INTO review
(booking_id, service_provider_id, user_id, rating, comment)
VALUES
    (1,1,16,4.8,'Excellent service'),
    (2,2,17,4.6,'Very professional'),
    (3,3,18,4.7,'Great workmanship'),
    (4,4,19,4.5,'Good and clean'),
    (5,5,20,4.9,'Outstanding service');


INSERT INTO notification (user_id, type, message)
VALUES
    (16,'BOOKING','Your booking is confirmed'),
    (1,'REVIEW','You received a new review'),
    (2,'BOOKING','New booking assigned'),
    (3,'REVIEW','Customer left a review');

