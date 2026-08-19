INSERT INTO ordenes (id_cliente, fecha_creacion, estado, tipo_comprobante, metodo_pago, momento_pago, total)
VALUES (1, now(), 'CONFIRMADA', 'BOLETA_CON_DNI', 'TARJETA', 'ADELANTADO', 129.80);

INSERT INTO orden_detalles (id_orden, id_producto, cantidad, precio_unitario)
VALUES (1, 1, 1, 89.90);
INSERT INTO orden_detalles (id_orden, id_producto, cantidad, precio_unitario)
VALUES (1, 2, 1, 39.90);

INSERT INTO ordenes (id_cliente, fecha_creacion, estado, tipo_comprobante, metodo_pago, momento_pago, total)
VALUES (2, now(), 'PENDIENTE', 'FACTURA', 'TRANSFERENCIA', 'ADELANTADO', 399.80);

INSERT INTO orden_detalles (id_orden, id_producto, cantidad, precio_unitario)
VALUES (2, 5, 2, 199.90);

INSERT INTO ordenes (id_cliente, fecha_creacion, estado, tipo_comprobante, metodo_pago, momento_pago, total)
VALUES (3, now(), 'PENDIENTE', 'FACTURA', 'PAGO_EFECTIVO', 'CONTRA_ENTREGA', 339.60);

INSERT INTO orden_detalles (id_orden, id_producto, cantidad, precio_unitario)
VALUES (3, 4, 3, 29.90);
INSERT INTO orden_detalles (id_orden, id_producto, cantidad, precio_unitario)
VALUES (3, 7, 1, 249.90);
