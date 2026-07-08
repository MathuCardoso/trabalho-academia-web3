# Camadas do Sistema - Bella Fit & Women

## 1. Visão geral do sistema

O **Bella Fit & Women** é uma aplicação web para gestão de uma academia feminina. O sistema permite administrar alunas, professoras, treinos, matrículas e frequências, além de oferecer login e controle de acesso para três perfis: `ADMIN`, `PROFESSORA` e `ALUNA`.

O projeto está separado em duas aplicações:

- **Backend:** API REST feita com Java e Spring Boot. É responsável pelas regras de negócio, persistência no PostgreSQL, validações, autenticação JWT e autorização.
- **Frontend:** aplicação Vue.js executada com Bun e Vite. É responsável pelas telas, formulários, navegação, estado do usuário e consumo da API.

De forma resumida, o frontend envia requisições HTTP em JSON; o backend processa essas requisições, consulta ou altera o banco e devolve respostas que a interface apresenta ao usuário.

# Backend

## 2. Camada de Entidades / Models

As entidades estão em `backend/src/main/java/br/edu/ifpr/academia/entities`. Cada classe anotada com `@Entity` representa uma tabela gerenciada pelo JPA/Hibernate. Os campos com `@Id` e `@GeneratedValue(strategy = IDENTITY)` são chaves primárias geradas pelo banco.

### `Usuario`

Representa a conta usada para entrar no sistema.

- **Atributos principais:** `id`, `login`, `senha`, `perfil` e `status`.
- **Regras importantes:** a senha é criptografada com BCrypt e possui `@JsonIgnore`, portanto não é devolvida no JSON.
- **Login por perfil:** a aluna usa o CPF, a professora usa o CREF e o administrador possui login próprio.
- **Relacionamentos:** `Usuario` não guarda IDs de aluna ou professora. O relacionamento fica nas entidades `Aluna` e `Professora`.

Não existe uma entidade `Administrador`. Um administrador é um `Usuario` com o perfil `ADMIN` e não precisa estar associado a uma aluna ou professora.

### `Aluna`

Representa os dados cadastrais de uma aluna.

- **Atributos principais:** `id`, `nome`, `email`, `telefone`, `cpf`, `dataNascimento` e `status`.
- **Validações:** nome, e-mail, telefone, CPF e data de nascimento são obrigatórios; e-mail e CPF são únicos.
- **Relacionamento:** possui `@OneToOne` com `Usuario` por meio da coluna `usuario_id`.
- **JSON:** o objeto completo de usuário é ignorado para não expor a senha; a API apresenta somente `usuarioId` por um atributo calculado e somente de leitura.
- **Persistência:** o relacionamento usa `cascade = ALL`, então as operações da aluna podem incluir o usuário vinculado.

### `Professora`

Representa os dados pessoais e profissionais de uma professora.

- **Atributos principais:** `id`, `nome`, `email`, `cref`, `especialidade` e `status`.
- **Validações:** os campos cadastrais são obrigatórios; e-mail e CREF são únicos.
- **Relacionamento:** possui `@OneToOne` com `Usuario` pela coluna `usuario_id`, também com `cascade = ALL`.
- **JSON:** devolve `usuarioId`, mas não serializa o objeto `Usuario` completo.
- **Outros vínculos:** uma professora pode ser responsável por vários treinos, embora a referência seja armazenada no lado de `Treino`.

### `Treino`

Representa um treino oferecido pela academia.

- **Atributos principais:** `id`, `nome`, `descricao`, `nivel` e `status`.
- **Relacionamento:** possui `@ManyToOne` com `Professora`, usando a coluna `professora_id`.
- **Regra estrutural:** a aluna não se liga diretamente ao treino; essa associação acontece por uma matrícula.
- **Persistência dos enums:** nível e status são salvos como texto por `EnumType.STRING`.

### `Matricula`

Representa a associação entre uma aluna e um treino.

- **Atributos principais:** `id`, `dataInicio`, `dataVencimento` e `status`.
- **Relacionamentos:** possui `@ManyToOne` com `Aluna` e `@ManyToOne` com `Treino`, pelas colunas `aluna_id` e `treino_id`.
- **Função no modelo:** substitui uma relação direta muitos-para-muitos e permite guardar datas e situação do vínculo.
- **Status:** é salvo como texto e pode ser `ATIVA`, `VENCIDA` ou `CANCELADA`.

### `Frequencia`

Representa a presença ou check-in de uma aluna.

- **Atributos principais:** `id` e `dataHoraEntrada`.
- **Relacionamento:** possui `@ManyToOne` com `Aluna`, pela coluna `aluna_id`.
- **Regra:** quando a data e hora não são informadas, o service usa o momento atual.

### Resumo dos relacionamentos

```text
Usuario 1 <--- 1 Aluna
Usuario 1 <--- 1 Professora
Professora 1 <--- N Treino
Aluna 1 <--- N Matricula N ---> 1 Treino
Aluna 1 <--- N Frequencia
```

