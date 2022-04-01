APP_BASE_PATH=$(cd `dirname $0`; pwd)
echo $APP_BASE_PATH
ps -ef | grep $APP_BASE_PATH | grep java |grep -v grep | awk '{print $2}' | xargs kill -9