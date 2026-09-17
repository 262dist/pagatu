package pe.edu.upeu.orden.service;

import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import java.util.List;

public interface OrdenService {
    OrdenResponse crear(OrdenRequest request);
    OrdenResponse findById(Long id);
    List<OrdenResponse> listar();
}
