
for i in `seq 1 181584`
do
 url=http://www.swkong.com/domain/index${i}.html
 curl $url | awk 'BEGIN {RS="</n>"} {print $0}' | grep 'http://www.swkong.com/?q' | awk -F '>' '{print $NF}' >> domains.data
done
