package pe.edu.upeu.orden.service;

import pe.edu.upeu.orden.dto.OrdenDetalleRequest;
import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.entity.OrdenDetalle;
import pe.edu.upeu.orden.exception.ResourceNotFoundException;
import pe.edu.upeu.orden.mapper.OrdenMapper;
import pe.edu.upeu.orden.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String TIPO_COMPROBANTE_DEFAULT = "BOLETA_SIMPLE";
    private static final String MOMENTO_PAGO_DEFAULT = "ADELANTADO";

    private final OrdenRepository ordenRepository;
    private final OrdenMapper ordenMapper;

    public List<OrdenResponse> listar() {
        return ordenRepository.findAllConDetalles().stream()
                .map(ordenMapper::toResponse)
                .toList();
    }

    public OrdenResponse obtener(Long id) {
        return ordenMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public OrdenResponse crear(OrdenRequest request) {
        Orden orden = ordenMapper.toEntity(request);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado(ESTADO_PENDIENTE);
        if (orden.getTipoComprobante() == null) {
            orden.setTipoComprobante(TIPO_COMPROBANTE_DEFAULT);
        }
        if (orden.getMomentoPago() == null) {
            orden.setMomentoPago(MOMENTO_PAGO_DEFAULT);
        }

        for (OrdenDetalleRequest itemRequest : request.getItems()) {
            OrdenDetalle detalle = ordenMapper.toEntity(itemRequest);
            detalle.setOrden(orden);
            orden.getDetalles().add(detalle);
        }
        orden.setTotal(calcularTotal(orden.getDetalles()));

        return ordenMapper.toResponse(ordenRepository.save(orden));
    }

    public OrdenResponse actualizarEstado(Long id, String estado) {
        Orden orden = buscarOFallar(id);
        orden.setEstado(estado);
        return ordenMapper.toResponse(ordenRepository.save(orden));
    }

    private BigDecimal calcularTotal(List<OrdenDetalle> detalles) {
        return detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Orden buscarOFallar(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));
    }
}
