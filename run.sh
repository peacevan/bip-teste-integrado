#!/bin/bash

# 🚀 Script de Build e Execução Rápida - BIP Sistema
# Autor: Peace Van
# Versão: 1.0

set -e  # Parar em caso de erro

echo "🏦 BIP - Sistema de Benefícios Integrado"
echo "=========================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Verificar pré-requisitos
check_prerequisites() {
    log_info "Verificando pré-requisitos..."
    
    if ! command -v java &> /dev/null; then
        log_error "Java não encontrado. Instale Java 17+"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        log_error "Maven não encontrado. Instale Maven 3.8+"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "Java versão $JAVA_VERSION detectada. É necessário Java 17+"
        exit 1
    fi
    
    log_success "Pré-requisitos OK - Java $JAVA_VERSION"
}

# Limpar builds anteriores
clean_build() {
    log_info "Limpando builds anteriores..."
    mvn -f ejb-module clean -q
    mvn -f backend-module clean -q
    log_success "Build limpo"
}

# Build do módulo EJB
build_ejb() {
    log_info "Construindo módulo EJB..."
    mvn -f ejb-module clean install -q
    
    if [ $? -eq 0 ]; then
        log_success "Módulo EJB construído com sucesso"
    else
        log_error "Falha no build do módulo EJB"
        exit 1
    fi
}

# Build do módulo Backend
build_backend() {
    log_info "Construindo módulo Backend..."
    mvn -f backend-module clean package -q
    
    if [ $? -eq 0 ]; then
        log_success "Módulo Backend construído com sucesso"
    else
        log_error "Falha no build do módulo Backend"
        exit 1
    fi
}

# Executar testes
run_tests() {
    log_info "Executando testes do EJB..."
    mvn -f ejb-module test -q
    
    if [ $? -eq 0 ]; then
        log_success "Todos os testes EJB passaram (11/11)"
    else
        log_error "Alguns testes falharam"
        exit 1
    fi
}

# Executar aplicação
run_application() {
    log_info "Iniciando aplicação Spring Boot..."
    log_info "🌐 API estará disponível em: http://localhost:8080/api/v1/beneficios"
    log_info "📖 Swagger UI em: http://localhost:8080/swagger-ui.html"
    log_info "🗄️  H2 Console em: http://localhost:8080/h2-console"
    log_warning "Pressione Ctrl+C para parar"
    echo ""
    
    mvn -f backend-module spring-boot:run
}

# Menu principal
show_menu() {
    echo ""
    echo "Escolha uma opção:"
    echo "1) 🏗️  Build completo (EJB + Backend)"
    echo "2) 🧪 Executar testes"
    echo "3) 🚀 Executar aplicação"
    echo "4) 🔄 Build completo + Executar"
    echo "5) 🧹 Limpar build"
    echo "6) ❌ Sair"
    echo ""
    read -p "Opção: " choice
}

# Função principal
main() {
    check_prerequisites
    
    if [ $# -eq 0 ]; then
        # Modo interativo
        while true; do
            show_menu
            case $choice in
                1)
                    build_ejb
                    build_backend
                    log_success "Build completo finalizado!"
                    ;;
                2)
                    run_tests
                    ;;
                3)
                    run_application
                    ;;
                4)
                    build_ejb
                    build_backend
                    run_tests
                    run_application
                    ;;
                5)
                    clean_build
                    ;;
                6)
                    log_info "Até logo! 👋"
                    exit 0
                    ;;
                *)
                    log_error "Opção inválida"
                    ;;
            esac
        done
    else
        # Modo não interativo
        case $1 in
            "build")
                build_ejb
                build_backend
                ;;
            "test")
                run_tests
                ;;
            "run")
                run_application
                ;;
            "all")
                build_ejb
                build_backend
                run_tests
                run_application
                ;;
            "clean")
                clean_build
                ;;
            *)
                echo "Uso: $0 [build|test|run|all|clean]"
                echo "Ou execute sem parâmetros para modo interativo"
                exit 1
                ;;
        esac
    fi
}

# Capturar Ctrl+C
trap 'log_warning "Operação cancelada"; exit 130' INT

# Executar função principal
main "$@"