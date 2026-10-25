-- SITRAC - Base limpia para exposición
-- ADVERTENCIA: este script ELIMINA y RECREA las 10 bases de datos SITRAC.
-- Datos de conductores y teléfonos son ficticios y se usan únicamente para demostración.
-- Fecha de preparación: 2026-10-06

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 1. AUTENTICACIÓN
-- =========================================================
DROP DATABASE IF EXISTS sitrac_auth;
CREATE DATABASE sitrac_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_auth;

CREATE TABLE usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  activo BIT(1) NOT NULL,
  nombre_completo VARCHAR(120) NOT NULL,
  password VARCHAR(100) NOT NULL,
  rol ENUM('ADMIN','MANTENIMIENTO','OPERADOR','SOMMA','SUPERVISOR') NOT NULL,
  username VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_username (username)
) ENGINE=InnoDB;

INSERT INTO usuarios (id, activo, nombre_completo, password, rol, username) VALUES
(1, b'1', 'Administrador SITRAC', '$2a$10$gEEvtgmQX7OLA9BTScFTQeTj4MhShKHQepDP0aPqRHXkJuAqvV6ju', 'ADMIN', 'admin.sitrac'),
(2, b'1', 'Operador de Transporte', '$2a$10$l6WZ/TR/teCVgT0U4nIiYe1XIz63ZtA.HXKC8a/goUiT93cWfq.PS', 'OPERADOR', 'operador.sitrac'),
(3, b'1', 'Responsable SOMMA', '$2a$10$GLpckELCc7sG5uZVvz7UYu6CD1jaNZI4It4EA/4Lhp/bSpwKJIo.6', 'SOMMA', 'somma.sitrac'),
(4, b'1', 'Responsable de Mantenimiento', '$2a$10$ghGgAme1UiqFG0t.dJr5aeSDGm4/SidZLWt26ZtyyhMnJxt5yhhpa', 'MANTENIMIENTO', 'mantenimiento.sitrac'),
(5, b'1', 'Supervisor de Operaciones', '$2a$10$yvPR7pfzJAwTtqCO0ujIm.8O9ShuHpQoztOvDur81EYqO/Ra7oNbK', 'SUPERVISOR', 'supervisor.sitrac');

-- =========================================================
-- 2. CLIENTES - 10 registros base
-- =========================================================
DROP DATABASE IF EXISTS sitrac_clientes;
CREATE DATABASE sitrac_clientes CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_clientes;

CREATE TABLE clientes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  activo BIT(1) NOT NULL,
  direccion VARCHAR(200) DEFAULT NULL,
  email VARCHAR(120) DEFAULT NULL,
  nombre_razon_social VARCHAR(150) NOT NULL,
  numero_documento VARCHAR(20) NOT NULL,
  telefono VARCHAR(30) DEFAULT NULL,
  tipo_documento ENUM('CE','DNI','OTRO','RUC') NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_clientes_numero_documento (numero_documento)
) ENGINE=InnoDB;

INSERT INTO clientes (id, activo, direccion, email, nombre_razon_social, numero_documento, telefono, tipo_documento) VALUES
(1, b'1', 'Pacasmayo, La Libertad', 'contacto@cpsaa.com.pe', 'CEMENTOS PACASMAYO S.A.A.', '20419387658', '(01) 317-6000', 'RUC'),
(2, b'1', 'Pacasmayo, La Libertad', 'operaciones@tecnicaavicola.demo', 'TECNICA AVICOLA S.A.', '20505120702', '900-100-002', 'RUC'),
(3, b'1', 'Panamericana Sur, Arequipa', 'logistica@acerosarequipa.demo', 'CORPORACION ACEROS AREQUIPA S.A.', '20370146994', '900-100-003', 'RUC'),
(4, b'1', 'Carmen de la Legua, Callao', 'logistica@alicorp.demo', 'ALICORP S.A.A.', '20100055237', '900-100-004', 'RUC'),
(5, b'1', 'San Juan de Lurigancho, Lima', 'distribucion@lindley.demo', 'ARCA CONTINENTAL LINDLEY S.A.', '20101024645', '900-100-005', 'RUC'),
(6, b'1', 'Zona Industrial, Chimbote', 'logistica@siderperu.demo', 'EMPRESA SIDERURGICA DEL PERU S.A.A.', '20402885549', '900-100-006', 'RUC'),
(7, b'1', 'San Borja, Lima', 'operaciones@solgas.demo', 'SOLGAS S.A.', '20100176450', '900-100-007', 'RUC'),
(8, b'1', 'San Borja, Lima', 'operaciones@limagas.demo', 'LIMA GAS S.A.', '20100007348', '900-100-008', 'RUC'),
(9, b'1', 'Quiruvilca, La Libertad', 'logistica@boroo.demo', 'MINERA BOROO MISQUICHILCA S.A.', '20209133394', '900-100-009', 'RUC'),
(10, b'1', 'Trujillo, La Libertad', 'operaciones@agregadosnorte.demo', 'TRANSPORTES Y AGREGADOS DEL NORTE S.A.C.', '20600000010', '900-100-010', 'RUC');

