# Concessionária Grupo 2

Sistema de gerenciamento de uma concessionária de veículos, desenvolvido em Java com interface gráfica Swing e persistência em MySQL, como Trabalho 1 de Programação Orientada a Objetos.

## Descrição do sistema

### Problema que o sistema resolve
Uma concessionária precisa controlar seu estoque de veículos, cadastrar clientes e vendedores, e registrar vendas com diferentes formas de pagamento (PIX ou cartão de crédito), aplicando regras específicas para cada caso.

### Principais usuários
- **Atendente/Vendedor da concessionária**, que usa o sistema para cadastrar clientes e veículos, controlar o estoque e registrar vendas.

### Entidades principais
- **Cliente** — pessoa que compra um veículo.
- **Concessionária** — unidade que possui o estoque de veículos e emprega os vendedores.
- **Vendedor** — funcionário responsável por uma venda, recebe comissão sobre ela.
- **Veículo** — carro ou moto do estoque, com marca, modelo, ano, placa, preço e status.
- **Venda** — registro que liga um cliente, um vendedor, um veículo e um pagamento.
- **Pagamento** — classe abstrata com duas formas concretas: PIX e Cartão de Crédito.

### Operações disponíveis na interface gráfica
- Cadastrar cliente
- Cadastrar veículo (vinculado a uma concessionária)
- Consultar veículos e alterar seu status (Disponível / Em manutenção)
- Registrar uma venda (seleciona cliente, veículo e vendedor já cadastrados, escolhe a forma de pagamento e finaliza)

### Regras de negócio
- Um veículo só pode ser vendido se estiver com status **Disponível**. Um veículo **em manutenção** não pode ser vendido, e um veículo **já vendido** nunca pode ser vendido de novo nem voltar a ficar disponível.
- Pagamentos via **PIX** recebem automaticamente **5% de desconto** sobre o valor do veículo.
- Pagamentos no **cartão de crédito** exigem: número com 16 dígitos, validade no formato MM/AA e não vencida, CVV com 3 ou 4 dígitos, e parcelamento entre 1 e 12 vezes.
- Uma venda só é finalizada se todos os dados (cliente, vendedor, veículo, pagamento) estiverem completos e válidos; qualquer inconsistência lança uma exceção própria do domínio antes de qualquer gravação no banco.
- O vendedor recebe uma comissão percentual sobre o valor da venda, reduzida em pontos percentuais quando o veículo vendido é uma moto.

## Como executar o projeto

### Pré-requisitos
- JDK 17 ou superior
- Maven
- MySQL (recomendado usar o XAMPP, que já inclui o MySQL e o phpMyAdmin)

### Passo a passo
1. Inicie o MySQL (pelo XAMPP Control Panel, clique em **Start** ao lado de MySQL).
2. No phpMyAdmin (`http://localhost/phpmyadmin`), crie um banco de dados chamado `projeto_poo`.
3. Importe o arquivo `backupBanco.sql` (na raiz do repositório) dentro desse banco, na aba **Importar**.
4. Confirme que `src/main/java/conexao/ConexaoBanco.java` aponta para:
   - URL: `jdbc:mysql://localhost:3306/projeto_poo`
   - Usuário: `root`
   - Senha: (em branco)
5. Abra o projeto no VS Code (ou outra IDE com suporte a Maven/Java) e rode a classe `interfacegrafica.TelaPrincipal`, que contém o método `main`.

## Classes principais

