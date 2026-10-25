@echo off
setlocal
set "ROOT=%~dp0"

cd /d "%ROOT%"

echo =============================================
echo      SITRAC - INICIO INTEGRADO CON DOCKER
echo =============================================
echo.

where docker >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Docker no esta instalado o no esta disponible en PATH.
  echo Instala o inicia Docker Desktop y vuelve a ejecutar este archivo.
  pause
  exit /b 1
)

docker info >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Docker Desktop no esta iniciado.
  pause
  exit /b 1
)

echo Construyendo e iniciando SITRAC...
docker compose up -d --build
if errorlevel 1 (
  echo.
  echo [ERROR] No se pudo iniciar SITRAC. Revisa: docker compose logs
  pause
  exit /b 1
)

echo.
echo =============================================
echo SITRAC iniciado correctamente
echo =============================================
echo Frontend:          http://localhost:4200
echo API Gateway:       http://localhost:8080
echo Eureka:            http://localhost:8761
echo RabbitMQ:          http://localhost:15672
echo MySQL:             localhost:3307
echo.
echo Credenciales demo:
echo   admin.sitrac / Admin123*
echo   operador.sitrac / Operador123*
echo   combustible.sitrac / Combustible123*
echo   supervisor.sitrac / Supervisor123*
echo.
echo Para revisar contenedores: docker compose ps
echo Para detener SITRAC:      docker compose down
pause
