# Desafio tecnico - Tasker - JTech - Gestor de tarefas

## Sistema TODO List Multi-usuário com Arquitetura Avançada

### 1. Visão Geral da Arquitetura

O sistema **Tasker** foi projetado seguindo uma arquitetura desacoplada e modular, garantindo separação completa entre a interface com o usuário (Frontend) e o motor de regras de negócio (Backend).

#### Backend (Java Spring Boot)
O backend adota o padrão **Clean Architecture Hexagonal** (Ports and Adapters), que protege a regra de negócios (core) de influências e dependências de frameworks externos ou bancos de dados:
*   **Domain (Core)**: Contém as entidades de negócio puras ([User](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/application/core/domains/User.java), [Task](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/application/core/domains/Task.java), [TaskGroup](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/application/core/domains/TaskGroup.java)) e enums ([TaskStatusEnum](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/application/core/domains/TaskStatusEnum.java)), livres de qualquer anotação de persistência (JPA) ou frameworks.
*   **Use Cases (Core)**: Implementa as regras de negócio e fluxos da aplicação (como [TaskUseCase](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/application/core/usecases/TaskUseCase.java), [TaskGroupUseCase](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/application/core/usecases/TaskGroupUseCase.java), e [AuthUseCase](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/application/core/usecases/AuthUseCase.java)).
*   **Ports (Core)**: Interfaces de entrada (Input Gateways) e saída (Output Gateways) que definem o contrato de comunicação entre o Core e os agentes externos.
*   **Adapters (Infrastructure)**:
    *   **Input**: Controllers REST ([AuthController](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/adapters/input/controllers/AuthController.java), [TaskGroupController](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/adapters/input/controllers/TaskGroupController.java), [TaskController](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/adapters/input/controllers/TaskController.java)) recebem requisições HTTP e validam payloads.
    *   **Output**: Mapeamento de dados e persistência utilizando JPA/Hibernate ([TaskGroupAdapter](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/adapters/output/TaskGroupAdapter.java)) encapsulando os Repositories JPA em banco PostgreSQL.
    *   **Security (JWT)**: O [JwtAdapter](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/adapters/JwtAdapter.java) gerencia a verificação e geração de tokens que delimitam a autorização, assegurando que os dados de tarefas sejam isolados por usuário.

#### Frontend (Vue.js 3)
O frontend está estruturado como uma **Single Page Application (SPA)** modular focada em tipagem forte e alto desempenho:
1.  **Views & Componentes**: Implementado utilizando a **Composition API** e `<script setup>`. O painel principal ([VisualizarTarefaView.vue](file:///Users/italosousa/desafio-jtech/fullstack2-jtech/jtech-tasklist-frontend/src/views/VisualizarTarefaView.vue)) gerencia as tarefas em colunas interativas Kanban reativas com funcionalidade de arrastar e soltar (Drag & Drop).
2.  **Gerenciamento de Estado (Pinia)**: Controla o estado de autenticação (JWT) de forma persistente, preservando a sessão ativa após recarregamentos.
3.  **Services**: Uma camada centralizada para requisições HTTP (API Client) contendo tratamento e interceptação automática para adicionar os cabeçalhos de autenticação JWT (`Bearer <token>`).

### 2. Stack Tecnológica

*   **Backend**:
    *   **Java 21** e **Spring Boot 3.4.0**: Escolhidos por sua estabilidade, injeção de dependências robusta e ecossistema maduro.
    *   **Spring Security & JWT**: Essencial para a proteção e isolamento multi-usuário dos dados.
    *   **PostgreSQL & Hibernate (JPA)**: Banco relacional robusto com excelente suporte a transações ACID e consultas estruturadas.
    *   **JUnit 5, Mockito & AssertJ**: Garantia de alta cobertura e estabilidade das regras de negócios.
*   **Frontend**:
    *   **Vue 3 (Composition API)** e **TypeScript**: Desenvolvimento moderno com tipagem estática e reatividade eficiente.
    *   **Pinia**: Gerenciador de estado moderno e otimizado para Vue 3.
    *   **CSS Customizado**: CSS customizado altamente performático para a interface fluida do painel Kanban.
    *   **NeonDB PostGreSQL**: Resolvi manter um banco de dados real mesmo que a nível de teste para melhor comunicação e manter o mais real possível

### 3. Como Rodar Localmente

#### Requisitos
- JDK 21 instalado
- Node.js (v18+) e Yarn instalados
- Banco PostgreSQL ativo (ou use a string de conexão configurada em nuvem)
- Use o banco de dados de teste disponível na string de conexão no application.properties

#### Backend
1. Entre na pasta do backend:
   ```bash
   cd jtech-tasklist-backend
   ```
2. Configure a variável de ambiente `JWT_SECRET` (pode exportar no terminal ou colocar no arquivo `.env`):
   ```bash
   export JWT_SECRET=sua-chave-secreta-muito-segura-e-longa-com-mais-de-256-bits
   ```
3. Execute o servidor Spring Boot:
   ```bash
   ./gradlew bootRun
   ```

#### Frontend
1. Entre na pasta do frontend:
   ```bash
   cd jtech-tasklist-frontend
   ```
2. Instale as dependências:
   ```bash
   yarn install
   ```
3. Execute o servidor de desenvolvimento:
   ```bash
   yarn dev
   ```

### 4. Como Rodar os Testes (Backend)
Para rodar a suíte de testes unitários do backend, execute o seguinte comando na pasta `jtech-tasklist-backend`:
```bash
./gradlew test
```

### 5. Estrutura de Pastas Detalhada (Backend)
```text
jtech-tasklist-backend/src/main/java/br/com/jtech/tasklist/
├── adapters/                  # Interface Adapters (Web Controllers, DB Adapters, JWT Services)
│   ├── input/controllers/     # REST Endpoints (Auth, Task, TaskGroup)
│   └── output/                # Implementações concretas de Gateways (JPA, DB repositories)
├── application/               # Core Application Layer
│   ├── core/                  # Regras de Negócio e Domínio
│   │   ├── domains/           # Entidades Puras (User, Task, TaskGroup)
│   │   └── usecases/          # Casos de Uso (AuthUseCase, TaskUseCase, TaskGroupUseCase)
│   └── ports/                 # Interfaces de fronteira (Input e Output Gateways)
└── config/                    # Configurações de frameworks e injeção de dependências
```

### 6. Decisões Técnicas Aprofundadas
*   **Clean Architecture**: Optou-se por isolar a lógica de domínio (`application/core`) para que mudanças futuras no banco de dados (ex. migrar para MongoDB) ou frameworks HTTP exijam apenas alterações na camada de `adapters`, sem tocar nas regras de negócio.
*   **Controle de Propriedade**: Cada requisição de alteração/exclusão valida se o ID do recurso solicitado pertence ao `userId` extraído do token JWT correspondente à sessão do usuário logado.

### 7. Melhorias e Roadmap
*   **Conteinerização**: Adicionar suporte a `Docker` e `Docker Compose` para inicializar a aplicação e banco de dados localmente de forma automatizada.
*   **Testes de Integração E2E**: Implementar testes automatizados end-to-end com WebTestClient no backend e Vitest/Cypress no frontend.

```.env
PORT=8081
DS_URL=ep-snowy-cloud-ac29xlw0-pooler.sa-east-1.aws.neon.tech
DS_DATABASE=neondb
DS_USER=neondb_owner
DS_PASS=npg_HXu1ndyWj4vi
JWT_SECRET=JTechTaskListSecretKeyMustBeAtLeast32BytesLong

```