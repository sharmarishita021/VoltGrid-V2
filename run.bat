@echo off
title VoltGrid V2 - EV Charging & Reservation System
cd /d "%~dp0"

echo ===================================================
echo   Compiling VoltGrid V2 (43 Classes)...
echo ===================================================

if not exist "bin" mkdir bin

javac -d bin -sourcepath src/main/java src/main/java/com/evstation/app/EVStationApp.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===================================================
echo   Launching VoltGrid V2 System...
echo ===================================================
java -cp bin com.evstation.app.EVStationApp

pause