| Pacote | Classes |
|---|---|
| `modelo` | `Cliente`, `Concessionaria`, `Vendedor`, `Veiculo`, `StatusVeiculo` (enum), `Pagamento` (abstrata), `PagamentoPix`, `PagamentoCartao`, `Venda`, `PoliticaDesconto` (interface), `DescontoPix`, `SemDesconto` |
| `servico` | `ClienteService`, `VeiculoService`, `VendaService` |
| `dao` | `ClienteDAO`, `ConcessionariaDAO`, `VendedorDAO`, `VeiculoDAO`, `VendaDAO`, `PagamentoDAO`, `PagamentoPixDAO`, `PagamentoCartaoDAO` |
| `excecao` | `VeiculoIndisponivelException`, `VendaInvalidaException` |
| `interfacegrafica` | `TelaPrincipal`, `ClienteFront`, `VeiculoFront`, `ConcessionariaFront` (tela de vendas) |
| `conexao` | `ConexaoBanco` |

## Checklist de avaliação

| Conceito | Como este projeto cumpre o item |
|---|---|
| Classes e objetos Java | Classes de domínio como `Cliente`, `Veiculo` e `Venda` são instanciadas e manipuladas ao longo do fluxo de cadastro e venda. |
| Atributos de classe Java | `Veiculo` armazena `marca`, `modelo`, `ano`, `placa`, `preco` e `status`, entre outros, como estado do objeto. |
| Métodos de classe Java | `Veiculo.vender()`, `Veiculo.verificarDisponibilidade()` e `Pagamento.obterReciboDetalhado()` são comportamentos associados às classes corretas. |
| Desafio da aula de Java | *(preencher com o desafio específico resolvido em aula)* |
| Construtores em Java | O construtor de `Veiculo` exige marca, modelo, ano, placa e preço válidos, lançando `IllegalArgumentException` se algum dado estiver incorreto. |
| Palavra-chave `this` | Usada nos construtores de `Cliente`, `Veiculo`, `Vendedor` e `Concessionaria` para diferenciar atributo do parâmetro (ex.: `this.marca = marca`). |
| Modificadores Java | Atributos são `private`/`private final`; cada classe expõe apenas os métodos públicos necessários (ex.: não existe `setStatus` público em `Veiculo`). |
| Encapsulamento em Java | O status de um veículo só muda através de `vender()`, `colocarEmManutencao()` e `tornarDisponivel()`, que impõem as regras de transição válidas. |
| Pacotes Java / API | Código organizado em `modelo`, `servico`, `dao`, `excecao`, `interfacegrafica` e `conexao`, cada um com uma responsabilidade. |
| Herança em Java | `PagamentoPix` e `PagamentoCartao` estendem a classe abstrata `Pagamento`. |
| Polimorfismo em Java | O `VendaService` manipula qualquer pagamento pelo tipo `Pagamento`, chamando `obterReciboDetalhado()` sem saber se é PIX ou Cartão. |
| Palavra-chave `super` | Os construtores de `PagamentoPix` e `PagamentoCartao` chamam `super(venda, formaPagamento, valorBase, dataPagamento, politica)`. |
| Abstração em Java | `Pagamento` é uma classe abstrata que não pode ser instanciada diretamente e declara o método abstrato `obterReciboDetalhado()`. |
| Interfaces em Java | A interface `PoliticaDesconto` é implementada por `DescontoPix` (5% de desconto) e `SemDesconto`, permitindo novas políticas sem alterar `Pagamento`. |
| Enum em Java | `StatusVeiculo { DISPONIVEL, VENDIDO, EM_MANUTENCAO }` evita estados inválidos para o veículo. |
| Tratamento de exceções em Java | `VeiculoIndisponivelException` e `VendaInvalidaException` são lançadas nas regras de negócio e tratadas nas telas com `JOptionPane`. |
| Interface gráfica com Swing | `TelaPrincipal` (JFrame com abas) reúne `ClienteFront`, `VeiculoFront` e `ConcessionariaFront`, usando `JTextField`, `JComboBox`, `JButton` e `JOptionPane`. |
| Data e hora em Java | `LocalDate` é usado nas datas de venda e pagamento; `YearMonth` valida a validade do cartão de crédito. |

## Capturas de tela

*(inserir aqui prints das telas de Clientes, Veículos e Vendas em funcionamento)*
