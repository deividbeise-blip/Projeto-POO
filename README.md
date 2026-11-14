# Concessionária Grupo 2

Sistema de gestão de uma concessionária de veículos, feito em **Java 25 + Swing** com persistência em **MySQL**,
como Trabalho 2 de Programação Orientada a Objetos.

- **Problema:** a concessionária precisa controlar o estoque de carros e motos, manter o cadastro de clientes,
  vendedores e lojas, e registrar vendas com formas de pagamento diferentes, cada uma com suas regras (desconto
  no PIX, parcelamento no cartão, comissão menor para motos).
- **Usuários:** atendentes e vendedores da concessionária.
- **Entidades:** Cliente, Concessionária, Vendedor, Veículo (carro ou moto, com status Disponível /
  Em manutenção / Vendido), Venda e Pagamento (abstrato, com PIX, Cartão e Dinheiro).

### Regras de negócio
- Só é possível vender veículo **disponível**: em manutenção não pode, e vendido não volta a ficar disponível.
- **PIX** tem 5% de desconto. **Cartão** exige número com 16 dígitos, validade MM/AA não vencida, CVV com 3 ou 4
  dígitos e de 1 a 12 parcelas. **Dinheiro** é à vista, sem desconto.
- A comissão do vendedor é o percentual dele sobre o valor da venda; em **motos** o percentual cai 3 pontos.
- A venda só é gravada se passar em todas as regras; venda, pagamento e status do veículo são gravados numa única
  transação (ou tudo, ou nada).
- Cliente, vendedor, veículo ou concessionária que já têm vendas ou vínculos não podem ser excluídos.

## Integrantes

<!-- CONFIRMAR: revisar os nomes completos -->
- Deivid Beise
- Matheus Fonseca
- Lorenzo de Brum Schons
- Fernando Nicola

## Como executar

### Pré-requisitos
- **JDK 25** (o `pom.xml` usa `maven.compiler.release` 25)
- **Maven 3.9+**
- **MySQL 8** (local, XAMPP ou Docker)

### 1. Criar o banco
Os scripts ficam em [`db/`](db/). O `schema.sql` cria o banco `projeto_poo` (apaga e recria as tabelas) e o
`dados.sql` coloca dados de exemplo: 2 concessionárias, 3 vendedores, 6 veículos (2 motos, 1 em manutenção,
1 vendido), 4 clientes e 1 venda paga com PIX.

```bash
mysql -u root -p < db/schema.sql
mysql -u root -p < db/dados.sql
```

No phpMyAdmin/Workbench, basta abrir e executar os dois arquivos nessa ordem.

Com Docker, se preferir:

```bash
docker run -d --name mysql-poo -e MYSQL_ROOT_PASSWORD=senha -p 3306:3306 mysql:8.4
docker exec -i mysql-poo mysql -uroot -psenha < db/schema.sql
docker exec -i mysql-poo mysql -uroot -psenha < db/dados.sql
```

### 2. Configurar as credenciais
O sistema lê a conexão de três variáveis de ambiente: `DB_URL`, `DB_USER` e `DB_PASSWORD`.
Nenhuma senha fica no código.

**PowerShell** (vale para o terminal aberto):

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/projeto_poo"
$env:DB_USER = "root"
$env:DB_PASSWORD = "senha"
```

**Arquivo `.env`** (copie o [`.env.example`](.env.example); o `.env` não é versionado):

```env
DB_URL=jdbc:mysql://localhost:3306/projeto_poo
DB_USER=root
DB_PASSWORD=senha
```

O Java não lê o `.env` sozinho: carregue-o no terminal antes de executar (por exemplo, no Git Bash
`set -a; source .env; set +a`) ou configure as variáveis na execução da IDE.

### 3. Executar

```bash
mvn compile exec:java "-Dexec.mainClass=apresentacao.TelaPrincipal"
```

Se as variáveis não estiverem definidas, o sistema abre uma mensagem dizendo quais faltam.

## Arquitetura

```
                 ┌──────────────────────────────┐
                 │ config (AppConfig)           │  monta tudo: cria DAOs, políticas e services
                 └──────────────┬───────────────┘
                                │ injeta
          ┌─────────────────────┼─────────────────────────┐
          ▼                     ▼                         ▼
