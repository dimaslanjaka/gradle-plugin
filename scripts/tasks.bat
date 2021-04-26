@echo off
setlocal
cd %~dp0../
set /P task="Input Task : "
gradlew.bat %task%
pause