## 3. Camada de Enums

Os enums estão em `backend/src/main/java/br/edu/ifpr/academia/enums`. Eles limitam campos a um conjunto conhecido de valores e evitam textos livres inconsistentes.

| Enum | Valores | Onde é usado |
| --- | --- | --- |
| `PerfilUsuario` | `ADMIN`, `PROFESSORA`, `ALUNA` | Define o tipo de acesso de `Usuario` e origina as roles do Spring Security. |
| `StatusCadastro` | `ATIVO`, `INATIVO` | Controla a situação de `Usuario`, `Aluna`, `Professora` e `Treino`. Usuário inativo não pode entrar. |
| `StatusMatricula` | `ATIVA`, `VENCIDA`, `CANCELADA` | Indica se a matrícula está válida, passou do vencimento ou foi cancelada. |
| `NivelTreino` | `INICIANTE`, `INTERMEDIARIO`, `AVANCADO` | Classifica o nível de dificuldade de um treino. |

## 4. Camada de DTOs

DTO significa **Data Transfer Object**. Esses objetos definem os dados recebidos ou devolvidos em operações específicas, evitando incluir campos desnecessários ou sensíveis.

Os DTOs estão em `backend/src/main/java/br/edu/ifpr/academia/dtos`.

| DTO | Tipo | Finalidade e dados principais |
| --- | --- | --- |
| `LoginRequest` | Request | Recebe `login` e `senha` em `POST /api/auth/login`. Ambos são obrigatórios. |
| `LoginResponse` | Response | Devolve `token`, dados do usuário, perfil, status, nome e, conforme o perfil, `alunaId` ou `professoraId`. Nunca devolve senha. |
| `AlunaRequest` | Cadastro | Recebe nome, e-mail, telefone, CPF, data de nascimento, senha inicial e status para criar a aluna junto com seu usuário. |
| `ProfessoraRequest` | Cadastro | Recebe nome, e-mail, CREF, especialidade, senha inicial e status para criar a professora junto com seu usuário. |
| `AtualizarPerfilAlunaRequest` | Atualização | Permite à aluna atualizar nome, e-mail, telefone, CPF, nascimento e, opcionalmente, senha. Não permite alterar o próprio status. |
| `AtualizarPerfilProfessoraRequest` | Atualização | Permite à professora atualizar nome, e-mail, CREF, especialidade e, opcionalmente, senha. Não permite alterar o próprio status. |

O uso de DTOs não cobre toda a API. Nas operações administrativas de atualização de alunas e professoras, e nas operações de treino, matrícula e frequência, os controllers ainda recebem as próprias entidades. Portanto, a camada existe, mas é aplicada apenas aos fluxos que exigem formatos específicos.

## 5. Camada de Repositórios

Os repositories estão em `backend/src/main/java/br/edu/ifpr/academia/repositories`. Eles estendem `JpaRepository`, que já oferece operações como listar, buscar por ID, salvar e excluir. Os métodos com nomes como `findBy...` são transformados pelo Spring Data JPA em consultas ao PostgreSQL.

| Repository | Entidade | Consultas personalizadas principais |
| --- | --- | --- |
| `UsuarioRepository` | `Usuario` | Busca por login e verifica login duplicado, inclusive ignorando o próprio usuário durante uma edição. |
| `AlunaRepository` | `Aluna` | Busca por status, CPF ou ID do usuário; verifica CPF e e-mail duplicados. |
| `ProfessoraRepository` | `Professora` | Busca por status, CREF ou ID do usuário; verifica CREF e e-mail duplicados. |
| `TreinoRepository` | `Treino` | Busca treinos por status ou pela professora responsável. |
| `MatriculaRepository` | `Matricula` | Busca por status, aluna, professora do treino, período de vencimento e matrículas vencidas; verifica matrícula ativa da aluna. |
| `FrequenciaRepository` | `Frequencia` | Busca frequências por aluna ou por intervalo de data e hora. |

Essa camada não decide regras de negócio. Ela apenas fornece a interface de acesso aos registros; as decisões ficam nos services.

## 6. Camada de Services

Os services estão em `backend/src/main/java/br/edu/ifpr/academia/services`. Eles ficam entre controllers e repositories e concentram validações, regras de acesso aos dados e coordenação de operações.

