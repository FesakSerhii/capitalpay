#!/bin/sh


dt=$(date '+%Y%m%d%H%M');

cd /var/log/capitalpay/
mv smart.txt smart"$dt".txt
gzip  smart"$dt".txt


cd ~/
nohup sh start.sh > /var/log/capitalpay/smart.txt 2>&1 &

tail -f /var/log/capitalpay/smart.txt