┌──────────────────┐   ┌──────────────────┐      ┌──────────────────┐
│ apresentacao     │──▶│ aplicacao        │─────▶│ dominio          │◀──── infra
│ telas Swing      │   │ casos de uso,    │      │ entidades,       │      DAOs JDBC,
│                  │   │ DTOs, pagamento  │      │ regras, portas   │      conexão,
└──────────────────┘   └──────────────────┘      └──────────────────┘      transação
```

As setas apontam a direção das dependências: `apresentacao → aplicacao → dominio ← infra`.
O domínio não conhece ninguém; a infra implementa as interfaces do domínio; só o `config` conhece as classes
concretas.

| Pacote | O que contém |
|--------|--------------|
| `dominio.modelo` | Entidades e suas regras: `Cliente`, `Veiculo`, `Venda`, `Vendedor`, `Concessionaria`, `Pagamento` e subclasses, `StatusVeiculo`, `ResumoVenda`. |
| `dominio.politica` | Regras substituíveis: `PoliticaDesconto` (`DescontoPix`, `SemDesconto`) e `PoliticaComissao` (`ComissaoCarro`, `ComissaoMoto`). |
| `dominio.excecao` | Exceções de negócio: `VeiculoIndisponivelException`, `VendaInvalidaException`. |
| `dominio.repositorio` | Portas de persistência (interfaces): um repositório por entidade, mais `DetalhePagamentoRepository`. |
| `aplicacao.servico` | Casos de uso: `ClienteService`, `VeiculoService`, `VendaService`, `VendedorService`, `ConcessionariaService`. |
| `aplicacao.dto` | Dados de entrada sem entidades: `DadosVenda`, `DadosPix`, `DadosCartao`, `DadosDinheiro`. |
| `aplicacao.pagamento` | `FormaPagamento` e suas implementações (PIX, cartão, dinheiro). |
| `aplicacao` | `Transacao`: porta para executar várias gravações juntas. |
| `infra.conexao` | `ConexaoBanco` (lê as variáveis de ambiente), `ConnectionFactory`, `TransacaoJdbc`. |
| `infra.dao` | DAOs JDBC que implementam os repositórios, mais `ExecutorSql` e `ErrosSql`. |
| `infra.excecao` | `PersistenciaException`: erro de banco não verificado. |
| `apresentacao` | Telas (`ClienteFront`, `VeiculoFront`, `VendaFront`, `ConcessionariaFront`, `VendedorFront`, `TelaPrincipal`) e os `TableModel`s. |
| `apresentacao.comum` | Comportamento compartilhado das telas: `TarefaEmSegundoPlano`, `Feedback`, `Validacao`, `BarraStatus`, `TelaComDados`, `TelaComBotaoPrincipal`. |
| `apresentacao.estilo` | Visual único: `Tema` (cores, fontes, espaçamentos) e `Componentes` (fábrica de botões, campos, cartões). |
| `config` | `AppConfig`: o único lugar que cria DAOs e liga as dependências. |

## Onde cada requisito foi atendido

### SOLID
- **SRP (responsabilidade única):** cada camada tem um motivo para mudar. As regras ficam nas entidades
  (`Veiculo.vender`, `Venda.finalizarVenda`), o fluxo nos services, o SQL nos DAOs e o desenho nas telas.
  Até a tela foi dividida: `ClienteFront` desenha, `ClienteTableModel` formata a tabela, `Validacao` marca
  erros e `Feedback` mostra mensagens.
- **OCP (aberto/fechado):**
  - **Pagamento em dinheiro sem mexer no `VendaService`.** Para criar a forma nova bastaram
    [`DadosDinheiro`](src/main/java/aplicacao/dto/DadosDinheiro.java),
    [`PagamentoDinheiro`](src/main/java/dominio/modelo/PagamentoDinheiro.java),
    [`FormaPagamentoDinheiro`](src/main/java/aplicacao/pagamento/FormaPagamentoDinheiro.java) e uma linha no
    [`AppConfig`](src/main/java/config/AppConfig.java). O `VendaService` escolhe a forma pelo tipo do DTO, num
    mapa recebido pronto. Prova: o commit que adicionou o dinheiro não altera o `VendaService`, e o comando abaixo
    sai vazio.
    ```bash
    git show --stat b4aec43   # lista só DTO, modelo, forma de pagamento, AppConfig e VendaFront
    git diff aadc459 b4aec43 -- src/main/java/aplicacao/servico/VendaService.java   # saída vazia
    ```
  - **`PoliticaComissao`** ([`ComissaoCarro`](src/main/java/dominio/politica/ComissaoCarro.java),
    [`ComissaoMoto`](src/main/java/dominio/politica/ComissaoMoto.java)): um tipo novo de veículo ganha uma
    política nova, sem reescrever o cálculo da `Venda`.
  - **`PoliticaDesconto`** ([`DescontoPix`](src/main/java/dominio/politica/DescontoPix.java),
    [`SemDesconto`](src/main/java/dominio/politica/SemDesconto.java)): uma promoção nova é só outra classe,
    entregue pelo `AppConfig` à forma de pagamento.
- **LSP (substituição de Liskov):** [`Pagamento`](src/main/java/dominio/modelo/Pagamento.java) é abstrato, e
  `PagamentoPix`, `PagamentoCartao` e `PagamentoDinheiro` podem ser usados em qualquer lugar que espera um
  `Pagamento`. A `Venda` calcula valor, comissão e recibo (`obterReciboDetalhado`) sem saber qual é a subclasse.
- **ISP (segregação de interfaces):** repositórios pequenos e específicos em
  [`dominio/repositorio`](src/main/java/dominio/repositorio/). `PagamentoRepository` só grava a parte comum,
  `DetalhePagamentoRepository<T>` só o detalhe de cada forma, e `VendaRepository` não tem métodos de cliente nem
  de veículo.
- **DIP (inversão de dependência):** os services recebem interfaces (`ClienteRepository`, `Transacao`,
  `FormaPagamento`...) pelo construtor e não conhecem JDBC. As classes concretas só se encontram no
  [`AppConfig`](src/main/java/config/AppConfig.java), que também injeta as políticas de desconto.

### Clean Architecture
- **Regra de dependência:** `dominio` e `aplicacao` não importam `apresentacao`, `infra`, `config`, `java.sql`,
  `java.awt` nem `javax.swing`. Dá para conferir com:
  `grep -rnE "import (apresentacao|infra|config|java\.sql|java\.awt|javax\.swing)" src/main/java/dominio src/main/java/aplicacao`
  (saída vazia).
- **DTOs na fronteira:** a tela envia só valores simples (`DadosVenda` com IDs e `DadosPix`/`DadosCartao`/
  `DadosDinheiro`) e recebe `ResumoVenda` no histórico. As conversões do formato do banco (ex.: `'Pix'` →
  `"PIX"`) ficam nos DAOs.
- **Casos de uso sem a tela:** os services funcionam sem Swing. Por exemplo,
  `new AppConfig().vendaService().vender(new DadosVenda(2L, 2L, 5L, new DadosDinheiro()))` realiza uma venda
  completa a partir de qualquer `main` ou teste.

### Clean Code
- **Nomes do domínio** em português e verbos claros (`finalizarVenda`, `colocarEmManutencao`, `listarDisponiveis`).
- **Sem duplicação no JDBC:** [`ExecutorSql`](src/main/java/infra/dao/ExecutorSql.java) concentra abrir conexão,
  `PreparedStatement`, ID gerado e tradução de erro; [`ErrosSql`](src/main/java/infra/dao/ErrosSql.java)
  reconhece chave estrangeira e valor duplicado, e os DAOs transformam isso em mensagens de negócio.
- **Erros explícitos:** [`PersistenciaException`](src/main/java/infra/excecao/PersistenciaException.java) (não
  verificada) para falhas de banco; `IllegalArgumentException` para dado inválido; `IllegalStateException` para
  "possui vendas" ou "não encontrado"; exceções de domínio para venda inválida e veículo indisponível.
- **Regras em um lugar só:** as telas validam chamando as mesmas regras do domínio (`Cliente.validarCpf`,
  `PagamentoCartao.validarValidade`...), sem copiar a lógica.

### Interface gráfica
- **Visual único:** [`Tema`](src/main/java/apresentacao/estilo/Tema.java) tem todas as cores, fontes e
  espaçamentos; [`Componentes`](src/main/java/apresentacao/estilo/Componentes.java) cria botões, campos,
  cartões e formulários alinhados. O tema base é o **FlatLaf** (`FlatLightLaf`).
- **Janela não trava:** todo acesso a banco passa por
  [`TarefaEmSegundoPlano`](src/main/java/apresentacao/comum/TarefaEmSegundoPlano.java) (SwingWorker), que
  desabilita o botão durante a operação.
- **Feedback consistente:** [`Feedback`](src/main/java/apresentacao/comum/Feedback.java) mostra sucesso, erro
  e confirmação; [`Validacao`](src/main/java/apresentacao/comum/Validacao.java) marca o campo inválido com
  contorno vermelho e a mensagem embaixo dele.
- **Teclado:** Alt+C/V/E/O/D no menu; Alt+N/S/X nos cadastros; Enter aciona o botão principal de cada tela
  (`TelaComBotaoPrincipal`); foco visível com anel de 2 px; rótulos ligados aos campos (`setLabelFor`) e nomes
  acessíveis nas buscas e tabelas.
- Conferência manual tela por tela: [`docs/checklist-telas.md`](docs/checklist-telas.md).

### Banco de dados
- **Repositórios:** interfaces em `dominio.repositorio`, implementadas pelos DAOs de `infra.dao`.
- **Segurança:** todo SQL usa `PreparedStatement` com parâmetros, sem concatenar valores digitados.
- **Transação:** [`TransacaoJdbc`](src/main/java/infra/conexao/TransacaoJdbc.java) faz a venda gravar venda,
  pagamento, detalhe do pagamento e status do veículo juntos, com rollback se algo falhar.
- **Reproduzível:** [`db/schema.sql`](db/schema.sql) e [`db/dados.sql`](db/dados.sql) recriam o banco do zero.

### JavaDoc
- Toda classe, interface, record, enum, construtor e método público está documentado.
- O HTML gerado está em [`docs/javadoc/index.html`](docs/javadoc/index.html) (abra no navegador).
- Para gerar de novo: `mvn javadoc:javadoc` (configurado no `pom.xml` com o `maven-javadoc-plugin`).

## Capturas de tela

| | |
|---|---|
| ![Clientes](imagens/01-clientes.png) | ![Veículos](imagens/02-veiculos.png) |
| Clientes | Veículos |
| ![Nova venda](imagens/03-vendas.png) | ![Recibo](imagens/04-recibo.png) |
| Nova venda | Recibo |
| ![Histórico](imagens/05-historico.png) | ![Concessionárias](imagens/06-concessionarias.png) |
| Histórico de vendas | Concessionárias |
| ![Vendedores](imagens/07-vendedores.png) | ![Erro de validação](imagens/08-erro-validacao.png) |
| Vendedores | Erro de validação embaixo do campo |

## Checklist do Trabalho 1

<!-- PREENCHER -->
