#!/bin/sh

usage() {
	echo "Usage:"
	echo "    startup.sh [-c config_folder] [-l log_folder] [-d debug mode] [-h]"
	echo "Description:"
	echo "    config_folder - config folder path, not must,if empty,default classpath"
	echo "    log_folder - hsb server's logs base folder, /home/work  log path: /home/work/logs"
	echo "    debug_mode - 1|0  1 means debug port is open, 0 close ,default 0"
	echo "    -h - show this help"
	exit -1
}
LOG_BASE_DIR=""
CONFIG_DIR=""
DEBUG_MODE="0";

while getopts "h:l:c:d:" arg
do
	case $arg in
	    l) LOG_BASE_DIR=$OPTARG;;
		c) CONFIG_DIR=$OPTARG;;
		d) DEBUG_MODE=$OPTARG;;
		h) usage;;
		?) usage;;
	esac
done

# echo Baidu.com,Inc.
# echo 'Copyright (c) 2000-2013 All Rights Reserved.'
# echo Distributed
# echo https://github.com/brucexx/heisenberg
# echo brucest0078@gmail.com

#check JAVA_HOME & java
noJavaHome=false
if [ -z "$JAVA_HOME" ] ; then
    noJavaHome=true
fi
if [ ! -e "$JAVA_HOME/bin/java" ] ; then
    noJavaHome=true
fi
if $noJavaHome ; then
    echo
    echo "Error: JAVA_HOME environment variable is not set."
    echo
    exit 1
fi
#==============================================================================
#set JAVA_OPTS
JAVA_OPTS="-server -Xms4G -Xmx6G -XX:MaxPermSize=128M  -XX:+AggressiveOpts -XX:MaxDirectMemorySize=6G"
#performance Options
JAVA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9008 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
#JAVA_OPTS="$JAVA_OPTS -Xss256k"
#JAVA_OPTS="$JAVA_OPTS -XX:+AggressiveOpts"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseBiasedLocking"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseFastAccessorMethods"
#JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
#JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
#JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"
#JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"
#GC Log Options
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
#debug Options
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8065,server=y,suspend=n"
#==============================================================================

#set HOME
CURR_DIR=`pwd`
cd `dirname "$0"`/..
REDISPROXY_HOME=`pwd`
cd $CURR_DIR
if [ -z "$REDISPROXY_HOME" ] ; then
    echo
    echo "Error: REDISPROXY_HOME environment variable is not defined correctly."
    echo
    exit 1
fi
#==============================================================================

#set CLASSPATH
REDISPROXY_CLASSPATH="$REDISPROXY_HOME/conf:$REDISPROXY_HOME/lib/classes"
for i in "$REDISPROXY_HOME"/lib/*.jar
do
    REDISPROXY_CLASSPATH="$REDISPROXY_CLASSPATH:$i"
done
#==============================================================================

#startup Server
RUN_CMD="\"$JAVA_HOME/bin/java\""
RUN_CMD="$RUN_CMD -DappName=redisproxy -DREDISPROXY_HOME=\"$REDISPROXY_HOME\""
RUN_CMD="$RUN_CMD -classpath \"$REDISPROXY_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.github.redis.proxy.server.startup.ProxyStartUp  $@"
RUN_CMD="$RUN_CMD >> \"$REDISPROXY_HOME/logs/console.log\" 2>&1 &"
echo $RUN_CMD
eval $RUN_CMD
echo $! > $REDISPROXY_HOME/bin/redisproxy.pid
#==============================================================================
