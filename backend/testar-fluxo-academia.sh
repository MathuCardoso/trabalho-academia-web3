#!/usr/bin/env bash
set -u

BASE_URL="http://localhost:8080"
TIMESTAMP=$(date +%s)

ADMIN_LOGIN="admin"
ADMIN_SENHA="admin123"

print_title() {
  echo ""
  echo "=================================================="
  echo "$1"
  echo "=================================================="
}

pretty_json() {
  python3 -m json.tool 2>/dev/null || cat
}

json_field() {
  python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('$1',''))"
}

gerar_cpf() {
  python3 - <<'PY'
import random

def gerar_cpf():
    while True:
        nums = [random.randint(0, 9) for _ in range(9)]
        if len(set(nums)) == 1:
            continue

        soma = sum(nums[i] * (10 - i) for i in range(9))
        d1 = 11 - (soma % 11)
        d1 = 0 if d1 >= 10 else d1
        nums.append(d1)

        soma = sum(nums[i] * (11 - i) for i in range(10))
        d2 = 11 - (soma % 11)
        d2 = 0 if d2 >= 10 else d2
        nums.append(d2)

        print(f"{nums[0]}{nums[1]}{nums[2]}.{nums[3]}{nums[4]}{nums[5]}.{nums[6]}{nums[7]}{nums[8]}-{nums[9]}{nums[10]}")
        return

gerar_cpf()
PY
}

print_title "0. VERIFICAR SE BACK-END ESTA RODANDO"

STATUS_BACK=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/auth/login")

if [ "$STATUS_BACK" = "000" ]; then
  echo "ERRO: back-end nao esta respondendo em $BASE_URL"
  echo "Rode antes: cd backend && ./mvnw spring-boot:run"
  exit 1
fi

echo "Back-end respondeu. HTTP $STATUS_BACK"

print_title "1. LOGIN ADMIN"

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"login\": \"$ADMIN_LOGIN\",
    \"senha\": \"$ADMIN_SENHA\"
  }")

echo "$LOGIN_RESPONSE" | pretty_json

TOKEN_ADMIN=$(echo "$LOGIN_RESPONSE" | json_field token)

if [ -z "$TOKEN_ADMIN" ]; then
  echo "ERRO: nao foi possivel capturar token admin."
  exit 1
fi

echo "OK: token admin capturado."

print_title "2. CADASTRAR PROFESSORA"

PROF_CREF="CREF$TIMESTAMP"
PROF_RESPONSE=$(curl -s -X POST "$BASE_URL/api/professoras" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d "{
    \"nome\": \"Professora Teste $TIMESTAMP\",
    \"email\": \"professora$TIMESTAMP@email.com\",
    \"cref\": \"$PROF_CREF\",
    \"especialidade\": \"Musculacao\",
    \"senhaInicial\": \"prof123\",
    \"status\": \"ATIVO\"
  }")

echo "$PROF_RESPONSE" | pretty_json

PROF_ID=$(echo "$PROF_RESPONSE" | json_field id)

if [ -z "$PROF_ID" ]; then
  echo "ERRO: professora nao foi cadastrada."
  exit 1
fi

echo "OK: professora criada com ID $PROF_ID"

print_title "3. CADASTRAR TREINO"

TREINO_RESPONSE=$(curl -s -X POST "$BASE_URL/api/treinos" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d "{
    \"nome\": \"Treino Teste $TIMESTAMP\",
    \"descricao\": \"Treino criado pelo script de teste\",
    \"nivel\": \"INICIANTE\",
    \"status\": \"ATIVO\",
    \"professora\": {
      \"id\": $PROF_ID
    }
  }")

echo "$TREINO_RESPONSE" | pretty_json

TREINO_ID=$(echo "$TREINO_RESPONSE" | json_field id)

if [ -z "$TREINO_ID" ]; then
  echo "ERRO: treino nao foi cadastrado."
  exit 1
fi

