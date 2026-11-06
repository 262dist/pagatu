package pe.edu.upeu.orden.controller;

import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public OrdenResponse crear(@Valid @RequestBody OrdenRequest request,
                            @AuthenticationPrincipal Jwt jwt) {
    String idCliente = jwt.getSubject();
    return ordenService.crear(request, idCliente);
}


    @GetMapping
    public List<OrdenResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return ordenService.listar(jwt.getSubject());
    }

    @GetMapping("/{id}")
    public OrdenResponse findById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ordenService.findById(id, jwt.getSubject());
    }
}
