#!/usr/bin/env bash
echo 'Copy files...'

scp -i ~/.ssh/id_rsa_pub \
    -r /home/alexflanker89/Документы/YesSimpleChat/yeschat/classes/artifacts/YesChat_jar \
 pi@192.168.0.102:/home/yeschat/

echo 'Restart server...'
echo 'start application'
sudo java -jar /home/yeschat/YesChat_jar/YesChat.jar
#ssh -i ~/.ssh/id_rsa_pub \
#pi@192.168.0.102 << EOF
#sudo pgrep java | xargs kill -9
#sudo nohup java -jar YesChat.jar > dev/null &
#EOF

echo 'Bye'


