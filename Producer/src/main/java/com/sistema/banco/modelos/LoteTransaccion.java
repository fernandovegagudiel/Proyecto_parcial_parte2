package com.sistema.banco.modelos;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LoteTransaccion {
    public String loteId;
    public String fechaGeneracion;
    public List<Transaccion> transacciones;
}