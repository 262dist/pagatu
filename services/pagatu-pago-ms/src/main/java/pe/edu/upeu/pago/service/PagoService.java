package pe.edu.upeu.pago.service;

import pe.edu.upeu.pago.dto.PagoResponse;
import pe.edu.upeu.pago.event.OrdenCreadaEvento;

import java.util.List;
import java.util.Optional;

public interface PagoService {
    void procesar(OrdenCreadaEvento orden);

    List<PagoResponse> listar();

    PagoResponse obtener(Long id);

    Optional<PagoResponse> buscarPorOrden(Long ordenId);
}
