# Checklist do modo escuro

Conferir abrindo o sistema com os dados de exemplo (`db/schema.sql` + `db/dados.sql`).
Passar por **cada item duas vezes**: no **modo claro** e no **modo escuro**. Troque de modo pelo botão
no rodapé do menu lateral (**Alt+R**) **com a janela aberta**, sem reiniciar.

Marque `[x]` quando o item estiver ok nos dois modos. Se algo falhar, anote ao lado o modo e o que aconteceu.

## Regra geral (vale para todos os itens)

No modo escuro **nada pode ficar com cor do modo claro**: nenhum fundo branco ou cinza-claro,
nenhum texto escuro sobre fundo escuro, nenhuma borda clara "brilhando". Todo texto deve ser lido sem esforço.

## Troca de modo e preferência

- [ ] O botão mostra **"Modo escuro"** no modo claro e **"Modo claro"** no modo escuro.
- [ ] Ao clicar (ou Alt+R), **toda a janela** muda na hora: menu, título, cartões, campos, tabelas e barra de status.
- [ ] Trocar várias vezes seguidas não deixa nenhuma parte "presa" na cor antiga.
- [ ] Trocar com dados digitados num formulário e com uma linha selecionada na tabela não perde nada.
- [ ] Fechar o aplicativo no modo escuro e abrir de novo: abre **no modo escuro**. Idem para o claro.
- [ ] Na primeira vez (sem preferência salva), abre no **modo claro**.

## Menu lateral

- [ ] Fundo azul do menu, nome "CONCESSIONÁRIA" e itens legíveis.
- [ ] Item da tela atual destacado (azul diferente dos demais) — **continua destacado depois de trocar de modo**.
- [ ] Botão de modo no rodapé com o mesmo visual dos itens do menu.
- [ ] Foco visível (anel claro) em cada item e no botão de modo ao navegar com Tab.
- [ ] Atalhos Alt+C, Alt+V, Alt+E, Alt+O, Alt+D e Alt+R funcionam.

## Cada tela

Em todas: título da tela, fundo da tela, cartões (fundo um pouco mais claro que o da tela no modo escuro,
com borda discreta), rótulos dos campos, campos de texto, listas suspensas e botões.

| Tela | O que observar além do geral |
|------|------------------------------|
| [ ] Clientes | Tabela de clientes e formulário; botões Novo, Salvar, Excluir. |
| [ ] Veículos | Filtros acima da tabela; botões Manutenção e Tornar disponível; status coloridos (abaixo). |
| [ ] Vendas — aba Nova venda | Busca de cliente, lista de veículos, campos de PIX/cartão/dinheiro, resumo da venda e Finalizar venda; rolagem vertical. |
| [ ] Vendas — aba Histórico | Tabela de vendas realizadas; abas legíveis (aba ativa destacada). |
| [ ] Concessionárias | Tabela e formulário. |
| [ ] Vendedores | Tabela (comissão alinhada à direita) e formulário. |

## Botões

- [ ] **Primário** (Salvar, Finalizar venda, Fechar): fundo azul, texto claro.
- [ ] **Secundário** (Novo): texto legível sobre o fundo do cartão e contorno visível.
- [ ] **Perigo** (Excluir): fundo vermelho, texto claro — no modo escuro o vermelho é mais claro; conferir se o texto continua legível.
- [ ] Botões desabilitados (durante salvar/carregar) visivelmente diferentes dos habilitados.

## Tabelas

- [ ] Cabeçalho legível e diferente das linhas.
- [ ] Linhas com fundo escuro e texto claro no modo escuro.
- [ ] **Linha selecionada**: destaque visível e texto legível (inclusive a coluna de status em Veículos).
- [ ] Grade/divisórias discretas, sem linhas brancas no modo escuro.
- [ ] Área vazia abaixo das linhas com a mesma cor do fundo da tabela.

## Status coloridos dos veículos

- [ ] **Disponível** em verde, **Em manutenção** em amarelo, **Vendido** em cinza — legíveis sobre o fundo da tabela nos dois modos (no escuro, cores mais claras).
- [ ] A coluna de **preço** fica sempre na cor normal do texto (não "herda" a cor do status da linha de cima).
- [ ] Ao selecionar uma linha, o status passa a usar a cor de texto da seleção.

## Campos com erro de validação

- [ ] Salvar com dado inválido: **contorno vermelho** no campo e **mensagem vermelha** logo abaixo, legíveis nos dois modos.
- [ ] Trocar de modo com um erro na tela: contorno e mensagem continuam vermelhos e legíveis.
- [ ] Ao corrigir o campo, contorno e mensagem somem.

## Barra de status

- [ ] Fundo e linha divisória superior acompanham o modo (sem faixa branca no escuro).
- [ ] Mensagem de sucesso em verde, legível nos dois modos; some sozinha após alguns segundos.

## Diálogos

- [ ] **Feedback — erro** (ex.: excluir algo com vínculo, ou banco desligado): fundo, texto, ícone e botão no modo atual.
- [ ] **Feedback — confirmação** (Excluir): botões Sim/Não legíveis; o foco começa em "Não" e é visível; Esc fecha.
- [ ] **Recibo da venda** (finalizar uma venda): cartão, título, texto do recibo e botão Fechar no modo atual; Enter/Esc fecham.
- [ ] Diálogo aberto depois de trocar de modo já aparece no modo novo.

## Foco visível

- [ ] Navegando só com Tab/Shift+Tab, o componente com foco sempre tem anel visível nos dois modos:
      campos, listas, tabelas, abas, botões primário/secundário/perigo, itens do menu e botão de modo.
