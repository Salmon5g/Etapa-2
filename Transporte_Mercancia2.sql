CREATE DATABASE Transporte_mercancia;
USE Transporte_mercancia;


-- ============================================
-- TABLAS
-- ============================================
drop table personal;

CREATE TABLE personal (
    id_personal    INT PRIMARY KEY AUTO_INCREMENT,
    nombre         VARCHAR(100) NOT NULL,
    rut            VARCHAR(12)  UNIQUE NOT NULL,
    contrasena     VARCHAR(255) NOT NULL,
    rol            ENUM('jefe_flota', 'admin_mantenimiento', 'admin_jefe', 'conductor', 'tecnico_equipo') NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE camion (
    id_camion        INT PRIMARY KEY AUTO_INCREMENT,
    marca            VARCHAR(50)  NOT NULL,
	modelo ENUM('Normal', 'Liviano', 'Pesado') NOT NULL,
    anio             INT          NOT NULL,
    kilometraje_total INT         DEFAULT 0,
    id_conductor     INT          NOT NULL,           
    matricula        VARCHAR(20)  NOT NULL UNIQUE,
    fecha_creacion   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_camion_conductor
    FOREIGN KEY (id_conductor)
	REFERENCES personal(id_personal)              
);



CREATE TABLE mantenimiento (
    id_mantenimiento    INT          PRIMARY KEY AUTO_INCREMENT,
    id_camion           INT          NOT NULL,
    fechaEntrada        DATE         NOT NULL,
    estado              VARCHAR(20)  NOT NULL DEFAULT 'En progreso',
    fecha_fin           DATE         NULL,
    dias_activos        INT          NOT NULL DEFAULT 0,
    fecha_ultimo_inicio DATE         NULL,
    descripcion         VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_camion) REFERENCES camion(id_camion)
);

select * from mantenimiento;


