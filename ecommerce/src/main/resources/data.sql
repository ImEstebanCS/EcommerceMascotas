-- Datos de prueba que se cargan al arrancar (H2 en memoria).
-- Vendedores: a1a1...0001 (Tienda Patitas) y a1a1...0002 (Michi Store).
-- Compradores: c1c1...0001, c1c1...0002 y c1c1...0003.
-- La publicacion 09 esta RETIRADA (eliminacion logica: la fila no se borra) y la 03 esta AGOTADA.

INSERT INTO publicaciones (id, vendedor_id, nombre_producto, descripcion, especie_destino, precio_monto, precio_moneda, stock, categoria, estado) VALUES
('b0b0b0b0-0000-4000-8000-000000000001', 'a1a1a1a1-0000-4000-8000-000000000001', 'Collar reflectivo', 'Collar de nylon con banda reflectiva para paseos de noche, talla M', 'PERRO', 20000, 'COP', 15, 'ACCESORIO', 'PUBLICADA'),
('b0b0b0b0-0000-4000-8000-000000000002', 'a1a1a1a1-0000-4000-8000-000000000001', 'Concentrado adulto 8 kg', 'Alimento seco para perros adultos de raza mediana, con pollo y arroz', 'PERRO', 89000, 'COP', 10, 'ALIMENTO', 'PUBLICADA'),
('b0b0b0b0-0000-4000-8000-000000000003', 'a1a1a1a1-0000-4000-8000-000000000001', 'Hueso de nylon', 'Hueso resistente para perros que muerden fuerte, sabor a tocino', 'PERRO', 15000, 'COP', 0, 'JUGUETE', 'AGOTADA'),
('b0b0b0b0-0000-4000-8000-000000000004', 'a1a1a1a1-0000-4000-8000-000000000001', 'Shampoo antipulgas', 'Shampoo para perros con aceite de neem, 500 ml', 'PERRO', 28000, 'COP', 8, 'HIGIENE', 'PUBLICADA'),
('b0b0b0b0-0000-4000-8000-000000000005', 'a1a1a1a1-0000-4000-8000-000000000002', 'Rascador de carton', 'Rascador de carton corrugado con catnip incluido', 'GATO', 35000, 'COP', 6, 'JUGUETE', 'PUBLICADA'),
('b0b0b0b0-0000-4000-8000-000000000006', 'a1a1a1a1-0000-4000-8000-000000000002', 'Arena aglomerante 4 kg', 'Arena para gato que forma bolas firmes y controla olores', 'GATO', 30000, 'COP', 20, 'HIGIENE', 'PUBLICADA'),
('b0b0b0b0-0000-4000-8000-000000000007', 'a1a1a1a1-0000-4000-8000-000000000002', 'Snacks de salmon', 'Premios blandos de salmon para gatos, bolsa de 80 g', 'GATO', 12000, 'COP', 25, 'ALIMENTO', 'PUBLICADA'),
('b0b0b0b0-0000-4000-8000-000000000008', 'a1a1a1a1-0000-4000-8000-000000000002', 'Desparasitante felino', 'Pipeta desparasitante para gatos de 2 a 5 kg', 'GATO', 45000, 'COP', 5, 'SALUD', 'PUBLICADA'),
('b0b0b0b0-0000-4000-8000-000000000009', 'a1a1a1a1-0000-4000-8000-000000000002', 'Cama termica', 'Cama acolchada que conserva el calor, modelo descontinuado', 'GATO', 60000, 'COP', 3, 'ACCESORIO', 'RETIRADA'),
('b0b0b0b0-0000-4000-8000-000000000010', 'a1a1a1a1-0000-4000-8000-000000000001', 'Correa extensible', 'Correa retractil de 5 metros para perros de hasta 25 kg', 'PERRO', 42000, 'COP', 7, 'ACCESORIO', 'PUBLICADA');

