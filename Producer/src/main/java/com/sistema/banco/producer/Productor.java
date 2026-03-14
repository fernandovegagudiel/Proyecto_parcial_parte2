package com.sistema.banco.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.sistema.banco.modelos.LoteTransaccion;
import com.sistema.banco.modelos.Transaccion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Productor {

    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("127.0.0.1");
        factory.setUsername("fernando");
        factory.setPassword("123fernando");

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            System.out.println("Producer iniciado. Conectado a RabbitMQ.");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://hly784ig9d.execute-api.us-east-1.amazonaws.com/default/transacciones"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            LoteTransaccion lote = mapper.readValue(response.body(), LoteTransaccion.class);

            for (Transaccion tx : lote.transacciones) {

                String colaDestino;
                String estado;

                // VALIDACIÓN DEL MONTO
                if (tx.monto > 4000) {

                    colaDestino = tx.bancoDestino.toUpperCase().trim();
                    estado = "ACEPTADA";

                } else {

                    colaDestino = "cola_rechazadas";
                    estado = "RECHAZADA";
                }

                // CREAR COLA SI NO EXISTE
                channel.queueDeclare(colaDestino, true, false, false, null);

                String payload = mapper.writeValueAsString(tx);

                // ENVIAR MENSAJE
                channel.basicPublish(
                        "",
                        colaDestino,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        payload.getBytes()
                );

                // REGISTRO EN CONSOLA
                System.out.println(
                        "ID: " + tx.idTransaccion +
                        " | Monto: Q" + tx.monto +
                        " | Estado: " + estado +
                        " | Cola destino: " + colaDestino
                );
            }

            System.out.println("Proceso terminado. Lote enviado.");

        } catch (Exception ex) {

            System.err.println("Error: " + ex.getMessage());
        }
    }
}