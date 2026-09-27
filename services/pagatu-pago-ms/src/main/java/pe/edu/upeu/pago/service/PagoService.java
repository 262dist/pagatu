package pe.edu.upeu.pago.service;

import pe.edu.upeu.pago.event.OrdenCreadaEvento;

public interface PagoService {
    void procesar(OrdenCreadaEvento orden);
}
