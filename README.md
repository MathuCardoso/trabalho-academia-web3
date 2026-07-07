# Bella Fit & Women

Sistema web acadêmico para gerenciamento de uma academia feminina. A aplicação reúne cadastro de alunas e professoras, organização de treinos, controle de matrículas e frequências, autenticação de usuários e dashboards específicos para cada perfil.

## Sobre o projeto

O **Bella Fit & Women** foi desenvolvido para fins acadêmicos com o objetivo de aplicar conceitos de desenvolvimento web full stack.

O sistema possui um backend REST responsável pelas regras de negócio, persistência, autenticação e autorização, além de um frontend responsável pelas telas e interações dos usuários. O acesso é dividido entre os perfis `ADMIN`, `PROFESSORA` e `ALUNA`.

Entre os recursos implementados estão:

- gerenciamento de alunas e professoras;
- criação e acompanhamento de treinos;
- controle de matrículas e vencimentos;
- registro de check-ins e frequências;
- autenticação stateless com JWT;
- controle de acesso por perfil;
- dashboards com informações específicas para cada usuário;
- edição dos próprios dados e da senha;
- pesquisa nas páginas de listagem.

## Tecnologias utilizadas

### Backend

| Tecnologia | Versão identificada | Uso |
| --- | --- | --- |
| Java | 25 | Linguagem principal do backend |
| Spring Boot | 4.0.6 | Configuração e execução da aplicação |
| Spring Web MVC | Gerenciada pelo Spring Boot | API REST |
| Spring Data JPA / Hibernate | Gerenciada pelo Spring Boot | Persistência e mapeamento objeto-relacional |
| Spring Security | Gerenciada pelo Spring Boot | Autenticação e autorização |
| Jakarta Validation | Gerenciada pelo Spring Boot | Validação de DTOs e entidades |
| JJWT | 0.12.6 | Geração e validação de tokens JWT |
| PostgreSQL Driver | Gerenciada pelo Spring Boot | Comunicação com o banco PostgreSQL |
| Lombok | Gerenciada pelo Spring Boot | Redução de código repetitivo nas entidades |
| Maven / Maven Wrapper | Configuração do projeto | Dependências, build e execução |

### Frontend

| Tecnologia | Versão declarada | Uso |
| --- | --- | --- |
| Vue.js | ^3.5.32 | Construção da interface |
| Vue Router | ^5.0.4 | Rotas e proteção de páginas |
| Pinia | ^3.0.4 | Estado de autenticação |
| Vite | ^8.0.8 | Servidor de desenvolvimento e build |
| Tailwind CSS | ^4.3.1 | Classes utilitárias de estilo |
| Lucide Vue | ^1.20.0 | Ícones da interface |
| Maska | ^3.2.0 | Máscaras de campos como CPF e telefone |
| Node.js | ^20.19.0 ou >=22.12.0 | Ambiente de execução do frontend |

### Banco de dados

- PostgreSQL;
- criação e atualização das tabelas pelo Hibernate com `ddl-auto=update`;
- carga inicial idempotente por meio de `DataInitializer`.

## Funcionalidades

### Administrador

- dashboard com totais de alunas, professoras, treinos e matrículas;
- indicadores de matrículas ativas, vencidas e próximas do vencimento;
- cadastro, listagem, pesquisa e edição de alunas e professoras;
- ativação e inativação de alunas e professoras;
- exclusão com confirmação;
- gerenciamento de treinos e seus status;
- gerenciamento de matrículas;
- registro, consulta e exclusão de frequências;
- acesso às listagens administrativas.

### Professora

- dashboard com informações dos próprios treinos, matrículas e alunas vinculadas;
- listagem, pesquisa, criação e edição dos próprios treinos;
- ativação e inativação dos próprios treinos;
- edição dos próprios dados e da senha;
- acesso restrito aos recursos relacionados ao próprio perfil.

### Aluna

- dashboard com matrícula atual, vencimento, treino, descrição do treino e quantidade de check-ins;
- registro de frequência pelo próprio dashboard;
- consulta e pesquisa das próprias frequências;
- edição dos próprios dados e da senha;
- acesso restrito às próprias informações.

### Recursos gerais

- login com credenciais cadastradas;
- emissão e envio de token JWT;
- logout no header da aplicação;
- respostas de erro padronizadas;
- validação de CPF, e-mail e campos obrigatórios;
- opção de mostrar ou ocultar a senha nos formulários;
- interface responsiva para os principais fluxos.

## Entidades do sistema

### `Usuario`

Representa a conta de acesso. Armazena login, senha criptografada, perfil e status. O vínculo cadastral fica em `Aluna` ou `Professora`.

### `Aluna`

Armazena nome, e-mail, telefone, CPF, data de nascimento e status da aluna. Possui uma relação de um para um com `Usuario`.

