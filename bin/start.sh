echo ""
echo "[信息] 运行Web工程。"
echo ""
APP_BASE_PATH=$(cd `dirname $0`; pwd)
SPRING_BOOT_OPTS="$SPRING_BOOT_OPTS --spring.profiles.active=dev"
# 优化JVM参数
JAVA_OPTS="$MAVEN_OPTS -Xms128m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./ -Ddolphin.conf=file:$APP_BASE_PATH/ -Ddolphin.log.path=$APP_BASE_PATH"
JAVA_OPTS="$JAVA_OPTS -Ddolphin.base.path=$APP_BASE_PATH"
if [ -z "$JAVA_HOME" ]; then
  RUN_JAVA=java
else
  RUN_JAVA="$JAVA_HOME"/bin/java
fi

# 根据情况修改 web.war 为您的 war 包名称
exec nohup $RUN_JAVA $JAVA_OPTS -jar $APP_BASE_PATH/JYXWXServer.jar $SPRING_BOOT_OPTS > nohup.out &
