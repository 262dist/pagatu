INSERT INTO categorias (nombre, descripcion) VALUES ('Electronica', 'Dispositivos y accesorios electronicos');
INSERT INTO categorias (nombre, descripcion) VALUES ('Hogar', 'Articulos para el hogar');

INSERT INTO productos (nombre, descripcion, precio, activo, id_categoria)
VALUES ('Audifonos inalambricos', 'Audifonos bluetooth con estuche de carga', 89.90, true, 1);

INSERT INTO productos (nombre, descripcion, precio, activo, id_categoria)
VALUES ('Cargador USB-C 20W', 'Cargador rapido compatible con la mayoria de dispositivos', 39.90, true, 1);

INSERT INTO productos (nombre, descripcion, precio, activo, id_categoria)
VALUES ('Set de ollas antiadherentes', 'Set de 3 ollas con revestimiento antiadherente', 129.90, true, 2);
