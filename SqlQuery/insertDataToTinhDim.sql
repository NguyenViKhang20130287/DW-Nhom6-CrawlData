CREATE DEFINER=`root`@`localhost` PROCEDURE `insertDataToTinhDim`()
BEGIN
    -- Biến để kiểm tra số lượng bản ghi được thêm
    DECLARE rowCount INT;

    -- Tắt chế độ autocommit để sử dụng giao dịch
START TRANSACTION;

-- Kiểm tra và thêm dữ liệu từ cột "mien" trong bảng ketquaxoso_staging vào bảng mien_dim
INSERT INTO warehouse.tinh_dim (warehouse.tinh_dim.tenTinh)
SELECT DISTINCT k.tinh
FROM staging.ketquaxoso_staging k
WHERE NOT EXISTS (
    SELECT 1
    FROM warehouse.tinh_dim m
    WHERE m.tenTinh = k.tinh
);

-- Lấy số lượng bản ghi được thêm
SELECT ROW_COUNT() INTO rowCount;

-- Kiểm tra xem có bản ghi nào được thêm không
IF rowCount > 0 THEN
        -- Commit giao dịch nếu có bản ghi được thêm
        COMMIT;
SELECT 'Dữ liệu đã được thêm vào bảng tinh_dim.' AS Message;
ELSE
        -- Rollback giao dịch nếu không có bản ghi nào được thêm
        ROLLBACK;
SELECT 'Không có dữ liệu mới cần thêm vào bảng tinh_dim.' AS Message;
END IF;
END