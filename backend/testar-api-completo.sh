#!/bin/bash

BASE_URL="http://localhost:8080"
TIMESTAMP=$(date +%s)

echo "=========================================="
echo " TESTE COMPLETO API ACADEMIA COM JWT"
echo "=========================================="
echo ""

ADMIN_LOGIN="admin"
ADMIN_SENHA="admin123"

ALUNA_NOME="Beatriz Lima"
ALUNA_EMAIL="beatriz${TIMESTAMP}@email.com"
ALUNA_TELEFONE="45988887777"

ALUNA_CPF=$(python3 - <<'PY'
import random

def gerar_cpf():
    while True:
        numeros = [random.randint(0, 9) for _ in range(9)]

        if len(set(numeros)) == 1:
            continue

        soma = sum(numeros[i] * (10 - i) for i in range(9))
        digito1 = 11 - (soma % 11)
        digito1 = 0 if digito1 >= 10 else digito1
        numeros.append(digito1)

        soma = sum(numeros[i] * (11 - i) for i in range(10))
        digito2 = 11 - (soma % 11)
        digito2 = 0 if digito2 >= 10 else digito2
        numeros.append(digito2)

        return f"{numeros[0]}{numeros[1]}{numeros[2]}.{numeros[3]}{numeros[4]}{numeros[5]}.{numeros[6]}{numeros[7]}{numeros[8]}-{numeros[9]}{numeros[10]}"

print(gerar_cpf())
PY
)

ALUNA_SENHA="aluna123"

PROF_NOME="Camila Souza"
PROF_EMAIL="camila${TIMESTAMP}@email.com"
PROF_CREF="CREF${TIMESTAMP: -6}"
PROF_ESPECIALIDADE="Musculacao"
PROF_SENHA="prof123"

echo "------------------------------------------"
echo "1. LOGIN ADMIN"
echo "------------------------------------------"

TOKEN_ADMIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
-H "Content-Type: application/json" \
-d "{
  \"login\": \"$ADMIN_LOGIN\",
  \"senha\": \"$ADMIN_SENHA\"
}" | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

if [ -z "$TOKEN_ADMIN" ]; then
  echo "ERRO: nao conseguiu capturar o token do admin."
  exit 1
fi

echo "Token admin capturado com sucesso."
echo ""

echo "------------------------------------------"
echo "2. TESTE DE ROTA PROTEGIDA SEM TOKEN"
echo "------------------------------------------"

STATUS_SEM_TOKEN=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/alunas")

echo "GET /api/alunas sem token -> HTTP $STATUS_SEM_TOKEN"
echo "Esperado: 401"
echo ""

echo "------------------------------------------"
echo "3. TESTE DE ROTA PROTEGIDA COM TOKEN ADMIN"
echo "------------------------------------------"

STATUS_COM_TOKEN=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/alunas" \
-H "Authorization: Bearer $TOKEN_ADMIN")

echo "GET /api/alunas com token admin -> HTTP $STATUS_COM_TOKEN"
echo "Esperado: 200"
echo ""

echo "------------------------------------------"
echo "4. LOGIN INVALIDO"
echo "------------------------------------------"

curl -s -X POST "$BASE_URL/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "login": "admin",
  "senha": "senhaerrada"
}' | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "5. VALIDACAO DE ALUNA INVALIDA"
echo "------------------------------------------"

curl -s -X POST "$BASE_URL/api/alunas" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d '{
  "nome": "",
  "email": "email-invalido",
  "telefone": "",
  "cpf": "",
  "dataNascimento": null,
  "senhaInicial": ""
}' | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "6. CADASTRAR ALUNA COM TOKEN ADMIN"
echo "------------------------------------------"

ALUNA_RESPONSE=$(curl -s -X POST "$BASE_URL/api/alunas" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d "{
  \"nome\": \"$ALUNA_NOME\",
  \"email\": \"$ALUNA_EMAIL\",
  \"telefone\": \"$ALUNA_TELEFONE\",
  \"cpf\": \"$ALUNA_CPF\",
  \"dataNascimento\": \"1999-08-20\",
  \"senhaInicial\": \"$ALUNA_SENHA\"
}")