| Service | Responsabilidades principais |
| --- | --- |
| `AlunaService` | Lista e busca alunas; valida CPF e e-mail únicos; cria `Aluna` e `Usuario` na mesma transação; atualiza cadastro e perfil; sincroniza CPF com login e status da aluna com o usuário; ativa, inativa e exclui. |
| `ProfessoraService` | Executa o equivalente para professoras: valida CREF e e-mail, cria o usuário, sincroniza CREF com login e situação cadastral, atualiza perfil, ativa, inativa e exclui. |
| `TreinoService` | Lista, busca, cadastra, atualiza, ativa, inativa e exclui treinos; carrega a professora existente antes de salvar e verifica se um treino pertence à professora autenticada. |
| `MatriculaService` | Liga aluna e treino, valida datas, calcula o status da matrícula, atualiza matrículas vencidas, lista vencidas ou próximas do vencimento, cancela registros e verifica matrícula ativa. |
| `FrequenciaService` | Lista e registra frequências, preenche a data/hora quando necessário e só permite check-in quando a aluna possui matrícula ativa e não vencida. |
| `UsuarioService` | Cria usuários de administrador, aluna e professora; criptografa senha; busca por login; atualiza login e senha; ativa, inativa e exclui contas; localiza o cadastro dono de um usuário. |
| `AuthService` | Realiza o login: busca o usuário, verifica o status, compara a senha com BCrypt, identifica os dados do perfil e solicita a criação do JWT. |
| `JwtService` | Gera, lê e valida tokens. O token contém login, `usuarioId`, perfil, data de emissão e expiração. |
| `UsuarioDetailsService` | Adapta `Usuario` ao formato `UserDetails` do Spring Security e transforma o perfil em autoridade, como `ROLE_ADMIN`. |

### Regras de negócio que merecem destaque

- CPF, e-mail e CREF duplicados geram conflito antes da gravação.
- O CPF da aluna e o CREF da professora também são seus logins; quando esses campos mudam, o login é atualizado.
- Senhas nunca são salvas diretamente: o `PasswordEncoder` aplica BCrypt.
- Ativar ou inativar aluna/professora também altera o status do `Usuario` correspondente.
- A data de vencimento da matrícula não pode ser anterior à data de início.
- Matrículas ativas com vencimento anterior ao dia atual passam para `VENCIDA`.
- Uma frequência só pode ser registrada para aluna com matrícula ativa e dentro da validade.
- Os métodos `pertenceAoUsuario` e equivalentes ajudam o Spring Security a garantir que alunas e professoras consultem somente dados próprios.

## 7. Camada de Controllers

Os controllers estão em `backend/src/main/java/br/edu/ifpr/academia/controllers`. Eles recebem requisições HTTP, validam o corpo com `@Valid`, verificam permissões com `@PreAuthorize` e delegam a operação ao service correspondente.

### `AuthController`

Usa o `AuthService`.

| Método | Endpoint | Permissão | Função |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Pública | Valida as credenciais e devolve os dados da sessão com o JWT. |

### `AlunaController`

Usa o `AlunaService`.

| Método | Endpoint | Permissão | Função |
| --- | --- | --- | --- |
| `GET` | `/api/alunas` | `ADMIN` | Lista todas as alunas. |
| `GET` | `/api/alunas/{id}` | `ADMIN` ou a própria `ALUNA` | Busca uma aluna. |
| `POST` | `/api/alunas` | `ADMIN` | Cadastra aluna e usuário. Retorna HTTP 201. |
| `PUT` | `/api/alunas/{id}` | `ADMIN` | Atualiza o cadastro administrativo. |
| `PUT` | `/api/alunas/{id}/perfil` | A própria `ALUNA` | Atualiza os próprios dados e senha opcional. |
| `PATCH` | `/api/alunas/{id}/ativar` | `ADMIN` | Ativa aluna e usuário. |
| `PATCH` | `/api/alunas/{id}/inativar` | `ADMIN` | Inativa aluna e usuário. |
| `DELETE` | `/api/alunas/{id}` | `ADMIN` | Exclui a aluna. Retorna HTTP 204. |

### `ProfessoraController`

Usa o `ProfessoraService`.

| Método | Endpoint | Permissão | Função |
| --- | --- | --- | --- |
| `GET` | `/api/professoras` | `ADMIN` | Lista todas as professoras. |
| `GET` | `/api/professoras/{id}` | `ADMIN` ou a própria `PROFESSORA` | Busca uma professora. |
| `POST` | `/api/professoras` | `ADMIN` | Cadastra professora e usuário. Retorna HTTP 201. |
| `PUT` | `/api/professoras/{id}` | `ADMIN` | Atualiza o cadastro administrativo. |
| `PUT` | `/api/professoras/{id}/perfil` | A própria `PROFESSORA` | Atualiza os próprios dados e senha opcional. |
| `PATCH` | `/api/professoras/{id}/ativar` | `ADMIN` | Ativa professora e usuário. |
| `PATCH` | `/api/professoras/{id}/inativar` | `ADMIN` | Inativa professora e usuário. |
| `DELETE` | `/api/professoras/{id}` | `ADMIN` | Exclui a professora. Retorna HTTP 204. |

### `TreinoController`

Usa o `TreinoService`.

