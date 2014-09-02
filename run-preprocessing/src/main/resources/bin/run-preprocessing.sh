#!/bin/sh

exec java -Dfile.encoding=UTF-8 -classpath "etc:resources:lib/*:." cc.topicexplorer.Run
