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

El servidor utiliza una base de datos PostgreSQL para almacenar historiales de conexiones y llamadas. Esta debe ejecutarse localmente y debe tener las siguientes propiedades:

```
Host: localhost
Puerto: 5432
Nombre: tp-socket
Usuario: postgres
Contraseña: postgres
```

En caso de que se utilice una base de datos remota, estas propeidades se pueden modificar en el archivo `servidor/src/servidor/BaseDatos.java`. Para crear las tablas correspondientes, la base de datos debe ejecutar el archivo `basedatos/crear_tablas.sql`. Para vaciar o eliminar las tablas de la base de datos se pueden utilizar los archivos `basedatos/vaciar_historial.sql` o `basedatos/eliminar_tablas.sql` respectivamente.


Compilación y ejecución del servidor
------------------------------------

1. 	Importar en el software Eclipse el proyecto Maven llamado “servidor” que se encuentra dentro de la carpeta “tp-socket”.
2.	El servidor es del tipo Multi-Servidor.
3.	Con el botón “Run MainServidor,java” compilar y ejecutar el archivo java llamado “MainServidor” una sola vez.
4.	Si ejecuta más de una vez podría encontrar el error: no se puede abrir el puerto 4444, porque se utiliza actualmente ese puerto para el servidor activo.


Compilación y ejecución de los clientes
---------------------------------------
1.	Importar en el software Eclipse el proyecto Maven llamado “cliente” que se encuentra dentro de la carpeta “tp-socket”.
2.	Con el botón “Run AppCliente.java” compilar y ejecutar el archivo java llamado “AppCliente” 2 o más veces para poder realizar una o más llamadas entre 2 clientes conectados que se encuentren disponibles.
3.	Se habilita una interfaz donde debe proceder a utilizar los servicios disponibles para los clientes.


API de servicios
================

El servidor provee los siguientes servicios a través de su API:

* Lista de clientes
* Inicio de llamada
* Envío de mensajes
* Fin de la llamada

Estos se implementan mediante el intercambio de objetos JSON a través de una conexión TCP, aunque algunos servicios también están disponibles sobre UDP. A cada solicitud del cliente corresponde una respuesta del servidor indicando el estado del servicio. Para conectarse al servidor, un cliente debe ser capaz de establecer una conexión y contestar las llamadas entrantes.

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

Los mensajes provenientes del servidor siempre también poseen el atributo `tipo_operacion` y además incluyen los atributos `estado` (entero) y `mensaje` (cadena) describiendo el resultado de la operación. El número 0 indica una transación exitosa, el número 1 representa una llamada entrante, y los otros números representan errores. En cada caso se incluye un descripción del estado. La siguiente tabla resume los diferentes tipos de mensaje, los cuales están definidos en el archivo `servidor/src/servidor/CodigoEstado.java`.

| estado  | Significado                                  |
| ------- | -------------------------------------------- |
| < -1    | El mensaje no está correctamente formateado. |
|   -1    | No existe una operación con ese código.      |
|    0    | Operación exitosa, no ocurrió ningún error.  |
|    1    | El usuario está recibiendo una llamada.      |
|    2    | La llamada ha sido cortada.                  |
|    3    | La llamada no ha sido contestada.            |
|    4    | La llamada fue cortada con un datagrama UDP. |
|  > 4    | La operación no se ha podido realizar.       |

A continuación se detalla cada operación y se incluye un ejemplo del mensaje que debe enviar el cliente. Estos mensajes deben ser enviados en una línea pero aquí se exponen en varias líneas para facilitar su lectura.


Cambiar de nombre
-----------------

```
{
    "tipo_operacion":0,
    "nombre":"cliente 1"
}
```

Solicita al servidor un cambio de nombre. Sin realizar esta operación, el usuario es asignado por defecto una cadena que representa la dirección del socket TCP desde el cual se está conectando. El mensaje debe contener el atributo `nombre` indicando el nombre a ser utilizado y no debe estar en uso por otro usuario. El cliente no debe estar en una llamada durante esta operación. El servidor puede enviar las siguientes respuestas:

| estado | Significado                          |
| ------ | ------------------------------------ |
|    0   | El nombre fue cambiado con éxito.    |
|    5   | El cliente está en una llamada.      |
|    9   | Ya existe un usuario con ese nombre. |
|   -4   | Falta el campo `nombre`.             |


Listar clientes
---------------

```
{
    "tipo_operacion":1
}
```

Solicita una lista de usuarios conectados al servidor. El servidor debe responder a la solicitud con un mensaje que posea el atributo `lista_clientes`, el cual consiste en un arreglo JSON donde cada objeto representa un cliente. Estos objetos deben poseer los siguientes atributos: `nombre`, una cadena por la cual se identifica cada cliente; `disponible`, un booleano indicando si el cliente puede recibir llamadas; `ip` y `puerto`, indicando la dirección del socket TCP del cliente. Una posible respuesta del servidor es la siguiente:

```
{
    "tipo_operacion":1,
    "estado":0,
    "mensaje":"ok",
    "lista_clientes":[
        {
            "nombre": "cliente 1",
            "ip":"/192.168.100.2",
            "puerto":56184,
            "disponible":true
        },
        {
            "nombre": "cliente 2",
            "ip":"/192.168.100.3",
            "puerto":43497,
            "disponible":true
        }
    ]
}
```