| Método | Endpoint | Permissão | Função |
| --- | --- | --- | --- |
| `GET` | `/api/treinos` | `ADMIN` | Lista todos os treinos. |
| `GET` | `/api/treinos/{id}` | `ADMIN` ou professora responsável | Busca um treino. |
| `GET` | `/api/treinos/professora/{professoraId}` | `ADMIN` ou a própria `PROFESSORA` | Lista treinos da professora. |
| `POST` | `/api/treinos` | `ADMIN` ou professora informada no treino | Cadastra um treino. Retorna HTTP 201. |
| `PUT` | `/api/treinos/{id}` | `ADMIN` ou professora responsável | Atualiza o treino, mantendo a verificação de propriedade. |
| `PATCH` | `/api/treinos/{id}/ativar` | `ADMIN` ou professora responsável | Ativa o treino. |
| `PATCH` | `/api/treinos/{id}/inativar` | `ADMIN` ou professora responsável | Inativa o treino. |
| `DELETE` | `/api/treinos/{id}` | `ADMIN` | Exclui o treino. Retorna HTTP 204. |

### `MatriculaController`

Usa o `MatriculaService`.

| Método | Endpoint | Permissão | Função |
| --- | --- | --- | --- |
| `GET` | `/api/matriculas` | `ADMIN` | Lista todas as matrículas. |
| `GET` | `/api/matriculas/{id}` | `ADMIN`, aluna dona ou professora relacionada | Busca uma matrícula. |
| `GET` | `/api/matriculas/aluna/{alunaId}` | `ADMIN` ou a própria `ALUNA` | Lista matrículas da aluna. |
| `GET` | `/api/matriculas/professora/{professoraId}` | `ADMIN` ou a própria `PROFESSORA` | Lista matrículas ligadas aos treinos da professora. |
| `GET` | `/api/matriculas/vencidas` | `ADMIN` | Lista matrículas vencidas. |
| `GET` | `/api/matriculas/a-vencer` | `ADMIN` | Lista matrículas ativas que vencem em até sete dias. |
| `POST` | `/api/matriculas` | `ADMIN` | Cadastra uma matrícula. Retorna HTTP 201. |
| `PUT` | `/api/matriculas/{id}` | `ADMIN` | Atualiza uma matrícula. |
| `PATCH` | `/api/matriculas/{id}/cancelar` | `ADMIN` | Altera o status para cancelada. |
| `DELETE` | `/api/matriculas/{id}` | `ADMIN` | Exclui uma matrícula. Retorna HTTP 204. |

### `FrequenciaController`

Usa o `FrequenciaService`.

| Método | Endpoint | Permissão | Função |
| --- | --- | --- | --- |
| `GET` | `/api/frequencias` | `ADMIN` | Lista todas as frequências. |
| `GET` | `/api/frequencias/{id}` | `ADMIN` ou a própria `ALUNA` | Busca uma frequência. |
| `GET` | `/api/frequencias/aluna/{alunaId}` | `ADMIN` ou a própria `ALUNA` | Lista frequências da aluna. |
| `POST` | `/api/frequencias` | `ADMIN` | Registra uma frequência administrativa. Retorna HTTP 201. |
| `POST` | `/api/frequencias/checkin/{alunaId}` | `ADMIN` ou a própria `ALUNA` | Registra um check-in no horário atual. Retorna HTTP 201. |
| `PUT` | `/api/frequencias/{id}` | `ADMIN` | Atualiza uma frequência. |
| `DELETE` | `/api/frequencias/{id}` | `ADMIN` | Exclui uma frequência. Retorna HTTP 204. |

## 8. Camada de Segurança / Autenticação

A segurança usa Spring Security, BCrypt e JWT. O fluxo de login funciona assim:

1. O frontend envia login e senha para `POST /api/auth/login`.
2. O `AuthController` chama o `AuthService`.
3. O service busca `Usuario`, bloqueia contas inativas e compara a senha enviada com a senha BCrypt do banco.
4. O `JwtService` gera um token assinado com login, ID do usuário, perfil, emissão e expiração.
5. O frontend guarda o token e o envia nas próximas requisições no cabeçalho `Authorization: Bearer <token>`.
6. O `JwtAuthenticationFilter` executa uma vez por requisição, extrai o token, valida assinatura, usuário, status e validade.
7. O `UsuarioDetailsService` cria a autoridade `ROLE_ADMIN`, `ROLE_PROFESSORA` ou `ROLE_ALUNA`.
8. O Spring Security usa essa autenticação e as expressões `@PreAuthorize` para liberar ou negar o endpoint.

O `SecurityConfig` define a aplicação como **stateless**, ou seja, o backend não mantém sessão HTTP. CSRF, login por formulário e HTTP Basic ficam desativados porque a autenticação é feita por token.

