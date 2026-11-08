-- =====================================================================
-- ElectroMarket - Sistema de Gestión de Inventario
-- Script de creación de base de datos (PostgreSQL)
-- Basado en el Modelo Físico (7 tablas)
-- Versión 2: se agrega usuario_actualizacion (auditoría: quién modificó)
-- Versión 3: activo (producto, proveedor), telefono y correo (proveedor),
--            tipo_documento (entrada_stock), documento (salida_stock)
-- =====================================================================
-- Ejecutar en pgAdmin sobre una base de datos vacía, por ejemplo:
--   CREATE DATABASE electromarket;
-- y luego conectarse a "electromarket" y correr este script completo.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Función para mantener fecha_actualizacion al día (auditoría)
--    Se dispara en cada UPDATE de las tablas que la usan.
--    OJO: usuario_actualizacion NO lo llena la base de datos, porque
--    PostgreSQL no sabe qué usuario de la aplicación hizo el cambio.
--    Lo debe enviar el backend (Spring Boot) en cada UPDATE.
-- ---------------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_fecha_actualizacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ---------------------------------------------------------------------
-- 2. Tablas (en orden: primero las que no dependen de otras)
-- ---------------------------------------------------------------------

-- usuario
CREATE TABLE usuario (
    id_usuario             SERIAL          PRIMARY KEY,
    nombre                 VARCHAR(100)    NOT NULL,
    usuario                CHAR(50)        NOT NULL UNIQUE,
    contrasena             VARCHAR(255)    NOT NULL,
    rol                    VARCHAR(30)     NOT NULL,
    fecha_creacion         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion  INTEGER,
    CONSTRAINT fk_usuario_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario)
);

-- proveedor
CREATE TABLE proveedor (
    id_proveedor           SERIAL          PRIMARY KEY,
    ruc                    CHAR(11)        NOT NULL UNIQUE,
    razon_social           VARCHAR(150)    NOT NULL,
    contacto               VARCHAR(100),
    tiempo_entrega         INTEGER,
    telefono               VARCHAR(15),
    correo                 VARCHAR(100),
    activo                 BOOLEAN         NOT NULL DEFAULT TRUE,
    fecha_creacion         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion  INTEGER,
    CONSTRAINT fk_proveedor_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario)
);

-- producto
CREATE TABLE producto (
    id_producto            SERIAL          PRIMARY KEY,
    codigo_sku             CHAR(20)        NOT NULL UNIQUE,
    nombre                 VARCHAR(150)    NOT NULL,
    categoria              VARCHAR(50)     NOT NULL,
    marca                  VARCHAR(50),
    precio                 NUMERIC(10,2)   NOT NULL,
    ubicacion              VARCHAR(50),
    stock_minimo           INTEGER         NOT NULL DEFAULT 0,
    stock_actual           INTEGER         NOT NULL DEFAULT 0,
    activo                 BOOLEAN         NOT NULL DEFAULT TRUE,
    fecha_creacion         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion  INTEGER,
    CONSTRAINT fk_producto_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario)
);

-- entrada_stock (cabecera de la entrada)
-- id_usuario = quién registró la entrada; usuario_actualizacion = quién la modificó después
CREATE TABLE entrada_stock (
    id_entrada             SERIAL          PRIMARY KEY,
    id_proveedor           INTEGER         NOT NULL,
    id_usuario             INTEGER         NOT NULL,
    tipo_documento         VARCHAR(20)     NOT NULL DEFAULT 'FACTURA',
    documento              VARCHAR(20)     NOT NULL,
    fecha                  DATE            NOT NULL DEFAULT CURRENT_DATE,
    estado                 VARCHAR(20)     NOT NULL DEFAULT 'REGISTRADA',
    fecha_creacion         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion  INTEGER,
    CONSTRAINT fk_entrada_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedor (id_proveedor),
    CONSTRAINT fk_entrada_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT fk_entrada_usuario_actualizacion FOREIGN KEY (usuario_actualizacion)
        REFERENCES usuario (id_usuario)
);

-- detalle_entrada (una línea por producto dentro de una entrada)
CREATE TABLE detalle_entrada (
    id_detalle          SERIAL          PRIMARY KEY,
    id_entrada          INTEGER         NOT NULL,
    id_producto         INTEGER         NOT NULL,
    cantidad            INTEGER         NOT NULL,
    fecha_creacion      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_detalle_entrada FOREIGN KEY (id_entrada)
        REFERENCES entrada_stock (id_entrada),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto)
);

-- salida_stock
CREATE TABLE salida_stock (
    id_salida           SERIAL          PRIMARY KEY,
    id_producto         INTEGER         NOT NULL,
    id_usuario          INTEGER         NOT NULL,
    fecha               DATE            NOT NULL DEFAULT CURRENT_DATE,
    tipo_salida         VARCHAR(20)     NOT NULL,
    documento           VARCHAR(20),
    cantidad            INTEGER         NOT NULL,
    fecha_creacion      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_salida_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto),
    CONSTRAINT fk_salida_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
);

-- kardex
CREATE TABLE kardex (
    id_kardex           SERIAL          PRIMARY KEY,
    id_producto         INTEGER         NOT NULL,
    fecha_calculo       DATE            NOT NULL DEFAULT CURRENT_DATE,
    stock_inicial       INTEGER         NOT NULL,
    entradas            INTEGER         NOT NULL DEFAULT 0,
    salidas             INTEGER         NOT NULL DEFAULT 0,
    stock_final         INTEGER         NOT NULL,
    fecha_creacion      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_kardex_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto)
);

-- ---------------------------------------------------------------------
-- 3. Índices (los de PK y UNIQUE ya se crean automáticamente)
-- ---------------------------------------------------------------------
CREATE INDEX idx_producto_categoria     ON producto (categoria);
CREATE INDEX idx_producto_marca         ON producto (marca);
CREATE INDEX idx_entrada_fecha          ON entrada_stock (fecha);
CREATE INDEX idx_salida_fecha           ON salida_stock (fecha);
CREATE INDEX idx_kardex_producto_fecha  ON kardex (id_producto, fecha_calculo);

-- ---------------------------------------------------------------------
-- 4. Triggers de auditoría (solo en las tablas con fecha_actualizacion)
-- ---------------------------------------------------------------------
CREATE TRIGGER trg_usuario_fecha_actualizacion
    BEFORE UPDATE ON usuario
    FOR EACH ROW EXECUTE FUNCTION set_fecha_actualizacion();

CREATE TRIGGER trg_proveedor_fecha_actualizacion
    BEFORE UPDATE ON proveedor
    FOR EACH ROW EXECUTE FUNCTION set_fecha_actualizacion();

CREATE TRIGGER trg_producto_fecha_actualizacion
    BEFORE UPDATE ON producto
    FOR EACH ROW EXECUTE FUNCTION set_fecha_actualizacion();

CREATE TRIGGER trg_entrada_fecha_actualizacion
    BEFORE UPDATE ON entrada_stock
    FOR EACH ROW EXECUTE FUNCTION set_fecha_actualizacion();
