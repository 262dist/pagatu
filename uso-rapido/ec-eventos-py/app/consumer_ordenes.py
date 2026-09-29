import json
import os
import time

from kafka import KafkaConsumer


TOPIC_ORDENES = os.getenv("KAFKA_TOPIC_ORDENES", "orden-eventos")
GROUP_ID = os.getenv("KAFKA_GROUP_ID", "ec-eventos-py-group")
BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")


def deserialize_message(value):
    text = value.decode("utf-8")
    try:
        return {
            "payload": json.loads(text),
            "raw": text,
            "isJson": True,
            "decodeError": None,
        }
    except json.JSONDecodeError as ex:
        return {
            "payload": None,
            "raw": text,
            "isJson": False,
            "decodeError": str(ex),
        }


consumer = KafkaConsumer(
    TOPIC_ORDENES,
    bootstrap_servers=BOOTSTRAP_SERVERS,
    auto_offset_reset="earliest",
    enable_auto_commit=True,
    group_id=GROUP_ID,
    value_deserializer=deserialize_message,
)

print(json.dumps({
    "service": "ec-eventos-py",
    "component": "consumer",
    "topic": TOPIC_ORDENES,
    "groupId": GROUP_ID,
    "bootstrapServers": BOOTSTRAP_SERVERS,
    "status": "listening",
}))

for msg in consumer:
    decoded = msg.value
    event = decoded["payload"] if decoded["isJson"] else {}
    timestamp = event.get("timestamp")
    processed_at = int(time.time() * 1000)
    latency_ms = processed_at - timestamp if timestamp is not None else None
    is_valid = (
        decoded["isJson"] and
        event.get("tipoEvento") is not None
        and event.get("ordenId") is not None
        and event.get("total") is not None
        and timestamp is not None
    )

    log = {
        "service": "ec-eventos-py",
        "component": "consumer",
        "topic": msg.topic,
        "partition": msg.partition,
        "offset": msg.offset,
        "groupId": GROUP_ID,
        "eventType": event.get("tipoEvento"),
        "ordenId": event.get("ordenId"),
        "total": event.get("total"),
        "estado": event.get("estado"),
        "origen": event.get("origen"),
        "timestamp": timestamp,
        "isValid": is_valid,
        "processedAt": processed_at,
        "latencyMs": latency_ms,
        "payload": event,
        "rawPayload": decoded["raw"],
        "isJson": decoded["isJson"],
        "decodeError": decoded["decodeError"],
        "status": "consumed" if is_valid else "invalid",
    }

    print(json.dumps(log))
