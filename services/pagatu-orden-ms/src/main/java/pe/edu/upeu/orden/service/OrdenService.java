package pe.edu.upeu.orden.service;

import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import java.util.List;

public interface OrdenService {
    OrdenResponse crear(OrdenRequest request, String idCliente);
    void marcarPagada(Long ordenId);
    OrdenResponse findById(Long id, String idCliente);
    List<OrdenResponse> listar(String idCliente);

    void compensar(Long ordenId);    
}
