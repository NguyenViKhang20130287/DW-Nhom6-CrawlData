CREATE
DEFINER=`root`@`localhost` PROCEDURE `InsertIntoKetQuaXoSoMart`()
BEGIN
    -- Insert data into ketquaxoso_mart table from warehouse tables
INSERT INTO data_mart.ketquaxoso_mart (id, date, tenMien, tenTinh, tenGiai, soTrungThuong)
SELECT f.id,
       d.full_date,
       m.tenMien,
       t.tenTinh,
       g.tenGiai,
       f.soTrungThuong
FROM warehouse.ketquaxoso_fact f
         JOIN warehouse.date_dim d ON f.date_id = d.id
         JOIN warehouse.mien_dim m ON f.mien_id = m.id
         JOIN warehouse.tinh_dim t ON f.tinh_id = t.id
         JOIN warehouse.giai_dim g ON f.giai_id = g.id
WHERE f.is_deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM data_mart.ketquaxoso_mart km
                  WHERE km.id = f.id
                    AND km.date = d.full_date
                    AND km.tenMien = m.tenMien
                    AND km.tenTinh = t.tenTinh
                    AND km.tenGiai = g.tenGiai
                    AND km.soTrungThuong = f.soTrungThuong);
END