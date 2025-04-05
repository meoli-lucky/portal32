#!/bin/sh
echo "Using Java version: $JAVA_VERSION"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set FLEX_HOME if not already set
[ -z "$FLEX_HOME" ] && FLEX_HOME=`cd "$PRGDIR/.." ; pwd`

echo $FLEX_HOME

# ----- Process the input command ----------------------------------------------
args=""
for c in $*
do
    if [ "$c" = "--start" ] || [ "$c" = "-start" ] || [ "$c" = "start" ]; then
          CMD="start"
    elif [ "$c" = "--stop" ] || [ "$c" = "-stop" ] || [ "$c" = "stop" ]; then
          CMD="stop"
    elif [ "$c" = "--version" ] || [ "$c" = "-version" ] || [ "$c" = "version" ]; then
          CMD="version"
    elif [ "$c" = "--restart" ] || [ "$c" = "-restart" ] || [ "$c" = "restart" ]; then
          CMD="restart"
    else
        args="$args $c"
    fi
done

PLUGINS_DIR="$FLEX_HOME/plugins"
CLASSPATH=""
for jar in "$PLUGINS_DIR"/*.jar; do
	if [ -z "$CLASSPATH" ]; then
		if [ "$jar" != "${jar#*impl}" ]; then
			echo "Ignore '$jar'"
		else
			CLASSPATH="$jar"
		fi
	else
		if [ "$jar" != "${jar#*impl}" ]; then
			echo "Ignore '$jar'"
		else
			CLASSPATH="$CLASSPATH:$jar"
		fi
	fi
done
JVM_MEM_OPTS="-Xms256m -Xmx512m"
JAVA_OPTS="-Djasypt.encryptor.password=flexcore"
SPRING_OPTS="-Dloader.path='$PLUGINS_DIR','$FLEX_HOME/bin' -Dspring-boot.run.jvmArguments='-Duser.timezone=UTC' -Dspring.config.location='file:application.properties'"

if [ "$CMD" = "start" ]; then
  CURRENT_PID=`ps axf | grep $FLEX_HOME/bin/portal325.jar | grep -v grep | awk '{ printf $1 }'`
  echo $CURRENT_PID
  if [ ! -z $CURRENT_PID ]; then
    echo "Process is already running"
	exit 0
  fi
  export FLEX_HOME="$FLEX_HOME"
  export JVM_MEM_OPTS="$JVM_MEM_OPTS"
  export JAVA_OPTS="$JAVA_OPTS"
  export SPRING_OPTS="$SPRING_OPTS"
  
  echo "Using Java memory options: $JVM_MEM_OPTS"
  echo "Using Java process options: $JAVA_OPTS"
  echo "Using Spring process options: $SPRING_OPTS"
  echo "Using Classpath: $CLASSPATH"

  nohup java  $JVM_MEM_OPTS -jar "$FLEX_HOME"/bin/portal325.jar $JAVA_OPTS $SPRING_OPTS > ../logs/catalina.out &
  
  #java -cp "$FLEX_HOME/bin/portal325.jar:$CLASSPATH" $JVM_MEM_OPTS $JAVA_OPTS $SPRING_OPTS > ../logs/catalina.out &
  
  tail -f ../logs/catalina.out
elif [ "$CMD" = "stop" ]; then
  export FLEX_HOME="$FLEX_HOME"
  #kill -term `cat "$FLEX_HOME"/portal325.pid`
  ps axf | grep $FLEX_HOME/bin/portal325.jar | grep -v grep | awk '{print "kill -term " $1}' | sh
  exit 0
elif [ "$CMD" = "restart" ]; then
  export FLEX_HOME="$FLEX_HOME"
  export JVM_MEM_OPTS="$JVM_MEM_OPTS"
  export JAVA_OPTS="$JAVA_OPTS"
  export SPRING_OPTS="$SPRING_OPTS"
  #kill -term `cat "$FLEX_HOME"/portal325.pid`
  CURRENT_PID=`ps axf | grep $FLEX_HOME/bin/portal325.jar | grep -v grep | awk '{ printf $1 }'`
  echo $CURRENT_PID
  if [ ! -z $CURRENT_PID ]; then 
	kill -term $CURRENT_PID
  fi
  echo "Using Java memory options: $JVM_MEM_OPTS"
  echo "Using Java process options: $JAVA_OPTS"
  echo "Using Spring process options: $SPRING_OPTS"
  #nohup java  $JVM_MEM_OPTS -jar "$FLEX_HOME"/bin/portal325.jar $JAVA_OPTS $SPRING_OPTS > ../logs/catalina.out &
  java -cp "$FLEX_HOME/bin/portal325.jar:$CLASSPATH" $JVM_MEM_OPTS $JAVA_OPTS $SPRING_OPTS > ../logs/catalina.out &
  tail -f ../logs/catalina.out
  exit 0

elif [ "$CMD" = "version" ]; then
  cat "$FLEX_HOME"/bin/version.txt
  cat "$FLEX_HOME"/bin/wso2carbon-version.txt
  exit 0
fi
