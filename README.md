# Proyecto RabbitMQ – Procesamiento de Transacciones Bancarias

## Descripción

Este proyecto implementa una arquitectura basada en mensajería utilizando RabbitMQ para procesar transacciones bancarias.

El sistema está dividido en dos aplicaciones principales:

* **Producer:** obtiene un lote de transacciones desde una API externa utilizando una petición GET y publica cada transacción en RabbitMQ.
* **Consumer:** escucha las colas de RabbitMQ, recibe las transacciones, agrega algunos datos adicionales y posteriormente envía la información a otra API mediante un POST.

El objetivo es demostrar cómo RabbitMQ permite desacoplar los sistemas y procesar información de forma asíncrona.

---

# Arquitectura del Sistema

El flujo general del sistema funciona de la siguiente manera:

1. El **Producer** realiza una petición **GET** a una API externa para obtener un lote de transacciones.
2. La respuesta JSON se convierte en objetos Java utilizando **ObjectMapper**.
3. Cada transacción se envía a RabbitMQ a una cola dependiendo del banco destino.
4. RabbitMQ almacena las transacciones en diferentes colas.
5. El **Consumer** escucha las colas correspondientes.
6. Cuando el Consumer recibe una transacción:

   * Convierte el JSON nuevamente a objeto Java.
   * Genera un identificador único para la transacción.
   * Agrega información del estudiante (carnet, nombre y correo).
7. Finalmente el Consumer envía la transacción modificada a una API mediante un **POST**.

---

# Componentes del Proyecto

## Producer

El Producer se encarga de:

* Realizar una petición **GET** a una API externa.
* Convertir la respuesta JSON a objetos Java.
* Crear colas en RabbitMQ según el banco destino.
* Publicar cada transacción en la cola correspondiente.

Las colas utilizadas son:

* BANRURAL
* GYT
* BAC
* BI

Cada transacción se envía a la cola que corresponde al banco destino.

---

## RabbitMQ

RabbitMQ funciona como un intermediario de mensajes.

Su función en el sistema es:

* Recibir los mensajes enviados por el Producer.
* Guardarlos en colas.
* Entregar los mensajes al Consumer cuando estén disponibles.

Esto permite que los sistemas funcionen de forma desacoplada.

---

## Consumer

El Consumer se conecta a RabbitMQ y escucha las colas de los diferentes bancos.

Cuando recibe una transacción realiza los siguientes pasos:

1. Convierte el mensaje JSON a un objeto `Transaccion`.
2. Genera un identificador único para la transacción.
3. Agrega información adicional como carnet, nombre y correo.
4. Convierte nuevamente el objeto a JSON.
5. Envía la información a una API externa mediante una petición **POST**.
6. Si la API responde correctamente (200 o 201), el mensaje se confirma en RabbitMQ.

---

# Flujo del Sistema

API de Transacciones
↓
Producer (GET)
↓
RabbitMQ (colas por banco)
↓
Consumer (procesamiento)
↓
POST a la API de almacenamiento

---

# Tecnologías Utilizadas

* Java
* RabbitMQ
* HTTP Client
* Jackson (ObjectMapper)
* API REST

---

# Ejecución del Proyecto

1. Iniciar RabbitMQ.
2. Ejecutar el **Consumer** para que empiece a escuchar las colas.
3. Ejecutar el **Producer**.
4. El Producer obtendrá las transacciones mediante GET y las enviará a RabbitMQ.
5. El Consumer recibirá las transacciones y las enviará a la API mediante POST.
