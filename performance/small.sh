

clear > small.data

head -n 10000 domains.data > .small.data

for i in `seq 1 10`
do
   cat .small.data >> .small-seq.data
done

# disorder the data
awk 'BEGIN{srand()}{b[rand()NR]=$0}END{for(x in b)print b[x], "A" }' .small-seq.data >> small.data

# clear
rm .small.data .small-seq.data

