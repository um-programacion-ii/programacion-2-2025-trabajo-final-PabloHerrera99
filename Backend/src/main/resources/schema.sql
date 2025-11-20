-- Esquema de usuarios para H2 (asegura que la seed funcione)
DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(200) NOT NULL,
  rol VARCHAR(50) NOT NULL,
  habilitado BOOLEAN NOT NULL,
  creado_en TIMESTAMP NOT NULL
);
