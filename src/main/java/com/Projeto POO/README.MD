**Tema: Concessionária LFDM**

* **O problema:** Gerenciar o estoque de veículos e automatizar o fluxo financeiro de vendas e pagamentos de uma concessionária.
* **Principais usuários:** Vendedores, gerentes e clientes.
* **Entidades:** cliente, Veiculo, Concessionaria, Venda, Pagamento (e suas subclasses).
* **Operações do Front:** Cadastrar a venda de um veículo para um cliente e processar o pagamento gerando um recibo detalhado.
* **Regras de Negócio:** Um veículo vendido não pode ser vendido novamente. O preço final é calculado polimorficamente com base no método de pagamento (PIX recebe desconto automático).
