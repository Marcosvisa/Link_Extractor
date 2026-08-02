@echo off
title Criar LinkExtractor

echo Compilando...
javac -encoding UTF-8 ExtractorPanel.java

echo Criando pasta...
mkdir LinkExtractor 2>nul
mkdir LinkExtractor\app 2>nul

echo Copiando arquivos...
copy ExtractorPanel.class LinkExtractor\app >nul
if exist "yt-dlp.exe" copy yt-dlp.exe LinkExtractor\app >nul
if exist "ffmpeg.exe" copy ffmpeg.exe LinkExtractor\app >nul
if exist "ffprobe.exe" copy ffprobe.exe LinkExtractor\app >nul
if exist "imagens" xcopy /E /I /Y imagens LinkExtractor\app\imagens >nul

echo Copiando JRE portable...
if exist "jre" (
  echo Copiando JRE 8...
  xcopy /E /I /Y jre LinkExtractor\jre >nul
  echo [OK] JRE 8 incluido!
)

echo Criando JAR...
echo Main-Class: ExtractorPanel > manifest.txt
jar cfm LinkExtractor\app\app.jar manifest.txt *.class

echo Criando VBS sem prompt...
echo On Error Resume Next > LinkExtractor\LinkExtractor.vbs
echo. >> LinkExtractor\LinkExtractor.vbs
echo Set WshShell = CreateObject("WScript.Shell") >> LinkExtractor\LinkExtractor.vbs
echo Set fso = CreateObject("Scripting.FileSystemObject") >> LinkExtractor\LinkExtractor.vbs
echo. >> LinkExtractor\LinkExtractor.vbs
echo currentDir = fso.GetParentFolderName(WScript.ScriptFullName) >> LinkExtractor\LinkExtractor.vbs
echo WshShell.CurrentDirectory = currentDir >> LinkExtractor\LinkExtractor.vbs
echo. >> LinkExtractor\LinkExtractor.vbs
echo jrePath = currentDir ^& "\jre\bin\java.exe" >> LinkExtractor\LinkExtractor.vbs
echo jarPath = currentDir ^& "\app\app.jar" >> LinkExtractor\LinkExtractor.vbs
echo. >> LinkExtractor\LinkExtractor.vbs
echo If fso.FileExists(jrePath) Then >> LinkExtractor\LinkExtractor.vbs
echo   cmd = """" ^& jrePath ^& """ -jar """ ^& jarPath ^& """" >> LinkExtractor\LinkExtractor.vbs
echo   WshShell.Run cmd, 0, False >> LinkExtractor\LinkExtractor.vbs
echo Else >> LinkExtractor\LinkExtractor.vbs
echo   cmd = "java -jar """ ^& jarPath ^& """" >> LinkExtractor\LinkExtractor.vbs
echo   WshShell.Run cmd, 0, False >> LinkExtractor\LinkExtractor.vbs
echo End If >> LinkExtractor\LinkExtractor.vbs

echo Limpando...
del *.class 2>nul
del manifest.txt 2>nul

echo.
echo ==============================================
echo PRONTO! Pasta: LinkExtractor
echo.
echo ARQUIVOS CRIADOS:
echo LinkExtractor\app\app.jar - Aplicativo principal
echo LinkExtractor\jre\        - Java 8 Portable (se disponivel)
echo LinkExtractor\LinkExtractor.vbs - Execute este arquivo
echo.
echo COMO USAR:
echo 1. Execute LinkExtractor.vbs
echo 2. Nao abre prompt de comando
echo 3. Usa JRE portable se disponivel
echo.
echo PARA DISTRIBUIR:
echo 1. Copie a pasta INTEIRA "LinkExtractor"
echo 2. No PC destino, execute LinkExtractor.vbs
echo 3. Funciona sem Java instalado (usa JRE da pasta)
echo.
pause