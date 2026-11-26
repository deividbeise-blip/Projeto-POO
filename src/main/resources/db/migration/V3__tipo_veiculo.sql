-- V3: troca a coluna booleana "moto" por "tipo", com o nome do tipo de veículo em maiúsculas
-- ('CARRO', 'MOTO'...). Assim um tipo novo (ex.: 'CAMINHAO') não exige mudar a estrutura do banco.
-- O valor é o mesmo de Veiculo.getTipo() em maiúsculas e a chave do RegistroTiposVeiculo (AppConfig).

-- 1) Coluna nova: todo veículo existente começa como carro.
ALTER TABLE veiculo
  ADD COLUMN tipo VARCHAR(20) NOT NULL DEFAULT 'CARRO' AFTER status_veiculo;

-- 2) Copia a informação da coluna antiga: quem era moto vira 'MOTO'.
UPDATE veiculo SET tipo = 'MOTO' WHERE moto = 1;

-- 3) A coluna antiga deixa de ser usada pelo sistema.
ALTER TABLE veiculo DROP COLUMN moto;
