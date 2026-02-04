# Balance API - Teste Técnico

Este projeto é uma API REST desenvolvida em Java com Spring Boot para processamento de transações financeiras e gestão de saldos, consumindo mensagens de uma fila SQS que foi
dada no teste.

No projeto foco em **concorrência**, **integridade de dados** e **performance**, aproximando o que consegui de um cenário bancário.

---

## Tecnologias aplicadas

- **Java 21**
- **Spring Boot 3.5.10**
- **Maven**
- **PostgreSQL** (Bd relacional)
- **Spring Cloud AWS / SQS**
- **Docker & Docker Compose**
- **Lombok** (evitar muito boilerplate)
- **SpringDoc OpenAPI** (swagger)

---

## executar no ambiente local

### Pré-requisitos
1. **Java JDK 21** instalado.
2. **Docker** e **Docker Compose** instalados e em execução.
3. **PostgreSQL**: configurei para conectar em um banco local (`localhost:5432`).
   - Banco: `balance`
   - Usuário: `balance`
   - Senha: `balance`
   *(Certifique-se de rodar a aplicação ao já ter subido a infra (docker-compose up -d)).*

### etapas para replicar

1. **Clonar o Repositório**
   ```bash
   git clone https://github.com/paulocgdb/balance-api.git
   cd balance-api
   ```

2. **Iniciar a Infraestrutura (SQS + Gerador do teste)**
   rodar o Docker Compose para subir o localStack e o container para gerar as transações:
   ```bash
   docker-compose up -d
   ```
   * o LocalStack estará rodando na porta `4566`.

3. **Executar a Aplicação**
   Caso não rode pela IDE, utilize o Maven Wrapper incluído no projeto:
   - **Linux/Mac**:
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Windows (PowerShell)**:
     ```powershell
     .\mvnw spring-boot:run
     ```

4. **Acessar a Documentação (Swagger)**
   Com a aplicação em execução, acesse:
    [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

##  Pontos da implementação

Aqui estão alguns pontos sobre as decisões que tomei, conforme pedido no teste:

### 1. Concorrência e Integridade
Pra garantir que o saldo não fique inconsistente com milhares de requisições:
- **Lock Pessimista**: Usei `PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`) no repository. Isso garante que só uma thread mexa no saldo da conta por vez, evitando "lost updates" e afins.
- **Retry na Criação**: Se duas mensagens tentarem criar a conta ao mesmo tempo, trato a `DataIntegrityViolationException` e tento de novo. Assim a segunda thread acha a conta já criada e segue o fluxo de atualização.

### 2. Performance (Paralelismo)
- Configurei o listener do SQS pra processar 10 mensagens em paralelo. Isso ajuda a dar vazão na fila processando contas diferentes ao mesmo tempo, em vez de criar um gargalo.

### 3. Idempotência
- Pra evitar processar a mesma transação duas vezes (já que o SQS garante entrega pelo menos uma vez), verifico se o id da transação já existe antes de fazer qualquer coisa.

### 4. Logs
- Deixei os logs focados em rastreabilidade (saber o que aconteceu com a transação X na conta Y) sem sujar o console com dados sensíveis.

### 5. Estrutura e Testes
- Segui a arquitetura padrão em camadas
- Criei testes para assertividade e maior confiabilidade no fluxo do projeto