echo "OK: treino criado com ID $TREINO_ID"

print_title "4. CADASTRAR ALUNA COM MATRICULA ATIVA"

CPF_ATIVA=$(gerar_cpf)

ALUNA_ATIVA_RESPONSE=$(curl -s -X POST "$BASE_URL/api/alunas" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d "{
    \"nome\": \"Aluna Ativa $TIMESTAMP\",
    \"email\": \"alunaativa$TIMESTAMP@email.com\",
    \"telefone\": \"45999999999\",
    \"cpf\": \"$CPF_ATIVA\",
    \"dataNascimento\": \"2000-01-01\",
    \"senhaInicial\": \"aluna123\",
    \"status\": \"ATIVO\"
  }")

echo "$ALUNA_ATIVA_RESPONSE" | pretty_json

ALUNA_ATIVA_ID=$(echo "$ALUNA_ATIVA_RESPONSE" | json_field id)

if [ -z "$ALUNA_ATIVA_ID" ]; then
  echo "ERRO: aluna ativa nao foi cadastrada."
  exit 1
fi

echo "OK: aluna ativa criada com ID $ALUNA_ATIVA_ID"

DATA_INICIO=$(date +%F)
DATA_VENCIMENTO_FUTURA=$(date -d "+5 days" +%F)

MATRICULA_ATIVA_RESPONSE=$(curl -s -X POST "$BASE_URL/api/matriculas" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d "{
    \"aluna\": {
      \"id\": $ALUNA_ATIVA_ID
    },
    \"treino\": {
      \"id\": $TREINO_ID
    },
    \"dataInicio\": \"$DATA_INICIO\",
    \"dataVencimento\": \"$DATA_VENCIMENTO_FUTURA\",
    \"status\": \"ATIVA\"
  }")

echo "$MATRICULA_ATIVA_RESPONSE" | pretty_json

MATRICULA_ATIVA_ID=$(echo "$MATRICULA_ATIVA_RESPONSE" | json_field id)
MATRICULA_ATIVA_STATUS=$(echo "$MATRICULA_ATIVA_RESPONSE" | json_field status)

if [ -z "$MATRICULA_ATIVA_ID" ]; then
  echo "ERRO: matricula ativa nao foi cadastrada."
  exit 1
fi

echo "OK: matricula ativa criada com ID $MATRICULA_ATIVA_ID"
echo "Status retornado: $MATRICULA_ATIVA_STATUS"

print_title "5. TESTAR CHECK-IN COM MATRICULA ATIVA"

CHECKIN_ATIVO_FILE=$(mktemp)

CHECKIN_ATIVO_STATUS=$(curl -s -o "$CHECKIN_ATIVO_FILE" -w "%{http_code}" \
  -X POST "$BASE_URL/api/frequencias/checkin/$ALUNA_ATIVA_ID" \
  -H "Authorization: Bearer $TOKEN_ADMIN")

cat "$CHECKIN_ATIVO_FILE" | pretty_json

if [ "$CHECKIN_ATIVO_STATUS" = "201" ]; then
  echo "OK: check-in com matricula ativa funcionou. HTTP $CHECKIN_ATIVO_STATUS"
else
  echo "ERRO: check-in com matricula ativa deveria retornar 201, mas retornou $CHECKIN_ATIVO_STATUS"
  exit 1
fi

print_title "6. CADASTRAR ALUNA COM MATRICULA VENCIDA"

CPF_VENCIDA=$(gerar_cpf)

ALUNA_VENCIDA_RESPONSE=$(curl -s -X POST "$BASE_URL/api/alunas" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d "{
    \"nome\": \"Aluna Vencida $TIMESTAMP\",
    \"email\": \"alunavencida$TIMESTAMP@email.com\",
    \"telefone\": \"45888888888\",
    \"cpf\": \"$CPF_VENCIDA\",
    \"dataNascimento\": \"2001-01-01\",
    \"senhaInicial\": \"aluna123\",
    \"status\": \"ATIVO\"
  }")

