# Instruções - CRUD de Benefícios

## 🎯 Funcionalidades Implementadas

### ✅ CRUD Completo
- **Criar** novo benefício
- **Listar** todos os benefícios com busca por nome
- **Editar** benefício existente
- **Excluir** benefício
- **Transferir** valores entre benefícios

## 📁 Arquivos Atualizados/Criados

### Models
- `src/app/models/beneficio.ts` - Interfaces Beneficio, BeneficioRequest, TransferRequest

### Services
- `src/app/services/beneficio.service.ts` - Métodos: `findAll()`, `findById()`, `create()`, `update()`, `delete()`, `transfer()`

### Components

#### 1. Beneficio List (Lista)
- `src/app/components/beneficio-list/beneficio-list.component.ts`
- `src/app/components/beneficio-list/beneficio-list.component.html`
- `src/app/components/beneficio-list/beneficio-list.component.scss`

**Funcionalidades:**
- Listagem de todos os benefícios
- Busca por nome
- Botões: Criar, Editar, Transferir, Excluir
- Atualizar lista

#### 2. Beneficio Form (Criar/Editar)
- `src/app/components/beneficio-form/beneficio-form.component.ts`
- `src/app/components/beneficio-form/beneficio-form.component.html`
- `src/app/components/beneficio-form/beneficio-form.component.scss`

**Funcionalidades:**
- Formulário reativo com validações
- Modo criar (sem ID na rota)
- Modo editar (com ID na rota)
- Campos: Nome, Descrição, Valor, Ativo
- Validações: Nome (obrigatório, mín. 3 caracteres), Valor (obrigatório, >= 0)

#### 3. Transfer Form (Transferência)
- `src/app/components/transfer-form/transfer-form.component.ts`
- `src/app/components/transfer-form/transfer-form.component.html`
- `src/app/components/transfer-form/transfer-form.component.scss`

**Funcionalidades:**
- Transferir valores entre benefícios
- Pré-seleção do benefício origem (quando vem da lista)
- Validações e preview da transferência
- Feedback de sucesso/erro com SnackBar

### Rotas
- `src/app/app.routes.ts`

**Rotas configuradas:**
```typescript
/beneficios                    → Lista de benefícios
/beneficios/create            → Criar novo benefício
/beneficios/edit/:id          → Editar benefício
/beneficios/transfer/:id      → Transferir a partir de um benefício
/transferencia                → Transferência livre
```

### App Component
- `src/app/app.component.html` - Menu lateral atualizado com link "Novo Benefício"

## 🚀 Como Executar

### 1. Instalar Dependências
```bash
cd /workspaces/bip-teste-integrado/frontend-module
npm install
```

### 2. Executar o Frontend
```bash
npm start
# ou
ng serve
```

O frontend estará disponível em: **http://localhost:4200**

### 3. Verificar Backend
Certifique-se de que o backend está rodando em: **http://localhost:8080**

## 🔗 Integração com Backend

O frontend consome a API REST do backend nos seguintes endpoints:

- `GET /api/v1/beneficios` - Listar todos
- `GET /api/v1/beneficios/{id}` - Buscar por ID
- `GET /api/v1/beneficios/search?nome={nome}` - Buscar por nome
- `POST /api/v1/beneficios` - Criar novo
- `PUT /api/v1/beneficios/{id}` - Atualizar
- `DELETE /api/v1/beneficios/{id}` - Excluir
- `POST /api/v1/beneficios/transfer` - Transferir valores

## 📋 Estrutura dos Dados

### Beneficio
```typescript
{
  id?: number;
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
  version?: number;
}
```

### BeneficioRequest (Criar/Editar)
```typescript
{
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
}
```

### TransferRequest
```typescript
{
  fromId: number;    // ID do benefício origem
  toId: number;      // ID do benefício destino
  amount: number;    // Valor a transferir
}
```

## 🎨 Tecnologias Utilizadas

- **Angular 18** (Standalone Components)
- **Angular Material** (UI Components)
- **Reactive Forms** (Formulários)
- **RxJS** (Observable streams)
- **TypeScript**

## 📝 Navegação

### Menu Lateral
- **Lista de Benefícios** - Ver todos os benefícios
- **Novo Benefício** - Criar novo benefício
- **Transferência** - Transferir valores entre benefícios

### Fluxo de Uso

1. **Criar Benefício**
   - Clicar em "Novo Benefício" no menu ou botão na lista
   - Preencher o formulário
   - Clicar em "Criar"

2. **Editar Benefício**
   - Na lista, clicar no botão "Editar" (ícone de lápis)
   - Modificar os dados
   - Clicar em "Atualizar"

3. **Excluir Benefício**
   - Na lista, clicar no botão "Excluir" (ícone de lixeira)
   - Confirmar a exclusão

4. **Transferir Valores**
   - Na lista, clicar no botão "Transferir" (ícone de setas)
   - Selecionar benefício destino e valor
   - Clicar em "Realizar Transferência"

## ✅ Validações Implementadas

- Nome: obrigatório, mínimo 3 caracteres
- Valor: obrigatório, deve ser >= 0
- Transferência: benefício origem ≠ destino, valor > 0

## 🎯 Próximos Passos (Opcional)

- [ ] Adicionar paginação na lista
- [ ] Filtros avançados
- [ ] Confirmação antes de excluir
- [ ] Histórico de transferências
- [ ] Dashboard com estatísticas
- [ ] Testes unitários e E2E
