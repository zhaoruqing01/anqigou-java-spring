@echo off
chcp 65001 >nul
echo ========================================
echo 安琦购电商平台 - 服务日志查看器
echo ========================================
echo.

:menu
echo 请选择操作:
echo [1] 查看所有服务的最新日志
echo [2] 查看所有服务的错误日志
echo [3] 实时监控所有服务错误(持续显示)
echo [4] 查看单个服务日志
echo [5] 检查服务状态
echo [6] 退出
echo.
set /p choice=请输入选项 (1-6): 

if "%choice%"=="1" goto view_all
if "%choice%"=="2" goto view_errors
if "%choice%"=="3" goto monitor_errors
if "%choice%"=="4" goto view_single
if "%choice%"=="5" goto check_status
if "%choice%"=="6" goto end
goto menu

:view_all
cls
echo ========================================
echo 所有服务最新日志 (最后30行)
echo ========================================
echo.
for %%s in (user product order payment logistics gateway) do (
    if exist "anqigou-%%s-service\logs.txt" (
        echo === anqigou-%%s-service ===
        powershell -Command "Get-Content 'anqigou-%%s-service\logs.txt' -Tail 30 -Encoding UTF8"
        echo.
    )
)
pause
goto menu

:view_errors
cls
echo ========================================
echo 所有服务错误日志
echo ========================================
echo.
for %%s in (user product order payment logistics gateway) do (
    if exist "anqigou-%%s-service\logs.txt" (
        echo === anqigou-%%s-service 错误 ===
        findstr /i /c:"error" /c:"exception" /c:"failed" /c:"warn" "anqigou-%%s-service\logs.txt" 2>nul
        echo.
    )
)
echo.
echo === JVM 崩溃日志 ===
if exist "hs_err_*.log" (
    dir /b /o-d hs_err_*.log | findstr /n "^" | findstr "^1:"
    for /f "tokens=2" %%f in ('dir /b /o-d hs_err_*.log ^| findstr /n "^" ^| findstr "^1:"') do (
        echo 最新JVM错误日志: %%f
        type "%%f" | findstr /i "error exception" | more
    )
) else (
    echo 未发现JVM崩溃日志
)
echo.
pause
goto menu

:monitor_errors
cls
echo ========================================
echo 实时监控所有服务错误 (按Ctrl+C停止)
echo ========================================
echo.
echo 监控中...每5秒刷新一次
echo.
:monitor_loop
cls
echo [%date% %time%] 服务错误监控
echo ========================================
for %%s in (user product order payment logistics gateway) do (
    if exist "anqigou-%%s-service\logs.txt" (
        echo === %%s-service ===
        powershell -Command "Get-Content 'anqigou-%%s-service\logs.txt' -Tail 10 -Encoding UTF8 | Select-String -Pattern 'ERROR|WARN|Exception|Failed' -CaseSensitive:$false"
        echo.
    )
)
timeout /t 5 /nobreak >nul
goto monitor_loop

:view_single
cls
echo ========================================
echo 查看单个服务日志
echo ========================================
echo.
echo 可用服务:
echo [1] user-service (用户服务)
echo [2] product-service (商品服务)
echo [3] order-service (订单服务)
echo [4] payment-service (支付服务)
echo [5] logistics-service (物流服务)
echo [6] gateway (网关)
echo [7] 返回主菜单
echo.
set /p svc_choice=请选择服务 (1-7): 

if "%svc_choice%"=="1" set service=user
if "%svc_choice%"=="2" set service=product
if "%svc_choice%"=="3" set service=order
if "%svc_choice%"=="4" set service=payment
if "%svc_choice%"=="5" set service=logistics
if "%svc_choice%"=="6" set service=gateway
if "%svc_choice%"=="7" goto menu

if not defined service goto view_single

cls
echo === anqigou-%service%-service 完整日志 ===
echo.
if exist "anqigou-%service%-service\logs.txt" (
    type "anqigou-%service%-service\logs.txt"
) else (
    echo 未找到日志文件
)
echo.
pause
goto menu

:check_status
cls
echo ========================================
echo 服务状态检查
echo ========================================
echo.
echo 正在运行的Java进程:
jps -l | findstr "anqigou"
echo.
echo 端口占用情况:
netstat -ano | findstr "808[0-5]" | findstr "LISTENING"
echo.
echo 说明:
echo   8080 - 网关服务
echo   8081 - 用户服务
echo   8082 - 商品服务
echo   8083 - 订单服务
echo   8084 - 支付服务
echo   8085 - 物流服务
echo.
pause
goto menu

:end
echo 退出日志查看器
exit /b 0
