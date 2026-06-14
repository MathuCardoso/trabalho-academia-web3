#!/bin/bash

echo "TESTE 1 - Listar alunas"
curl http://localhost:8080/api/alunas
echo -e "\n"

echo "TESTE 2 - Inativar aluna ID 1"
curl -X PATCH http://localhost:8080/api/alunas/1/inativar
echo -e "\n"

echo "TESTE 3 - Ativar aluna ID 1"
curl -X PATCH http://localhost:8080/api/alunas/1/ativar
echo -e "\n"

echo "TESTE 4 - Listar professoras"
curl http://localhost:8080/api/professoras
echo -e "\n"

echo "TESTE 5 - Inativar professora ID 1"
curl -X PATCH http://localhost:8080/api/professoras/1/inativar
echo -e "\n"

echo "TESTE 6 - Ativar professora ID 1"
curl -X PATCH http://localhost:8080/api/professoras/1/ativar
echo -e "\n"

echo "TESTE 7 - Listar treinos"
curl http://localhost:8080/api/treinos
echo -e "\n"

echo "TESTE 8 - Listar matriculas vencidas"
curl http://localhost:8080/api/matriculas/vencidas
echo -e "\n"

echo "TESTE 9 - Listar matriculas a vencer"
curl http://localhost:8080/api/matriculas/a-vencer
echo -e "\n"

echo "TESTE 10 - Registrar check-in da aluna ID 1"
curl -X POST http://localhost:8080/api/frequencias/checkin/1
echo -e "\n"

echo "TESTE 11 - Listar frequencias da aluna ID 1"
curl http://localhost:8080/api/frequencias/aluna/1
echo -e "\n"

echo "TESTES FINALIZADOS"
