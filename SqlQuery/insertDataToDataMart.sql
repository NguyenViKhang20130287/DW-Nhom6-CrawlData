CREATE DEFINER=`root`@`localhost` PROCEDURE `insertDataToDataMart`()
BEGIN
TRUNCATE TABLE data_mart.ketquaxoso_mart;
INSERT INTO data_mart.ketquaxoso_mart (id, date, tenMien, tenTinh, tenGiai, soTrungThuong)
SELECT
    id,
    date,
    mien,
    tinh,
    giai,
    soTrungThuong
FROM
    warehouse.ketquaxoso_aggerate;

END