CREATE DEFINER=`root`@`localhost` PROCEDURE `insertDataToAggerate`()
BEGIN
TRUNCATE TABLE ketquaxoso_aggerate;
INSERT INTO ketquaxoso_aggerate (id, date, mien, tinh, giai, soTrungThuong)
SELECT
    kf.id,
    dd.full_date,
    md.tenMien,
    td.tenTinh,
    gd.tenGiai,
    kf.soTrungThuong
FROM
    ketquaxoso_fact kf
        JOIN date_dim dd ON kf.date_id = dd.id
        JOIN mien_dim md ON kf.mien_id = md.id
        JOIN tinh_dim td ON kf.tinh_id = td.id
        JOIN giai_dim gd ON kf.giai_id = gd.id
WHERE
        kf.is_deleted = 0
  AND dd.full_date BETWEEN kf.date_change AND kf.date_expired;

END