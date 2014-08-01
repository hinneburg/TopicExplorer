TopicExplorer
=============

###Installation steps

####Generate ssh key and inform github about your public key
Follow the steps at https://help.github.com/articles/generating-ssh-keys .
After that clone the repository by executing in (some new) eclipse workspace folder
```
git clone git@github.com:hinneburg/TopicExplorer.git
```
####Install mysql 5.1 or later 
For Ubuntu see article http://wiki.ubuntuusers.de/MySQL .
```    
    sudo apt-get install mysql-server
```
This installation routine lets you chose a root password for the  mysql-db
####Change some mysql defaults to allow `load local infile`
This may not be neccessary in Ubuntu. You need to find mysql-server config file, 
for Ubuntu this is at `/etc/mysql/my.cnf`. Insert `local-infile=1` into both sections 
under `[mysqld]` and `[mysql]`.
####Create user as Mysql-Root and set privileges
using some mysql client, 
e.g. on Ubuntu: `mysql -u root -p`.
```
grant usage on *.* to <user>@localhost identified by '<password>';
grant all privileges on <Maerchen Datenbank>.* to <user>@localhost ;
grant file on *.* to <user>@localhost ;
```
####Create database as `<user>`
with some mysql client, e.g. on Ubuntu: `mysql -u <user> -p`.
```
create database <Maerchen Datenbank>;
```
####Download the document collection of Grimms fairy tales
  - Fulltexts: http://users.informatik.uni-halle.de/~hinnebur/maerchen/grimms_maerchen_without_duplicates.sql
  - Tokens: http://users.informatik.uni-halle.de/~hinnebur/maerchen/grimms_maerchen_without_duplicates_TE.csv

using an IP of uni-halle.de, e.g. login to the vpn of Uni Halle.
####Load documents into database
using some mysql client
e.g. on Ubuntu 
```
mysql -u <user> -p -d <Maerchen Datenbank> < <Path to File>grimms_maerchen_without_duplicates.sql
```
This creates and fills a table with the structure
```
CREATE TABLE orgTable (
  id int(11) NOT NULL,
  title text COLLATE utf8_bin NOT NULL,
  txt text COLLATE utf8_bin NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

####Create as developer the following two paths
`<path to your git copy>TopicExplorer/core-common/local/main/resources/` and 
`<path to your git copy>TopicExplorer/webapp/local/main/resources/`
and put into both the following two files with their respective contents.

Create file `config.local.properties`
```
	[Allgemein]
InCSVFile=<Path to File>grimms_maerchen_without_duplicates_TE.csv
plugins=text,hierarchicaltopic,colortopic
FrontendViews=slider,topic,text

	[Mallet]
malletNumTopics=10

	[Original Dokumenten-Table]
OrgTableName=orgTable
OrgTableId=id
OrgTableTxt=txt
````
and file `database.local.properties`
``` 
DbLocation=localhost:3306
DbUser=<user>
DbPassword=<password>
DB=<Maerchen Datenbank>
```
####Install R
See http://www.r-project.org . For Ubuntu see http://wiki.ubuntuusers.de/R .
```
sudo apt-get install r-base 
sudo apt-get install r-recommended 
```
For other systems, make sure that `Rscript` command is in your general search path.
####Install eclipse kepler 
from http://eclipse.org/ and install plugins via Help -> Install new Software
Select `Kepler - http://download.eclipse.org/releases/kepler` and chose the packages:
   - JST Server Adapters
   - JST Server Adapters Extensions
   - JST Server UI
   - m2e Maven Integration for Eclipse
   - m2e-WTP JAX-RS...
   - m2e-WTP JPA...
   - m2e-WTP JSF...
   - m2e-WTP Maven Integration

Download Apache TomCat 6.x zip file from 
https://tomcat.apache.org/download-60.cgi 
and extract it to some path.
Create a new server Apache TomCat 6. and reference the chosen path
File -> New -> Others -> Server
####Import Projects into Eclipse: 
Import -> Maven -> Existing Maven Project -> browse : Navigate to TopicExplorer Folder. 
Further, disable workspace resolution in Eclipse maven plugin: right click project, Maven -> Disable Workspace Resolution. This is important to prevent Eclipse from acidentally using artefacts from projects that are open in your workspace instead of the artefacts specified in dependencies in the `pom.xml`.
####Specify server settings
Create file `~/.m2/settings.xml`.

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
	<servers>
		<server>
			<id>snapshots</id>
			<username>db-devRead</username>
			<password></password>
			<filePermissions>444</filePermissions>
			<directoryPermissions>444</directoryPermissions>
		</server>
                <server>
                        <id>snapshots-write</id>
                        <username>db-devWrite</username>
                        <password></password>
                        <filePermissions>644</filePermissions>
                        <directoryPermissions>744</directoryPermissions>
                </server>
 	</servers>
 </settings>
```
The password for `db-devRead` must be left empty.
Ask the project manager about the password for `db-devWrite`.

####Build the project
Mouse right click on TopicExplorer -> Run as -> Maven Build (at first time input goals: clean install)

####Run preprocessing
Open a console and navigate to the workspace. Then go into the distribution module
```
cd TopicExplorer/distribution/target/distribution-1.0-SNAPSHOT-preprocessing/

```
Make sure your local property files are in the right place
```
ls resources/
```
should show at least `config.local.properties` and `database.local.properties`. 
When everything is fine, start the preprocessing
```
./bin/run-preprocessing.sh
```
This should create tables in your database, which are used by the web-based user interface. 
####Start webapp
Mouse right click on webapp -> Run -> Run on Server . 
In case of errors, do refresh (F5) on TopicExplorer and mouse right click -> Maven -> Update Projects. 
WebApp should appear in Eclipse-Browser. It is not functional there. 
You may use the webapp in Firefox or Safari http://localhost:8080/webapp/index.html

