package pe.edu.upeu.pago.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.pago.entity.EstadoPago;
import pe.edu.upeu.pago.entity.Pago;
import pe.edu.upeu.pago.event.OrdenCreadaEvento;
import pe.edu.upeu.pago.event.PagoValidadoEvento;
import pe.edu.upeu.pago.messaging.PagoEventosPublisher;
import pe.edu.upeu.pago.repository.PagoRepository;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private static final String PAGO_VALIDADO = "pago.validado";

    private final PagoRepository pagoRepository;
    private final PagoEventosPublisher publisher;

    @Value("${spring.application.name}")
    private String nombreServicio;

    @Override
    @Transactional
    public void procesar(OrdenCreadaEvento orden) {
        Pago pago = pagoRepository.save(Pago.builder()
                .ordenId(orden.getOrdenId())
                .monto(orden.getTotal())
                .metodoPago(orden.getMetodoPago())
                .estado(EstadoPago.VALIDADO)
                .fechaPago(LocalDateTime.now())
                .build());

        publisher.publicarTrasCommit(PagoValidadoEvento.builder()
                .tipoEvento(PAGO_VALIDADO)
                .ordenId(pago.getOrdenId())
                .monto(pago.getMonto())
                .estado(pago.getEstado().name())
                .origen(nombreServicio)
                .timestamp(Instant.now().toEpochMilli())
                .build());

        log.info("component=processor ordenId={} estado={} status=processed", pago.getOrdenId(), pago.getEstado());
    }
}
