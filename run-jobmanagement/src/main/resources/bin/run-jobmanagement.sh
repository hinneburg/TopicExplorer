#!/bin/sh

retcode=`java -Dfile.encoding=UTF-8 -classpath "etc:resources:lib/*:." cc.topicexplorer.StartJobManagement`
echo "LALA: ${retcode}"
