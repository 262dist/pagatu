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
        Number claimIdCliente = jwt.getClaim("idCliente");
        if (claimIdCliente == null) {
            throw new IllegalArgumentException("Token sin idCliente: solo un CLIENTE autenticado puede crear ordenes");
        }
        return ordenService.crear(request, claimIdCliente.longValue());
    }

    @GetMapping
    public List<OrdenResponse> listar() {
        return ordenService.listar();
    }

    @GetMapping("/{id}")
    public OrdenResponse findById(@PathVariable Long id) {
        return ordenService.findById(id);
    }
}
