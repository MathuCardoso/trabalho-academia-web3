#!/bin/bash

API="http://localhost:8080"

echo "=============================="
echo "TESTE COMPLETO DA API"
echo "=============================="

echo ""
echo "1 - Testando listagem inicial de alunas"
curl -s "$API/api/alunas"
echo -e "\n"

echo "2 - Testando validacao de aluna com dados invalidos"
curl -s -X POST "$API/api/alunas" \
-H "Content-Type: application/json" \
-d '{
  "nome": "",
  "email": "emailerrado",
  "telefone": "45999999999",
  "cpf": "12345678900",
  "dataNascimento": "2000-05-10"
}'
echo -e "\n"

echo "3 - Cadastrando aluna"
ALUNA_RESPONSE=$(curl -s -X POST "$API/api/alunas" \
-H "Content-Type: application/json" \
-d '{
  "nome": "Beatriz Lima",
  "email": "beatriz@email.com",
  "telefone": "45988887777",
  "cpf": "98765432100",
  "dataNascimento": "1999-08-20"
}')

echo "$ALUNA_RESPONSE"
ALUNA_ID=$(echo "$ALUNA_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo -e "\nAluna criada com ID: $ALUNA_ID\n"

echo "4 - Inativando aluna"
curl -s -X PATCH "$API/api/alunas/$ALUNA_ID/inativar"
echo -e "\n"

echo "5 - Ativando aluna"
curl -s -X PATCH "$API/api/alunas/$ALUNA_ID/ativar"
echo -e "\n"

echo "6 - Cadastrando professora"
PROFESSORA_RESPONSE=$(curl -s -X POST "$API/api/professoras" \
-H "Content-Type: application/json" \
-d '{
  "nome": "Carla Mendes",
  "email": "carla@email.com",
  "especialidade": "Funcional"
}')

echo "$PROFESSORA_RESPONSE"
PROFESSORA_ID=$(echo "$PROFESSORA_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo -e "\nProfessora criada com ID: $PROFESSORA_ID\n"

echo "7 - Inativando professora"
curl -s -X PATCH "$API/api/professoras/$PROFESSORA_ID/inativar"
echo -e "\n"

echo "8 - Ativando professora"
curl -s -X PATCH "$API/api/professoras/$PROFESSORA_ID/ativar"
echo -e "\n"

echo "9 - Cadastrando treino"
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
echo -e "\nTreino criado com ID: $TREINO_ID\n"

echo "10 - Inativando treino"
curl -s -X PATCH "$API/api/treinos/$TREINO_ID/inativar"
echo -e "\n"

echo "11 - Ativando treino"
curl -s -X PATCH "$API/api/treinos/$TREINO_ID/ativar"
echo -e "\n"

echo "12 - Cadastrando matricula ativa/a vencer"
MATRICULA_RESPONSE=$(curl -s -X POST "$API/api/matriculas" \
-H "Content-Type: application/json" \
-d "{
  \"aluna\": {
    \"id\": $ALUNA_ID
  },
  \"treino\": {
    \"id\": $TREINO_ID
  },
  \"dataInicio\": \"2026-06-12\",
  \"dataVencimento\": \"2026-06-19\"
}")

echo "$MATRICULA_RESPONSE"
MATRICULA_ID=$(echo "$MATRICULA_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo -e "\nMatricula criada com ID: $MATRICULA_ID\n"

echo "13 - Listando matriculas a vencer"
curl -s "$API/api/matriculas/a-vencer"
echo -e "\n"

echo "14 - Cancelando matricula"
curl -s -X PATCH "$API/api/matriculas/$MATRICULA_ID/cancelar"
echo -e "\n"

echo "15 - Cadastrando matricula vencida"
curl -s -X POST "$API/api/matriculas" \
-H "Content-Type: application/json" \
-d "{
  \"aluna\": {
    \"id\": $ALUNA_ID
  },
  \"treino\": {
    \"id\": $TREINO_ID
  },
  \"dataInicio\": \"2026-01-01\",
  \"dataVencimento\": \"2026-01-10\"
}"
echo -e "\n"

echo "16 - Listando matriculas vencidas"
curl -s "$API/api/matriculas/vencidas"
echo -e "\n"

echo "17 - Registrando check-in"
curl -s -X POST "$API/api/frequencias/checkin/$ALUNA_ID"
echo -e "\n"

echo "18 - Listando frequencias da aluna"
curl -s "$API/api/frequencias/aluna/$ALUNA_ID"
echo -e "\n"

echo "19 - Listando tudo"
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
