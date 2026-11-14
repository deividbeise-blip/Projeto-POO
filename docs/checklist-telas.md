# Checklist das telas

Conferir abrindo o sistema com os dados de exemplo (`db/schema.sql` + `db/dados.sql`).
Passar por **cada tela duas vezes**: com a janela em **1280×720** (tamanho inicial) e **maximizada**.

Marque `[x]` quando o item estiver ok nas duas situações. Se algo falhar, anote ao lado o que aconteceu.

## Itens a conferir em todas as telas

| # | Item | O que observar |
|---|------|----------------|
| 1 | Nada cortado ou sobreposto | Rótulos, campos, botões, tabelas e mensagens de erro aparecem inteiros; nenhuma barra de rolagem horizontal inesperada. |
| 2 | Espaçamentos iguais | Mesma margem nas bordas da tela e entre cartões; mesma distância entre linhas do formulário. |
| 3 | Rótulos alinhados | Rótulos numa coluna à esquerda, campos começando na mesma linha vertical. |
| 4 | Uso só com teclado | Tab/Shift+Tab percorrem tudo em ordem lógica; Enter aciona o botão principal; atalhos Alt+letra funcionam. |
| 5 | Foco visível | O componente com foco tem anel/contorno visível o tempo todo, inclusive nos botões coloridos e no menu. |
| 6 | Feedback em toda ação | Salvar, excluir, vender e erros de banco sempre mostram mensagem (sucesso ou erro). |
| 7 | Janela não trava | Durante salvar/carregar o botão fica desabilitado e a janela continua respondendo (dá para mover/redimensionar). |
| 8 | Erro embaixo do campo inválido | Ao salvar com dado inválido, o campo fica com contorno vermelho e a mensagem aparece logo abaixo dele; ao corrigir, some. |

## Atalhos esperados

- Menu lateral: **Alt+C** Clientes, **Alt+V** Veículos, **Alt+E** Vendas, **Alt+O** Concessionárias, **Alt+D** Vendedores.
- Telas de cadastro: **Alt+N** Novo, **Alt+S** Salvar, **Alt+X** Excluir, **Enter** salva.
- Veículos: também **Alt+M** Manutenção e **Alt+T** Tornar disponível.
- Vendas: **Alt+N** aba Nova venda, **Alt+H** aba Histórico, **Alt+F** / **Enter** Finalizar venda; no recibo, **Enter** ou **Esc** fecham.

## Tela por tela

### Clientes
- [ ] 1 Nada cortado ou sobreposto
- [ ] 2 Espaçamentos iguais
- [ ] 3 Rótulos alinhados
- [ ] 4 Uso só com teclado
- [ ] 5 Foco visível
- [ ] 6 Feedback em toda ação (cadastrar, editar, excluir, excluir cliente com venda → mensagem de erro)
- [ ] 7 Janela não trava
- [ ] 8 Erro embaixo do campo inválido (ex.: CPF com 10 dígitos, e-mail sem @)

### Veículos
- [ ] 1 Nada cortado ou sobreposto
- [ ] 2 Espaçamentos iguais
- [ ] 3 Rótulos alinhados
- [ ] 4 Uso só com teclado (incluindo filtros de status e tipo)
- [ ] 5 Foco visível
- [ ] 6 Feedback em toda ação (cadastrar, editar, excluir, manutenção, disponível)
- [ ] 7 Janela não trava
- [ ] 8 Erro embaixo do campo inválido (ex.: ano 1800, preço vazio)

### Vendas
- [ ] 1 Nada cortado ou sobreposto (cartões de cliente, veículo, vendedor, pagamento e resumo)
- [ ] 2 Espaçamentos iguais
- [ ] 3 Rótulos alinhados
- [ ] 4 Uso só com teclado (trocar forma de pagamento, abas, finalizar)
- [ ] 5 Foco visível
- [ ] 6 Feedback em toda ação (venda PIX, cartão e dinheiro mostram o recibo; erro de banco mostra mensagem)
- [ ] 7 Janela não trava
- [ ] 8 Erro embaixo do campo inválido (ex.: chave PIX vazia, cartão com 15 dígitos, validade vencida, CVV com 2 dígitos)
- [ ] Resumo: PIX mostra 5% de desconto; cartão mostra parcelas; dinheiro mostra valor cheio, sem parcelas
- [ ] Histórico mostra a venda de exemplo (PIX) e as novas, com a forma escrita como "PIX", "Cartão" ou "Dinheiro"

### Concessionárias
- [ ] 1 Nada cortado ou sobreposto
- [ ] 2 Espaçamentos iguais
- [ ] 3 Rótulos alinhados
- [ ] 4 Uso só com teclado
- [ ] 5 Foco visível
- [ ] 6 Feedback em toda ação (excluir concessionária com veículos/vendedores → mensagem de erro)
- [ ] 7 Janela não trava
- [ ] 8 Erro embaixo do campo inválido (ex.: nome vazio)

### Vendedores
- [ ] 1 Nada cortado ou sobreposto
- [ ] 2 Espaçamentos iguais
- [ ] 3 Rótulos alinhados
- [ ] 4 Uso só com teclado
- [ ] 5 Foco visível
- [ ] 6 Feedback em toda ação (excluir vendedor com venda → mensagem de erro)
- [ ] 7 Janela não trava
- [ ] 8 Erro embaixo do campo inválido (ex.: comissão 0 ou acima de 100)

### Janela principal (menu e barra de status)
- [ ] Menu lateral inteiro visível em 1280×720, item da tela atual destacado
- [ ] Título da tela muda ao navegar
- [ ] Barra de status mostra as mensagens de sucesso
- [ ] Sem as variáveis DB_URL/DB_USER/DB_PASSWORD, o sistema mostra a mensagem "Não foi possível iniciar" em vez de fechar sem aviso

## Capturas de tela

Tirar com a janela em 1280×720, usando os dados de exemplo. Salvar **com estes nomes exatos** na pasta `imagens/`
(o README já aponta para eles):

| Arquivo | O que mostrar |
|---------|---------------|
| `imagens/01-clientes.png` | Tela de Clientes com a lista preenchida e um cliente selecionado no formulário. |
| `imagens/02-veiculos.png` | Tela de Veículos com a lista mostrando os três status (disponível, em manutenção, vendido) e motos. |
| `imagens/03-vendas.png` | Aba "Nova venda" com cliente, veículo e vendedor escolhidos e o resumo com o desconto PIX. |
| `imagens/04-recibo.png` | Janela do recibo logo depois de finalizar uma venda. |
| `imagens/05-historico.png` | Aba "Histórico" com pelo menos duas vendas (formas de pagamento diferentes). |
| `imagens/06-concessionarias.png` | Tela de Concessionárias com a lista e o formulário. |
| `imagens/07-vendedores.png` | Tela de Vendedores com a lista e o formulário. |
| `imagens/08-erro-validacao.png` | Qualquer formulário com campos inválidos mostrando o contorno vermelho e a mensagem embaixo do campo. |
