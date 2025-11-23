-- Esquema completo para H2: crea tablas necesarias antes de data.sql
DROP TABLE IF EXISTS asientos_venta;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS eventos;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(200) NOT NULL,
  rol VARCHAR(50) NOT NULL,
  habilitado BOOLEAN NOT NULL,
  creado_en TIMESTAMP NOT NULL
);

CREATE TABLE eventos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  external_id VARCHAR(100) UNIQUE,
  nombre VARCHAR(200) NOT NULL,
  descripcion VARCHAR(1000),
  categoria VARCHAR(100),
  fecha_hora TIMESTAMP NOT NULL,
  lugar VARCHAR(200),
  sala VARCHAR(100),
  precio_base DECIMAL(12,2),
  moneda VARCHAR(10),
  activo BOOLEAN NOT NULL
);

CREATE TABLE ventas (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  evento_id BIGINT NOT NULL,
  precio_total DECIMAL(12,2) NOT NULL,
  fecha_hora TIMESTAMP NOT NULL,
  estado VARCHAR(20) NOT NULL,
  CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  CONSTRAINT fk_evento FOREIGN KEY (evento_id) REFERENCES eventos(id)
);

CREATE TABLE asientos_venta (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  venta_id BIGINT NOT NULL,
  fila VARCHAR(10) NOT NULL,
  columna VARCHAR(10) NOT NULL,
  nombre_persona VARCHAR(200),
  CONSTRAINT fk_venta FOREIGN KEY (venta_id) REFERENCES ventas(id)
);