Iniciar llamada
---------------

```
{
    "tipo_operacion":2,
    "destino":"cliente 2"
}
```

Realiza una llamada a otro cliente. Este  debe aceptar o rechazar la llamada, y si no lo hace esta se cancelará automáticamente después de 10 segundos. Ninguno de los clientes se debe encontrar en una llamada para realizar esta operación. El mensaje iniciando la llamada debe tener el atributo `destino` indicando el destino y el mensaje que llegará al otro cliente tendrá el atributo `origen` indicando el origen con el `estado` igual a 1. A continuación se muestra un ejemplo:

```
{
    "tipo_operacion":2,
    "estado":1,
    "mensaje":"ok",
    "origen":"cliente 1"
}
```

El servidor puede enviar las siguientes respuestas al emisor de la llamada. Si el emisor corta la llamada antes de que esta sea contestada o si esta no es contestada a tiempo, el receptor recibirá el mismo código.

| estado | Significado                         |
| ------ | ----------------------------------- |
|    0   | El cliente contestó la llamada.     |
|    2   | El cliente rechazó la llamada.      |
|    3   | El cliente no contestó la llamada.  |
|    5   | El cliente de origen está ocupado.  |
|    6   | El cliente de destino está ocupado. |
|    8   | No existe el usuario seleccionado.  |
|   -7   | Falta el campo `destino`.           |


Contestar llamada
----------------

```
{
    "tipo_operacion":5
}
```

Contesta la llamada entrante, si esta existe. Esta llamada puede ser rechazada con la operación terminar llamada.

| estado | Significado                 |
| ------ | --------------------------- |
|    0   | Una llamada fue contestada. |
|    7   | No existe ninguna llamada.  |


Terminar llamada
----------------

```
{
    "tipo_operacion":4
}
```

Corta la llamada en la que se encuentra el cliente, ya sea una entrante o una en transcurso.

| estado | Significado                |
| ------ | -------------------------- |
|    0   | La llamada fue cortada.    |
|    7   | No existe ninguna llamada. |

El otro cliente recibe un mensaje indicando que la llamada fue cortada.

| estado | Significado                                 |
| ------ | ------------------------------------------- |
|    2   | La llamada fue cortada por el otro cliente. |

-----

Esta operación puede realizarse mediante un datagrama UDP, pero debe contener el campo `origen` indicando el nombre del cliente y la IP debe ser la misma que la del socket TCP.

```
{
    "tipo_operacion":4,
    "origen":"cliente 1"
}
```

El resultado de la operación es enviado al mismo socket que envió el datagrama, y si tiene éxito el socket TCP recibe un mensaje con `estado` igual a 4. El otro cliente es notificado de manera normal.

| estado | Significado                         |
| ------ | ----------------------------------- |
|    0   | El datagrama ha cortado la llamada. |
|    4   | Un datagrama ha cortado la llamada. |
|    8   | No existe un usario con ese nombre. |
|   10   | La IP del datagrama es inválida.    |


Enviar mensaje
--------------

```
{
    "tipo_operacion":4,
    "cuerpo":"hola, mundo!"
}
```

Envía un mensaje a otro cliente. El cuerpo del mensaje debe estar contenido en el campo `cuerpo`, y ambos usuarios deben encontrarse en una llamada.

| estado | Significado                         |
| ------ | ----------------------------------- |
|    0   | El servidor ha recibido el mensaje. |
|    7   | No se encuentra en ninguna llamada. |
|   -5   | Falta el campo `cuerpo`.            |

Especificar la forma de invocación y parámetros de cada servicio ofrecido por el servidor
==========================================================================================

Forma de invocación de los servicios disponibles en la aplicación:

I.	  Cambiar de nombre: hacer click en el botón “Cambiar nombre” de la aplicación y cargar el nuevo nombre del cliente en la ventana que se presenta.
II.	  Mostrar clientes: en la aplicación ya se despliega la lista de los clientes conectados, muestra de cada cliente su Nombre, IP, Puerto y su disponibilidad para comenzar una llamada. Para actualizar la lista en caso de que existan nuevas conexiones o se eliminen conexiones hacer click en el botón “Actualizar”
III.  Realizar llamada: seleccionar a un cliente de la lista presentada y hacer click en el botón “Llamar” de la aplicación. Se desplegará un cuadro donde muestra el nombre del cliente a quien se esta llamando.
IV.	  Enviar mensaje: escribir el cuerpo del mensaje en el espacio en blanco en la sección inferior de la ventana de la llamada y hacer click en el botón “Enviar” . 
V.	  Terminar llamada: click en el botón “Cortar” dentro de la ventana de llamada. Al otro cliente se le desplegará un cuadro donde se especifica que el usuario con quien hablaba ha cortado la llamada.
VI.	  Contestar llamada: click en el botón “Contestar” en el cuadro que aparece cuando el cliente tiene una llamada entrante.
VII.  Cerrar la conexión: click en el botón X de la esquina derecha superior de la ventana de la aplicación.

