package pe.edu.upeu.orden.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pe.edu.upeu.orden.event.OrdenCreadaEvento;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrdenEventosPublisher {

    private final KafkaTemplate<String, OrdenCreadaEvento> kafkaTemplate;

    @Value("${app.kafka.topic.ordenes}")
    private String topicOrdenes;

    public void publicarTrasCommit(OrdenCreadaEvento evento) {
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

    private void enviar(OrdenCreadaEvento evento) {
        kafkaTemplate.send(topicOrdenes, String.valueOf(evento.getOrdenId()), evento)
                .whenComplete((resultado, ex) -> {
                    if (ex != null) {
                        log.error("component=producer topic={} eventType={} ordenId={} status=error error=\"{}\"",
                                topicOrdenes, evento.getTipoEvento(), evento.getOrdenId(), ex.getMessage());
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
