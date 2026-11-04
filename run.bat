@echo off
echo ========================================================
echo   FinTrack - Personal Finance Manager Desktop App
echo ========================================================

if not exist bin\com\fintrack\Main.class (
    echo Binaries not found. Compiling first...
    call compile.bat
)

start javaw -cp bin com.fintrack.Main

