package com.sistema.banco.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.sistema.banco.modelos.Transaccion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Consumidor {

    private static final String[] BANK_QUEUES = {"BANRURAL", "GYT", "BAC", "BI"};
    private static final String REJECT_QUEUE = "cola_rechazadas";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("fernando");
        factory.setPassword("123fernando");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        channel.queueDeclare(REJECT_QUEUE, true, false, false, null);

        for (String queue : BANK_QUEUES) {

            channel.queueDeclare(queue, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String payload = new String(delivery.getBody(), StandardCharsets.UTF_8);

                try {

                    Transaccion tx = mapper.readValue(payload, Transaccion.class);

                    String codigoUnico = UUID.randomUUID().toString().substring(0, 8);

                    tx.idTransaccion = "TX-" + tx.idTransaccion + "-" + codigoUnico + "-Fernando";
                    tx.carnet = "0905-24-3068";
                    tx.nombre = "Fernando Vega";
                    tx.correo = "fvegag1@.miumg.edu.gt";

                    String jsonModificado = mapper.writeValueAsString(tx);

                   
                    if (tx.monto > 4000.00) {

                       
                        if (postToApi(jsonModificado)) {

                            String registro = "{ \"idTransaccion\":\"" + tx.idTransaccion +
                                    "\", \"monto\":" + tx.monto +
                                    ", \"estado\":\"Aceptada\" }";

                            channel.basicPublish("", REJECT_QUEUE, null,
                                    registro.getBytes(StandardCharsets.UTF_8));

                            System.out.println("Transacción aceptada y registrada en cola: " + tx.idTransaccion);

                        } else {

                            System.err.println("Error enviando transacción al POST");

                        }

                    } else {

      
                        String registro = "{ \"idTransaccion\":\"" + tx.idTransaccion +
                                "\", \"monto\":" + tx.monto +
                                ", \"estado\":\"Rechazada\" }";

                        channel.basicPublish("", REJECT_QUEUE, null,
                                registro.getBytes(StandardCharsets.UTF_8));

                        System.out.println("Transacción rechazada registrada en cola: " + tx.idTransaccion);

                    }

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                } catch (Exception ex) {

                    System.err.println("Error procesando mensaje: " + ex.getMessage());

                }
            };

            channel.basicConsume(queue, false, deliverCallback, consumerTag -> {});
        }

        System.out.println("Consumidor esperando mensajes...");
    }

    private static boolean postToApi(String json) {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://7e0d9ogwzd.execute-api.us-east-1.amazonaws.com/default/guardarTransacciones"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Respuesta del servidor: " + response.statusCode() + " -> " + response.body());

            return response.statusCode() == 200 || response.statusCode() == 201;

        } catch (Exception e) {

            System.err.println("Error de conexión: " + e.getMessage());
            return false;

        }
    }
}