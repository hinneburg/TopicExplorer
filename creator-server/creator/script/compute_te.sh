#!/bin/bash

#check for base directory for configurations
#TE_BASE_DIR ... for /helper/tmp/data/token.csv
if [ ! -v TE_BASE_DIR ]; then
    echo "environment variable TE_BASE_DIR for the TE base directory is not set: example TE_BASE_DIR=/te_production"
    exit 1
fi

#check for login file for mysql db TE_MANAGEMENT
if [ ! -v MYSQL_TE_MANAGEMENT_LOGIN_FILE ]; then
    echo "environment variable MYSQL_TE_MANAGEMENT_LOGIN_FILE for local mysql login to TE_MANAGEMENT database is not set: example MYSQL_TE_MANAGEMENT_LOGIN_FILE=/topicexplorer/my.cnf"
    exit 1
fi

#check for db name of TE_MANAGEMENT
if [ ! -v TE_MANAGEMENT_DB_NAME ]; then
    echo "environment variable TE_MANAGEMENT_DB_NAME for name of TE_MANAGEMENT database is not set: example TE_MANAGEMENT_DB_NAME=TE_MANAGEMENT"
    exit 1
fi

#check for user@webapp-server
if [ ! -v USER_AT_WEBAPP_SERVER ]; then
    echo "environment variable USER_AT_WEBAPP_SERVER for user@webapp-server is not set: example USER_AT_WEBAPP_SERVER=te@topicexplorer.uni-halle.de"
    exit 1
fi

#check for webapp-directory on webapp-server
if [ ! -v TE_WEBAPP_BASE_DIR ]; then
    echo "environment variable TE_WEBAPP_BASE_DIR for webapp-directory on webapp-server is not set: example TE_WEBAPP_BASE_DIR=/var/log/tomcat6/webapps"
    exit 1
fi

#check if TE_CONFIG_TEMPLATE_BLOGS_JP dir exists
if [ ! -d "$TE_CONFIG_TEMPLATE_BLOGS_JP" ]; then
    echo "The directory for the template config files for japanese blogs does not exists: $TE_CONFIG_TEMPLATE_BLOGS_JP"
    exit 1
fi

#check for db-server to grant mysql access from the correct server
if [ ! -v TE_DBSERVER ]; then
    echo "environment variable TE_DBSERVER to grant mysql access from the correct server is not set: example TE_DBSERVER=topicexplorer.uni-halle.de"
    exit 1
fi

TE_MANAGEMENT_DB_USER=$(grep 'user=' "$MYSQL_TE_MANAGEMENT_LOGIN_FILE" |sed 's/user=//g')
TE_MANAGEMENT_DB_PASSWORD=$(grep 'password=' "$MYSQL_TE_MANAGEMENT_LOGIN_FILE" |sed 's/password=//g')

#clean up
# Recreate the general project directory to run the te-computations
rm -rf "$TE_BASE_DIR"/helper/tmp
cp -R "$TE_BASE_DIR"/helper/template "$TE_BASE_DIR"/helper/tmp
#copy configuration template to tmp
cp "$TE_CONFIG_TEMPLATE_BLOGS_JP"/cmdb.local.properties "$TE_BASE_DIR"/helper/tmp/resources/.
cp "$TE_CONFIG_TEMPLATE_BLOGS_JP"/log4j.local.properties "$TE_BASE_DIR"/helper/tmp/resources/.
cp "$TE_CONFIG_TEMPLATE_BLOGS_JP"/mecab.local.properties "$TE_BASE_DIR"/helper/tmp/resources/.
TE_IDENTIFIER=TMP
#replace placeholders in temporary config files
sed -i -- "s/<TE_DBSERVER>/$(echo $TE_DBSERVER | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/helper/tmp/resources/*
sed -i -- "s/<TE_MANAGEMENT_DB_NAME>/$(echo $TE_MANAGEMENT_DB_NAME | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/helper/tmp/resources/*
sed -i -- "s/<TE_MANAGEMENT_DB_USER>/$(echo $TE_MANAGEMENT_DB_USER | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/helper/tmp/resources/*
sed -i -- "s/<TE_MANAGEMENT_DB_PASSWORD>/$(echo $TE_MANAGEMENT_DB_PASSWORD | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/helper/tmp/resources/*
sed -i -- "s/<TE_IDENTIFIER>/$(echo $TE_IDENTIFIER | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/helper/tmp/resources/*

#run computation of topicexplorer
cd "$TE_BASE_DIR"/helper/tmp
./bin/run-jobmanagement.sh

#get TE_IDENTIFIER
TE_IDENTIFIER=$(grep 'DB=' resources/database.local.properties |sed 's/DB=//g')

#check if TE_IDENTIFIER is valid
if [[ ! "$TE_IDENTIFIER" =~ ^[A-Z][_0-9A-Z]{0,44}$ ]]; then
    echo "TopicExplorer identifier $TE_IDENTIFIER is not valid"
    exit 1
fi

#deploy te webapp
mkdir -p webapp/te/WEB-INF/classes
# get the right log4j properties file
cp "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/log4j.local.properties resources/.
cp resources/*.* webapp/te/WEB-INF/classes/.
cd webapp/te
mv webapp_prototype_te.tar "$TE_IDENTIFIER"_te.tar
#todo create template tarfile with correct permissions
tar --preserve-permissions --append --file="$TE_IDENTIFIER"_te.tar WEB-INF/classes/*.*
cat "$TE_IDENTIFIER"_te.tar | ssh  "$USER_AT_WEBAPP_SERVER" "mkdir $TE_WEBAPP_BASE_DIR/${TE_IDENTIFIER}_te && cd $TE_WEBAPP_BASE_DIR/${TE_IDENTIFIER}_te && tar xpf -"

#save output to the project directory
#update config files and data
cp "$TE_BASE_DIR"/helper/tmp/resources/*.* "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/.
cp "$TE_BASE_DIR"/helper/tmp/data/*.* "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/data/.
#append the log file from helper/tmp to the log in project/TE_IDENTIFIER
cat "$TE_BASE_DIR"/helper/tmp/logs/TMP.log >>"$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/logs/"$TE_IDENTIFIER".log
