# Relatorio de Alteracoes

## Arquivos alterados

### `backend/src/main/java/br/edu/ifpr/academia/config/DataInitializer.java`
Ampliado para popular, de forma idempotente, usuarios, alunas, professora, treinos, matriculas e frequencias de teste.

### `backend/src/main/java/br/edu/ifpr/academia/controllers/AlunaController.java`
Adicionada a rota de edicao do proprio perfil e ajustadas as permissoes de consulta.

### `backend/src/main/java/br/edu/ifpr/academia/controllers/ProfessoraController.java`
Adicionada a rota de edicao do proprio perfil e restringidas as consultas ao perfil autenticado ou administrador.

### `backend/src/main/java/br/edu/ifpr/academia/controllers/TreinoController.java`
Restringidas as consultas da professora aos proprios treinos.

### `backend/src/main/java/br/edu/ifpr/academia/controllers/MatriculaController.java`
Ajustadas as permissoes para dashboards e consultas de matriculas proprias.

### `backend/src/main/java/br/edu/ifpr/academia/controllers/FrequenciaController.java`
Limitadas as consultas de frequencia ao administrador ou a aluna proprietaria.

### `backend/src/main/java/br/edu/ifpr/academia/dtos/AtualizarPerfilAlunaRequest.java`
Criado o contrato de edicao pessoal da aluna sem campo de status e com senha opcional.

### `backend/src/main/java/br/edu/ifpr/academia/dtos/AtualizarPerfilProfessoraRequest.java`
Criado o contrato de edicao pessoal da professora sem campo de status e com senha opcional.

### `backend/src/main/java/br/edu/ifpr/academia/dtos/AlunaRequest.java`
Atualizada a mensagem de validacao de senha para remover o termo "senha inicial".

### `backend/src/main/java/br/edu/ifpr/academia/dtos/ProfessoraRequest.java`
Atualizada a mensagem de validacao de senha para remover o termo "senha inicial".

### `backend/src/main/java/br/edu/ifpr/academia/services/AlunaService.java`
Implementada a edicao dos dados e da senha da propria aluna sem permitir mudanca de status.

### `backend/src/main/java/br/edu/ifpr/academia/services/ProfessoraService.java`
Implementada a edicao dos dados e da senha da propria professora sem permitir mudanca de status.

### `backend/src/main/java/br/edu/ifpr/academia/services/UsuarioService.java`
Adicionada a atualizacao segura da senha criptografada do usuario.

### `backend/src/main/java/br/edu/ifpr/academia/services/MatriculaService.java`
Adicionada a verificacao de propriedade da matricula para o perfil professora.

### `backend/src/main/java/br/edu/ifpr/academia/repositories/MatriculaRepository.java`
Mantidas as consultas de matriculas ativas e vencidas usadas na validacao de check-in e nos dashboards.

### `backend/src/main/java/br/edu/ifpr/academia/services/FrequenciaService.java`
Mantida a validacao de matricula ativa para registrar frequencias de forma consistente.

### `frontend/src/components/form/Input.vue`
Adicionado o controle de mostrar e ocultar senha com icones Lucide em todos os campos de senha.

### `frontend/src/components/form/Textarea.vue`
Criado componente reutilizavel de textarea com o mesmo padrao visual dos formularios.

### `frontend/src/components/form/SearchInput.vue`
Criado componente reutilizavel de pesquisa com icones de busca e limpeza.

### `frontend/src/components/modal/ProfileModal.vue`
Criado modal para alunas e professoras editarem os proprios dados e a senha, sem acesso ao status.

### `frontend/src/components/ui/Header.vue`
Adicionada a opcao "Meus dados" para abrir o modal de edicao pessoal.

### `frontend/src/components/ui/Sidebar.vue`
Ajustada a navegacao visivel de acordo com as permissoes de aluna, professora e administrador.

### `frontend/src/composables/useApi.js`
Adicionado suporte padronizado a requisicoes PATCH.

### `frontend/src/composables/useListSearch.js`
Criado normalizador compartilhado para pesquisa textual nas listagens.

### `frontend/src/router/routes.js`
Atualizadas as permissoes das rotas para bloquear acesso direto a paginas nao permitidas.

### `frontend/src/services/alunaService.js`
Adicionadas operacoes de perfil proprio e ativacao/inativacao de alunas.

### `frontend/src/services/professoraService.js`
Adicionadas operacoes de perfil proprio e ativacao/inativacao de professoras.

### `frontend/src/services/treinoService.js`
Adicionada a consulta de treinos por professora.

### `frontend/src/services/matriculaService.js`
Adicionada a consulta de matriculas por professora para o dashboard.

### `frontend/src/services/frequenciaService.js`
Disponibilizadas as consultas de frequencia por aluna e a operacao de registro de check-in.

### `frontend/src/stores/authStore.js`
Adicionada a atualizacao dos dados locais do usuario apos editar o perfil.

### `frontend/src/views/AlunasView.vue`
Adicionadas pesquisa, lixeira com confirmacao, ativacao/inativacao direta, senha renomeada e botoes de largura fixa.

### `frontend/src/views/ProfessorasView.vue`
Adicionadas pesquisa, lixeira com confirmacao, ativacao/inativacao direta, senha renomeada e botoes de largura fixa.

### `frontend/src/views/TreinosView.vue`
Substituido o campo de descricao por textarea, adicionada pesquisa, cores de status, largura fixa e listagem propria da professora.

### `frontend/src/views/MatriculasView.vue`
Adicionadas pesquisa, cores do status e largura fixa nos botoes dos cards.

### `frontend/src/views/FrequenciasView.vue`
Adicionadas pesquisa e largura fixa no botao de exclusao, preservando os fluxos de check-in existentes.

### `frontend/src/views/HomeView.vue`
Implementados dashboards especificos para administrador, aluna e professora, incluindo registro de frequencia pela aluna.

### `frontend/src/views/auth/LoginView.vue`
Reorganizado o layout com apresentacao da aplicacao a esquerda e formulario de login a direita.

### `RELATORIO_ALTERACOES.md`
Criado este relatorio consolidando os arquivos e alteracoes da solicitacao.