- **Rota pública:** somente `/api/auth/login`.
- **Rotas protegidas:** todas as demais requisições.
- **Permissões específicas:** declaradas nos controllers com `@PreAuthorize`.
- **CORS:** aceita o frontend executado em `localhost` ou `127.0.0.1`, em qualquer porta local, para os métodos GET, POST, PUT, DELETE e PATCH.

### Classes auxiliares de configuração

- `BeanConfig`: disponibiliza o `BCryptPasswordEncoder` como implementação de `PasswordEncoder`.
- `DataInitializer`: executa uma carga inicial idempotente para o ambiente acadêmico, criando dados de demonstração apenas quando necessário.
- `AcademiaWebApplication`: classe principal que inicia o Spring Boot.

## 9. Camada de Exceptions / Tratamento de Erros

A API possui tratamento centralizado em `GlobalExceptionHandler`, anotado com `@RestControllerAdvice`. As respostas seguem a estrutura:

```json
{
  "status": 400,
  "erro": "Erro de validacao",
  "mensagem": "Dados invalidos",
  "errors": {
    "campo": "Mensagem relacionada ao campo"
  },
  "dataHora": "data e hora do erro"
}
```

Principais tratamentos:

| Situação | Código HTTP | Tratamento |
| --- | --- | --- |
| Campos inválidos pelo `@Valid` | 400 | Organiza as mensagens por nome de campo em `errors`. |
| Regra de negócio lançada como `ApiException` | Conforme a exceção | Pode retornar 400, 401, 403, 404 ou 409 com mensagem e campo relacionado. |
| JSON inválido ou tipo incompatível | 400 | Informa que o corpo da requisição é inválido. |
| Acesso sem permissão | 403 | Informa que o usuário não pode acessar o recurso. |
| Violação de integridade do banco | 409 | Informa registro duplicado ou ainda vinculado. |
| Erro não previsto | 500 | Devolve mensagem genérica sem expor o stack trace. |

`ApiException` é a exceção de negócio usada pelos services para definir status, descrição e erros por campo. Erros produzidos diretamente pelo filtro JWT ou pelo Spring Security também usam os campos `status`, `erro`, `mensagem`, `errors` e `dataHora`, mantendo o formato esperado pelo frontend.

## 10. Fluxo geral de uma requisição no backend

Exemplo de um cadastro de aluna:

1. O frontend envia `POST /api/alunas` com JSON e token JWT.
2. O `JwtAuthenticationFilter` valida o token e identifica o perfil.
3. O Spring Security confirma que o perfil é `ADMIN`.
4. O `AlunaController` recebe o JSON em `AlunaRequest` e executa as validações de `@Valid`.
5. O controller chama `AlunaService.cadastrarComUsuario`.
6. O service verifica CPF e e-mail duplicados e chama o `UsuarioService`.
7. O `UsuarioService` cria a conta com login igual ao CPF e senha criptografada.
8. O `AlunaRepository` e o `UsuarioRepository` gravam os dados no PostgreSQL dentro da transação.
9. O backend devolve HTTP 201 e a aluna criada.
10. O frontend recebe a resposta, fecha o formulário e atualiza a listagem. Em caso de falha, exibe as mensagens padronizadas da API.

# Frontend

## 11. Estrutura geral do frontend

O frontend está em `frontend/src` e usa Vue 3 com componentes de arquivo único (`.vue`), Vue Router, Pinia, Tailwind CSS, Maska e ícones Lucide.

| Caminho | Responsabilidade |
| --- | --- |
| `main.js` | Cria a aplicação Vue, instala Pinia e Vue Router, importa o CSS global e monta o projeto em `#app`. Também integra transições de navegação quando o navegador oferece essa API. |
| `App.vue` | Componente raiz. Renderiza a view correspondente à rota atual por meio de `RouterView`. |
| `assets/` | Contém o CSS global, fontes, variáveis de cores, raios, espaçamentos e integração com Tailwind CSS. |
| `views/` | Contém as páginas completas ligadas às rotas, como dashboard, login e listagens. |
| `components/` | Reúne elementos reutilizáveis de formulário, layout, modal, interface e ícones. |
| `services/` | Centraliza funções específicas de cada recurso para chamar a API. |
| `composables/` | Contém lógicas reutilizáveis: cliente HTTP e pesquisa em listagens. |
| `stores/` | Mantém o estado global de autenticação com Pinia. |
| `router/` | Declara as rotas e aplica as regras de navegação e perfil. |
| `config/` | Define a URL base da API a partir de `VITE_API_URL`, com fallback para `http://localhost:8080`. |

Não existem pastas `utils` ou uma camada separada de páginas além de `views`; as funções reutilizáveis presentes ficam em `composables`.

## 12. Camada de Views / Pages

As views coordenam estado local, componentes e services. Em geral, uma view carrega dados em `onMounted`, mantém formulários com `ref`, cria filtros com `computed` e mostra os resultados no template.

