-- Dados de exemplo para testar o sistema sem cadastrar nada.
-- Rode depois de schema.sql (que deixa as tabelas vazias e os IDs começando em 1).
USE projeto_poo;

INSERT INTO concessionaria (id_concessionaria, name_concessionaria, address_concessionaria) VALUES
  (1, 'Pampa Veículos',   'Av. Tiarajú, 810 - Alegrete/RS'),
  (2, 'Fronteira Motors', 'Rua dos Andradas, 1500 - Uruguaiana/RS');

INSERT INTO vendedor (id_vendedor, name_vendedor, cpf_vendedor, comissao_percentual, concessionario_vendedor) VALUES
  (1, 'Ana Souza',    '11122233344', 5.00, 1),
  (2, 'Bruno Lima',   '22233344455', 4.50, 1),
  (3, 'Carla Mendes', '33344455566', 6.00, 2);

-- 6 veículos: 2 motos, 1 em manutenção e 1 já vendido (o da venda abaixo).
INSERT INTO veiculo (id_veiculo, marca_veiculo, modelo_veiculo, ano_veiculo, placa_veiculo,
                     preco_veiculo, status_veiculo, moto, concessionario_veiculo) VALUES
  (1, 'Toyota',     'Corolla XEi',     2024, 'IQA1B23', 145000.00, 'Vendido',       0, 1),
  (2, 'Chevrolet',  'Onix LT',         2023, 'IQB2C34',  89900.00, 'Disponivel',    0, 1),
  (3, 'Volkswagen', 'T-Cross Comfort', 2025, 'IQC3D45', 139990.00, 'Em_Manutencao', 0, 1),
  (4, 'Fiat',       'Strada Freedom',  2024, 'IRD4E56', 112500.00, 'Disponivel',    0, 2),
  (5, 'Honda',      'CG 160 Fan',      2024, 'IRE5F67',  16490.00, 'Disponivel',    1, 2),
  (6, 'Yamaha',     'Fazer FZ25',      2023, 'IRF6G78',  23990.00, 'Disponivel',    1, 1);

INSERT INTO cliente (id_cliente, name_cliente, cpf, email, phone) VALUES
  (1, 'Mariana Alves',  '12345678901', 'mariana.alves@email.com',  '55999881122'),
  (2, 'Pedro Henrique', '98765432100', 'pedro.henrique@email.com', '55998776655'),
  (3, 'Juliana Rocha',  '45678912300', 'juliana.rocha@email.com',  '5534221100'),
  (4, 'Rafael Martins', '32165498700', 'rafael.martins@email.com', '55991234567');

-- Venda já realizada: Corolla (veículo 1) para Mariana (cliente 1), vendedora Ana (vendedor 1), paga com PIX.
-- Valor com 5% de desconto PIX: 145.000,00 - 7.250,00 = 137.750,00.
INSERT INTO pagamento (id_pagamento, forma_pagamento, valor_pagamento, data_pagamento,
                       cliente_pagamento, veiculo_pagamento) VALUES
  (1, 'Pix', 137750.00, '2026-11-10', 1, 1);

INSERT INTO pagamentopix (id_pagamento_pix, chave_pix, tipo_chave_pix, pagamento_pix) VALUES
  (1, '12345678901', 'CPF', 1);

INSERT INTO venda (id_venda, data_venda, cliente_venda, veiculo_venda, vendedor_venda, pagamento_venda) VALUES
  (1, '2026-11-10', 1, 1, 1, 1);
