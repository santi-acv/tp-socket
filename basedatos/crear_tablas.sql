CREATE TYPE usuario AS (
    nombre  character varying (20),
    dir_ip  character varying (16),
    puerto  integer
);

CREATE TABLE conexiones (
    numero  serial      PRIMARY KEY,
    dir_ip  varchar(16) NOT NULL,
    puerto  integer     NOT NULL,
    inicio  timestamp   NOT NULL,
    fin     timestamp
);

CREATE TABLE cambios_de_nombre (
    numero  serial      PRIMARY KEY,
    tiempo  timestamp   NOT NULL,
    cliente usuario     NOT NULL
);

CREATE TABLE llamadas (
    numero  serial      PRIMARY KEY,
    origen  usuario     NOT NULL,
    destino usuario     NOT NULL,
    inicio  timestamp   NOT NULL,
    contestada  timestamp,
    terminada   timestamp
);
