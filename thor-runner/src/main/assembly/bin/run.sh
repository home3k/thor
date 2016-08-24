#!/bin/bash

service="thor"
INSTANCE_ID=${2:-0}

#dir config
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
LIB_DIR=$DEPLOY_DIR/lib
DATA_DIR=$DEPLOY_DIR/data
APM_DIR=$DEPLOY_DIR/oneapm
EXEC_DIR=$DATA_DIR/instance-$INSTANCE_ID
EXEC_LOG_DIR=$EXEC_DIR/logs
EXEC_TMP_DIR=$EXEC_DIR/tmp
EXEC_CONFCENTER_DIR=$EXEC_DIR/conf
mkdir -p $EXEC_LOG_DIR
mkdir -p $EXEC_TMP_DIR
mkdir -p $EXEC_CONFCENTER_DIR

#extra opt
JMX_PORT=$((9880+INSTANCE_ID))
DEBUG_PORT=$((19880+INSTANCE_ID))

GC_LOG=$EXEC_LOG_DIR/gc.log
DUMP_LOG=$EXEC_LOG_DIR/oom.hprof
SERVER_NAME=`hostname`
INSTANCE_PROP="instance.id"

#jvm config
JAVA_BASE_OPTS=" -Djava.awt.headless=true -Dfile.encoding=gbk -D$INSTANCE_PROP=$INSTANCE_ID -Djava.io.tmpdir=$EXEC_TMP_DIR"

JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "

JAVA_MEM_OPTS=" -server -Xms1g -Xmx1g -Xmn512m -XX:PermSize=256m -XX:MaxPermSize=256m -Xss256K -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
JAVA_MEM_OPTS_TEST=" -server -Xms512m -Xmx512m -Xmn256m -XX:PermSize=256m -XX:MaxPermSize=256m -Xss256K -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "

JAVA_GC_OPTS=" -verbose:gc -Xloggc:$GC_LOG -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$DUMP_LOG" 

LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
JAVA_CP=" -classpath $CONF_DIR:$LIB_JARS "

JAVA_OPTS="$EXTRA_JAVA_OPTS $JAVA_BASE_OPTS $JAVA_JMX_OPTS $JAVA_GC_OPTS $JAVA_CP"

#opts for test
JAVA_TEST_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=n $JAVA_MEM_OPTS_TEST "
#opts for production
JAVA_PROD_OPTS=" $JAVA_MEM_OPTS -DdictCache=redis"
JAVA_DEV_OPTS=" $JAVA_MEM_OPTS"

export JAVA_HOME="/home/wmkq/workspace/jdk"
export PATH=$PATH:"$JAVA_HOME/bin"

RUNJAVA="java"

EXEC_PID="$EXEC_DIR/instance.pid"

STDOUT_FILE=$EXEC_LOG_DIR/thor.out.log

case $1 in
start)
	#safe check
    PIDS=`ps  --no-heading -C java -f --width 2000 | grep "$CONF_DIR" | grep "${INSTANCE_PROP}=$INSTANCE_ID" |awk '{print $2}'`
    if [ -n "$PIDS" ]; then
        echo "ERROR: The thor instance $INSTANCE already started!"
        echo "PID: $PIDS"
        exit 0
    fi
    echo  "Starting thor instance $INSTANCE_ID ... "
	
    if [  -f $STDOUT_FILE ]
    then
        mv $STDOUT_FILE $STDOUT_FILE.$(date +%Y%m%d%H%M)
    fi
	
    if [  -f $GC_LOG ]
    then
        mv $GC_LOG $GC_LOG.$(date +%Y%m%d%H%M)
    fi

    $RUNJAVA $JAVA_PROD_OPTS $JAVA_OPTS com.haoyayi.thor.server.Bootstrap >> $STDOUT_FILE 2>&1 &

    echo -n $! > $EXEC_PID
    echo STARTED
    wait `cat $EXEC_PID`

    ;;
