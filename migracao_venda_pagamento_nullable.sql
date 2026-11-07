-- A venda é gravada ANTES do pagamento (o pagamento precisa do veículo e do cliente já gravados,
-- e a venda é ligada a ele depois, em VendaDAO.atualizarPagamento). Mas no backupBanco.sql a coluna
-- venda.pagamento_venda é NOT NULL, então o INSERT da venda (que envia NULL) é rejeitado.
-- Rodar uma vez no banco. A chave estrangeira e o UNIQUE continuam valendo; só passa a aceitar NULL.
ALTER TABLE venda MODIFY pagamento_venda INT(11) NULL;