echo "$ALUNA_VENCIDA_RESPONSE" | pretty_json

ALUNA_VENCIDA_ID=$(echo "$ALUNA_VENCIDA_RESPONSE" | json_field id)

if [ -z "$ALUNA_VENCIDA_ID" ]; then
  echo "ERRO: aluna vencida nao foi cadastrada."
  exit 1
fi

echo "OK: aluna vencida criada com ID $ALUNA_VENCIDA_ID"

DATA_INICIO_VENCIDA=$(date -d "-30 days" +%F)
DATA_VENCIMENTO_VENCIDA=$(date -d "-1 day" +%F)

MATRICULA_VENCIDA_RESPONSE=$(curl -s -X POST "$BASE_URL/api/matriculas" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d "{
    \"aluna\": {
      \"id\": $ALUNA_VENCIDA_ID
    },
    \"treino\": {
      \"id\": $TREINO_ID
    },
    \"dataInicio\": \"$DATA_INICIO_VENCIDA\",
    \"dataVencimento\": \"$DATA_VENCIMENTO_VENCIDA\",
    \"status\": \"ATIVA\"
  }")

echo "$MATRICULA_VENCIDA_RESPONSE" | pretty_json

MATRICULA_VENCIDA_ID=$(echo "$MATRICULA_VENCIDA_RESPONSE" | json_field id)
MATRICULA_VENCIDA_STATUS=$(echo "$MATRICULA_VENCIDA_RESPONSE" | json_field status)

if [ -z "$MATRICULA_VENCIDA_ID" ]; then
  echo "ERRO: matricula vencida nao foi cadastrada."
  exit 1
fi

echo "OK: matricula vencida criada com ID $MATRICULA_VENCIDA_ID"
echo "Status retornado: $MATRICULA_VENCIDA_STATUS"

if [ "$MATRICULA_VENCIDA_STATUS" = "VENCIDA" ]; then
  echo "OK: back-end converteu matricula vencida para status VENCIDA."
else
  echo "ALERTA: status esperado era VENCIDA, mas veio $MATRICULA_VENCIDA_STATUS"
fi

print_title "7. TESTAR BLOQUEIO DE CHECK-IN COM MATRICULA VENCIDA"

CHECKIN_VENCIDO_FILE=$(mktemp)

CHECKIN_VENCIDO_STATUS=$(curl -s -o "$CHECKIN_VENCIDO_FILE" -w "%{http_code}" \
  -X POST "$BASE_URL/api/frequencias/checkin/$ALUNA_VENCIDA_ID" \
  -H "Authorization: Bearer $TOKEN_ADMIN")

cat "$CHECKIN_VENCIDO_FILE" | pretty_json

if [ "$CHECKIN_VENCIDO_STATUS" = "400" ]; then
  echo "OK: check-in com matricula vencida foi bloqueado. HTTP $CHECKIN_VENCIDO_STATUS"
else
  echo "ERRO: check-in com matricula vencida deveria retornar 400, mas retornou $CHECKIN_VENCIDO_STATUS"
  exit 1
fi

print_title "8. LISTAR FREQUENCIAS"

curl -s "$BASE_URL/api/frequencias" \
  -H "Authorization: Bearer $TOKEN_ADMIN" | pretty_json

print_title "9. LISTAR MATRICULAS VENCIDAS"

curl -s "$BASE_URL/api/matriculas/vencidas" \
  -H "Authorization: Bearer $TOKEN_ADMIN" | pretty_json

print_title "RESULTADO FINAL"

echo "OK: fluxo principal testado."
echo "OK: professora cadastrada."
echo "OK: treino cadastrado."
echo "OK: aluna com matricula ativa cadastrada."
echo "OK: check-in com matricula ativa permitido."
echo "OK: aluna com matricula vencida cadastrada."
echo "OK: check-in com matricula vencida bloqueado."