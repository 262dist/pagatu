INSERT INTO categorias (nombre, descripcion) VALUES ('Electronica', 'Dispositivos y accesorios electronicos');
INSERT INTO categorias (nombre, descripcion) VALUES ('Hogar', 'Articulos para el hogar');
INSERT INTO categorias (nombre, descripcion) VALUES ('Ropa', 'Prendas de vestir');
INSERT INTO categorias (nombre, descripcion) VALUES ('Deportes', 'Articulos deportivos y fitness');

INSERT INTO productos (nombre, descripcion, precio, activo, stock, id_categoria)
VALUES ('Audifonos inalambricos', 'Audifonos bluetooth con estuche de carga', 89.90, true, 25, 1);

INSERT INTO productos (nombre, descripcion, precio, activo, stock, id_categoria)
VALUES ('Cargador USB-C 20W', 'Cargador rapido compatible con la mayoria de dispositivos', 39.90, true, 40, 1);

INSERT INTO productos (nombre, descripcion, precio, activo, stock, id_categoria)
VALUES ('Set de ollas antiadherentes', 'Set de 3 ollas con revestimiento antiadherente', 129.90, true, 15, 2);

INSERT INTO productos (nombre, descripcion, precio, activo, stock, id_categoria)
VALUES ('Polo basico algodon', 'Polo de algodon 100%, varias tallas', 29.90, true, 50, 3);

INSERT INTO productos (nombre, descripcion, precio, activo, stock, id_categoria)
VALUES ('Zapatillas running', 'Zapatillas para correr, amortiguacion media', 199.90, true, 20, 4);

INSERT INTO productos (nombre, descripcion, precio, activo, stock, id_categoria)
VALUES ('Mochila deportiva', 'Mochila resistente al agua, 20 litros', 79.90, true, 30, 4);

INSERT INTO productos (nombre, descripcion, precio, activo, stock, id_categoria)
VALUES ('Smartwatch fitness', 'Reloj inteligente con monitor de ritmo cardiaco', 249.90, true, 10, 1);
