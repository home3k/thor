
mvn install

mvn exec:java -Dexec.mainClass="com.haoyayi.thor.bizgen.DatabaseCodegenRunner" -Dexec.classpathScope=runtime -Dexec.args="$*"
