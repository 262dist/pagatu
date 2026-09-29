import json
import random
import time

from kafka import KafkaProducer


TOPIC_ORDENES = "orden-eventos"
METODOS_PAGO = ["YAPE_PLIN", "TARJETA", "PAGO_EFECTIVO"]

producer = KafkaProducer(
    bootstrap_servers="kafka:9092",
    value_serializer=lambda value: json.dumps(value).encode("utf-8"),
)

print(json.dumps({
    "service": "pagatu-eventos-py",
    "component": "producer",
    "bootstrapServers": "kafka:9092",
    "status": "connected",
}))

while True:
    data = {
        "tipoEvento": "orden.creada",
        "ordenId": random.randint(1, 1000),
        "idCliente": random.randint(1, 20),
        "total": float(random.randint(50, 500)),
        "metodoPago": random.choice(METODOS_PAGO),
        "origen": "python",
        "timestamp": int(time.time() * 1000),
    }

    metadata = producer.send(TOPIC_ORDENES, value=data).get(timeout=10)

    log = {
        "service": "pagatu-eventos-py",
        "component": "producer",
        "topic": metadata.topic,
        "partition": metadata.partition,
        "offset": metadata.offset,
        "eventType": data["tipoEvento"],
        "ordenId": data["ordenId"],
        "timestamp": data["timestamp"],
        "status": "published",
    }

    print(json.dumps(log))
    time.sleep(2)
