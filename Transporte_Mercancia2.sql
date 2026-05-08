CREATE DATABASE Transporte_mercancia;
USE Transporte_mercancia;


-- ============================================
-- TABLAS
-- ============================================


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
    id_mantenimiento INT  PRIMARY KEY AUTO_INCREMENT,
    id_camion        INT  NOT NULL,
    fechaEntrada     DATE NOT NULL,
    fechaSalida      DATE NOT NULL,
    descripcion      VARCHAR(255) NOT NULL,
    CONSTRAINT chk_fecha_salida CHECK (fechaSalida > fechaEntrada),
    FOREIGN KEY (id_camion) REFERENCES camion(id_camion)
);


CREATE TABLE equipo_oficina (
    id_equipo      INT          PRIMARY KEY AUTO_INCREMENT,
    tipo           ENUM('PC', 'Impresora', 'Scanner', 'Otro') NOT NULL,
    marca          VARCHAR(50)  NOT NULL,
    numero_serie   VARCHAR(50)  NOT NULL UNIQUE,
    estado         ENUM('Operativo', 'En mantención', 'Dado de baja') NOT NULL DEFAULT 'Operativo',
    fecha_registro TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mantenimiento_equipo (
    id_mantenimiento   INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo          INT NOT NULL,
    tipo_mantenimiento VARCHAR(20) NOT NULL,          -- 'Preventivo' o 'Correctivo'
    fecha_entrada      DATE NOT NULL,
    fecha_salida       DATE NOT NULL,
    descripcion        VARCHAR(500),
    FOREIGN KEY (id_equipo) REFERENCES equipo_oficina(id_equipo)
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