-- =========================================================
-- 3. PEDIDOS - 10 registros con estados útiles para exposición
-- =========================================================
DROP DATABASE IF EXISTS sitrac_pedidos;
CREATE DATABASE sitrac_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_pedidos;

CREATE TABLE pedidos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cliente_id BIGINT NOT NULL,
  descripcion_carga VARCHAR(200) DEFAULT NULL,
  destino VARCHAR(150) NOT NULL,
  estado ENUM('CANCELADO','EN_VIAJE','FINALIZADO','PROGRAMADO','REGISTRADO') NOT NULL,
  fecha_solicitud DATE NOT NULL,
  origen VARCHAR(150) NOT NULL,
  tipo_carga ENUM('CAL_GRANEL','CARGA_ANCHA','CEMENTO_BOLSA','ESPECIAL','MAQUINARIA','OTRO') NOT NULL,
  toneladas DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO pedidos (id, cliente_id, descripcion_carga, destino, estado, fecha_solicitud, origen, tipo_carga, toneladas) VALUES
(1, 1, 'Cal a granel para distribución industrial', 'Trujillo, La Libertad', 'FINALIZADO', '2026-10-03', 'Pacasmayo, La Libertad', 'CAL_GRANEL', 35.00),
(2, 1, 'Cemento embolsado para centro de distribución', 'Chiclayo, Lambayeque', 'PROGRAMADO', '2026-10-06', 'Pacasmayo, La Libertad', 'CEMENTO_BOLSA', 35.00),
(3, 3, 'Maquinaria pesada para proyecto industrial', 'Cajamarca, Cajamarca', 'EN_VIAJE', '2026-10-06', 'Trujillo, La Libertad', 'MAQUINARIA', 30.00),
(4, 2, 'Insumos a granel para planta avícola', 'Trujillo, La Libertad', 'REGISTRADO', '2026-10-06', 'Pacasmayo, La Libertad', 'CAL_GRANEL', 35.00),
(5, 6, 'Estructuras metálicas de gran dimensión', 'Trujillo, La Libertad', 'REGISTRADO', '2026-10-06', 'Chimbote, Áncash', 'CARGA_ANCHA', 32.00),
(6, 4, 'Productos de consumo masivo paletizados', 'Trujillo, La Libertad', 'REGISTRADO', '2026-10-06', 'Callao, Callao', 'OTRO', 28.00),
(7, 7, 'Equipamiento para operación de GLP', 'Piura, Piura', 'REGISTRADO', '2026-10-06', 'Lima, Lima', 'ESPECIAL', 25.00),
(8, 9, 'Equipo para operación minera', 'Quiruvilca, La Libertad', 'REGISTRADO', '2026-10-06', 'Trujillo, La Libertad', 'MAQUINARIA', 30.00),
(9, 5, 'Productos terminados para distribución', 'Trujillo, La Libertad', 'REGISTRADO', '2026-10-06', 'Lima, Lima', 'OTRO', 26.00),
(10, 10, 'Cal industrial para obra civil', 'Virú, La Libertad', 'REGISTRADO', '2026-10-06', 'Pacasmayo, La Libertad', 'CAL_GRANEL', 35.00);

-- =========================================================
-- 4. CONDUCTORES - 10 registros realistas de demostración
-- =========================================================
DROP DATABASE IF EXISTS sitrac_conductores;
CREATE DATABASE sitrac_conductores CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_conductores;

CREATE TABLE conductores (
  id BIGINT NOT NULL AUTO_INCREMENT,
  activo BIT(1) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  categoria_licencia VARCHAR(20) NOT NULL,
  disponible BIT(1) NOT NULL,
  dni VARCHAR(8) NOT NULL,
  fecha_vencimiento_licencia DATE NOT NULL,
  nombres VARCHAR(100) NOT NULL,
  numero_licencia VARCHAR(20) NOT NULL,
  telefono VARCHAR(30) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_conductores_dni (dni),
  UNIQUE KEY uk_conductores_licencia (numero_licencia)
) ENGINE=InnoDB;

