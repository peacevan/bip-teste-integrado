# Script de Build e Execucao Rapida - BIP Sistema (Windows)
# Autor: Peace Van
# Versao: 1.0

param(
    [string]$Action = ""
)

# Configuracoes
$ErrorActionPreference = "Stop"

Write-Host "BIP - Sistema de Beneficios Integrado" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# Funcoes de log colorido
function Log-Info {
    param([string]$Message)
    Write-Host "INFO: $Message" -ForegroundColor Blue
}

function Log-Success {
    param([string]$Message)
    Write-Host "SUCESSO: $Message" -ForegroundColor Green
}

function Log-Warning {
    param([string]$Message)
    Write-Host "AVISO: $Message" -ForegroundColor Yellow
}

function Log-Error {
    param([string]$Message)
    Write-Host "ERRO: $Message" -ForegroundColor Red
}

# Verificar pre-requisitos
function Check-Prerequisites {
    Log-Info "Verificando pre-requisitos..."
    
    try {
        $javaOutput = cmd /c "java -version 2>&1"
        if ($javaOutput -match 'version "([^"]+)"') {
            $javaVersion = $matches[1]
            $majorVersion = [int]($javaVersion.Split('.')[0])
            
            if ($majorVersion -lt 17) {
                Log-Error "Java versao $javaVersion detectada. E necessario Java 17+"
                exit 1
            }
            
            Log-Success "Java $javaVersion detectado"
        } else {
            throw "Java version not found"
        }
    }
    catch {
        Log-Error "Java nao encontrado. Instale Java 17+"
        exit 1
    }
    
    try {
        $mvnOutput = cmd /c "mvn -version 2>&1"
        if ($mvnOutput -like "*Apache Maven*") {
            Log-Success "Maven detectado"
        } else {
            throw "Maven not found"
        }
    }
    catch {
        Log-Error "Maven nao encontrado. Instale Maven 3.8+"
        exit 1
    }
    
    Log-Success "Pre-requisitos OK"
}

# Limpar builds anteriores
function Clean-Build {
    Log-Info "Limpando builds anteriores..."
    
    try {
        & mvn -f ejb-module clean -q
        & mvn -f backend-module clean -q
        Log-Success "Build limpo"
    }
    catch {
        Log-Error "Erro ao limpar build"
        exit 1
    }
}

# Build do modulo EJB
function Build-EJB {
    Log-Info "Construindo modulo EJB..."
    
    try {
        & mvn -f ejb-module clean install -q
        Log-Success "Modulo EJB construido com sucesso"
    }
    catch {
        Log-Error "Falha no build do modulo EJB"
        exit 1
    }
}

# Build do modulo Backend
function Build-Backend {
    Log-Info "Construindo modulo Backend..."
    
    try {
        & mvn -f backend-module clean package -q
        Log-Success "Modulo Backend construido com sucesso"
    }
    catch {
        Log-Error "Falha no build do modulo Backend"
        exit 1
    }
}

# Executar testes
function Run-Tests {
    Log-Info "Executando testes do EJB..."
    
    try {
        & mvn -f ejb-module test -q
        Log-Success "Todos os testes EJB passaram (11/11)"
    }
    catch {
        Log-Error "Alguns testes falharam"
        exit 1
    }
}

# Executar aplicacao
function Run-Application {
    Log-Info "Iniciando aplicacao Spring Boot..."
    Log-Info "API estara disponivel em: http://localhost:8080/api/v1/beneficios"
    Log-Info "Swagger UI em: http://localhost:8080/swagger-ui.html"
    Log-Info "H2 Console em: http://localhost:8080/h2-console"
    Log-Warning "Pressione Ctrl+C para parar"
    Write-Host ""
    
    try {
        & mvn -f backend-module spring-boot:run
    }
    catch {
        Log-Error "Erro ao executar aplicacao"
        exit 1
    }
}

# Menu principal
function Show-Menu {
    Write-Host ""
    Write-Host "Escolha uma opcao:"
    Write-Host "1) Build completo (EJB + Backend)"
    Write-Host "2) Executar testes"
    Write-Host "3) Executar aplicacao"
    Write-Host "4) Build completo + Executar"
    Write-Host "5) Limpar build"
    Write-Host "6) Sair"
    Write-Host ""
}

# Função principal
function Main {
    Check-Prerequisites
    
    if ([string]::IsNullOrEmpty($Action)) {
        # Modo interativo
        do {
            Show-Menu
            $choice = Read-Host "Opção"
            
            switch ($choice) {
                "1" {
                    Build-EJB
                    Build-Backend
                    Log-Success "Build completo finalizado!"
                }
                "2" {
                    Run-Tests
                }
                "3" {
                    Run-Application
                }
                "4" {
                    Build-EJB
                    Build-Backend
                    Run-Tests
                    Run-Application
                }
                "5" {
                    Clean-Build
                }
                "6" {
                    Log-Info "Ate logo!"
                    return
                }
                default {
                    Log-Error "Opcao invalida"
                }
            }
        } while ($choice -ne "6")
    }
    else {
        # Modo não interativo
        switch ($Action.ToLower()) {
            "build" {
                Build-EJB
                Build-Backend
            }
            "test" {
                Run-Tests
            }
            "run" {
                Run-Application
            }
            "all" {
                Build-EJB
                Build-Backend
                Run-Tests
                Run-Application
            }
            "clean" {
                Clean-Build
            }
            default {
                Write-Host "Uso: .\run.ps1 [build|test|run|all|clean]"
                Write-Host "Ou execute sem parametros para modo interativo"
                exit 1
            }
        }
    }
}

# Executar funcao principal
try {
    Main
}
catch {
    Log-Error "Erro inesperado: $($_.Exception.Message)"
    exit 1
}