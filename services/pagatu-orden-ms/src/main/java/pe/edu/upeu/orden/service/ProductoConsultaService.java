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

    @CircuitBreaker(name = "catalogo", fallbackMethod = "fallbackDescontarStock")
    public boolean descontarStock(Long idProducto, Integer cantidad) {
        productoClient.descontarStock(idProducto, cantidad);
        return true;
    }

    public boolean fallbackDescontarStock(Long idProducto, Integer cantidad, Throwable ex) {
        log.warn("[CATALOGO] No se pudo descontar stock de {}. Motivo: {}", idProducto, ex.getMessage());
        return false;
    }
    
    @CircuitBreaker(name = "catalogo", fallbackMethod = "fallbackRestaurarStock")
    public void restaurarStock(Long idProducto, Integer cantidad) {
        productoClient.restaurarStock(idProducto, cantidad);
    }

    public void fallbackRestaurarStock(Long idProducto, Integer cantidad, Throwable ex) {
        log.error("[CATALOGO] No se pudo restaurar stock de {} al compensar. Motivo: {} — requiere correccion manual",
                idProducto, ex.getMessage());
    }

}