INSERT INTO pedidos (id, comprador_id, estado, total_monto, total_moneda) VALUES
('d0d0d0d0-0000-4000-8000-000000000001', 'c1c1c1c1-0000-4000-8000-000000000001', 'ENTREGADO', 129000, 'COP'),
('d0d0d0d0-0000-4000-8000-000000000002', 'c1c1c1c1-0000-4000-8000-000000000001', 'CONFIRMADO', 35000, 'COP'),
('d0d0d0d0-0000-4000-8000-000000000003', 'c1c1c1c1-0000-4000-8000-000000000002', 'ENVIADO', 56000, 'COP'),
('d0d0d0d0-0000-4000-8000-000000000004', 'c1c1c1c1-0000-4000-8000-000000000002', 'BORRADOR', 60000, 'COP'),
('d0d0d0d0-0000-4000-8000-000000000005', 'c1c1c1c1-0000-4000-8000-000000000003', 'CANCELADO', 28000, 'COP'),
('d0d0d0d0-0000-4000-8000-000000000006', 'c1c1c1c1-0000-4000-8000-000000000003', 'ENTREGADO', 105000, 'COP'),
('d0d0d0d0-0000-4000-8000-000000000007', 'c1c1c1c1-0000-4000-8000-000000000001', 'CONFIRMADO', 24000, 'COP');

INSERT INTO lineas_pedido (id, pedido_id, publicacion_id, especie_destino, cantidad, precio_unitario_monto, precio_unitario_moneda, subtotal_monto, subtotal_moneda, posicion) VALUES
('e0e0e0e0-0000-4000-8000-000000000001', 'd0d0d0d0-0000-4000-8000-000000000001', 'b0b0b0b0-0000-4000-8000-000000000001', 'PERRO', 2, 20000, 'COP', 40000, 'COP', 0),
('e0e0e0e0-0000-4000-8000-000000000002', 'd0d0d0d0-0000-4000-8000-000000000001', 'b0b0b0b0-0000-4000-8000-000000000002', 'PERRO', 1, 89000, 'COP', 89000, 'COP', 1),
('e0e0e0e0-0000-4000-8000-000000000003', 'd0d0d0d0-0000-4000-8000-000000000002', 'b0b0b0b0-0000-4000-8000-000000000005', 'GATO', 1, 35000, 'COP', 35000, 'COP', 0),
('e0e0e0e0-0000-4000-8000-000000000004', 'd0d0d0d0-0000-4000-8000-000000000003', 'b0b0b0b0-0000-4000-8000-000000000001', 'PERRO', 1, 20000, 'COP', 20000, 'COP', 0),
('e0e0e0e0-0000-4000-8000-000000000005', 'd0d0d0d0-0000-4000-8000-000000000003', 'b0b0b0b0-0000-4000-8000-000000000007', 'GATO', 3, 12000, 'COP', 36000, 'COP', 1),
('e0e0e0e0-0000-4000-8000-000000000006', 'd0d0d0d0-0000-4000-8000-000000000004', 'b0b0b0b0-0000-4000-8000-000000000006', 'GATO', 2, 30000, 'COP', 60000, 'COP', 0),
('e0e0e0e0-0000-4000-8000-000000000007', 'd0d0d0d0-0000-4000-8000-000000000005', 'b0b0b0b0-0000-4000-8000-000000000004', 'PERRO', 1, 28000, 'COP', 28000, 'COP', 0),
('e0e0e0e0-0000-4000-8000-000000000008', 'd0d0d0d0-0000-4000-8000-000000000006', 'b0b0b0b0-0000-4000-8000-000000000001', 'PERRO', 3, 20000, 'COP', 60000, 'COP', 0),
('e0e0e0e0-0000-4000-8000-000000000009', 'd0d0d0d0-0000-4000-8000-000000000006', 'b0b0b0b0-0000-4000-8000-000000000008', 'GATO', 1, 45000, 'COP', 45000, 'COP', 1),
('e0e0e0e0-0000-4000-8000-000000000010', 'd0d0d0d0-0000-4000-8000-000000000007', 'b0b0b0b0-0000-4000-8000-000000000007', 'GATO', 2, 12000, 'COP', 24000, 'COP', 0);
