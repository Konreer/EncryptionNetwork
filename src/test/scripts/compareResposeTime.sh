#!/bin/bash

output_file="execution_times.csv"

echo "Iteration,Command1_Time(s),Command2_Time(s)" > $output_file

iterations=100

measure_time() {
    start_sec=$(date +%s)
    start_nsec=$(date +%N)
    eval "$1" > /dev/null 2>&1
    end_sec=$(date +%s)
    end_nsec=$(date +%N)

    elapsed_sec=$((end_sec - start_sec))
    elapsed_nsec=$((end_nsec - start_nsec))

    total_time=$(echo "$elapsed_sec + $elapsed_nsec / 1000000000" | bc -l)
    printf "%.3f" "$total_time"
}

for ((i=1; i<=iterations; i++))
do
    echo "Iteration $i/$iterations"

    time1=$(measure_time "peer chaincode query -C keychannel -n EncryptionSmartcontract -c '{\"function\":\"retrieveData\",\"Args\":[\"john_doe\", \"secretPassword\"]}'")

    time2=$(measure_time "peer chaincode query -C keychannel -n EncryptionSmartcontract -c '{\"function\":\"retrieveData\",\"Args\":[\"john_doe\", \"secretpassword\"]}'")

    echo "$i,$time1,$time2" >> $output_file
done

echo "Test completed. Results saved to $output_file"
