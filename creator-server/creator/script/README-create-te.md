### Creation of TopicExplorer ####
The creation consists of two steps
(i) initialization and
(ii) computation.
In each step the respective bash-script is executed.
Prior to execution the following environment variables have to be set:
```
export TE_BASE_DIR=<path/to/te/production/site>
export TE_MANAGEMENT_DB_NAME=<TE_MANAGEMENT_DB>
export TE_VERSION=te-1.4-SNAPSHOT
export MYSQL_TE_MANAGEMENT_LOGIN_FILE="$TE_BASE_DIR"/config/tedb_user_login.cnf
# user needs to be able to automatically login with ssh key without any interaction
export USER_AT_WEBAPP_SERVER=<user>@<machine.with.tomcat.server>
export TE_WEBAPP_BASE_DIR=>/path/to/tomcat6/webapps/>
# dir with templates for config files
export TE_CONFIG_TEMPLATE_BLOGS_JP="$TE_BASE_DIR"/helper/te_config_template_jp
export TE_WEBSERVER=<machine.with.tomcat.server>
export TE_DBSERVER=<machine.with.database.server>
export TE_DBSERVER_4MYSQL=<local.name.of.machine.with.database.server>
```

#### Step 1: Initialization ####
```
/path/to/te-configuration-ui/creator/script/initialize_te.sh <TE_IDENTIFIER> <SEARCH_STRING_ID>
```
Before running, the script calls
```
/path/to/te-configuration-ui/creator/script/exists_te.sh <TE_IDENTIFIER>
```
to check that
(i) no database `<TE_IDENTIFIER>` exists already,
(ii) no configuration dir `"$TE_BASE_DIR"/project/<TE_IDENTIFIER>` exists
already and
(iii) no webapps `<TE_IDENTIFIER>_nlp` and `<TE_IDENTIFIER>_te` exists already
at the tomcat server.

After initialization, a database `<TE_IDENTIFIER>` with the corpus is created
and the nlp-webapp is deployed to the tomcat server. Using the nlp-webapp,
the user can select the vocabulary, specifies the number of topics and
specify frames, which, however, is disabled at the moment.
After completion of the specification, a zip-file with configuration files is
put into table `TOPIC_EXPLORER` with a date in `PENDING` and
null-values in `RUNNING` and `FINISHED`.
#### Step 2: Computation ####
```
/path/to/te-install-test/te-configuration-ui/creator/script/compute_te.sh
```
This script prepares the temporary computation directory
at `"$TE_BASE_DIR"/helper/tmp`.
From there it runs `bin/run-jobmanagement.sh`.
This Java-Program checks in table `TOPIC_EXPLORER` for tuples with null-values
in both `RUNNING` and `FINISHED`.
It grabs the one of the tuples, extracts the related zip-file with
configurations to `resources` and runs the topic-explorer computation,
i.e preparing the input for topic modeling, run topic modeling,
run several post-analyses and deploys the te-webapp to the tomcat server.
When, finished, the database `<TE_IDENTIFIER>` is complete,
the te-webapp is ready, all relevant files are copied to `"$TE_BASE_DIR"/project/<TE_IDENTIFIER>`
and the `<TE_IDENTIFIER>` can be found
in `"$TE_BASE_DIR"/helper/tmp/resources/database.local.properties` as
the `DB` property.
