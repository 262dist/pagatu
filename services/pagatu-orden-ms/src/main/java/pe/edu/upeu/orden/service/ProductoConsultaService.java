package pe.edu.upeu.orden.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import pe.edu.upeu.orden.client.ProductoClient;
import pe.edu.upeu.orden.dto.ProductoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoConsultaService {

    private final ProductoClient productoClient;

    @CircuitBreaker(name = "catalogo", fallbackMethod = "fallbackProducto")
    public ProductoDto consultarProducto(Long idProducto) {
        return productoClient.findById(idProducto);
    }

    public ProductoDto fallbackProducto(Long idProducto, Throwable ex) {
        log.warn("[CATALOGO] Fallback activado para idProducto {}. Motivo: {}", idProducto, ex.getMessage());
        return null;
    }
}
