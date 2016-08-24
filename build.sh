#!/bin/bash
export JAVA_HOME=${JAVA_HOME_1_6}
export PATH=${JAVA_HOME_1_6}/bin:${PATH}

if [ "x$MAVEN_HOME" != "x" ]
then
    MVN_BIN=$MAVEN_HOME/bin/mvn
else
	MVN_BIN=mvn
fi

cd `dirname $0`
BUILD_DIR=`pwd`

echo $BUILD_DIR

function check_error()
{
	if [ ${?} -ne 0 ]
	then
		echo "Error! Please Check..."
 		exit 1
	fi
}

PROFILE=prod

if [ $# -ne 0 ]
then
	PROFILE=$1
fi

$MVN_BIN clean install -U -P$PROFILE -Dmaven.test.skip=true
check_error
rm -rf output
mkdir output

mv ./thor-runner/target/*.zip ./output
