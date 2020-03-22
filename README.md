Integrantes del grupo:
----------------------

* Santiago Acevedo
* Mateo Fidabel
* Osvaldo Caje
* Sebastian Ferreira
* Monica Auler


Instalación
===========

Requerimientos
--------------

* Java 1.8 o superior *(cliente y servidor)*

* PostgreSQL 8.2 o superior *(servidor)*


Configuración de la base de datos
---------------------------------

El servidor de base de datos PostgreSQL debe ejecutarse localmente y debe tener las siguientes propiedades:

```
Host: localhost
Puerto: 5432
Nombre: sd
Usuario: postgres
Contraseña: postgres
```

Estos se pueden modifical mediante el archivo `servidor/src/servidor/BaseDatos.java`. Para crear la tabla correspondiente, el servidor debe ejecutar el archivo `crear_tabla.sql`.


Compilación y ejecución del servidor
------------------------------------


Compilación y ejecución de los clientes
---------------------------------------


API de servicios
================

Documentación de un API de servicios ofrecidos por el Servidor:

Especificar la forma de invocación y parámetros de cada servicio ofrecido por el servidor:

El API documentado debe ser leído e implementado por otros grupos de desarrolladores: