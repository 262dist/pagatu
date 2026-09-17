package pe.edu.upeu.orden.service;

import pe.edu.upeu.orden.dto.*;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.entity.OrdenDetalle;
import pe.edu.upeu.orden.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;

    @Override
    @Transactional
    public OrdenResponse crear(OrdenRequest request) {
        Orden orden = Orden.builder()
                .idCliente(request.getIdCliente())
                .metodoPago(request.getMetodoPago())
                .build();

        List<OrdenDetalle> detalles = new ArrayList<>();

        for (DetalleOrdenRequest item : request.getDetalles()) {
            detalles.add(OrdenDetalle.builder()
                    .orden(orden)
                    .idProducto(item.getIdProducto())
                    .nombreProducto(null) // se completa en la Parte B, con Feign
                    .cantidad(item.getCantidad())
                    .precioUnitario(null) // se completa en la Parte B, con Feign
                    .build());
        }

        orden.setDetalles(detalles);

        Orden guardada = ordenRepository.save(orden);
        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenResponse findById(Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));
        return toResponse(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponse> listar() {
        return ordenRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private OrdenResponse toResponse(Orden orden) {
        List<DetalleOrdenResponse> detalles = orden.getDetalles().stream()
                .map(d -> DetalleOrdenResponse.builder()
                        .idProducto(d.getIdProducto())
                        .nombreProducto(d.getNombreProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getPrecioUnitario() == null
                                ? null
                                : d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                        .build())
                .collect(Collectors.toList());

        return OrdenResponse.builder()
                .id(orden.getId())
                .idCliente(orden.getIdCliente())
                .fechaCreacion(orden.getFechaCreacion())
                .estado(orden.getEstado().name())
                .total(orden.getTotal())
                .detalles(detalles)
                .build();
    }
}
