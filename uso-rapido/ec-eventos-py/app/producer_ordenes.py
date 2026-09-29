import json
import random
import time

from kafka import KafkaProducer


TOPIC_ORDENES = "orden-eventos"

producer = KafkaProducer(
    bootstrap_servers="kafka:9092",
    value_serializer=lambda value: json.dumps(value).encode("utf-8"),
)

print(json.dumps({
    "service": "ec-eventos-py",
    "component": "producer",
    "bootstrapServers": "kafka:9092",
    "status": "connected",
}))

while True:
    data = {
        "tipoEvento": "orden.creada",
        "ordenId": random.randint(1, 1000),
        "total": float(random.randint(50, 500)),
        "estado": "PENDIENTE",
        "origen": "python",
        "timestamp": int(time.time() * 1000),
    }

    metadata = producer.send(TOPIC_ORDENES, value=data).get(timeout=10)

    log = {
        "service": "ec-eventos-py",
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
