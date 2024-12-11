#!/bin/bash

CHANNEL_NAME="keychannel"
CHAINCODE_NAME="EncryptionSmartcontract"
ORDERER_CA="${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem"
PEER0_ORG1_CA="${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt"
PEER0_ORG2_CA="${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt"
ORDERER_ADDRESS="localhost:7050"
PEER0_ORG1_ADDRESS="localhost:7051"
PEER0_ORG2_ADDRESS="localhost:9051"

CONCURRENCY=80
TRANSACTIONS=1000

function send_transaction() {
    local USERNAME=$1
    local DATA=$2
    local PASSWORD=$3
    local SALT=$4
    local NOTE=$5
    local SERVICE_NAME=$6
    local TAGS=$7

    peer chaincode invoke \
        -o $ORDERER_ADDRESS --ordererTLSHostnameOverride orderer.example.com --tls --cafile $ORDERER_CA \
        -C $CHANNEL_NAME -n $CHAINCODE_NAME \
        --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_CA \
        --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_CA \
        -c '{"function":"storeData","Args":["'"$USERNAME"'", "'"$DATA"'", "'"$PASSWORD"'", "'"$SALT"'", "'"$NOTE"'", "'"$SERVICE_NAME"'", "'"$TAGS"'"]}' \
        >/dev/null 2>&1
}

echo "Rozpoczynam test obciążenia z $CONCURRENCY równoczesnymi transakcjami i $TRANSACTIONS łącznymi transakcjami..."

start_time=$(date +%s)
for ((i = 0; i < TRANSACTIONS; i++)); do
    USERNAME="user_$i"
    DATA="data_$i"
    PASSWORD="password_$i"
    SALT="salt_$i"
    NOTE="Test note $i"
    SERVICE_NAME="Service_$i"
    TAGS="tag1,tag2,tag3"

    send_transaction "$USERNAME" "$DATA" "$PASSWORD" "$SALT" "$NOTE" "$SERVICE_NAME" "$TAGS" &

    if (( i % CONCURRENCY == 0)); then
        wait
    fi
done

wait
end_time=$(date +%s)

duration=$((end_time - start_time))
echo "Test zakończony w czasie $duration sekund."
echo "Przepustowość: $((TRANSACTIONS / duration)) transakcji na sekundę."