#!/bin/bash

#parameter $1 is te_identifier
#parameter $2 is search_string_id
if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters: topic explorer identifier and search string id is needed"
    exit 1
fi
TE_IDENTIFIER=$1
#check if TE_IDENTIFIER is valid
if [[ ! $TE_IDENTIFIER =~ ^[A-Z][_0-9A-Z]{0,44}$ ]]; then
    echo "TopicExplorer identifier $TE_IDENTIFIER is not valid"
    exit 1
fi

SEARCH_STRING_ID=$2
#check if SEARCH_STRING_ID is valid
if [[ ! $SEARCH_STRING_ID =~ ^[0-9]{1,9}$ ]]; then
    echo "Search String Id $SEARCH_STRING_ID is not valid"
    exit 1
fi

#check for base directory for configurations
#TE_BASE_DIR ... for /helper/tmp/data/token.csv
if [ ! -v TE_BASE_DIR ]; then
    echo "environment variable TE_BASE_DIR for the TE base directory is not set: example TE_BASE_DIR=/te_production"
    exit 1
fi

#check for the template config files for japanese blogs
if [ ! -v TE_CONFIG_TEMPLATE_BLOGS_JP ]; then
    echo "environment variable TE_CONFIG_TEMPLATE_BLOGS_JP for the template config files for japanese blogs is not set: example TE_CONFIG_TEMPLATE_BLOGS_JP=/te_config_template_jp"
    exit 1
fi

#check for web-server to grant mysql access from the correct server
if [ ! -v TE_WEBSERVER ]; then
    echo "environment variable TE_WEBSERVER to grant mysql access from the correct server is not set: example TE_WEBSERVER=topicexplorer.uni-halle.de"
    exit 1
fi

#check for db-server to grant mysql access from the correct server
if [ ! -v TE_DBSERVER_4MYSQL ]; then
    echo "environment variable TE_DBSERVER_4MYSQL to grant mysql access from the correct server is not set: example TE_DBSERVER_4MYSQL=topicexplorer.uni-halle.de"
    exit 1
fi

#check for db-server to grant mysql access from the correct server
if [ ! -v TE_DBSERVER ]; then
    echo "environment variable TE_DBSERVER to grant mysql access from the correct server is not set: example TE_DBSERVER=topicexplorer.uni-halle.de"
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

# Copy template tar files for the webapps into the right places
SOURCE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
if [ ! -e "$SOURCE"/exists_te.sh ]; then
    echo "$SOURCE/exists_te.sh does not exists."
    exit 1
fi

#check if $TE_IDENTIFIER is already in use.
"$SOURCE"/exists_te.sh $TE_IDENTIFIER
if [ ! $? -eq 0 ]
then
  echo "$TE_IDENTIFIER is already in use."
  exit 1
fi

#check whether search_string_id exists in crawl table
echo 'select SEARCH_STRING_ID from CRAWL;'|\
mysql --defaults-extra-file="$MYSQL_TE_MANAGEMENT_LOGIN_FILE" "$TE_MANAGEMENT_DB_NAME" |\
sed 1,1d|\
grep "$SEARCH_STRING_ID" >/dev/null
if [ ! $? -eq 0 ]
then
  echo "$SEARCH_STRING_ID is not found in CRAWL-Table, thus respective corpus is not available."
  exit 1
fi

#check if TE_CONFIG_TEMPLATE_BLOGS_JP dir exists
if [ ! -d "$TE_CONFIG_TEMPLATE_BLOGS_JP" ]; then
    echo "The directory for the template config files for japanese blogs does not exists: $TE_CONFIG_TEMPLATE_BLOGS_JP"
    exit 1
fi


TE_MANAGEMENT_DB_USER=$(grep 'user=' "$MYSQL_TE_MANAGEMENT_LOGIN_FILE" |sed 's/user=//g')
TE_MANAGEMENT_DB_PASSWORD=$(grep 'password=' "$MYSQL_TE_MANAGEMENT_LOGIN_FILE" |sed 's/password=//g')

#copy te-template to new te-project
cp -R "$TE_BASE_DIR"/helper/template "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"

#copy config template to resouces
cp "$TE_CONFIG_TEMPLATE_BLOGS_JP"/*.* "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/.

#replace placeholders in temporary config files
sed -i -- "s/<TE_BASE_DIR>/$(echo $TE_BASE_DIR | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<TE_DBSERVER>/$(echo $TE_DBSERVER | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<TE_DBSERVER_4MYSQL>/$(echo $TE_DBSERVER_4MYSQL | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<TE_MANAGEMENT_DB_NAME>/$(echo $TE_MANAGEMENT_DB_NAME | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<TE_MANAGEMENT_DB_USER>/$(echo $TE_MANAGEMENT_DB_USER | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<TE_MANAGEMENT_DB_PASSWORD>/$(echo $TE_MANAGEMENT_DB_PASSWORD | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<TE_IDENTIFIER>/$(echo $TE_IDENTIFIER | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<SEARCH_STRING_ID>/$(echo $SEARCH_STRING_ID | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*
sed -i -- "s/<TE_WEBSERVER>/$(echo $TE_WEBSERVER | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g"  "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"/resources/*


#run init topicexplorer
cd "$TE_BASE_DIR"/project/"$TE_IDENTIFIER"
./bin/run-initcorpus.sh

#deploy nlp webapp
mkdir -p webapp/nlp/WEB-INF/classes
cp resources/*.* webapp/nlp/WEB-INF/classes/.
cd webapp/nlp
mv webapp_prototype_nlp.tar ${TE_IDENTIFIER}_nlp.tar
#todo create template tarfile with correct permissions
tar --preserve-permissions --append --file=${TE_IDENTIFIER}_nlp.tar WEB-INF/classes/*.*
cat ${TE_IDENTIFIER}_nlp.tar | ssh  "$USER_AT_WEBAPP_SERVER" "mkdir $TE_WEBAPP_BASE_DIR/${TE_IDENTIFIER}_nlp && cd $TE_WEBAPP_BASE_DIR/${TE_IDENTIFIER}_nlp && tar xpf -"


