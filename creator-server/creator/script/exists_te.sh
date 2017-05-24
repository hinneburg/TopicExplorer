#!/bin/bash

#parameter $1 is te_identifier
if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters: topic explorer identifier is needed"
    exit 1
fi
TE_IDENTIFIER=$1
#check if TE_IDENTIFIER is valid
if [[ ! $TE_IDENTIFIER =~ ^[A-Z][_0-9A-Z]{0,44}$ ]]; then
    echo "TopicExplorer identifier $TE_IDENTIFIER is not valid"
    exit 1
fi

#check for base directory for configurations
if [ ! -v TE_BASE_DIR ]; then
    echo "environment variable TE_BASE_DIR for the te base directory is not set: example TE_BASE_DIR=/te_production"
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

#check for login file for mysql db TE_MANAGEMENT
if [ ! -v MYSQL_TE_MANAGEMENT_LOGIN_FILE ]; then
    echo "environment variable MYSQL_TE_MANAGEMENT_LOGIN_FILE for local mysql login to TE_MANAGEMENT database is not set: example MYSQL_TE_MANAGEMENT_LOGIN_FILE=/topicexplorer/my.cnf"
    exit 1
fi

TE_CONFIG_DIR="$TE_BASE_DIR/project/$TE_IDENTIFIER"   # directory of the configuration files
if [ -d "$TE_CONFIG_DIR" ]
then
  echo "$TE_CONFIG_DIR config-directory exists already."
  exit 2
fi
#check whether webapp with name exists
TE_WEBAPP_TE_DIR="$TE_WEBAPP_BASE_DIR/${TE_IDENTIFIER}_te"     # directory of webapp
TE_WEBAPP_NLP_DIR="$TE_WEBAPP_BASE_DIR/${TE_IDENTIFIER}_nlp"   # directory of webapp
if (ssh $USER_AT_WEBAPP_SERVER "[ -d $TE_WEBAPP_TE_DIR ]")
then
  echo "$TE_WEBAPP_TE_DIR webapp-directory exists already."
  exit 3
fi
if (ssh $USER_AT_WEBAPP_SERVER "[ -d $TE_WEBAPP_NLP_DIR ]")
then
  echo "$TE_WEBAPP_NLP_DIR webapp-directory exists already."
  exit 4
fi

#check whether database with name exists
echo 'show databases;'|\
mysql --defaults-extra-file="$MYSQL_TE_MANAGEMENT_LOGIN_FILE" |\
sed 1,1d|\
grep "$TE_IDENTIFIER" >/dev/null
if [ $? -eq 0 ]
then
  echo "$TE_IDENTIFIER database exists already."
  exit 5
fi


