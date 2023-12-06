CREATE DEFINER=`root`@`localhost` PROCEDURE `insertDataToFact`()
BEGIN
    DECLARE recordCount INT;

    -- Kiểm tra xem dữ liệu đã tồn tại trong bảng warehouse.ketquaxoso_fact hay chưa
SELECT COUNT(*) INTO recordCount
FROM warehouse.ketquaxoso_fact wf
         JOIN staging.ketquaxoso_staging ks ON wf.date_id = ks.ngayXo
    AND wf.mien_id = ks.mien
    AND wf.tinh_id = ks.tinh
    AND wf.giai_id = ks.giai
    AND wf.soTrungThuong = ks.soTrungThuong;

-- Nếu không tồn tại, thêm dữ liệu mới
IF recordCount = 0 THEN
        INSERT INTO warehouse.ketquaxoso_fact (date_id, mien_id, tinh_id, giai_id, soTrungThuong, is_deleted, date_change, date_expired)
SELECT
    dd.id,
    md.id,
    td.id,
    gd.id,
    ks.soTrungThuong,
    0, -- Giá trị mặt định cho is_deleted
    CURRENT_DATE, -- Giá trị mặt định cho date_change
    '9999-12-30' -- Giá trị mặt định cho date_expired
FROM
    staging.ketquaxoso_staging ks
        JOIN warehouse.date_dim dd ON ks.ngayXo = dd.full_date
        JOIN warehouse.mien_dim md ON ks.mien = md.tenMien
        JOIN warehouse.tinh_dim td ON ks.tinh = td.tenTinh
        JOIN warehouse.giai_dim gd ON ks.giai = gd.tenGiai
WHERE NOT EXISTS (
    SELECT 1
    FROM warehouse.ketquaxoso_fact wf2
    WHERE wf2.date_id = dd.id
      AND wf2.mien_id = md.id
      AND wf2.tinh_id = td.id
      AND wf2.giai_id = gd.id
      AND wf2.soTrungThuong = ks.soTrungThuong
);
END IF;

END