
--- DUMMY DRONES ---
insert into drone(
    serial_number,
    model,
    state,
    battery_level,
    weight_limit_gram)
values
('demo-drone#A_1', 'HEAVYWEIGHT', 'DELIVERING', 80, 450),
('demo-drone#A_2', 'LIGHTWEIGHT', 'IDLE', 51, 50),
('demo-drone#A_3', 'MIDDLEWEIGHT', 'RETURNING', 66, 230);

--- DUMMY LOADS ---

insert into load (drone_id) values(1);

insert into load_medications(
    load_load_id,
    code,
    name,
    weight_gram
)
values
(1, 'MED_1', 'Really-stiff-drugs', 15),
(1, 'MED_2', 'Pain-killers-for-mummies-8-', 10),
(1, 'MED_3', 'Strange-stuff-___nobody_knows', 1);