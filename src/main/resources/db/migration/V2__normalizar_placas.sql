-- V2: normaliza as placas para o formato exigido por Veiculo.validarPlaca
-- (maiúsculas, sem hífen e sem espaço: ABC1234 ou ABC1D23).
-- Serve para bancos criados antes dessa regra existir, que podem ter placas como "abc-1234".
-- Placas que continuarem fora do padrão depois disto (ex.: "123") precisam ser corrigidas à mão.
UPDATE veiculo SET placa_veiculo = REPLACE(REPLACE(UPPER(placa_veiculo), '-', ''), ' ', '');
