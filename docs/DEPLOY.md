# 🚀 Guia de Deploy - BIP Sistema

Este documento fornece instruções detalhadas para deploy do sistema BIP em diferentes ambientes.

## 📋 Ambientes Suportados

- **Desenvolvimento** - Configuração local com H2
- **Homologação** - Ambiente de testes com PostgreSQL
- **Produção** - Deploy em servidor com banco dedicado

## 🔧 Pré-requisitos para Produção

### Sistema Operacional
- **Linux**: Ubuntu 20.04+ ou CentOS 8+
- **Windows**: Windows Server 2019+
- **macOS**: macOS 11+

### Software Necessário
- ☕ **Java 17** (OpenJDK Temurin recomendado)
- 🗄️ **PostgreSQL 13+** ou **MySQL 8+**
- 🌐 **Nginx** (proxy reverso)
- 🔧 **Maven 3.8+** (para build)
- 🐳 **Docker** (opcional)

## 📦 Deploy Tradicional

### 1. Preparação do Servidor

```bash
# Instalar Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# Verificar instalação
java -version
```

### 2. Configuração do Banco de Dados

```sql
-- PostgreSQL
CREATE DATABASE bip_production;
CREATE USER bip_user WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE bip_production TO bip_user;
```

### 3. Build do Projeto

```bash
# Clone e build
git clone https://github.com/peacevan/bip-teste-integrado.git
cd bip-teste-integrado

# Build EJB module
mvn -f ejb-module clean install

# Build backend module
mvn -f backend-module clean package -Pproduction
```

### 4. Configuração de Produção

Criar `application-production.yml`:

```yaml
spring:
  profiles:
    active: production
  
  datasource:
    url: jdbc:postgresql://localhost:5432/bip_production
    username: ${DB_USERNAME:bip_user}
    password: ${DB_PASSWORD:secure_password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
  
  sql:
    init:
      mode: never # Não executar scripts em produção

# Configurações de segurança
logging:
  level:
    root: INFO
    com.example.backend: INFO
  
server:
  port: 8080
  servlet:
    context-path: /api
```

### 5. Executar em Produção

```bash
# Executar como serviço
java -jar backend-module/target/backend-module-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=production \
     --server.port=8080
```

## 🐳 Deploy com Docker

### 1. Dockerfile

```dockerfile
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copiar dependências
COPY ejb-module/target/*.jar ejb-module.jar
COPY backend-module/target/*.jar backend-module.jar

# Expor porta
EXPOSE 8080

# Comando de execução
CMD ["java", "-jar", "backend-module.jar", "--spring.profiles.active=production"]
```

### 2. Docker Compose

```yaml
version: '3.8'

services:
  bip-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DB_USERNAME=bip_user
      - DB_PASSWORD=secure_password
      - DB_URL=jdbc:postgresql://db:5432/bip_production
    depends_on:
      - db
    restart: unless-stopped
  
  db:
    image: postgres:13
    environment:
      POSTGRES_DB: bip_production
      POSTGRES_USER: bip_user
      POSTGRES_PASSWORD: secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: unless-stopped
  
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - bip-backend
    restart: unless-stopped

volumes:
  postgres_data:
```

### 3. Executar com Docker

```bash
# Build e execução
docker-compose up -d

# Verificar logs
docker-compose logs -f bip-backend

# Parar serviços
docker-compose down
```

## 🌐 Configuração Nginx

### nginx.conf

```nginx
events {
    worker_connections 1024;
}

http {
    upstream bip-backend {
        server bip-backend:8080;
    }
    
    server {
        listen 80;
        server_name your-domain.com;
        
        # Redirecionar para HTTPS
        return 301 https://$server_name$request_uri;
    }
    
    server {
        listen 443 ssl;
        server_name your-domain.com;
        
        # Configurações SSL
        ssl_certificate /etc/ssl/certs/your-cert.pem;
        ssl_certificate_key /etc/ssl/private/your-key.pem;
        
        # Proxy para backend
        location /api/ {
            proxy_pass http://bip-backend/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # Servir frontend (quando implementado)
        location / {
            root /var/www/html;
            try_files $uri $uri/ /index.html;
        }
    }
}
```

## 🔍 Monitoramento

### Health Check

```bash
# Verificar saúde da aplicação
curl -X GET "http://localhost:8080/actuator/health"
```

### Métricas (Adicionar Spring Boot Actuator)

```yaml
# application-production.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

## 🔐 Segurança

### 1. Variáveis de Ambiente

```bash
# Criar arquivo .env (não versionar!)
DB_USERNAME=bip_user
DB_PASSWORD=very_secure_password_here
JWT_SECRET=your_jwt_secret_key_here
ADMIN_PASSWORD=admin_secure_password
```

### 2. Firewall

```bash
# Ubuntu UFW
sudo ufw allow 22        # SSH
sudo ufw allow 80        # HTTP
sudo ufw allow 443       # HTTPS
sudo ufw enable
```

### 3. SSL/TLS

```bash
# Certificado Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

## 🔄 CI/CD com GitHub Actions

### Deploy Automático

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    tags:
      - 'v*.*.*'

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build project
      run: |
        mvn -f ejb-module clean install
        mvn -f backend-module clean package -Pproduction
    
    - name: Deploy to server
      uses: appleboy/ssh-action@v0.1.7
      with:
        host: ${{ secrets.HOST }}
        username: ${{ secrets.USERNAME }}
        key: ${{ secrets.PRIVATE_KEY }}
        script: |
          cd /opt/bip
          git pull origin main
          ./deploy.sh
```

## 📊 Backup

### Backup do Banco

```bash
#!/bin/bash
# backup.sh
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -h localhost -U bip_user bip_production > backup_bip_$DATE.sql
```

### Cron Job

```bash
# Adicionar ao crontab
0 2 * * * /opt/bip/backup.sh
```

## 🚨 Troubleshooting

### Problemas Comuns

1. **Conexão com banco falha**
   ```bash
   # Verificar conectividade
   telnet localhost 5432
   ```

2. **Aplicação não inicia**
   ```bash
   # Verificar logs
   tail -f logs/spring.log
   ```

3. **Performance baixa**
   ```bash
   # Ajustar JVM
   java -Xmx2g -Xms1g -jar backend-module.jar
   ```

### Logs Importantes

```bash
# Logs da aplicação
tail -f /var/log/bip/application.log

# Logs do sistema
journalctl -u bip-backend -f

# Logs do nginx
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

## 📞 Suporte

Para problemas de deploy, consulte:
- 📖 [Documentação Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- 🐳 [Docker Documentation](https://docs.docker.com/)
- 🗄️ [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

💡 **Dica**: Sempre teste o deploy em ambiente de homologação antes da produção!