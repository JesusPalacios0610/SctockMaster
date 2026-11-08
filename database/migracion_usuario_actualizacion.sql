-- =====================================================================
-- ElectroMarket - Migración: agregar usuario_actualizacion
-- Para bases de datos que YA fueron creadas con la versión 1 del script.
-- Si la base es nueva, usa script_electromarket.sql (versión 2) y NO este archivo.
-- Ejecutar UNA sola vez, en un Query Tool conectado a "electromarket".
-- No borra ni modifica los datos existentes: las filas actuales quedan
-- con usuario_actualizacion = NULL.
-- =====================================================================

ALTER TABLE usuario
    ADD COLUMN usuario_actualizacion INTEGER,
    ADD CONSTRAINT fk_usuario_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario);

ALTER TABLE proveedor
    ADD COLUMN usuario_actualizacion INTEGER,
    ADD CONSTRAINT fk_proveedor_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario);

ALTER TABLE producto
    ADD COLUMN usuario_actualizacion INTEGER,
    ADD CONSTRAINT fk_producto_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario);

ALTER TABLE entrada_stock
    ADD COLUMN usuario_actualizacion INTEGER,
    ADD CONSTRAINT fk_entrada_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario);