echo "$ALUNA_RESPONSE" | python3 -m json.tool

ALUNA_ID=$(echo "$ALUNA_RESPONSE" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('id',''))")

if [ -z "$ALUNA_ID" ]; then
  echo "ERRO: aluna nao foi cadastrada. Resposta nao possui ID."
  exit 1
fi

echo ""
echo "Aluna criada com ID: $ALUNA_ID"
echo "CPF/login da aluna: $ALUNA_CPF"
echo ""

echo "------------------------------------------"
echo "7. LOGIN ALUNA"
echo "------------------------------------------"

TOKEN_ALUNA=$(curl -s -X POST "$BASE_URL/api/auth/login" \
-H "Content-Type: application/json" \
-d "{
  \"login\": \"$ALUNA_CPF\",
  \"senha\": \"$ALUNA_SENHA\"
}" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('token',''))")

if [ -z "$TOKEN_ALUNA" ]; then
  echo "ERRO: nao conseguiu capturar o token da aluna."
  exit 1
fi

echo "Token aluna capturado com sucesso."
echo ""

echo "------------------------------------------"
echo "8. TESTE DE PERMISSAO DA ALUNA"
echo "------------------------------------------"

STATUS_ALUNA_LISTAR_ALUNAS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/alunas" \
-H "Authorization: Bearer $TOKEN_ALUNA")

echo "ALUNA tentando GET /api/alunas -> HTTP $STATUS_ALUNA_LISTAR_ALUNAS"
echo "Esperado: 403"
echo ""

STATUS_ALUNA_BUSCAR_PROPRIA=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/alunas/$ALUNA_ID" \
-H "Authorization: Bearer $TOKEN_ALUNA")

echo "ALUNA tentando GET /api/alunas/$ALUNA_ID -> HTTP $STATUS_ALUNA_BUSCAR_PROPRIA"
echo "Esperado: 200"
echo ""

echo "------------------------------------------"
echo "9. TESTE CPF DUPLICADO"
echo "------------------------------------------"

curl -s -X POST "$BASE_URL/api/alunas" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d "{
  \"nome\": \"$ALUNA_NOME\",
  \"email\": \"duplicado${TIMESTAMP}@email.com\",
  \"telefone\": \"$ALUNA_TELEFONE\",
  \"cpf\": \"$ALUNA_CPF\",
  \"dataNascimento\": \"1999-08-20\",
  \"senhaInicial\": \"$ALUNA_SENHA\"
}" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "10. VALIDACAO DE PROFESSORA INVALIDA"
echo "------------------------------------------"

curl -s -X POST "$BASE_URL/api/professoras" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d '{
  "nome": "",
  "email": "email-invalido",
  "cref": "",
  "especialidade": "",
  "senhaInicial": ""
}' | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "11. CADASTRAR PROFESSORA COM TOKEN ADMIN"
echo "------------------------------------------"

PROF_RESPONSE=$(curl -s -X POST "$BASE_URL/api/professoras" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d "{
  \"nome\": \"$PROF_NOME\",
  \"email\": \"$PROF_EMAIL\",
  \"cref\": \"$PROF_CREF\",
  \"especialidade\": \"$PROF_ESPECIALIDADE\",
  \"senhaInicial\": \"$PROF_SENHA\"
}")

echo "$PROF_RESPONSE" | python3 -m json.tool

PROF_ID=$(echo "$PROF_RESPONSE" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('id',''))")

if [ -z "$PROF_ID" ]; then
  echo "ERRO: professora nao foi cadastrada. Resposta nao possui ID."
  exit 1
fi

echo ""
echo "Professora criada com ID: $PROF_ID"
echo "CREF/login da professora: $PROF_CREF"
echo ""

echo "------------------------------------------"
echo "12. LOGIN PROFESSORA"
echo "------------------------------------------"

TOKEN_PROF=$(curl -s -X POST "$BASE_URL/api/auth/login" \
-H "Content-Type: application/json" \
-d "{
  \"login\": \"$PROF_CREF\",
  \"senha\": \"$PROF_SENHA\"
}" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('token',''))")

if [ -z "$TOKEN_PROF" ]; then
  echo "ERRO: nao conseguiu capturar o token da professora."
  exit 1
