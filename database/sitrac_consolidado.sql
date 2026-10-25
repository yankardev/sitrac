-- SITRAC - Consolidación de esquemas para la arquitectura reducida.
-- Se ejecuta después de 01-sitrac_demo_exposicion.sql y copia sus datos de demostración.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- COMERCIAL: clientes + pedidos
-- =========================================================
DROP DATABASE IF EXISTS sitrac_comercial;
CREATE DATABASE sitrac_comercial CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE sitrac_comercial.clientes LIKE sitrac_clientes.clientes;
INSERT INTO sitrac_comercial.clientes SELECT * FROM sitrac_clientes.clientes;

CREATE TABLE sitrac_comercial.pedidos LIKE sitrac_pedidos.pedidos;
INSERT INTO sitrac_comercial.pedidos SELECT * FROM sitrac_pedidos.pedidos;

-- =========================================================
-- RECURSOS: conductores + flota + mantenimiento
-- =========================================================
DROP DATABASE IF EXISTS sitrac_recursos;
CREATE DATABASE sitrac_recursos CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE sitrac_recursos.conductores LIKE sitrac_conductores.conductores;
INSERT INTO sitrac_recursos.conductores SELECT * FROM sitrac_conductores.conductores;

CREATE TABLE sitrac_recursos.tractos LIKE sitrac_flota.tractos;
INSERT INTO sitrac_recursos.tractos SELECT * FROM sitrac_flota.tractos;

CREATE TABLE sitrac_recursos.semirremolques LIKE sitrac_flota.semirremolques;
INSERT INTO sitrac_recursos.semirremolques SELECT * FROM sitrac_flota.semirremolques;

CREATE TABLE sitrac_recursos.mantenimientos LIKE sitrac_mantenimiento.mantenimientos;
INSERT INTO sitrac_recursos.mantenimientos SELECT * FROM sitrac_mantenimiento.mantenimientos;

-- =========================================================
-- OPERACIONES: programación + viajes + combustible
-- =========================================================
DROP DATABASE IF EXISTS sitrac_operaciones;
CREATE DATABASE sitrac_operaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE sitrac_operaciones.programaciones LIKE sitrac_programaciones.programaciones;
INSERT INTO sitrac_operaciones.programaciones SELECT * FROM sitrac_programaciones.programaciones;

CREATE TABLE sitrac_operaciones.viajes LIKE sitrac_viajes.viajes;
INSERT INTO sitrac_operaciones.viajes SELECT * FROM sitrac_viajes.viajes;

CREATE TABLE sitrac_operaciones.abastecimientos_combustible LIKE sitrac_combustible.abastecimientos_combustible;
INSERT INTO sitrac_operaciones.abastecimientos_combustible SELECT * FROM sitrac_combustible.abastecimientos_combustible;

-- =========================================================
-- PREVENCIÓN: SOMMA
-- =========================================================
DROP DATABASE IF EXISTS sitrac_prevencion;
CREATE DATABASE sitrac_prevencion CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE sitrac_prevencion.registros_somma LIKE sitrac_somma.registros_somma;
INSERT INTO sitrac_prevencion.registros_somma SELECT * FROM sitrac_somma.registros_somma;

SET FOREIGN_KEY_CHECKS = 1;
