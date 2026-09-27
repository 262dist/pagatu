package pe.edu.upeu.pago.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pe.edu.upeu.pago.event.OrdenCreadaEvento;
import pe.edu.upeu.pago.service.PagoService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrdenEventosListener {

    private static final String ORDEN_CREADA = "orden.creada";

    private final PagoService pagoService;

    @KafkaListener(topics = "${app.kafka.topic.ordenes}")
    public void alRecibirOrden(OrdenCreadaEvento evento) {
        if (!ORDEN_CREADA.equals(evento.getTipoEvento())) {
            log.warn("component=consumer eventType={} status=ignored", evento.getTipoEvento());
            return;
        }
        log.info("component=consumer eventType={} ordenId={} status=consumed",
                evento.getTipoEvento(), evento.getOrdenId());
        pagoService.procesar(evento);
    }
}
