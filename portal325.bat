@echo off
setlocal enabledelayedexpansion

echo Using Java version: %JAVA_VERSION%

rem Determine FLEX_HOME based on the current script location
set "PRG=%~dp0"
set "PRGDIR=%PRG%"
cd /d "%PRGDIR%\.."
set "FLEX_HOME=%cd%"
echo %FLEX_HOME%

rem ----- Process the input command ----------------------------------------------
set "CMD="
set "ARGS="

:parseArgs
if "%~1"=="" goto endParse
    if "%~1"=="--start"  set CMD=start
    if "%~1"=="-start"   set CMD=start
    if "%~1"=="start"    set CMD=start
    if "%~1"=="--stop"   set CMD=stop
    if "%~1"=="-stop"    set CMD=stop
    if "%~1"=="stop"     set CMD=stop
    if "%~1"=="--version" set CMD=version
    if "%~1"=="-version"  set CMD=version
    if "%~1"=="version"   set CMD=version
    if "%~1"=="--restart" set CMD=restart
    if "%~1"=="-restart"  set CMD=restart
    if "%~1"=="restart"   set CMD=restart
    shift
    goto parseArgs
:endParse

rem Set classpath
set "PLUGINS_DIR=%FLEX_HOME%\plugins"
set "CLASSPATH="
for %%f in ("%PLUGINS_DIR%\*.jar") do (
    echo %%~nxf | findstr /i "impl" >nul
    if errorlevel 1 (
        if not defined CLASSPATH (
            set "CLASSPATH=%%f"
        ) else (
            set "CLASSPATH=!CLASSPATH!;%%f"
        )
    ) else (
        echo Ignore '%%f'
    )
)

set "JVM_MEM_OPTS=-Xms256m -Xmx512m"
set "JAVA_OPTS=-Djasypt.encryptor.password=flexcore"
set "SPRING_OPTS=-Dloader.path=\"%PLUGINS_DIR%\",\"%FLEX_HOME%\bin\" -Dspring-boot.run.jvmArguments=\"-Duser.timezone=UTC\" -Dspring.config.location=\"file:application.properties\""

if "%CMD%"=="start" (
    for /f "tokens=2 delims=," %%a in ('tasklist /FI "IMAGENAME eq java.exe" /v /fo csv ^| findstr /i "portal325.jar"') do (
        echo Process is already running with PID %%a
        exit /b 0
    )

    echo Using Java memory options: %JVM_MEM_OPTS%
    echo Using Java process options: %JAVA_OPTS%
    echo Using Spring process options: %SPRING_OPTS%
    echo Using Classpath: %CLASSPATH%

    start "portal325" cmd /c java %JVM_MEM_OPTS% -jar "%FLEX_HOME%\bin\portal325.jar" %JAVA_OPTS% %SPRING_OPTS% > "%FLEX_HOME%\logs\catalina.out" 2>&1
    exit /b 0
)

if "%CMD%"=="stop" (
    for /f "tokens=2 delims=," %%a in ('tasklist /FI "IMAGENAME eq java.exe" /v /fo csv ^| findstr /i "portal325.jar"') do (
        echo Killing PID %%a
        taskkill /PID %%a /F
    )
    exit /b 0
)

if "%CMD%"=="restart" (
    for /f "tokens=2 delims=," %%a in ('tasklist /FI "IMAGENAME eq java.exe" /v /fo csv ^| findstr /i "portal325.jar"') do (
        echo Killing PID %%a
        taskkill /PID %%a /F
    )
    echo Restarting...
    start "portal325" cmd /c java -cp "%FLEX_HOME%\bin\portal325.jar;%CLASSPATH%" %JVM_MEM_OPTS% %JAVA_OPTS% %SPRING_OPTS% > "%FLEX_HOME%\logs\catalina.out" 2>&1
    exit /b 0
)

if "%CMD%"=="version" (
    type "%FLEX_HOME%\bin\version.txt"
    type "%FLEX_HOME%\bin\wso2carbon-version.txt"
    exit /b 0
)

echo Invalid command. Use start, stop, restart, or version.
exit /b 1