fi

echo "Token professora capturado com sucesso."
echo ""

echo "------------------------------------------"
echo "13. TESTE DE PERMISSAO DA PROFESSORA"
echo "------------------------------------------"

STATUS_PROF_LISTAR_ALUNAS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/alunas" \
-H "Authorization: Bearer $TOKEN_PROF")

echo "PROFESSORA tentando GET /api/alunas -> HTTP $STATUS_PROF_LISTAR_ALUNAS"
echo "Esperado: 200"
echo ""

STATUS_PROF_CADASTRAR_ALUNA=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/alunas" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_PROF" \
-d "{
  \"nome\": \"Teste Bloqueado\",
  \"email\": \"bloqueado${TIMESTAMP}@email.com\",
  \"telefone\": \"45999999999\",
  \"cpf\": \"$ALUNA_CPF\",
  \"dataNascimento\": \"2000-01-01\",
  \"senhaInicial\": \"teste123\"
}")

echo "PROFESSORA tentando POST /api/alunas -> HTTP $STATUS_PROF_CADASTRAR_ALUNA"
echo "Esperado: 403"
echo ""

echo "------------------------------------------"
echo "14. TESTE CREF DUPLICADO"
echo "------------------------------------------"

curl -s -X POST "$BASE_URL/api/professoras" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d "{
  \"nome\": \"$PROF_NOME\",
  \"email\": \"profduplicada${TIMESTAMP}@email.com\",
  \"cref\": \"$PROF_CREF\",
  \"especialidade\": \"$PROF_ESPECIALIDADE\",
  \"senhaInicial\": \"$PROF_SENHA\"
}" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "15. CADASTRAR TREINO COM PROFESSORA"
echo "------------------------------------------"

TREINO_RESPONSE=$(curl -s -X POST "$BASE_URL/api/treinos" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d "{
  \"nome\": \"Treino Inicial $TIMESTAMP\",
  \"descricao\": \"Treino de adaptacao para nova aluna\",
  \"nivel\": \"INICIANTE\",
  \"professora\": {
    \"id\": $PROF_ID
  }
}")

echo "$TREINO_RESPONSE" | python3 -m json.tool

TREINO_ID=$(echo "$TREINO_RESPONSE" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('id',''))")

if [ -z "$TREINO_ID" ]; then
  echo "ERRO: treino nao foi cadastrado. Resposta nao possui ID."
  exit 1
fi

echo ""
echo "Treino criado com ID: $TREINO_ID"
echo ""

echo "------------------------------------------"
echo "16. LISTAR TREINOS COMO ALUNA"
echo "------------------------------------------"

STATUS_ALUNA_TREINOS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/treinos" \
-H "Authorization: Bearer $TOKEN_ALUNA")

echo "ALUNA tentando GET /api/treinos -> HTTP $STATUS_ALUNA_TREINOS"
echo "Esperado: 200"
echo ""

echo "------------------------------------------"
echo "17. CADASTRAR MATRICULA ATIVA"
echo "------------------------------------------"

DATA_INICIO=$(date +%F)
DATA_VENCIMENTO=$(date -d "+30 days" +%F)

MATRICULA_RESPONSE=$(curl -s -X POST "$BASE_URL/api/matriculas" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN_ADMIN" \
-d "{
  \"aluna\": {
    \"id\": $ALUNA_ID
  },
  \"treino\": {
    \"id\": $TREINO_ID
  },
  \"dataInicio\": \"$DATA_INICIO\",
  \"dataVencimento\": \"$DATA_VENCIMENTO\"
}")

echo "$MATRICULA_RESPONSE" | python3 -m json.tool

MATRICULA_ID=$(echo "$MATRICULA_RESPONSE" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('id',''))")

if [ -z "$MATRICULA_ID" ]; then
  echo "ERRO: matricula nao foi cadastrada. Resposta nao possui ID."
  exit 1
fi

echo ""
echo "Matricula criada com ID: $MATRICULA_ID"
echo ""

echo "------------------------------------------"
echo "18. ALUNA CONSULTANDO SUAS MATRICULAS"
echo "------------------------------------------"

