# Modelagem inicial - Sistema de Academia

Entidades principais:
- Usuario
- Aluna
- Professora
- Treino
- Matrícula
- Frequência

Relacionamentos:
- Uma professora pode ter vários treinos.
- Um treino pertence a uma professora.
- Uma aluna pode ter várias matrículas.
- Uma matrícula pertence a uma aluna.
- Uma matrícula pode estar vinculada a um treino.
- Uma aluna pode ter vários registros de frequência.
- Uma aluna possui um usuário de acesso.
- Uma professora possui um usuário de acesso.

## Atributos das entidades

### Aluna
- id
- nome
- email
- telefone
- cpf
- dataNascimento
- status
- usuarioId

### Professora
- id
- nome
- email
- cref
- especialidade
- status
- usuarioId

### Usuario
- id
- login
- senha
- perfil
- status

### Treino
- id
- nome
- descricao
- nivel
- professora
- status

### Matrícula
- id
- aluna
- treino
- dataInicio
- dataVencimento
- status

### Frequência
- id
- aluna
- dataHoraEntrada

## Relacionamentos

### Aluna, Professora e Usuário
- Uma aluna referencia um único usuário de acesso.
- Uma professora referencia um único usuário de acesso.
- Usuário não armazena referências para aluna ou professora.
- Relações: 1:1

### Professora e Treino
- Uma professora pode ter vários treinos.
- Um treino pertence a uma única professora.
- Relação: 1:N

### Aluna e Matrícula
- Uma aluna pode ter várias matrículas.
- Uma matrícula pertence a uma única aluna.
- Relação: 1:N

### Treino e Matrícula
- Um treino pode aparecer em várias matrículas.
- Uma matrícula está vinculada a um único treino.
- Relação: 1:N

### Aluna e Frequência
- Uma aluna pode ter vários registros de frequência.
- Uma frequência pertence a uma única aluna.
- Relação: 1:N

## Enumerações

### StatusCadastro
Usado em: Aluna, Professora e Treino

Valores:
- ATIVO
- INATIVO

### NivelTreino
Usado em: Treino

Valores:
- INICIANTE
- INTERMEDIARIO
- AVANCADO

### StatusMatricula
Usado em: Matrícula

Valores:
- ATIVA
- VENCIDA
- CANCELADA
