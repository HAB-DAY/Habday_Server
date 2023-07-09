WAR_NAME=$(basename server-0.0.1-SNAPSHOT.war)
DEPLOY_PATH=/home/ec2-user/action
CURRENT_PID=$(pgrep -f $WAR_NAME)
NOW=$(date)

echo "> 현재 시간: $NOW" >>$DEPLOY_PATH/deploy.log

echo "> 현재 실행중인 애플리케이션 pid: $CURRENT_PID" >>$DEPLOY_PATH/deploy.log
if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> $DEPLOY_PATH/deploy.log
else
  echo "> kill -15 $CURRENT_PID" >> $DEPLOY_PATH/deploy.log
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH/$WAR_NAME
echo "> DEPLOY_JAR 배포: $DEPLOY_JAR"    >> $DEPLOY_PATH/deploy.log
nohup java -jar $DEPLOY_JAR >> $DEPLOY_PATH/deploy_spring.log 2>$DEPLOY_PATH/deploy_err.log &

echo "=====" >> $DEPLOY_PATH/deploy.log