#!/bin/bash

API="http://localhost:8080"
TIMESTAMP=$(date +%s)

CPF_ALUNA="987654${TIMESTAMP: -5}"
EMAIL_ALUNA="beatriz$TIMESTAMP@email.com"

CREF_PROFESSORA="CREF$TIMESTAMP"
EMAIL_PROFESSORA="carla$TIMESTAMP@email.com"

HOJE=$(date +%F)
VENCE_EM_5_DIAS=$(date -d "+5 days" +%F)
VENCIDA=$(date -d "-10 days" +%F)
INICIO_VENCIDA=$(date -d "-20 days" +%F)

echo "=============================="
echo "TESTE COMPLETO DA API"
echo "=============================="

echo ""
echo "API: $API"
echo ""

echo "=============================="
echo "1 - Testando login do ADMIN"
echo "=============================="

curl -s -X POST "$API/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "login": "admin",
  "senha": "admin123"
}'
echo -e "\n"

echo "=============================="
echo "2 - Testando login invalido"
echo "=============================="

curl -s -X POST "$API/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "login": "admin",
  "senha": "senhaerrada"
}'
echo -e "\n"

echo "=============================="
echo "3 - Listando alunas inicialmente"
echo "=============================="

curl -s "$API/api/alunas"
echo -e "\n"

echo "=============================="
echo "4 - Testando validacao de aluna com dados invalidos"
echo "=============================="

curl -s -X POST "$API/api/alunas" \
-H "Content-Type: application/json" \
-d '{
  "nome": "",
  "email": "emailerrado",
  "telefone": "",
  "cpf": "",
  "dataNascimento": null,
  "senhaInicial": ""
}'
echo -e "\n"

echo "=============================="
echo "5 - Cadastrando aluna"
echo "=============================="

ALUNA_RESPONSE=$(curl -s -X POST "$API/api/alunas" \
-H "Content-Type: application/json" \
-d "{
  \"nome\": \"Beatriz Lima\",
  \"email\": \"$EMAIL_ALUNA\",
  \"telefone\": \"45988887777\",
  \"cpf\": \"$CPF_ALUNA\",
  \"dataNascimento\": \"1999-08-20\",
  \"senhaInicial\": \"123456\"
}")

echo "$ALUNA_RESPONSE"

ALUNA_ID=$(echo "$ALUNA_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

echo ""
echo "Aluna criada com ID: $ALUNA_ID"
echo "Login da aluna: $CPF_ALUNA"
echo "Senha da aluna: 123456"
echo ""

echo "=============================="
echo "6 - Testando login da ALUNA"
echo "=============================="

curl -s -X POST "$API/api/auth/login" \
-H "Content-Type: application/json" \
-d "{
  \"login\": \"$CPF_ALUNA\",
  \"senha\": \"123456\"
}"
echo -e "\n"

echo "=============================="
echo "7 - Testando CPF duplicado"
echo "=============================="

curl -s -X POST "$API/api/alunas" \
-H "Content-Type: application/json" \
-d "{
  \"nome\": \"Outra Aluna\",
  \"email\": \"outra$TIMESTAMP@email.com\",
  \"telefone\": \"45999999999\",
  \"cpf\": \"$CPF_ALUNA\",
  \"dataNascimento\": \"2001-01-01\",
  \"senhaInicial\": \"123456\"
}"
echo -e "\n"

echo "=============================="
echo "8 - Inativando aluna"
echo "=============================="

curl -s -X PATCH "$API/api/alunas/$ALUNA_ID/inativar"
echo -e "\n"

echo "=============================="
echo "9 - Ativando aluna"
echo "=============================="

curl -s -X PATCH "$API/api/alunas/$ALUNA_ID/ativar"
echo -e "\n"

echo "=============================="
echo "10 - Testando validacao de professora com dados invalidos"
echo "=============================="

curl -s -X POST "$API/api/professoras" \
-H "Content-Type: application/json" \
-d '{
  "nome": "",
  "email": "emailerrado",
  "cref": "",
  "especialidade": "",
  "senhaInicial": ""
}'
echo -e "\n"

echo "=============================="
echo "11 - Cadastrando professora"
echo "=============================="

PROFESSORA_RESPONSE=$(curl -s -X POST "$API/api/professoras" \
-H "Content-Type: application/json" \
-d "{
  \"nome\": \"Carla Mendes\",
  \"email\": \"$EMAIL_PROFESSORA\",
  \"cref\": \"$CREF_PROFESSORA\",
  \"especialidade\": \"Funcional\",
  \"senhaInicial\": \"123456\"
}")

echo "$PROFESSORA_RESPONSE"

PROFESSORA_ID=$(echo "$PROFESSORA_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

echo ""
echo "Professora criada com ID: $PROFESSORA_ID"
echo "Login da professora: $CREF_PROFESSORA"
echo "Senha da professora: 123456"
echo ""

echo "=============================="
echo "12 - Testando login da PROFESSORA"
echo "=============================="

curl -s -X POST "$API/api/auth/login" \
-H "Content-Type: application/json" \
-d "{
  \"login\": \"$CREF_PROFESSORA\",
  \"senha\": \"123456\"
}"
echo -e "\n"

echo "=============================="
echo "13 - Testando CREF duplicado"
echo "=============================="

