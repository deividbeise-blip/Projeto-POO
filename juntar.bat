@echo off
setlocal enabledelayedexpansion

:: Define o arquivo de saída
set "OUTPUT_FILE=tudo_junto.txt"

:: Apaga o arquivo de saída se ele já existir para não duplicar
if exist "%OUTPUT_FILE%" del "%OUTPUT_FILE%"

echo Mesclando arquivos Java...

:: Percorre todos os arquivos .java na pasta atual e subpastas
for /r %%F in (*.java) do (
    
    :: Pega o caminho relativo para identificar se é da pasta principal ou subpasta
    set "FULL_PATH=%%~dpF"
    set "CURRENT_DIR=%~dp0"
    
    echo ============================================================================== >> "%OUTPUT_FILE%"
    echo INÍCIO DO ARQUIVO: %%~nxF >> "%OUTPUT_FILE%"
    
    :: Verifica se o arquivo está na pasta principal ou em uma subpasta
    if "!FULL_PATH!"=="!CURRENT_DIR!" (
        echo LOCALIZAÇÃO: Pasta Principal >> "%OUTPUT_FILE%"
    ) else (
        echo LOCALIZAÇÃO: Subpasta (.\!FULL_PATH:%~dp0=!^) >> "%OUTPUT_FILE%"
    )
    
    echo ============================================================================== >> "%OUTPUT_FILE%"
    echo. >> "%OUTPUT_FILE%"
    
    :: Copia o conteúdo do arquivo Java para o TXT
    type "%%F" >> "%OUTPUT_FILE%"
    
    echo. >> "%OUTPUT_FILE%"
    echo ============================================================================== >> "%OUTPUT_FILE%"
    echo FIM DO ARQUIVO: %%~nxF >> "%OUTPUT_FILE%"
    echo ============================================================================== >> "%OUTPUT_FILE%"
    echo. >> "%OUTPUT_FILE%"
    echo. >> "%OUTPUT_FILE%"
)

echo Concluído! O arquivo "%OUTPUT_FILE%" foi gerado com sucesso.
pause