| View | Tela e funcionalidades | Componentes e services principais |
| --- | --- | --- |
| `LoginView.vue` | Formulário de login. Envia credenciais, mostra erros e redireciona para o dashboard após autenticar. | Usa `Input`, `Button`, `Errors`, `Loading`, ícone Lucide e `authStore`, que chama `authService`. |
| `HomeView.vue` | Dashboard adaptado ao perfil. O administrador vê totais e alertas; a professora vê dados dos próprios treinos e matrículas; a aluna vê matrícula, treino atual com descrição, vencimento, check-ins e pode registrar frequência. | Usa `MainLayout`, `Button`, `Errors`, `Loading`, cards próprios do dashboard e os services de aluna, professora, treino, matrícula e frequência conforme o perfil. |
| `AlunasView.vue` | Listagem pesquisável e formulário de cadastro/edição. Permite ativar ou inativar diretamente e excluir com confirmação. | Usa `MainLayout`, `Card`, `Modal`, `Confirm`, `Input`, `Select`, `SearchInput`, `Button`, `Errors`, `Loading` e `alunaService`. |
| `ProfessorasView.vue` | Listagem pesquisável e formulário de cadastro/edição. Permite ativar ou inativar diretamente e excluir com confirmação. | Usa os mesmos componentes de listagem e formulário e chama `professoraService`. |
| `TreinosView.vue` | Lista e pesquisa treinos, abre modal para cadastro ou edição e permite exclusão administrativa. Professoras carregam apenas os próprios treinos e só alteram registros que lhes pertencem. | Usa `MainLayout`, `Card`, `Modal`, `Confirm`, campos de formulário, `treinoService`, `professoraService` e `authStore`. |
| `MatriculasView.vue` | Lista, pesquisa, cadastra, edita e exclui matrículas, relacionando aluna e treino e permitindo selecionar datas e status. | Usa componentes de layout, cards, modal, confirmação e formulário; chama `matriculaService`, `alunaService` e `treinoService`. |
| `FrequenciasView.vue` | Para o administrador, lista todas as frequências, permite selecionar uma aluna para check-in e excluir registros. Para a aluna, lista apenas os próprios check-ins e permite registrar uma nova frequência. | Usa `MainLayout`, `Card`, `Modal`, `Confirm`, `Select`, pesquisa, feedback visual, `frequenciaService`, `alunaService` e `authStore`. |
| `ErrorView.vue` | Tela genérica para erros HTTP. Recebe `code`, `title` e `message` por props, escolhe um ícone Lucide e oferece ações para voltar ou ir ao início. | Usa `Button`, Vue Router e ícones de acesso negado, página não encontrada, servidor e alerta genérico. |

As views internas usam `provide` para enviar o título e, no dashboard, uma descrição ao `Header` montado pelo `MainLayout`.

## 13. Camada de Componentes

Os componentes evitam repetir estruturas, aparência e comportamento entre as views.

### Formulários

| Componente | Função |
| --- | --- |
| `Button.vue` | Padroniza botões, cores, variantes (`info`, `success`, `danger`), tipo, ícone e dimensões. |
| `Input.vue` | Padroniza label, input e erro; suporta máscaras com Maska e alternância de visibilidade para senha com ícones Lucide. |
| `Select.vue` | Padroniza campos de seleção, opções por slot e mensagem de validação. |
| `Textarea.vue` | Padroniza campos de texto longo e seus erros. |
| `SearchInput.vue` | Campo de pesquisa com ícones de busca e limpeza; emite o valor para a view filtrar a lista. |
| `Errors.vue` | Exibe uma mensagem de erro recebida por prop com o padrão visual da aplicação. |

### Layout e interface

| Componente | Função |
| --- | --- |
| `MainLayout.vue` | Estrutura as telas autenticadas com `Header`, `Sidebar` e área principal para o conteúdo da view. |
| `Header.vue` | Mostra título, descrição, nome do usuário, acesso à edição do perfil e logout. O título é recebido por `inject`. |
| `Sidebar.vue` | Exibe a marca e filtra os links de navegação conforme o perfil armazenado na Pinia. Também destaca a rota ativa. |
| `Card.vue` | Estrutura cards com slots de cabeçalho, corpo e rodapé, usada nas páginas de listagem. |
| `Loading.vue` | Indicador visual reutilizável para carregamento de página, envio, exclusão ou check-in. |
| `UserIcon.vue` | Ícone visual do usuário exibido no header. |

### Modais

| Componente | Função |
| --- | --- |
| `Modal.vue` | Cria a sobreposição, o painel e a ação de fechar. O conteúdo é recebido por slot. |
| `Confirm.vue` | Reutiliza `Modal`, `Button` e `Loading` para pedir confirmação em ações destrutivas, principalmente exclusões. |
| `ProfileModal.vue` | Carrega e edita os dados da aluna ou professora autenticada. Atualiza o nome na store e encerra a sessão quando CPF/CREF, que também é login, muda. |