### `Professora`

Armazena nome, e-mail, CREF, especialidade e status. Possui uma relação de um para um com `Usuario` e pode estar vinculada a vários treinos.

### `Treino`

Representa um treino cadastrado por uma professora. Contém nome, descrição, nível, status e a professora responsável.

### `Matricula`

Relaciona uma aluna a um treino. Registra as datas de início e vencimento e o status atual da matrícula.

### `Frequencia`

Registra o check-in de uma aluna, armazenando a aluna vinculada e a data/hora de entrada.

### Enumerações

- `PerfilUsuario`: `ADMIN`, `PROFESSORA` e `ALUNA`;
- `StatusCadastro`: `ATIVO` e `INATIVO`;
- `StatusMatricula`: `ATIVA`, `VENCIDA` e `CANCELADA`;
- `NivelTreino`: `INICIANTE`, `INTERMEDIARIO` e `AVANCADO`.

## Estrutura do projeto

```text
academia-web3/
├── backend/
│   ├── .mvn/                         # Arquivos do Maven Wrapper
│   ├── src/main/java/.../academia/
│   │   ├── config/                   # Segurança, JWT e carga inicial
│   │   ├── controllers/              # Endpoints REST
│   │   ├── dtos/                     # Objetos de entrada e saída
│   │   ├── entities/                 # Entidades JPA
│   │   ├── enums/                    # Perfis, níveis e status
│   │   ├── exceptions/               # Tratamento padronizado de erros
│   │   ├── repositories/             # Acesso ao banco de dados
│   │   └── services/                 # Regras de negócio
│   ├── src/main/resources/
│   │   └── application.properties    # Banco, JPA e JWT
│   ├── src/test/                      # Testes do backend
│   ├── mvnw
│   └── pom.xml
├── frontend/
│   ├── public/                        # Arquivos públicos
│   ├── src/
│   │   ├── assets/                    # Estilos globais
│   │   ├── components/                # Formulários, layouts, modais e UI
│   │   ├── composables/               # Cliente HTTP e filtros compartilhados
│   │   ├── config/                    # URL da API
│   │   ├── router/                    # Rotas e guardas de acesso
│   │   ├── services/                  # Integração com os endpoints
│   │   ├── stores/                    # Estado global com Pinia
│   │   └── views/                     # Telas da aplicação
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## Como baixar e executar o projeto

### Pré-requisitos

Antes de iniciar, instale:

- Git;
- JDK 25;
- PostgreSQL;
- Node.js `^20.19.0` ou `>=22.12.0`;
- npm, incluído com o Node.js.

O Maven não precisa ser instalado globalmente, pois o backend inclui o Maven Wrapper.

### 1. Clonar o repositório

```bash
git clone https://github.com/MathuCardoso/trabalho-academia-web3.git
cd trabalho-academia-web3
```

### 2. Criar o banco de dados

Acesse o PostgreSQL com um usuário autorizado e crie o banco esperado pela configuração padrão:

```sql
CREATE DATABASE academia_db;
```

### 3. Configurar o backend

Edite `backend/src/main/resources/application.properties` com os dados do seu ambiente. Não versione credenciais ou chaves reais.

Exemplo sem valores sensíveis:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/academia_db
spring.datasource.username=<USUARIO_DO_BANCO>
spring.datasource.password=<SENHA_DO_BANCO>
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update

jwt.secret=<CHAVE_BASE64_FORTE>
jwt.expiration=7200000
```

A chave JWT deve ser forte, compatível com o algoritmo utilizado pela aplicação e armazenada de forma segura.

### 4. Instalar, testar e iniciar o backend

Em sistemas Linux ou macOS:

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

No Windows:

```powershell
cd backend
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

Por padrão, a API ficará disponível em `http://localhost:8080`.

Na primeira inicialização, o Hibernate cria/atualiza as tabelas e o `DataInitializer` inclui dados acadêmicos para demonstração. Revise esse inicializador antes de utilizar o projeto fora de um ambiente de estudo.

### 5. Configurar e iniciar o frontend

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

O frontend usa a variável `VITE_API_URL` para localizar o backend. O arquivo `frontend/.env` pode conter:

```dotenv
VITE_API_URL=http://localhost:8080
```

O Vite normalmente disponibiliza a aplicação em `http://localhost:5173` e informa outra porta no terminal caso ela já esteja ocupada.

### Build do frontend

```bash
cd frontend
npm run build
```

Os arquivos de produção serão gerados em `frontend/dist`.

## Configuração do banco de dados

As propriedades de conexão ficam em `backend/src/main/resources/application.properties`:

