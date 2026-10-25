-- SITRAC - base limpia para demostración final
-- Alineada con el informe: 9 microservicios funcionales y 4 roles internos.
-- Las tablas de negocio son creadas/actualizadas por Spring Data JPA.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS sitrac_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS sitrac_clientes CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS sitrac_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS sitrac_conductores CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS sitrac_flota CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS sitrac_programaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS sitrac_viajes CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS sitrac_combustible CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE sitrac_auth;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  activo BIT(1) NOT NULL,
  nombre_completo VARCHAR(120) NOT NULL,
  password VARCHAR(100) NOT NULL,
  rol ENUM('ADMIN','OPERADOR','COMBUSTIBLE','SUPERVISOR') NOT NULL,
  username VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_username (username)
) ENGINE=InnoDB;

DELETE FROM usuarios;

-- Contraseña para los cuatro usuarios de demostración: Sitrac2026*
INSERT INTO usuarios (activo, nombre_completo, password, rol, username) VALUES
(b'1', 'Administrador SITRAC', '$2a$10$9VVs3Pqb3qFepK7Fg5lp3.m.nBCip4rW67g5K1TZuB8n7FK.pbvji', 'ADMIN', 'admin.sitrac'),
(b'1', 'Operador de Transporte', '$2a$10$9VVs3Pqb3qFepK7Fg5lp3.m.nBCip4rW67g5K1TZuB8n7FK.pbvji', 'OPERADOR', 'operador.sitrac'),
(b'1', 'Responsable de Combustible', '$2a$10$9VVs3Pqb3qFepK7Fg5lp3.m.nBCip4rW67g5K1TZuB8n7FK.pbvji', 'COMBUSTIBLE', 'combustible.sitrac'),
(b'1', 'Supervisor de Operaciones', '$2a$10$9VVs3Pqb3qFepK7Fg5lp3.m.nBCip4rW67g5K1TZuB8n7FK.pbvji', 'SUPERVISOR', 'supervisor.sitrac');

SET FOREIGN_KEY_CHECKS = 1;