curl -s "$BASE_URL/api/matriculas/aluna/$ALUNA_ID" \
-H "Authorization: Bearer $TOKEN_ALUNA" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "19. REGISTRAR CHECK-IN DA ALUNA"
echo "------------------------------------------"

CHECKIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/frequencias/checkin/$ALUNA_ID" \
-H "Authorization: Bearer $TOKEN_ALUNA")

echo "$CHECKIN_RESPONSE" | python3 -m json.tool

FREQUENCIA_ID=$(echo "$CHECKIN_RESPONSE" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('id',''))")

if [ -z "$FREQUENCIA_ID" ]; then
  echo "ERRO: frequencia nao foi cadastrada. Resposta nao possui ID."
  exit 1
fi

echo ""
echo "Frequencia criada com ID: $FREQUENCIA_ID"
echo ""

echo "------------------------------------------"
echo "20. LISTAR FREQUENCIAS DA ALUNA"
echo "------------------------------------------"

curl -s "$BASE_URL/api/frequencias/aluna/$ALUNA_ID" \
-H "Authorization: Bearer $TOKEN_ALUNA" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "21. LISTAR MATRICULAS A VENCER COMO ADMIN"
echo "------------------------------------------"

curl -s "$BASE_URL/api/matriculas/a-vencer" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "22. CANCELAR MATRICULA COMO ADMIN"
echo "------------------------------------------"

curl -s -X PATCH "$BASE_URL/api/matriculas/$MATRICULA_ID/cancelar" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "23. TESTE ALUNA TENTANDO CANCELAR MATRICULA"
echo "------------------------------------------"

STATUS_ALUNA_CANCELAR=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH "$BASE_URL/api/matriculas/$MATRICULA_ID/cancelar" \
-H "Authorization: Bearer $TOKEN_ALUNA")

echo "ALUNA tentando PATCH /api/matriculas/$MATRICULA_ID/cancelar -> HTTP $STATUS_ALUNA_CANCELAR"
echo "Esperado: 403"
echo ""

echo "------------------------------------------"
echo "24. INATIVAR ALUNA COMO ADMIN"
echo "------------------------------------------"

curl -s -X PATCH "$BASE_URL/api/alunas/$ALUNA_ID/inativar" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "25. TENTAR LOGIN COM ALUNA INATIVA"
echo "------------------------------------------"

curl -s -X POST "$BASE_URL/api/auth/login" \
-H "Content-Type: application/json" \
-d "{
  \"login\": \"$ALUNA_CPF\",
  \"senha\": \"$ALUNA_SENHA\"
}" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "26. REATIVAR ALUNA COMO ADMIN"
echo "------------------------------------------"

curl -s -X PATCH "$BASE_URL/api/alunas/$ALUNA_ID/ativar" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "27. INATIVAR PROFESSORA COMO ADMIN"
echo "------------------------------------------"

curl -s -X PATCH "$BASE_URL/api/professoras/$PROF_ID/inativar" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "28. TENTAR LOGIN COM PROFESSORA INATIVA"
echo "------------------------------------------"

curl -s -X POST "$BASE_URL/api/auth/login" \
-H "Content-Type: application/json" \
-d "{
  \"login\": \"$PROF_CREF\",
  \"senha\": \"$PROF_SENHA\"
}" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "29. REATIVAR PROFESSORA COMO ADMIN"
echo "------------------------------------------"

curl -s -X PATCH "$BASE_URL/api/professoras/$PROF_ID/ativar" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo ""

echo "------------------------------------------"
echo "30. LISTAGENS FINAIS COMO ADMIN"
echo "------------------------------------------"

echo ""
echo "ALUNAS:"
curl -s "$BASE_URL/api/alunas" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo "PROFESSORAS:"
curl -s "$BASE_URL/api/professoras" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo "TREINOS:"
curl -s "$BASE_URL/api/treinos" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo "MATRICULAS:"
curl -s "$BASE_URL/api/matriculas" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo "FREQUENCIAS:"
curl -s "$BASE_URL/api/frequencias" \
-H "Authorization: Bearer $TOKEN_ADMIN" | python3 -m json.tool

echo ""
echo "=========================================="
echo " TESTE COMPLETO FINALIZADO"
echo "=========================================="