- `spring.datasource.url`: endereço JDBC e nome do banco;
- `spring.datasource.username`: usuário do PostgreSQL;
- `spring.datasource.password`: senha do usuário;
- `spring.datasource.driver-class-name`: driver JDBC do PostgreSQL;
- `spring.jpa.hibernate.ddl-auto`: estratégia de atualização do esquema.

O projeto utiliza `spring.jpa.hibernate.ddl-auto=update`, portanto o Hibernate cria ou ajusta as tabelas a partir das entidades JPA. Isso simplifica o ambiente acadêmico, mas migrações versionadas são mais apropriadas para um ambiente de produção.

## Autenticação e autorização

O login é realizado em `POST /api/auth/login`. Após validar login, senha e status do usuário, o backend devolve um token JWT.

O frontend armazena o token e o envia nas requisições protegidas pelo header:

```http
Authorization: Bearer <TOKEN_JWT>
```

A aplicação usa sessões stateless: o servidor não mantém uma sessão HTTP tradicional. O filtro JWT valida o token em cada requisição e o Spring Security verifica as permissões declaradas nos controllers.

As páginas do frontend também possuem guardas por perfil. Essas guardas melhoram a navegação, enquanto a autorização efetiva permanece no backend.

## Endpoints principais

Todos os recursos abaixo usam o prefixo `/api`.

### Autenticação

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/auth/login` | Autentica o usuário e devolve o JWT |

### Alunas

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET`, `POST` | `/alunas` | Lista ou cadastra alunas |
| `GET`, `PUT`, `DELETE` | `/alunas/{id}` | Consulta, atualiza ou exclui uma aluna |
| `PUT` | `/alunas/{id}/perfil` | Atualiza os próprios dados da aluna |
| `PATCH` | `/alunas/{id}/ativar` | Ativa a aluna e seu usuário |
| `PATCH` | `/alunas/{id}/inativar` | Inativa a aluna e seu usuário |

### Professoras

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET`, `POST` | `/professoras` | Lista ou cadastra professoras |
| `GET`, `PUT`, `DELETE` | `/professoras/{id}` | Consulta, atualiza ou exclui uma professora |
| `PUT` | `/professoras/{id}/perfil` | Atualiza os próprios dados da professora |
| `PATCH` | `/professoras/{id}/ativar` | Ativa a professora e seu usuário |
| `PATCH` | `/professoras/{id}/inativar` | Inativa a professora e seu usuário |

### Treinos

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET`, `POST` | `/treinos` | Lista ou cadastra treinos, conforme o perfil |
| `GET`, `PUT`, `DELETE` | `/treinos/{id}` | Consulta, atualiza ou exclui um treino |
| `GET` | `/treinos/professora/{professoraId}` | Lista os treinos de uma professora |
| `PATCH` | `/treinos/{id}/ativar` | Ativa um treino |
| `PATCH` | `/treinos/{id}/inativar` | Inativa um treino |

### Matrículas

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET`, `POST` | `/matriculas` | Lista ou cadastra matrículas |
| `GET`, `PUT`, `DELETE` | `/matriculas/{id}` | Consulta, atualiza ou exclui uma matrícula |
| `GET` | `/matriculas/aluna/{alunaId}` | Lista as matrículas de uma aluna |
| `GET` | `/matriculas/professora/{professoraId}` | Lista matrículas relacionadas à professora |
| `GET` | `/matriculas/vencidas` | Lista matrículas vencidas |
| `GET` | `/matriculas/a-vencer` | Lista matrículas próximas do vencimento |
| `PATCH` | `/matriculas/{id}/cancelar` | Cancela uma matrícula |

### Frequências

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET`, `POST` | `/frequencias` | Lista ou registra frequências administrativamente |
| `GET`, `PUT`, `DELETE` | `/frequencias/{id}` | Consulta, atualiza ou exclui uma frequência |
| `GET` | `/frequencias/aluna/{alunaId}` | Lista as frequências de uma aluna |
| `POST` | `/frequencias/checkin/{alunaId}` | Registra o check-in da aluna |

> Os métodos disponíveis em cada endpoint dependem do perfil autenticado. Consulte as anotações `@PreAuthorize` dos controllers para as regras detalhadas.

## Observações acadêmicas

Este sistema foi desenvolvido como trabalho acadêmico. Algumas decisões, como atualização automática do esquema pelo Hibernate e carga inicial de dados pela própria aplicação, priorizam aprendizado, simplicidade de configuração e demonstração dos fluxos.

Antes de utilizar o projeto em produção, recomenda-se revisar gerenciamento de segredos, credenciais iniciais, estratégia de migração do banco, logs, testes automatizados e configuração de implantação.

## Autores

- **Nome do aluno/integrante:** ____________________________________
- **Nome dos demais integrantes:** _________________________________
- **Instituição:** _________________________________________________
- **Curso:** ______________________________________________________