CREATE TABLE equipo_oficina (
    id_equipo      INT          PRIMARY KEY AUTO_INCREMENT,
    tipo           ENUM('PC', 'Impresora', 'Scanner', 'Otro') NOT NULL,
    marca          VARCHAR(50)  NOT NULL,
    numero_serie   VARCHAR(50)  NOT NULL UNIQUE,
    estado         ENUM('Operativo', 'En mantención', 'Dado de baja') NOT NULL DEFAULT 'Operativo',
    fecha_registro TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE MANTENIMIENTO_EQUIPO;
SET FOREIGN_KEY_CHECKS = 1;
CREATE TABLE mantenimiento_equipo (
    id_mantenimiento    INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo           INT NOT NULL,
    tipo_mantenimiento  VARCHAR(20) NOT NULL,           -- 'Preventivo' o 'Correctivo'
    estado              VARCHAR(20) NOT NULL DEFAULT 'En progreso', -- 'En progreso', 'Postergado', 'Terminado'
    fecha_entrada       DATE NOT NULL,                  -- fecha de inicio del mantenimiento
    fecha_fin           DATE,                           -- se llena automáticamente al marcar 'Terminado'
    dias_activos        INT NOT NULL DEFAULT 0,         -- días acumulados en estado 'En progreso'
    fecha_ultimo_inicio DATE,                           -- última vez que pasó a 'En progreso'
    descripcion         VARCHAR(500),
    FOREIGN KEY (id_equipo) REFERENCES equipo_oficina(id_equipo)
);






-- ============================================================
-- RF-07: Módulo de Software
-- Relación N:M entre software y equipo_oficina
-- ============================================================

-- Tabla maestra de software
CREATE TABLE software (
    id_software      INT PRIMARY KEY AUTO_INCREMENT,
    nombre           VARCHAR(100) NOT NULL,
    version          VARCHAR(30)  NOT NULL,
    fabricante       VARCHAR(100) NOT NULL,
    fecha_registro   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla puente: un equipo puede tener varios software,
-- un software puede estar en varios equipos
CREATE TABLE software_equipo (
    id_software_equipo  INT PRIMARY KEY AUTO_INCREMENT,
    id_software         INT NOT NULL,
    id_equipo           INT NOT NULL,
    fecha_instalacion   DATE NOT NULL,
    estado              ENUM('Activo','Desinstalado') NOT NULL DEFAULT 'Activo',
    CONSTRAINT fk_se_software FOREIGN KEY (id_software) REFERENCES software(id_software)   ON DELETE CASCADE,
    CONSTRAINT fk_se_equipo   FOREIGN KEY (id_equipo)   REFERENCES equipo_oficina(id_equipo) ON DELETE CASCADE,
    CONSTRAINT uq_software_equipo UNIQUE (id_software, id_equipo)
);


-- ============================================================
-- RF-09: Control de inventario de piezas
-- ============================================================

-- Tabla maestra de piezas (inventario)
DROP TABLE detalle_pieza_mantenimiento;
CREATE TABLE pieza (
    id_pieza        INT PRIMARY KEY AUTO_INCREMENT,
    nombre          VARCHAR(100) NOT NULL UNIQUE,  -- ← agregar UNIQUE (flujo alterno: pieza duplicada)
    descripcion     VARCHAR(255),
    stock           INT NOT NULL DEFAULT 0,
    fecha_registro  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE detalle_pieza_mantenimiento (
    id_detalle        INT PRIMARY KEY AUTO_INCREMENT,
    id_mantenimiento  INT NOT NULL,
    id_pieza          INT NOT NULL,
    cantidad          INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_dpm_mantenimiento FOREIGN KEY (id_mantenimiento) 
        REFERENCES mantenimiento_equipo(id_mantenimiento) ON DELETE CASCADE,
    CONSTRAINT fk_dpm_pieza FOREIGN KEY (id_pieza) 
        REFERENCES pieza(id_pieza) ON DELETE RESTRICT,
    CONSTRAINT uq_mant_pieza UNIQUE (id_mantenimiento, id_pieza)  -- ← evita duplicar pieza en mismo mantenimiento
);

-- ============================================
-- ÍNDICES
-- ============================================

CREATE INDEX idx_camion_matricula ON camion (matricula);
CREATE INDEX idx_camion_marca     ON camion (marca);
CREATE INDEX idx_camion_modelo    ON camion (modelo);
CREATE INDEX idx_personal_nombre  ON personal (nombre);
CREATE INDEX idx_personal_rol     ON personal (rol);   
CREATE INDEX idx_equipo_tipo    ON equipo_oficina (tipo);
CREATE INDEX idx_equipo_estado  ON equipo_oficina (estado);
CREATE INDEX idx_equipo_marca   ON equipo_oficina (marca);

-- ============================================
-- DATOS INICIALES
-- ============================================


INSERT INTO personal (nombre, rut, contrasena, rol)
VALUES ('Admin Principal', '11111111-1', '1234', 'admin_jefe');


INSERT INTO personal (nombre, rut, contrasena, rol)
VALUES
    ('María González', '98765432-1', '1234', 'conductor'),
    ('Carlos Soto',    '11222333-4', '1234', 'conductor'),
    ('Ana Rojas',      '55666777-8', '1234', 'conductor');


INSERT INTO camion (marca, modelo, anio, kilometraje_total, id_conductor, matricula)
VALUES ('Volvo', 'Pesado', 2006, 0, 2, 'hfur87');

INSERT INTO equipo_oficina (tipo, marca, numero_serie, estado)
VALUES ('PC', 'Dell', 'SN-DELL-0001', 'Operativo');


INSERT INTO mantenimiento_equipo (id_equipo, tipo_mantenimiento, fecha_entrada, fecha_salida, descripcion)
VALUES (1, 'Preventivo', '2026-05-02', '2026-05-03', 'Limpieza interna, actualización de sistema operativo y revisión de componentes del PC Dell SN-DELL-0001');

SELECT * FROM equipo_oficina;
SELECT * FROM personal;
SELECT * FROM camion;