curl -s -X POST "$API/api/professoras" \
-H "Content-Type: application/json" \
-d "{
  \"nome\": \"Outra Professora\",
  \"email\": \"outraprof$TIMESTAMP@email.com\",
  \"cref\": \"$CREF_PROFESSORA\",
  \"especialidade\": \"Musculacao\",
  \"senhaInicial\": \"123456\"
}"
echo -e "\n"

echo "=============================="
echo "14 - Inativando professora"
echo "=============================="

curl -s -X PATCH "$API/api/professoras/$PROFESSORA_ID/inativar"
echo -e "\n"

echo "=============================="
echo "15 - Ativando professora"
echo "=============================="

curl -s -X PATCH "$API/api/professoras/$PROFESSORA_ID/ativar"
echo -e "\n"

echo "=============================="
echo "16 - Cadastrando treino"
echo "=============================="

TREINO_RESPONSE=$(curl -s -X POST "$API/api/treinos" \
-H "Content-Type: application/json" \
-d "{
  \"nome\": \"Treino Completo\",
  \"descricao\": \"Treino geral para iniciantes\",
  \"nivel\": \"INICIANTE\",
  \"professora\": {
    \"id\": $PROFESSORA_ID
  }
}")

echo "$TREINO_RESPONSE"

TREINO_ID=$(echo "$TREINO_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

echo ""
echo "Treino criado com ID: $TREINO_ID"
echo ""

echo "=============================="
echo "17 - Listando treinos da professora"
echo "=============================="

curl -s "$API/api/treinos/professora/$PROFESSORA_ID"
echo -e "\n"

echo "=============================="
echo "18 - Inativando treino"
echo "=============================="

curl -s -X PATCH "$API/api/treinos/$TREINO_ID/inativar"
echo -e "\n"

echo "=============================="
echo "19 - Ativando treino"
echo "=============================="

curl -s -X PATCH "$API/api/treinos/$TREINO_ID/ativar"
echo -e "\n"

echo "=============================="
echo "20 - Cadastrando matricula ativa/a vencer"
echo "=============================="

MATRICULA_RESPONSE=$(curl -s -X POST "$API/api/matriculas" \
-H "Content-Type: application/json" \
-d "{
  \"aluna\": {
    \"id\": $ALUNA_ID
  },
  \"treino\": {
    \"id\": $TREINO_ID
  },
  \"dataInicio\": \"$HOJE\",
  \"dataVencimento\": \"$VENCE_EM_5_DIAS\"
}")

echo "$MATRICULA_RESPONSE"

MATRICULA_ID=$(echo "$MATRICULA_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

echo ""
echo "Matricula ativa criada com ID: $MATRICULA_ID"
echo ""

echo "=============================="
echo "21 - Listando matriculas da aluna"
echo "=============================="

curl -s "$API/api/matriculas/aluna/$ALUNA_ID"
echo -e "\n"

echo "=============================="
echo "22 - Listando matriculas da professora"
echo "=============================="

curl -s "$API/api/matriculas/professora/$PROFESSORA_ID"
echo -e "\n"

echo "=============================="
echo "23 - Listando matriculas a vencer"
echo "=============================="

curl -s "$API/api/matriculas/a-vencer"
echo -e "\n"

echo "=============================="
echo "24 - Registrando check-in com matricula ativa"
echo "=============================="

FREQUENCIA_RESPONSE=$(curl -s -X POST "$API/api/frequencias/checkin/$ALUNA_ID")

echo "$FREQUENCIA_RESPONSE"

FREQUENCIA_ID=$(echo "$FREQUENCIA_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

echo ""
echo "Frequencia criada com ID: $FREQUENCIA_ID"
echo ""

echo "=============================="
echo "25 - Listando frequencias da aluna"
echo "=============================="

curl -s "$API/api/frequencias/aluna/$ALUNA_ID"
echo -e "\n"

echo "=============================="
echo "26 - Cadastrando matricula vencida"
echo "=============================="

MATRICULA_VENCIDA_RESPONSE=$(curl -s -X POST "$API/api/matriculas" \
-H "Content-Type: application/json" \
-d "{
  \"aluna\": {
    \"id\": $ALUNA_ID
  },
  \"treino\": {
    \"id\": $TREINO_ID
  },
  \"dataInicio\": \"$INICIO_VENCIDA\",
  \"dataVencimento\": \"$VENCIDA\"
}")

echo "$MATRICULA_VENCIDA_RESPONSE"
echo -e "\n"

echo "=============================="
echo "27 - Listando matriculas vencidas"
echo "=============================="

curl -s "$API/api/matriculas/vencidas"
echo -e "\n"

echo "=============================="
echo "28 - Cancelando matricula ativa"
echo "=============================="

curl -s -X PATCH "$API/api/matriculas/$MATRICULA_ID/cancelar"
echo -e "\n"

echo "=============================="
echo "29 - Listando todos os cadastros"
echo "=============================="

echo ""
echo "Alunas:"
curl -s "$API/api/alunas"
echo -e "\n"

echo "Professoras:"
curl -s "$API/api/professoras"
echo -e "\n"

echo "Treinos:"
curl -s "$API/api/treinos"
echo -e "\n"

echo "Matriculas:"
curl -s "$API/api/matriculas"
echo -e "\n"

echo "Frequencias:"
curl -s "$API/api/frequencias"
echo -e "\n"

echo "=============================="
echo "TESTE COMPLETO FINALIZADO"
echo "=============================="