dev-start)
	#safe check
    PIDS=`ps  --no-heading -C java -f --width 2000 | grep "$CONF_DIR" | grep "${INSTANCE_PROP}=$INSTANCE_ID" |awk '{print $2}'`
    if [ -n "$PIDS" ]; then
        echo "ERROR: The thor instance $INSTANCE already started!"
        echo "PID: $PIDS"
        exit 0
    fi
    echo  "Starting thor instance $INSTANCE_ID ... "
	
    if [  -f $STDOUT_FILE ]
    then
        mv $STDOUT_FILE $STDOUT_FILE.$(date +%Y%m%d%H%M)
    fi
	
    if [  -f $GC_LOG ]
    then
        mv $GC_LOG $GC_LOG.$(date +%Y%m%d%H%M)
    fi

    $RUNJAVA $JAVA_DEV_OPTS $JAVA_OPTS com.haoyayi.thor.server.Bootstrap >> $STDOUT_FILE 2>&1 &

    echo -n $! > $EXEC_PID
    echo STARTED
    wait `cat $EXEC_PID`

    ;;
test-start)
	#safe check
    PIDS=`ps  --no-heading -C java -f --width 2000 | grep "$CONF_DIR" | grep "${INSTANCE_PROP}=$INSTANCE_ID" |awk '{print $2}'`
    if [ -n "$PIDS" ]; then
        echo "ERROR: The $SERVER_NAME instance $INSTANCE already started!"
        echo "PID: $PIDS"
        exit 1
    fi
    echo  "Starting thor instance $INSTANCE_ID for debuging... "
    $RUNJAVA $JAVA_TEST_OPTS $JAVA_OPTS com.haoyayi.thor.server.Bootstrap >> $STDOUT_FILE 2>&1 &
    echo -n $! > $EXEC_PID
    echo STARTED
    wait `cat $EXEC_PID`
    ;;
stop)
    echo "Stopping $SERVER_NAME instance $INSTANCE_ID ... "
	netstat -nl  2>/dev/null |grep -w "${port}" |grep "LISTEN" &>/dev/null
	if [ $? -ne 0 ];then
			echo "[INFO] The ${port} is CLOSED!"
			echo "[INFO] The service is already STOPPED!"
			exit 0;
	fi
	
	if [ $? -ne 0 ];then
		echo "[ERROR] shutdown monitor the ${host} FAIL!"
	else
		echo "[SUCC] shutdown monitor the ${host} SUCCESS!"
    fi

    if [ ! -f "$EXEC_PID" ];then
    	echo "error: could not find file $EXEC_PID"
    	exit 1;
    else
    PID=$(cat "$EXEC_PID")
    echo "kill this pid:" "${PID}"
    	kill "${PID}" > /dev/null 2>&1
    	rm $EXEC_PID
    	COUNT=`ps  --no-heading -C java -f --width 2000 | grep "$DEPLOY_DIR" | grep "${INSTANCE_PROP}=$INSTANCE_ID" | grep "${PID}" | awk '{print $2}' | wc -l`
	    if [ $COUNT -gt 0 ]; then
	    	sleep 4
	    	kill -9 "${PID}" > /dev/null 2>&1
	    fi
    	echo STOPPED
    fi
    ;;
test-stop)
    echo "Stopping $SERVER_NAME instance $INSTANCE_ID ... "
	netstat -nl  2>/dev/null |grep -w "${port}" |grep "LISTEN" &>/dev/null
	if [ $? -ne 0 ];then
			echo "[INFO] The ${port} is CLOSED!"
			echo "[INFO] The service is already STOPPED!"
			exit 0;
	fi
	
    if [ ! -f "$EXEC_PID" ];then
    	echo "error: could not find file $EXEC_PID"
    	exit 1;
    else
    	kill $(cat "$EXEC_PID") > /dev/null 2>&1
    	COUNT=`ps  --no-heading -C java -f --width 2000 | grep "$DEPLOY_DIR" | grep "${INSTANCE_PROP}=$INSTANCE_ID" | awk '{print $2}' | wc -l`
	    if [ $COUNT -gt 0 ]; then
	    	sleep 4
	    	kill -9 $(cat "$EXEC_PID") > /dev/null 2>&1
	    fi
    	rm $EXEC_PID
    	echo STOPPED
    fi
    ;;
*)
    echo "Usage: $0 {start|test-start|stop} {id}" >&2
    exit 1
esac

