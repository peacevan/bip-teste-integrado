# 🚀 Script de Build e Execução Rápida - BIP Sistema (Windows)
# Autor: Peace Van
# Versão: 1.0

param(
    [string]$Action = ""
)

# Configurações
$ErrorActionPreference = "Stop"

Write-Host "🏦 BIP - Sistema de Benefícios Integrado" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Funções de log colorido
function Log-Info {
    param([string]$Message)
    Write-Host "ℹ️  $Message" -ForegroundColor Blue
}

function Log-Success {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor Green
}

function Log-Warning {
    param([string]$Message)
    Write-Host "⚠️  $Message" -ForegroundColor Yellow
}

function Log-Error {
    param([string]$Message)
    Write-Host "❌ $Message" -ForegroundColor Red
}

# Verificar pré-requisitos
function Check-Prerequisites {
    Log-Info "Verificando pré-requisitos..."
    
    try {
        $javaVersion = & java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString().Split('"')[1] }
        $majorVersion = [int]($javaVersion.Split('.')[0])
        
        if ($majorVersion -lt 17) {
            Log-Error "Java versão $javaVersion detectada. É necessário Java 17+"
            exit 1
        }
        
        Log-Success "Java $javaVersion detectado"
    }
    catch {
        Log-Error "Java não encontrado. Instale Java 17+"
        exit 1
    }
    
    try {
        & mvn -version | Out-Null
        Log-Success "Maven detectado"
    }
    catch {
        Log-Error "Maven não encontrado. Instale Maven 3.8+"
        exit 1
    }
    
    Log-Success "Pré-requisitos OK"
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

# Build do módulo EJB
function Build-EJB {
    Log-Info "Construindo módulo EJB..."
    
    try {
        & mvn -f ejb-module clean install -q
        Log-Success "Módulo EJB construído com sucesso"
    }
    catch {
        Log-Error "Falha no build do módulo EJB"
        exit 1
    }
}

# Build do módulo Backend
function Build-Backend {
    Log-Info "Construindo módulo Backend..."
    
    try {
        & mvn -f backend-module clean package -q
        Log-Success "Módulo Backend construído com sucesso"
    }
    catch {
        Log-Error "Falha no build do módulo Backend"
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

# Executar aplicação
function Run-Application {
    Log-Info "Iniciando aplicação Spring Boot..."
    Log-Info "🌐 API estará disponível em: http://localhost:8080/api/v1/beneficios"
    Log-Info "📖 Swagger UI em: http://localhost:8080/swagger-ui.html"
    Log-Info "🗄️  H2 Console em: http://localhost:8080/h2-console"
    Log-Warning "Pressione Ctrl+C para parar"
    Write-Host ""
    
    try {
        & mvn -f backend-module spring-boot:run
    }
    catch {
        Log-Error "Erro ao executar aplicação"
        exit 1
    }
}

# Menu principal
function Show-Menu {
    Write-Host ""
    Write-Host "Escolha uma opção:"
    Write-Host "1) 🏗️  Build completo (EJB + Backend)"
    Write-Host "2) 🧪 Executar testes"
    Write-Host "3) 🚀 Executar aplicação"
    Write-Host "4) 🔄 Build completo + Executar"
    Write-Host "5) 🧹 Limpar build"
    Write-Host "6) ❌ Sair"
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
                    Log-Info "Até logo! 👋"
                    return
                }
                default {
                    Log-Error "Opção inválida"
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
                Write-Host "Ou execute sem parâmetros para modo interativo"
                exit 1
            }
        }
    }
}

# Capturar Ctrl+C
$Host.UI.RawUI.KeyAvailable = $false
[Console]::TreatControlCAsInput = $false

# Executar função principal
try {
    Main
}
catch {
    Log-Error "Erro inesperado: $($_.Exception.Message)"
    exit 1
}