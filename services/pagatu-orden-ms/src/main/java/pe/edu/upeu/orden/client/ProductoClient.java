package pe.edu.upeu.orden.client;

import pe.edu.upeu.orden.dto.ProductoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pagatu-catalogo-ms")
public interface ProductoClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoDto findById(@PathVariable("id") Long id);

    @PatchMapping("/api/v1/productos/{id}/descontar-stock")
    void descontarStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);

    @PatchMapping("/api/v1/productos/{id}/restaurar-stock")
    void restaurarStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);
        
}
