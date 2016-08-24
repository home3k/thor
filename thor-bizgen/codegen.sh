#!/usr/bin/env bash

# freemarker cache ftl.

if [ $# ne 2 ]
then
    echo "sh codegen model proc.step"
fi

cd ../

sh build.sh dev

cd -

mvn exec:java -Dexec.mainClass="com.haoyayi.thor.bizgen.CodegenRunner" -Dexec.classpathScope=runtime -Dexec.args="$1 $2"
