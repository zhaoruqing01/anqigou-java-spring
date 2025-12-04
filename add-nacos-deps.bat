@echo off
echo 为订单、支付、物流服务添加Nacos依赖...

REM 这个脚本用于批量添加Nacos依赖到pom.xml
echo 请手动编辑以下服务的pom.xml文件:
echo - anqigou-order-service/pom.xml
echo - anqigou-payment-service/pom.xml
echo - anqigou-logistics-service/pom.xml
echo.
echo 在 spring-boot-starter-data-redis 依赖后添加:
echo.
echo ^<^!-- Nacos 服务注册 --^>
echo ^<dependency^>
echo     ^<groupId^>com.alibaba.cloud^</groupId^>
echo     ^<artifactId^>spring-cloud-starter-alibaba-nacos-discovery^</artifactId^>
echo ^</dependency^>
echo.
echo ^<^!-- Nacos 配置中心 --^>
echo ^<dependency^>
echo     ^<groupId^>com.alibaba.cloud^</groupId^>
echo     ^<artifactId^>spring-cloud-starter-alibaba-nacos-config^</artifactId^>
echo ^</dependency^>
echo.
pause