## 14. Camada de Services no frontend

Os services encapsulam os endpoints, para que as views não precisem montar URLs ou repetir a configuração de `fetch`.

| Service | Funções e endpoints utilizados |
| --- | --- |
| `authService.js` | Executa login com `POST /api/auth/login`. |
| `alunaService.js` | Busca uma ou todas as alunas; cadastra, atualiza cadastro/perfil, ativa, inativa e exclui em `/api/alunas`. |
| `professoraService.js` | Executa as operações equivalentes em `/api/professoras`, incluindo atualização do próprio perfil. |
| `treinoService.js` | Busca todos os treinos, um treino ou treinos por professora; cadastra, atualiza e exclui em `/api/treinos`. |
| `matriculaService.js` | Busca todas, uma, por aluna ou por professora; cadastra, atualiza e exclui em `/api/matriculas`. |
| `frequenciaService.js` | Busca todas, uma ou por aluna; cadastra, atualiza, exclui e registra check-in em `/api/frequencias`. |
| `loginService.js` | Arquivo existente, mas atualmente vazio. O login real está concentrado em `authService.js`. |

### Cliente HTTP: `useApi.js`

O composable `useApi` é a base de todos os services:

- monta a URL usando `VITE_API_URL`;
- usa `fetch` para GET, POST, PUT, PATCH e DELETE;
- define `Content-Type: application/json`;
- busca o token no `localStorage` e adiciona `Authorization: Bearer <token>`;
- transforma respostas de sucesso em objetos com `success`, `message`, `data` e `status`;
- normaliza erros da API para `message`, `errors`, `data` e `status`;
- em resposta HTTP 401, limpa a autenticação e redireciona para `/login`;
- devolve uma mensagem própria quando não consegue se conectar ao servidor.

O composable `useListSearch.js` normaliza texto, remove acentos e converte para minúsculas para pesquisar qualquer conteúdo serializado dos registros.

## 15. Camada de Store / Gerenciamento de Estado

O projeto usa uma store Pinia chamada `auth`, definida em `authStore.js`.

Ela mantém:

- `token`: JWT da sessão;
- `usuario`: ID, login, nome, perfil, status e IDs de aluna/professora quando aplicáveis;
- `isAuthenticated`: valor calculado que informa se existe token.

### Operações da store

- `login(login, senha)`: chama `authService`, guarda o token e monta os dados do usuário.
- `logout()`: limpa token e usuário.
- `atualizarUsuario(dados)`: atualiza parte dos dados, usada após editar o perfil.

Token e usuário também são salvos no `localStorage`. Isso permite restaurar a sessão ao recarregar a página e dá ao `useApi` acesso ao token para autenticar as chamadas.

## 16. Camada de Rotas

As rotas ficam em `frontend/src/router/routes.js`; a criação do Vue Router e os guardas de navegação ficam em `router/index.js`. O projeto usa `createWebHistory`, portanto as URLs não contêm `#`.

| Caminho | View | Autenticação e perfis |
| --- | --- | --- |
| `/login` | `LoginView` | Pública. |
| `/` | `HomeView` | Exige autenticação; o conteúdo muda conforme o perfil. |
| `/alunas` | `AlunasView` | Somente `ADMIN`. |
| `/professoras` | `ProfessorasView` | Somente `ADMIN`. |
| `/treinos` | `TreinosView` | `ADMIN` ou `PROFESSORA`. |
| `/matriculas` | `MatriculasView` | Somente `ADMIN`. |
| `/frequencias` | `FrequenciasView` | `ADMIN` ou `ALUNA`. |
| `/403` | `ErrorView` | Exibe acesso negado. |
| `/404` | `ErrorView` | Exibe página não encontrada. |
| `/500` | `ErrorView` | Exibe erro interno. |
| Qualquer caminho desconhecido | Redirecionamento | Envia para `/404`. |

### Proteção das rotas

As rotas usam metadados:

- `requiresAuth`: informa que a página precisa de usuário autenticado;
- `roles`: lista os perfis autorizados.

Antes de cada navegação, `router.beforeEach` consulta a `authStore`:

1. Se a rota exige autenticação e não existe token, redireciona para `/login`.
2. Se a rota define perfis e o perfil atual não está na lista, redireciona para `/403`.
3. Caso contrário, permite a navegação.

Depois da navegação, `router.afterEach` atualiza o título da aba para o título da rota seguido de `Bella Fit & Women`. O `main.js` também usa a API de View Transitions quando ela está disponível no navegador.

A proteção do frontend melhora a navegação, mas não substitui a segurança do backend. Mesmo que alguém tente chamar uma URL manualmente, o Spring Security ainda verifica token e perfil.

## 17. Layout e padrão visual

O `MainLayout` é usado nas páginas autenticadas. Ele mantém:

