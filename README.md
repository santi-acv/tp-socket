https://docs.google.com/document/d/1rUhwpmuTmqkFFpGbaqO-_PJW0e6X2CpqukvbDY3jDgM/view


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

El servidor provee los siguientes servicios a través de su API:

* Lista de clientes
* Inicio de llamada
* Envío de mensajes
* Fin de la llamada

Estos se implementan mediante el intercambio de objetos JSON a través del protocolo TCP, aunque algunos servicios también están disponibles sobre UDP. A cada solicitud del cliente corresponde una respuesta del servidor indicando el estado del servicio.

Cada mensaje se representa por un objeto JSON y este debe abarcar exactamente una línea—en otras palabras, debe haber un fin de línea entre cada mensaje y una línea no puede terminar dentro de un mismo mensaje. El fin de una línea puede ser representado por un salto de línea (`\n`), un retorno de carro (`\r`), o un retorno de carro inmediatamente seguido de un salto de línea (`\r\n`).

Todos los mensajes provenientes de un cliente deben tener el atributo `tipo_operacion` especificando la operación siendo realizada, según la siguiente tabla. Ciertas operaciones necesitan otro atributo especificando detalles sobre la operación.

| Operación          | tipo_operacion | Protocolo | Atributo adicional |
| ------------------ | -------------- | --------- | ------------------ |
| Cerrar la conexion | -1             | TCP       |                    |
| Cambiar de nombre  |  0             | TCP       | nombre             |
| Mostrar clientes   |  1             | TCP / UDP |                    |
| Realizar llamada   |  2             | TCP       | destino            |
| Enviar mensaje     |  3             | TCP       | cuerpo             |
| Terminar llamada   |  4             | TCP / UDP |                    |
| Contestar llamada  |  5             | TCP       | origen (UDP)       |

Las respuestas del servidor siempre poseen los atributos `estado` y `mensaje` describiendo el resultado de la operación. Si la operación fue exitosa estos contienen el número 0 y la cadena `ok`; de caso contrario contienen un código de error y una descripción del error.

| estado | Significado                                     |
| ------ | ----------------------------------------------- |
| -2     | El mensaje no representa un objeto JSON válido. |
| -1     | No existe ninguna operación con ese código.     |
|  0     | Operación exitosa, no ocurrió ningún error.     |
|  1     | Falta un atributo adicional en el mensaje.      |
| >1     | Depende de la operación solicitada.             |

A continuación se detalla cada operación y se incluye un ejemplo del mensaje que debe enviar el cliente.


Lista de clientes
-----------------


Inicio de llamada
-----------------


Envío de mensajes
-----------------


Fin de la llamada
-----------------