INSERT INTO conductores (id, activo, apellidos, categoria_licencia, disponible, dni, fecha_vencimiento_licencia, nombres, numero_licencia, telefono) VALUES
(1, b'1', 'RAMIREZ TORRES', 'A-IIIC', b'1', '70000001', '2029-12-31', 'JUAN CARLOS', 'Q70000001', '900000001'),
(2, b'1', 'MENDOZA RUIZ', 'A-IIIC', b'0', '70000002', '2029-11-30', 'LUIS ALBERTO', 'Q70000002', '900000002'),
(3, b'1', 'ROJAS MENDOZA', 'A-IIIC', b'0', '70000003', '2030-03-15', 'MIGUEL ALEJANDRO', 'Q70000003', '900000003'),
(4, b'1', 'VARGAS PEREZ', 'A-IIIC', b'1', '70000004', '2029-08-20', 'CESAR AUGUSTO', 'Q70000004', '900000004'),
(5, b'1', 'TORRES RAMIREZ', 'A-IIIC', b'1', '70000005', '2030-07-08', 'LUIS FERNANDO', 'Q70000005', '900000005'),
(6, b'1', 'MENDOZA FLORES', 'A-IIIC', b'1', '70000006', '2030-01-12', 'JORGE LUIS', 'Q70000006', '900000006'),
(7, b'1', 'GUTIERREZ SILVA', 'A-IIIC', b'1', '70000007', '2029-09-30', 'JOSE CARLOS', 'Q70000007', '900000007'),
(8, b'1', 'SALAZAR VEGA', 'A-IIIC', b'1', '70000008', '2030-06-18', 'MARCO ANTONIO', 'Q70000008', '900000008'),
(9, b'1', 'CASTILLO CRUZ', 'A-IIIC', b'1', '70000009', '2029-12-05', 'RAUL EDUARDO', 'Q70000009', '900000009'),
(10, b'1', 'PAREDES LEON', 'A-IIIC', b'1', '70000010', '2030-10-14', 'RICARDO JAVIER', 'Q70000010', '900000010');

-- =========================================================
-- 5. FLOTA - 10 unidades totales: 5 tractos + 5 semirremolques
-- =========================================================
DROP DATABASE IF EXISTS sitrac_flota;
CREATE DATABASE sitrac_flota CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_flota;

CREATE TABLE tractos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  activo BIT(1) NOT NULL,
  anio INT NOT NULL,
  capacidad_toneladas DECIMAL(10,2) NOT NULL,
  estado ENUM('ASIGNADO','DISPONIBLE','INACTIVO','MANTENIMIENTO') NOT NULL,
  marca VARCHAR(60) NOT NULL,
  modelo VARCHAR(60) NOT NULL,
  placa VARCHAR(10) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_tractos_placa (placa)
) ENGINE=InnoDB;

CREATE TABLE semirremolques (
  id BIGINT NOT NULL AUTO_INCREMENT,
  activo BIT(1) NOT NULL,
  capacidad_toneladas DECIMAL(10,2) NOT NULL,
  estado ENUM('ASIGNADO','DISPONIBLE','INACTIVO','MANTENIMIENTO') NOT NULL,
  placa VARCHAR(10) NOT NULL,
  tipo ENUM('BOMBONA','CAMA_BAJA','OTRO','PLATAFORMA') NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_semirremolques_placa (placa)
) ENGINE=InnoDB;

INSERT INTO tractos (id, activo, anio, capacidad_toneladas, estado, marca, modelo, placa) VALUES
(1, b'1', 2022, 35.00, 'DISPONIBLE', 'VOLVO', 'FH 540', 'T1A-201'),
(2, b'1', 2023, 35.00, 'ASIGNADO', 'SCANIA', 'R450', 'T1A-202'),
(3, b'1', 2022, 35.00, 'ASIGNADO', 'VOLVO', 'FH 500', 'T1A-203'),
(4, b'1', 2021, 35.00, 'MANTENIMIENTO', 'FREIGHTLINER', 'CASCADIA', 'T1A-204'),
(5, b'1', 2023, 35.00, 'DISPONIBLE', 'MACK', 'ANTHEM', 'T1A-205');

INSERT INTO semirremolques (id, activo, capacidad_toneladas, estado, placa, tipo) VALUES
(1, b'1', 35.00, 'DISPONIBLE', 'S1A-301', 'BOMBONA'),
(2, b'1', 35.00, 'ASIGNADO', 'S1A-302', 'PLATAFORMA'),
(3, b'1', 35.00, 'ASIGNADO', 'S1A-303', 'CAMA_BAJA'),
(4, b'1', 35.00, 'DISPONIBLE', 'S1A-304', 'BOMBONA'),
(5, b'1', 35.00, 'DISPONIBLE', 'S1A-305', 'CAMA_BAJA');

