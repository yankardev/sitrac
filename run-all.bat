@echo off
setlocal
set "ROOT=%~dp0"

echo =============================================
echo      SITRAC - INICIO COMPLETO DEL SISTEMA
echo =============================================
echo.

echo Verificando Java...
where java >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Java no esta instalado o no esta en PATH.
  pause
  exit /b 1
)

echo Verificando Node.js...
where node >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Node.js no esta instalado o no esta en PATH.
  pause
  exit /b 1
)

echo Verificando npm...
where npm >nul 2>&1
if errorlevel 1 (
  echo [ERROR] npm no esta instalado o no esta en PATH.
  pause
  exit /b 1
)

echo.
echo Configuracion de MySQL para esta maquina.
set "MYSQL_USER=root"
set /p MYSQL_USER_INPUT=Usuario MySQL [root]: 
if not "%MYSQL_USER_INPUT%"=="" set "MYSQL_USER=%MYSQL_USER_INPUT%"
set /p MYSQL_PASSWORD=Contrasena MySQL [Enter si no tiene]: 

echo.
echo IMPORTANTE: MySQL debe estar iniciado en localhost:3306.
echo Se abriran ventanas separadas para cada microservicio y el frontend.
echo.

start "SITRAC auth-service 8081" /D "%ROOT%" cmd /k "call mvnw.cmd -pl auth-service spring-boot:run"
start "SITRAC cliente-service 8082" /D "%ROOT%" cmd /k "call mvnw.cmd -pl cliente-service spring-boot:run"
start "SITRAC pedido-service 8083" /D "%ROOT%" cmd /k "call mvnw.cmd -pl pedido-service spring-boot:run"
start "SITRAC conductor-service 8084" /D "%ROOT%" cmd /k "call mvnw.cmd -pl conductor-service spring-boot:run"
start "SITRAC flota-service 8085" /D "%ROOT%" cmd /k "call mvnw.cmd -pl flota-service spring-boot:run"
start "SITRAC programacion-service 8086" /D "%ROOT%" cmd /k "call mvnw.cmd -pl programacion-service spring-boot:run"
start "SITRAC viaje-service 8087" /D "%ROOT%" cmd /k "call mvnw.cmd -pl viaje-service spring-boot:run"
start "SITRAC mantenimiento-service 8088" /D "%ROOT%" cmd /k "call mvnw.cmd -pl mantenimiento-service spring-boot:run"
start "SITRAC somma-service 8089" /D "%ROOT%" cmd /k "call mvnw.cmd -pl somma-service spring-boot:run"
start "SITRAC combustible-service 8090" /D "%ROOT%" cmd /k "call mvnw.cmd -pl combustible-service spring-boot:run"

start "SITRAC Angular 4200" /D "%ROOT%sitrac-frontend" cmd /k "if not exist node_modules call npm install & call npm start"

echo.
echo Procesos lanzados.
echo Backend: 8081 al 8090
echo Frontend: http://localhost:4200
echo Usuario MySQL: %MYSQL_USER%
echo.
echo La primera ejecucion puede demorar mientras Maven y npm descargan dependencias.
pause
