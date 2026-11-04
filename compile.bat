@echo off
echo ========================================================
echo         FinTrack - Compiling Core Java Project
echo ========================================================

if not exist bin mkdir bin
if not exist data mkdir data

javac -encoding UTF-8 -d bin src\com\fintrack\*.java

if %ERRORLEVEL% equ 0 (
    echo.
    echo [SUCCESS] Compilation finished successfully!
    echo Classes compiled to the "bin" directory.
) else (
    echo.
    echo [ERROR] Compilation failed. Please check the errors above.
)
pause