- **Sidebar fixa:** marca Bella Fit & Women e links filtrados por perfil.
- **Header fixo:** título da página, descrição opcional, usuário, edição de perfil e logout.
- **Área principal:** espaço onde cada view insere seu conteúdo pelo slot.

O CSS global define um tema escuro e variáveis reutilizadas pelos componentes:

| Elemento | Padrão usado |
| --- | --- |
| Fundo principal | `--color-near-black` (`#0B0D12`) |
| Superfícies e layout | `--color-card` (`#14161D`) |
| Destaque principal | rosa `--color-pink-accent` (`#FF4D8D`) |
| Destaque secundário | ciano `--color-cyan-accent` (`#4DD0E1`) |
| Sucesso | verde `--color-success` |
| Informação | azul `--color-info` |
| Perigo | vermelho `--color-danger` |

Os cards usam fundo escuro, sombra e destaque rosa no hover. Botões reutilizam variantes de informação, sucesso e perigo. Inputs, selects e textareas compartilham labels, bordas, raio e exibição de erro. Modais usam uma sobreposição e o mesmo painel escuro das demais superfícies.

As fontes globais são `Inter` para a interface e `Playfair Display` na marca. Tailwind CSS é usado junto com estilos `scoped` dos componentes. Ícones vêm da biblioteca Lucide e máscaras de CPF e telefone são aplicadas com Maska.

## 18. Fluxo geral de uma ação no frontend

Exemplo de cadastro de aluna pelo administrador:

1. O administrador acessa `/alunas`.
2. O guard do router confirma autenticação e perfil `ADMIN`.
3. `AlunasView` chama `getAlunas` ao montar e exibe a listagem em cards.
4. O usuário abre o modal de cadastro e preenche os componentes `Input` e `Select`.
5. Ao enviar, a view chama `postAluna` em `alunaService`.
6. O service usa `useApi.post`, que monta o JSON e inclui o token JWT.
7. O backend valida permissão, campos e regras de duplicidade e grava aluna e usuário.
8. Em caso de sucesso, o frontend fecha o modal e carrega novamente a lista.
9. Em caso de erro, `useApi` normaliza a resposta e a view distribui as mensagens para `Errors` e para os campos correspondentes.

O mesmo padrão é reutilizado nas demais telas: view controla a interação, service escolhe o endpoint, `useApi` executa a requisição e componentes mostram carregamento, resultado ou erro.

# Integração entre backend e frontend

## 19. Como o backend e o frontend se comunicam

O frontend consome a API REST do backend por HTTP. A URL base vem de `VITE_API_URL`; quando a variável não está definida, usa `http://localhost:8080`.

Na maioria das operações:

1. O frontend converte os dados do formulário para JSON.
2. `useApi` envia a requisição para um endpoint com prefixo `/api`.
3. Se houver token, ele é enviado no cabeçalho `Authorization`.
4. O backend converte o JSON em DTO ou entidade e executa validação, autorização e regra de negócio.
5. Controllers retornam os dados em JSON com códigos como 200, 201 ou 204.
6. Falhas retornam códigos 400, 401, 403, 404, 409 ou 500 e uma mensagem padronizada.
7. O frontend transforma a resposta em um formato comum e atualiza a interface.

### Exemplo resumido

```text
View Vue
   -> Service do frontend
      -> useApi / fetch + JWT
         -> Controller Spring
            -> Service do backend
               -> Repository JPA
                  -> PostgreSQL

PostgreSQL
   -> Repository
      -> Service
         -> Controller / JSON
            -> useApi
               -> View Vue
```

O frontend também trata HTTP 401 de forma especial: remove a sessão local e volta para o login. Tentativas de abrir páginas incompatíveis com o perfil são encaminhadas pelo router para a tela `/403`.

## Resumo para apresentação

O sistema foi separado em backend e frontend. No backend, as entidades representam as tabelas do banco de dados, os enums limitam valores como perfil e status, os repositories fazem o acesso ao PostgreSQL, os services concentram as regras de negócio e os controllers disponibilizam os endpoints da API. A segurança usa Spring Security e JWT: depois do login, cada requisição envia um token e o backend confere tanto a identidade quanto o perfil do usuário. Os erros são tratados de forma centralizada e devolvidos em um formato padronizado.

No frontend, as views representam as telas e coordenam cada fluxo. Os componentes reutilizam botões, campos, cards, modais e o layout. Os services organizam as chamadas da API, o `useApi` envia as requisições e inclui o JWT, a Pinia mantém a sessão do usuário e o Vue Router controla a navegação e as páginas permitidas para cada perfil. Dessa forma, cada camada possui uma responsabilidade clara e a comunicação entre interface, regras de negócio e banco de dados fica organizada.

Uma forma curta de explicar a arquitetura é:

> A view recebe a ação do usuário, o service do frontend chama a API, o controller recebe a requisição, o service do backend aplica as regras, o repository acessa o banco e a resposta percorre o caminho de volta até a tela.
