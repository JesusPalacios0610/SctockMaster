-- =====================================================================
-- ElectroMarket - Migración v3
-- Para bases de datos que YA fueron creadas con la versión 2 del script.
-- Si la base es nueva, usa script_electromarket.sql (versión 3) y NO este archivo.
-- Ejecutar UNA sola vez, en un Query Tool conectado a "electromarket".
--
-- Cambios:
--   producto       -> activo            (borrado lógico)
--   proveedor      -> activo, telefono, correo
--   entrada_stock  -> tipo_documento    (FACTURA, GUIA_REMISION, BOLETA)
--   salida_stock   -> documento         (opcional)
--
-- No borra ni modifica los datos existentes: las filas actuales quedan
-- con activo = TRUE, tipo_documento = 'FACTURA' y los demás campos en NULL.
-- =====================================================================

ALTER TABLE producto
    ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE proveedor
    ADD COLUMN activo   BOOLEAN      NOT NULL DEFAULT TRUE,
    ADD COLUMN telefono VARCHAR(15),
    ADD COLUMN correo   VARCHAR(100);

ALTER TABLE entrada_stock
    ADD COLUMN tipo_documento VARCHAR(20) NOT NULL DEFAULT 'FACTURA';

ALTER TABLE salida_stock
    ADD COLUMN documento VARCHAR(20);

-- ---------------------------------------------------------------------
-- Verificación (opcional): debe mostrar las 5 columnas nuevas
-- ---------------------------------------------------------------------
SELECT table_name, column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND (   (table_name = 'producto'      AND column_name = 'activo')
       OR (table_name = 'proveedor'     AND column_name IN ('activo', 'telefono', 'correo'))
       OR (table_name = 'entrada_stock' AND column_name = 'tipo_documento')
       OR (table_name = 'salida_stock'  AND column_name = 'documento'))
ORDER BY table_name, column_name;
