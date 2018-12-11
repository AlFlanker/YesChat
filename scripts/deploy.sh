#!/usr/bin/env bash
echo 'Copy files...'
sudo scp -r /home/alexflanker89/Документы/YesSimpleChat/yeschat/target/out/ root@188.225.32.238:/home/YesChat/newVersion/
#  oX3WuZjs
#scp -i ~/.ssh/id_rsa_pub \
#    -r /home/alexflanker89/Документы/YesSimpleChat/yeschat/out \
# pi@192.168.0.102:/home/yeschat/

echo 'Restart server...'
echo 'start application'
#sudo java -jar /home/yeschat/YesChat_jar/YesChat.jar
#ssh -i ~/.ssh/id_rsa_pub \
#pi@192.168.0.102 << EOF
#sudo mvn  java | xargs kill -9
#sudo nohup java -jar YesChat.jar > dev/null &
#EOF

echo 'Bye'