-- =========================================================
-- 6. PROGRAMACIONES - ejemplos mínimos de estados clave
-- =========================================================
DROP DATABASE IF EXISTS sitrac_programaciones;
CREATE DATABASE sitrac_programaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_programaciones;

CREATE TABLE programaciones (
  id BIGINT NOT NULL AUTO_INCREMENT,
  conductor_id BIGINT NOT NULL,
  estado ENUM('CANCELADA','FINALIZADA','PROGRAMADA') NOT NULL,
  fecha_programada DATETIME(6) NOT NULL,
  observacion VARCHAR(300) DEFAULT NULL,
  pedido_id BIGINT NOT NULL,
  semirremolque_id BIGINT NOT NULL,
  tracto_id BIGINT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO programaciones (id, conductor_id, estado, fecha_programada, observacion, pedido_id, semirremolque_id, tracto_id) VALUES
(1, 1, 'FINALIZADA', '2026-10-04 08:00:00.000000', 'Operación completa de cal a granel para demostrar el flujo cerrado', 1, 1, 1),
(2, 2, 'PROGRAMADA', '2026-10-07 08:00:00.000000', 'Cemento embolsado listo para iniciar viaje', 2, 2, 2),
(3, 3, 'PROGRAMADA', '2026-10-06 20:00:00.000000', 'Maquinaria pesada actualmente en ruta', 3, 3, 3);

-- =========================================================
-- 7. VIAJES
-- =========================================================
DROP DATABASE IF EXISTS sitrac_viajes;
CREATE DATABASE sitrac_viajes CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_viajes;

CREATE TABLE viajes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  estado ENUM('CANCELADO','EN_VIAJE','FINALIZADO','PROGRAMADO') NOT NULL,
  fecha_fin DATETIME(6) DEFAULT NULL,
  fecha_inicio DATETIME(6) DEFAULT NULL,
  kilometraje_final DECIMAL(12,2) DEFAULT NULL,
  kilometraje_inicial DECIMAL(12,2) DEFAULT NULL,
  observacion VARCHAR(500) DEFAULT NULL,
  programacion_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_viajes_programacion (programacion_id)
) ENGINE=InnoDB;

INSERT INTO viajes (id, estado, fecha_fin, fecha_inicio, kilometraje_final, kilometraje_inicial, observacion, programacion_id) VALUES
(1, 'FINALIZADO', '2026-10-04 15:30:00.000000', '2026-10-04 08:20:00.000000', 125610.00, 125430.00, 'Viaje finalizado correctamente', 1),
(2, 'PROGRAMADO', NULL, NULL, NULL, NULL, 'Viaje generado y pendiente de inicio', 2),
(3, 'EN_VIAJE', NULL, '2026-10-06 20:20:00.000000', NULL, 210350.00, 'Viaje en curso con maquinaria pesada', 3);

-- =========================================================
-- 8. MANTENIMIENTO
-- =========================================================
DROP DATABASE IF EXISTS sitrac_mantenimiento;
CREATE DATABASE sitrac_mantenimiento CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_mantenimiento;

CREATE TABLE mantenimientos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  costo DECIMAL(12,2) DEFAULT NULL,
  descripcion VARCHAR(500) NOT NULL,
  estado ENUM('CANCELADO','EN_PROCESO','FINALIZADO','PROGRAMADO') NOT NULL,
  fecha_fin DATE DEFAULT NULL,
  fecha_inicio DATE NOT NULL,
  tipo_mantenimiento ENUM('CORRECTIVO','PREVENTIVO') NOT NULL,
  tipo_unidad ENUM('SEMIRREMOLQUE','TRACTO') NOT NULL,
  unidad_id BIGINT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO mantenimientos (id, costo, descripcion, estado, fecha_fin, fecha_inicio, tipo_mantenimiento, tipo_unidad, unidad_id) VALUES
(1, 1850.00, 'Cambio de kit de embrague y revisión del sistema de frenos', 'EN_PROCESO', '2026-10-08', '2026-10-06', 'CORRECTIVO', 'TRACTO', 4),
(2, 680.00, 'Inspección preventiva de suspensión, luces y sistema neumático', 'FINALIZADO', '2026-10-05', '2026-10-05', 'PREVENTIVO', 'SEMIRREMOLQUE', 5);

