package pe.edu.upeu.orden.client;

import pe.edu.upeu.orden.dto.ProductoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pagatu-catalogo-ms")
public interface ProductoClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoDto findById(@PathVariable("id") Long id);
}
