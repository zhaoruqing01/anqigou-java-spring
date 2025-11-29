@echo off
echo ========================================
echo 安琦购电商平台 - 启动所有后端服务
echo ========================================
echo.

echo [1/6] 启动用户服务 (User Service) - 端口 8081
start "User-Service-8081" cmd /k "cd anqigou-user-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [2/6] 启动商品服务 (Product Service) - 端口 8082
start "Product-Service-8082" cmd /k "cd anqigou-product-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [3/6] 启动订单服务 (Order Service) - 端口 8083
start "Order-Service-8083" cmd /k "cd anqigou-order-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [4/6] 启动支付服务 (Payment Service) - 端口 8084
start "Payment-Service-8084" cmd /k "cd anqigou-payment-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [5/6] 启动物流服务 (Logistics Service) - 端口 8085
start "Logistics-Service-8085" cmd /k "cd anqigou-logistics-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [6/6] 启动网关服务 (Gateway) - 端口 8080
start "Gateway-8080" cmd /k "cd anqigou-gateway && mvn spring-boot:run"

echo.
echo ========================================
echo 所有服务正在启动中...
echo 请等待各个服务完全启动(约1-2分钟)
echo ========================================
echo.
echo 服务端口映射:
echo   - 网关服务: http://localhost:8080
echo   - 用户服务: http://localhost:8081
echo   - 商品服务: http://localhost:8082
echo   - 订单服务: http://localhost:8083
echo   - 支付服务: http://localhost:8084
echo   - 物流服务: http://localhost:8085
echo.
echo 按任意键关闭此窗口...
pause >nul
