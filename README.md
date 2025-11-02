# 🏦 BIP - Sistema de Benefícios Integrado

[![CI](https://github.com/peacevan/bip-teste-integrado/workflows/CI/badge.svg)](https://github.com/peacevan/bip-teste-integrado/actions)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg)](https://spring.io/projects/spring-boot)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-4.0.1-orange.svg)](https://jakarta.ee/)

Sistema integrado para gestão de benefícios desenvolvido com **arquitetura híbrida** combinando **Jakarta EE EJB** para lógica de negócio e **Spring Boot** para API REST.

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Como Executar](#-como-executar)
- [API Documentation](#-api-documentation)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Desenvolvimento](#-desenvolvimento)
- [Contribuição](#-contribuição)

## 🎯 Visão Geral

O **BIP (Benefícios Integrados Platform)** é um sistema completo para gestão de benefícios que demonstra a integração entre diferentes tecnologias Java Enterprise:

- **Backend EJB**: Lógica de negócio robusta com controle de transações
- **Backend Spring Boot**: API REST moderna para integração frontend
- **Frontend Angular**: Interface web responsiva (em desenvolvimento)
- **Database H2**: Banco em memória para desenvolvimento e testes

### ✨ Funcionalidades Principais

- 🔍 **CRUD Completo** de benefícios
- 💰 **Transferências** entre benefícios com validação de saldo
- 🔒 **Controle de Concorrência** com locking otimista e pessimista
- 📊 **Relatórios** e consultas avançadas
- 🛡️ **Validação** robusta de dados
- 📖 **Documentação** automática com Swagger
- 🧪 **Testes** abrangentes (11/11 passando)

## 🏗️ Arquitetura

```
┌─── Frontend (Angular) ─────────────── [Em Desenvolvimento]
│    ├── Components & Services
│    ├── HTTP Client
│    └── User Interface
│
├─── Backend Module (Spring Boot) ──── [✅ COMPLETO]
│    ├── REST Controllers (/api/v1/*)
│    ├── DTOs (Request/Response)
│    ├── Service Layer (Integração EJB)
│    ├── JPA Repositories
│    ├── Global Exception Handler
│    └── OpenAPI/Swagger Documentation
│
├─── EJB Module (Jakarta EE) ────────── [✅ COMPLETO]
│    ├── Business Logic (BeneficioEjbService)
│    ├── JPA Entities (Beneficio)
│    ├── Transaction Management (@Transactional)
│    ├── Concurrency Control (Optimistic/Pessimistic)
│    └── Custom Exceptions
│
└─── Database Layer (H2) ────────────── [✅ CONFIGURADO]
     ├── Schema DDL (schema.sql)
     ├── Initial Data (data.sql)
     └── Console Web (/h2-console)
```

## 🛠️ Tecnologias

### Backend Stack
- **Java 17** - LTS Version
- **Spring Boot 3.2.5** - Framework principal para API REST
- **Jakarta EE 4.0.1** - Enterprise Java Beans para lógica de negócio
- **Spring Data JPA** - Abstração de persistência
- **Hibernate 6.4.4** - ORM Implementation
- **H2 Database** - Banco em memória para desenvolvimento
- **Maven** - Gerenciamento de dependências e build

### Qualidade & Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking framework
- **SpringDoc OpenAPI** - Documentação automática da API
- **Bean Validation** - Validação de dados
- **SLF4J + Logback** - Sistema de logging

### CI/CD & DevOps
- **GitHub Actions** - Pipeline de CI/CD
- **Docker** (planejado) - Containerização
- **Maven Surefire** - Execução de testes

## 🚀 Como Executar

### Pré-requisitos

- ☕ **Java 17** ou superior
- 📦 **Maven 3.8+**
- 🌐 **Git**

### 1. Clone o Repositório

```bash
git clone https://github.com/peacevan/bip-teste-integrado.git
cd bip-teste-integrado
```

### 2. Build do Projeto Completo

```bash
# Primeiro, construir o módulo EJB (dependência)
mvn -f ejb-module clean install

# Depois, construir o módulo Spring Boot
mvn -f backend-module clean package
```

### 3. Executar a Aplicação

```bash
# Executar o servidor Spring Boot
mvn -f backend-module spring-boot:run
```

A aplicação estará disponível em:
- 🌐 **API REST**: http://localhost:8080/api/v1/beneficios
- 📖 **Swagger UI**: http://localhost:8080/swagger-ui.html
- 🗄️ **H2 Console**: http://localhost:8080/h2-console

### 4. Acessar o Banco H2

- **URL**: `jdbc:h2:mem:testdb`
- **Usuário**: `sa`
- **Senha**: *(vazio)*

## 📖 API Documentation

### Base URL
```
http://localhost:8080/api/v1/beneficios
```

### 🔗 Endpoints Disponíveis

| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| `GET` | `/` | Listar todos os benefícios | ✅ |
| `GET` | `/active` | Listar apenas benefícios ativos | ✅ |
| `GET` | `/{id}` | Buscar benefício por ID | ✅ |
| `GET` | `/search?nome={nome}` | Buscar por nome | ✅ |
| `GET` | `/count` | Contar benefícios ativos | ✅ |
| `POST` | `/` | Criar novo benefício | ✅ |
| `PUT` | `/{id}` | Atualizar benefício completo | ✅ |
| `PATCH` | `/{id}/deactivate` | Desativar benefício | ✅ |
| `DELETE` | `/{id}` | Remover benefício | ✅ |
| `POST` | `/transfer` | Transferir valor entre benefícios | ✅ |

### 📋 Estrutura dos DTOs

#### BeneficioRequest (POST/PUT)
```json
{
  "nome": "string (obrigatório)",
  "descricao": "string (opcional)",
  "valor": "decimal (obrigatório)",
  "ativo": "boolean (padrão: true)"
}
```

#### BeneficioResponse (GET)
```json
{
  "id": "long",
  "nome": "string",
  "descricao": "string",
  "valor": "decimal",
  "ativo": "boolean",
  "dataCriacao": "datetime",
  "dataAtualizacao": "datetime",
  "version": "long"
}
```

#### TransferRequest (POST /transfer)
```json
{
  "fromId": "long (ID do benefício origem)",
  "toId": "long (ID do benefício destino)",
  "amount": "decimal (valor a transferir)"
}
```

## 💡 Exemplos de Uso

### 1. Listar Todos os Benefícios

```bash
curl -X GET "http://localhost:8080/api/v1/beneficios" \
     -H "Accept: application/json"
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome": "Vale Refeição",
    "descricao": "Benefício para alimentação",
    "valor": 500.00,
    "ativo": true,
    "dataCriacao": "2025-11-02T10:00:00",
    "dataAtualizacao": "2025-11-02T10:00:00",
    "version": 1
  }
]
```

### 2. Criar Novo Benefício

```bash
curl -X POST "http://localhost:8080/api/v1/beneficios" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d '{
       "nome": "Vale Transporte",
       "descricao": "Auxílio transporte mensal",
       "valor": 200.00,
       "ativo": true
     }'
```

**Resposta:**
```json
{
  "id": 2,
  "nome": "Vale Transporte",
  "descricao": "Auxílio transporte mensal",
  "valor": 200.00,
  "ativo": true,
  "dataCriacao": "2025-11-02T10:30:00",
  "dataAtualizacao": "2025-11-02T10:30:00",
  "version": 1
}
```

### 3. Buscar Benefício por ID

```bash
curl -X GET "http://localhost:8080/api/v1/beneficios/1" \
     -H "Accept: application/json"
```

### 4. Atualizar Benefício

```bash
curl -X PUT "http://localhost:8080/api/v1/beneficios/1" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d '{
       "nome": "Vale Refeição Atualizado",
       "descricao": "Benefício para alimentação - valor atualizado",
       "valor": 600.00,
       "ativo": true
     }'
```

### 5. Transferir Valor Entre Benefícios

```bash
curl -X POST "http://localhost:8080/api/v1/beneficios/transfer" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d '{
       "fromId": 1,
       "toId": 2,
       "amount": 100.00
     }'
```

**Resposta:**
```json
"Transferência realizada com sucesso"
```

### 6. Buscar por Nome

```bash
curl -X GET "http://localhost:8080/api/v1/beneficios/search?nome=Vale" \
     -H "Accept: application/json"
```

### 7. Contar Benefícios Ativos

```bash
curl -X GET "http://localhost:8080/api/v1/beneficios/count" \
     -H "Accept: application/json"
```

**Resposta:**
```json
5
```

### 8. Desativar Benefício

```bash
curl -X PATCH "http://localhost:8080/api/v1/beneficios/1/deactivate" \
     -H "Accept: application/json"
```

### 9. Remover Benefício

```bash
curl -X DELETE "http://localhost:8080/api/v1/beneficios/1" \
     -H "Accept: application/json"
```

## 🧪 Testes

### Executar Testes do EJB Module

```bash
mvn -f ejb-module test
```

**Cobertura de Testes EJB:**
- ✅ 11/11 testes passando
- ✅ Validação de parâmetros
- ✅ Validação de saldo
- ✅ Controle de concorrência
- ✅ Casos extremos (edge cases)
- ✅ Tratamento de exceções

### Executar Testes do Backend Module

```bash
mvn -f backend-module test
```

### Executar Todos os Testes

```bash
# Build completo com testes
mvn -f ejb-module clean install
mvn -f backend-module clean test
```

## 🔧 Desenvolvimento

### Estrutura do Projeto

```
bip-teste-integrado/
├── ejb-module/                     # Módulo Jakarta EE EJB
│   ├── src/main/java/             # Código fonte EJB
│   ├── src/test/java/             # Testes unitários (11 testes)
│   └── pom.xml                    # Dependências EJB
├── backend-module/                # Módulo Spring Boot
│   ├── src/main/java/             # Código fonte API REST
│   ├── src/main/resources/        # Configurações e scripts SQL
│   └── pom.xml                    # Dependências Spring Boot
├── frontend/                      # Módulo Angular (futuro)
├── docs/                          # Documentação adicional
├── .github/workflows/             # Pipeline CI/CD
└── README.md                      # Este arquivo
```

### Configuração de Desenvolvimento

1. **IDE Recomendada**: IntelliJ IDEA ou Eclipse com suporte Jakarta EE
2. **JDK**: OpenJDK 17 (Temurin distribution)
3. **Maven**: 3.8+ com perfil de desenvolvimento
4. **Database**: H2 em memória (desenvolvimento) / PostgreSQL (produção)

### Variables de Ambiente

```bash
# Desenvolvimento
export JAVA_HOME=/path/to/jdk-17
export MAVEN_OPTS="-Xmx512m"

# Database (opcional para produção)
export DB_URL=jdbc:postgresql://localhost:5432/bip
export DB_USERNAME=bip_user
export DB_PASSWORD=bip_password
```

### Perfis Maven

```bash
# Desenvolvimento (padrão)
mvn spring-boot:run

# Produção
mvn spring-boot:run -Pproduction

# Testes
mvn test -Ptest
```

## 🤝 Contribuição

### Como Contribuir

1. 🍴 **Fork** o projeto
2. 🌿 **Crie uma branch** para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. ✨ **Commit** suas mudanças (`git commit -m 'feat: adicionar nova funcionalidade'`)
4. 📤 **Push** para a branch (`git push origin feature/nova-funcionalidade`)
5. 🔃 **Abra um Pull Request**

### Convenções de Commit

Utilizamos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` nova funcionalidade
- `fix:` correção de bug
- `docs:` documentação
- `style:` formatação
- `refactor:` refatoração
- `test:` testes
- `chore:` manutenção

### Guidelines de Desenvolvimento

- ✅ **Testes**: Manter cobertura de testes alta (>80%)
- 📖 **Documentação**: Documentar APIs e métodos públicos
- 🎯 **Code Style**: Seguir convenções Java padrão
- 🔍 **Code Review**: Todo código deve ser revisado
- 🚀 **CI/CD**: Todos os testes devem passar no pipeline

## 📜 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Autores

- **Peace Van** - *Desenvolvedor Principal* - [peacevan](https://github.com/peacevan)

## 🙏 Agradecimentos

- Spring Boot team pela excelente documentação
- Jakarta EE community pelos padrões enterprise
- H2 Database pelos recursos de desenvolvimento
- GitHub Actions pela infraestrutura de CI/CD

---

⭐ **Se este projeto te ajudou, considere dar uma estrela!**

📧 **Tem dúvidas?** Abra uma [issue](https://github.com/peacevan/bip-teste-integrado/issues) ou entre em contato!

🚀 **Ready for production?** Consulte nossa [documentação de deploy](docs/DEPLOY.md)!