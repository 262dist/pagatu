package pe.edu.upeu.pago.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pe.edu.upeu.pago.event.PagoValidadoEvento;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagoEventosPublisher {

    private final KafkaTemplate<String, PagoValidadoEvento> kafkaTemplate;

    @Value("${app.kafka.topic.pagos}")
    private String topicPagos;

    public void publicarTrasCommit(PagoValidadoEvento evento) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviar(evento);
                }
            });
        } else {
            enviar(evento);
        }
    }

    private void enviar(PagoValidadoEvento evento) {
        kafkaTemplate.send(topicPagos, String.valueOf(evento.getOrdenId()), evento)
                .whenComplete((resultado, ex) -> {
                    if (ex != null) {
                        log.error("component=producer topic={} eventType={} ordenId={} status=error error=\"{}\"",
                                topicPagos, evento.getTipoEvento(), evento.getOrdenId(), ex.getMessage());
                        return;
                    }
                    log.info("component=producer topic={} partition={} offset={} eventType={} ordenId={} status=published",
                            resultado.getRecordMetadata().topic(),
                            resultado.getRecordMetadata().partition(),
                            resultado.getRecordMetadata().offset(),
                            evento.getTipoEvento(), evento.getOrdenId());
                });
    }
}
