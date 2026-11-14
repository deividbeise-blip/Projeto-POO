-- Estrutura do banco do sistema da concessionária (MySQL 8).
-- Pode ser executado de novo a qualquer momento: apaga e recria todas as tabelas.

CREATE DATABASE IF NOT EXISTS projeto_poo
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;
USE projeto_poo;

-- Ordem inversa das chaves estrangeiras: quem referencia sai primeiro.
DROP TABLE IF EXISTS venda;
DROP TABLE IF EXISTS pagamentopix;
DROP TABLE IF EXISTS pagamentocartao;
DROP TABLE IF EXISTS pagamento;
DROP TABLE IF EXISTS veiculo;
DROP TABLE IF EXISTS vendedor;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS concessionaria;

CREATE TABLE concessionaria (
  id_concessionaria      INT          NOT NULL AUTO_INCREMENT,
  name_concessionaria    VARCHAR(50)  NOT NULL,
  address_concessionaria VARCHAR(100) NOT NULL,
  PRIMARY KEY (id_concessionaria),
  UNIQUE KEY name_concessionaria (name_concessionaria),
  UNIQUE KEY address_concessionaria (address_concessionaria)
) ENGINE=InnoDB;

CREATE TABLE cliente (
  id_cliente   INT          NOT NULL AUTO_INCREMENT,
  name_cliente VARCHAR(50)  NOT NULL,
  cpf          VARCHAR(11)  NOT NULL,
  email        VARCHAR(100) NOT NULL,
  phone        VARCHAR(15)  NOT NULL,
  PRIMARY KEY (id_cliente),
  UNIQUE KEY name_cliente (name_cliente),
  UNIQUE KEY cpf (cpf),
  UNIQUE KEY email (email),
  UNIQUE KEY phone (phone)
) ENGINE=InnoDB;

CREATE TABLE vendedor (
  id_vendedor             INT          NOT NULL AUTO_INCREMENT,
  name_vendedor           VARCHAR(50)  NOT NULL,
  cpf_vendedor            VARCHAR(11)  NOT NULL,
  comissao_percentual     DECIMAL(5,2) NOT NULL,
  concessionario_vendedor INT          NOT NULL,
  PRIMARY KEY (id_vendedor),
  UNIQUE KEY name_vendedor (name_vendedor),
  UNIQUE KEY cpf_vendedor (cpf_vendedor),
  KEY concessionario_vendedor (concessionario_vendedor),
  CONSTRAINT vendedor_ibfk_1 FOREIGN KEY (concessionario_vendedor)
    REFERENCES concessionaria (id_concessionaria) ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE veiculo (
  id_veiculo             INT           NOT NULL AUTO_INCREMENT,
  marca_veiculo          VARCHAR(50)   NOT NULL,
  modelo_veiculo         VARCHAR(50)   NOT NULL,
  ano_veiculo            INT           NOT NULL,
  placa_veiculo          VARCHAR(10)   NOT NULL,
  preco_veiculo          DECIMAL(10,2) NOT NULL,
  status_veiculo         ENUM('Disponivel','Vendido','Em_Manutencao') NOT NULL DEFAULT 'Disponivel',
  moto                   TINYINT(1)    NOT NULL DEFAULT 0,
  concessionario_veiculo INT           NOT NULL,
  PRIMARY KEY (id_veiculo),
  UNIQUE KEY placa_veiculo (placa_veiculo),
  KEY concessionario_veiculo (concessionario_veiculo),
  CONSTRAINT veiculo_ibfk_1 FOREIGN KEY (concessionario_veiculo)
    REFERENCES concessionaria (id_concessionaria) ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pagamento (
  id_pagamento      INT           NOT NULL AUTO_INCREMENT,
  forma_pagamento   ENUM('Dinheiro','Cartao','Pix') NOT NULL,
  valor_pagamento   DECIMAL(10,2) NOT NULL,
  data_pagamento    DATE          NOT NULL,
  cliente_pagamento INT           NOT NULL,
  veiculo_pagamento INT           NOT NULL,
  PRIMARY KEY (id_pagamento),
  KEY cliente_pagamento (cliente_pagamento),
  KEY veiculo_pagamento (veiculo_pagamento),
  CONSTRAINT pagamento_ibfk_1 FOREIGN KEY (cliente_pagamento)
    REFERENCES cliente (id_cliente) ON UPDATE CASCADE,
  CONSTRAINT pagamento_ibfk_2 FOREIGN KEY (veiculo_pagamento)
    REFERENCES veiculo (id_veiculo) ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pagamentocartao (
  id_pagamento_cartao INT           NOT NULL AUTO_INCREMENT,
  numero_cartao       VARCHAR(16)   NOT NULL,
  nomeTitular         VARCHAR(255)  DEFAULT NULL,
  validade_cartao     DATE          NOT NULL,
  codigo_seguranca    VARCHAR(4)    NOT NULL,
  numero_parcelas     INT           NOT NULL,
  valor_parcela       DECIMAL(10,2) NOT NULL,
  pagamento_cartao    INT           NOT NULL,
  PRIMARY KEY (id_pagamento_cartao),
  UNIQUE KEY pagamento_cartao (pagamento_cartao),
  CONSTRAINT pagamentocartao_ibfk_1 FOREIGN KEY (pagamento_cartao)
    REFERENCES pagamento (id_pagamento) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pagamentopix (
  id_pagamento_pix INT         NOT NULL AUTO_INCREMENT,
  chave_pix        VARCHAR(50) NOT NULL,
  tipo_chave_pix   ENUM('CPF','CNPJ','Email','Telefone') NOT NULL,
  pagamento_pix    INT         NOT NULL,
  PRIMARY KEY (id_pagamento_pix),
  UNIQUE KEY pagamento_pix (pagamento_pix),
  CONSTRAINT pagamentopix_ibfk_1 FOREIGN KEY (pagamento_pix)
    REFERENCES pagamento (id_pagamento) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- pagamento_venda aceita NULL: a venda é gravada antes do pagamento e ligada a ele logo depois,
-- tudo na mesma transação (veja VendaService e VendaDAO.atualizarPagamento).
CREATE TABLE venda (
  id_venda        INT  NOT NULL AUTO_INCREMENT,
  data_venda      DATE NOT NULL,
  cliente_venda   INT  NOT NULL,
  veiculo_venda   INT  NOT NULL,
  vendedor_venda  INT  NOT NULL,
  pagamento_venda INT  DEFAULT NULL,
  PRIMARY KEY (id_venda),
  UNIQUE KEY pagamento_venda (pagamento_venda),
  KEY cliente_venda (cliente_venda),
  KEY veiculo_venda (veiculo_venda),
  KEY vendedor_venda (vendedor_venda),
  CONSTRAINT venda_ibfk_1 FOREIGN KEY (cliente_venda)
    REFERENCES cliente (id_cliente) ON UPDATE CASCADE,
  CONSTRAINT venda_ibfk_2 FOREIGN KEY (veiculo_venda)
    REFERENCES veiculo (id_veiculo) ON UPDATE CASCADE,
  CONSTRAINT venda_ibfk_3 FOREIGN KEY (vendedor_venda)
    REFERENCES vendedor (id_vendedor) ON UPDATE CASCADE,
  CONSTRAINT venda_ibfk_4 FOREIGN KEY (pagamento_venda)
    REFERENCES pagamento (id_pagamento) ON UPDATE CASCADE
) ENGINE=InnoDB;