-- =========================================================
-- 9. SOMMA
-- =========================================================
DROP DATABASE IF EXISTS sitrac_somma;
CREATE DATABASE sitrac_somma CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_somma;

CREATE TABLE registros_somma (
  id BIGINT NOT NULL AUTO_INCREMENT,
  conductor_id BIGINT DEFAULT NULL,
  descripcion VARCHAR(1000) NOT NULL,
  estado ENUM('CANCELADO','CERRADO','REGISTRADO') NOT NULL,
  fecha DATETIME(6) NOT NULL,
  lugar VARCHAR(150) DEFAULT NULL,
  tipo ENUM('ACCIDENTE','CAPACITACION','CHARLA','INCIDENTE','INSPECCION') NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  programacion_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO registros_somma (id, conductor_id, descripcion, estado, fecha, lugar, tipo, titulo, programacion_id) VALUES
(1, 1, 'Charla preventiva sobre conducción segura, revisión de unidad y riesgos de carretera.', 'CERRADO', '2026-10-04 07:40:00.000000', 'Base Trujillo', 'CHARLA', 'Charla de seguridad previa al viaje', 1),
(2, 2, 'Verificación de EPP, documentación, estado del vehículo y condiciones de ruta.', 'CERRADO', '2026-10-07 07:30:00.000000', 'Base Pacasmayo', 'CHARLA', 'Charla de seguridad - ruta Pacasmayo Chiclayo', 2),
(3, 3, 'Revisión de riesgos asociados al transporte de maquinaria pesada y carga sobredimensionada.', 'CERRADO', '2026-10-06 19:30:00.000000', 'Base Trujillo', 'CHARLA', 'Charla para transporte de maquinaria', 3),
(4, 4, 'Capacitación periódica sobre conducción defensiva y gestión de fatiga.', 'CERRADO', '2026-10-05 10:00:00.000000', 'Base Trujillo', 'CAPACITACION', 'Manejo defensivo', NULL),
(5, NULL, 'Inspección preventiva de extintores, conos, tacos y botiquines en patio de operaciones.', 'REGISTRADO', '2026-10-06 09:00:00.000000', 'Patio de Operaciones Trujillo', 'INSPECCION', 'Inspección de implementos de seguridad', NULL);

-- =========================================================
-- 10. COMBUSTIBLE
-- =========================================================
DROP DATABASE IF EXISTS sitrac_combustible;
CREATE DATABASE sitrac_combustible CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sitrac_combustible;

CREATE TABLE abastecimientos_combustible (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cantidad_galones DECIMAL(12,3) NOT NULL,
  conductor_id BIGINT NOT NULL,
  costo_total DECIMAL(14,2) DEFAULT NULL,
  fecha_hora DATETIME(6) NOT NULL,
  kilometraje DECIMAL(12,2) NOT NULL,
  numero_comprobante VARCHAR(80) DEFAULT NULL,
  observacion VARCHAR(500) DEFAULT NULL,
  precio_unitario DECIMAL(12,4) DEFAULT NULL,
  programacion_id BIGINT NOT NULL,
  proveedor VARCHAR(150) DEFAULT NULL,
  tanque_origen VARCHAR(100) DEFAULT NULL,
  tipo_abastecimiento ENUM('INTERNO','TERCERO') NOT NULL,
  tracto_id BIGINT NOT NULL,
  viaje_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO abastecimientos_combustible (id, cantidad_galones, conductor_id, costo_total, fecha_hora, kilometraje, numero_comprobante, observacion, precio_unitario, programacion_id, proveedor, tanque_origen, tipo_abastecimiento, tracto_id, viaje_id) VALUES
(1, 40.000, 1, 620.00, '2026-10-04 08:10:00.000000', 125430.00, NULL, 'Abastecimiento previo a operación finalizada', 15.5000, 1, NULL, 'Tanque Base Trujillo 01', 'INTERNO', 1, 1),
(2, 35.000, 3, 542.50, '2026-10-06 20:30:00.000000', 210360.00, NULL, 'Abastecimiento durante viaje de maquinaria', 15.5000, 3, NULL, 'Tanque Base Trujillo 01', 'INTERNO', 3, 3);

SET FOREIGN_KEY_CHECKS = 1;

-- Resumen esperado:
-- Usuarios: 5
-- Clientes: 10
-- Pedidos: 10
-- Conductores: 10
-- Flota total: 10 unidades (5 tractos + 5 semirremolques)
-- Programaciones: 3
-- Viajes: 3
-- Mantenimientos: 2
-- Registros SOMMA: 5
-- Abastecimientos de combustible: 2
