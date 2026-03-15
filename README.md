# Proyecto RabbitMQ – Procesamiento de Transacciones
## video 
https://drive.google.com/file/d/17n7RcaqbW2rci_ktHmfFos4UfvwOVid6/view?usp=drive_link
## Descripción

Este proyecto muestra cómo usar **RabbitMQ** para enviar y procesar transacciones entre dos aplicaciones: un **Producer** y un **Consumer**.

El **Producer** obtiene un lote de transacciones desde una API usando un **GET**. Luego cada transacción se envía a RabbitMQ.

El **Consumer** escucha las colas de RabbitMQ, recibe las transacciones, les agrega algunos datos y finalmente las envía a otra API mediante un **POST**.

La idea es mostrar cómo RabbitMQ permite que los sistemas se comuniquen usando colas de mensajes.

---

## Cómo funciona el sistema

El flujo es el siguiente:

1. El **Producer** hace una petición **GET** para obtener un lote de transacciones.
2. Cada transacción se envía a RabbitMQ.
3. RabbitMQ guarda las transacciones en colas según el banco destino.
4. El **Consumer** escucha esas colas.
5. Cuando llega una transacción, el Consumer la procesa.
6. Luego envía la información a una API mediante un **POST**.

---

## Colas utilizadas

Las colas se crean dependiendo del banco destino de la transacción:

* BANRURAL
* GYT
* BAC
* BI

Cada transacción se envía a la cola que corresponde a su banco.

---

## Tecnologías usadas

* Java
* RabbitMQ
* Docker
* HTTP Client
* Jackson (para manejar JSON)

---

## Cómo ejecutar el proyecto

1. Iniciar RabbitMQ usando Docker.
2. Abrir el panel de RabbitMQ en:
   `http://localhost:15672`
3. Ejecutar el **Consumer**.
4. Ejecutar el **Producer**.
5. Ver cómo las transacciones pasan por las colas y luego se envían a la API.

---

