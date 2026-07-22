@echo off
:: run.bat
:: Double-click this file on Windows to compile and launch the Hotel Reservation System.

echo ==================================================
echo Compiling Hotel Reservation System...
echo ==================================================

:: Ensure bin directory exists
if not exist bin (
    mkdir bin
)

:: Compile all Java files under src/
javac -d bin src/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check for errors above.
    echo.
    pause
    exit /b %errorlevel%
)

echo.
echo [SUCCESS] Compilation completed successfully.
echo Launching dashboard interface...
echo ==================================================

:: Launch the main application
java -cp bin Main
