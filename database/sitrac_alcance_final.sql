-- SITRAC - Ajustes posteriores al dataset histórico de exposición
-- Este script se ejecuta después de sitrac_demo_exposicion.sql.
-- Conserva los datos útiles y alinea la base con el alcance final del informe.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Roles finales del sistema
USE sitrac_auth;
DELETE FROM usuarios WHERE rol IN ('SOMMA', 'MANTENIMIENTO');
ALTER TABLE usuarios
  MODIFY COLUMN rol ENUM('ADMIN','OPERADOR','COMBUSTIBLE','SUPERVISOR') NOT NULL;

-- El conductor recibe notificaciones de abastecimiento por correo.
USE sitrac_conductores;
ALTER TABLE conductores ADD COLUMN email VARCHAR(120) NULL AFTER telefono;
UPDATE conductores SET email = CONCAT('conductor', id, '@sitrac.demo') WHERE email IS NULL;

-- Los módulos SOMMA y Mantenimiento quedaron fuera del alcance final.
DROP DATABASE IF EXISTS sitrac_somma;
DROP DATABASE IF EXISTS sitrac_mantenimiento;

SET FOREIGN_KEY_CHECKS = 1;
