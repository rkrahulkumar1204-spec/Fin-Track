@echo off
echo ========================================================
echo        FinTrack - Creating Standalone Executable JAR
echo ========================================================

if not exist bin\com\fintrack\Main.class (
    echo Compiling classes...
    javac -encoding UTF-8 -d bin src\com\fintrack\*.java
)

set JAR_CMD=jar
where jar >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Java\jdk-17\bin\jar.exe" (
        set JAR_CMD="C:\Program Files\Java\jdk-17\bin\jar.exe"
    )
)

%JAR_CMD% cfe FinTrack.jar com.fintrack.Main -C bin com/fintrack

if %ERRORLEVEL% equ 0 (
    echo.
    echo [SUCCESS] FinTrack.jar created successfully!
    echo You can run the application with:
    echo   java -jar FinTrack.jar
) else (
    echo.
    echo [ERROR] JAR packaging failed.
)
pause
