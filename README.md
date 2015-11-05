Postduif2
--------------------------------------------------------------------------
* https://confluence.42.nl/display/projects/Postduif+V2

Description
-----------

REST webservice that is (going to be) used as backend for all internal applications.

Instant developer
-----------------

Postduif can be installed simply by performing the following commands:

* git clone https:// \<user>@stash.42.nl/scm/vol/postduif.git
* mvn clean install 
* mvn tomcat7:run

To get a feel for what the application can do, go to: http://localhost:8042/api/index.html
When deployed, the application uses Crowd. For local development, the username & password are "dev" resp. "dev42".

### Release
1. Create a zip of postduif war and db directory and deploy it to nexus.
    ```
    mvn release:prepare
    mvn clean release:perform
    ```
2. Copy the war file to the server.  
    ```
    scp <local-path>/postduif/target/postduif-<version>.war <user>@<hostname>:/home/<user>
    ```
3. Login in to the server and open a shell.  
    ```
    ssh <user>@<hostname>
    ```
4. Copy war file to old_releases.  
    ```
    cd ~/postduif/oude_releases 
    -- create directory for release
    mkdir postduif-\<version> && cd postduif-\<version>
    -- move war file to created directory
    mv /home/\<user>/postduif-\<version>.war .
    ```
5. Remove current war from the server.  
    ```
    rm -f ~/postduif/base/webapps/ROOT.war
    ```
6. Stop postduif.  
    ```
    service postduif stop
    ```
7. Copy the new war file to webapps.  
    ```
    cp ~/postduif/oude_releases/postduif-<vesion>/postduif-<version>.war ~/postduif/base/webapps/ROOT.war
    ```
8. Start postduif.  
    ```
    service postduif start
    ```
      
You can view the logs in ~/postduif/base/logs  

### Migrate database
1. Copy new changelogs to ~/postduif/db/changelogs/
2. Stop mus & postduif.
3. Run migrate_with_backup.sh  
    ```
	./migrate_with_backup.sh
    ```
4. Start mus & postuif.

### First release postduif1 -> postduif2
1. Copy a few files to the server  
    ```
        -- copy the liquibase jar   
        scp <local-path>/postduif/target/dbmigrator/lib/liquibase.jar <user>@<hostname>:/home/<user>  
        -- copy changelogs directory and changelog file.  
        scp -r <local-path>/postduif/src/main/db/changelogs <user>@<hostname>:/home/<user>  
        scp <local-path>/postduif/src/main/db/changelog.xml <user>@<hostname>:/home/<user>  
    ```
2. Go to db directory
    ```
    cd ~/postduif/db
    ```
3. Remove groovy files
    ```
    rm -r changelogs
    rm changelog.groovy
    ```
4. Move new changelogs and jar file    
    ```
    mv /home/<user>/changelogs .
    mv /home/<user>/changelog.xml .
    mv /home/<user>/liquibase.jar lib/
    ```
5. Edit liquibase properties so the file looks like this:
  - driver=com.mysql.jdbc.Driver
  - classpath=lib/mysql-connector-java-5.1.9.jar
  - url=jdbc:mysql://localhost/voliere_test
  - username=voliere
  - password=v0l13r3
  - changeLogFile=changelog.xml

6. Edit context.xml replace this line.
    ```xml
    <Environment name="HibernateDialect"  
      value="nl.mad.voliere.postduif.configuration.MyMysqlDialect"
      type="java.lang.String"/>
      ```
  By  
  ```xml
  <Environment name="HibernateDialect"  
    value="nl.mad.voliere.postduif2.config.database.MyMysqlDialect"  
    type="java.lang.String"/>
  ```

7. Edit line 42 in migrate_with_backup.sh, replace:
  ```
  java -classpath "lib/jarb-migrations-2.1.0-liquibase.jar:lib/mysql-connector-java-5.1.9.jar:." org.jarbframework.migrations.liquibase.LiquibaseMigratorMain
  ```
  By
  ```
  java -jar lib/liquibase.jar update
  ```

8. run following sql command to recreate databasechangelog table.
    ```sql
    DROP TABLE IF EXISTS `DATABASECHANGELOG`;
    CREATE TABLE `DATABASECHANGELOG` (
      `ID` varchar(255) NOT NULL,
      `AUTHOR` varchar(255) NOT NULL,
      `FILENAME` varchar(255) NOT NULL,
      `DATEEXECUTED` datetime NOT NULL,
      `ORDEREXECUTED` int(11) NOT NULL,
      `EXECTYPE` varchar(10) NOT NULL,
      `MD5SUM` varchar(35) DEFAULT NULL,
      `DESCRIPTION` varchar(255) DEFAULT NULL,
      `COMMENTS` varchar(255) DEFAULT NULL,
      `TAG` varchar(255) DEFAULT NULL,
      `LIQUIBASE` varchar(20) DEFAULT NULL,
      `CONTEXTS` varchar(255) DEFAULT NULL,
      `LABELS` varchar(255) DEFAULT NULL
    );
    ```
    ```sql
    INSERT INTO `DATABASECHANGELOG` VALUES 
    ('10','Quadcore','/usr/local/voliere/postduif/db/changelogs/changelog_000.xml','2015-09-24 15:44:35',1,'EXECUTED','7:915484e47c410be4a72580303002560b','createTable (x50)','Initial creation of all tables in database.',NULL,'3.4.0',NULL,NULL),
    ('11','Quadcore','/usr/local/voliere/postduif/db/changelogs/changelog_000.xml','2015-09-24 15:44:35',2,'EXECUTED','7:bb763a27a64b9f443abb12d4ec54a185','createView (x13)','Initial creation of all views in database.',NULL,'3.4.0',NULL,NULL),
    ('12','Quadcore','/usr/local/voliere/postduif/db/changelogs/changelog_000.xml','2015-09-24 15:44:35',3,'EXECUTED','7:0437ca97c8fbfc6f13ce25b5baa66457','createView (x3)','Initial creation of all mysql specific views in database.',NULL,'3.4.0',NULL,NULL),
    ('13','Quadcore','/usr/local/voliere/postduif/db/changelogs/changelog_000.xml','2015-09-24 15:44:35',4,'EXECUTED','7:10f5377bc66735f4ed91bbca945f179f','addUniqueConstraint, createIndex','Add all special unique keys (with 2 or more columns in one key) to the database',NULL,'3.4.0',NULL,NULL),
    ('14','Quadcore','/usr/local/voliere/postduif/db/changelogs/changelog_000.xml','2015-09-24 15:44:36',5,'EXECUTED','7:9c616e7ff63f34d76ff5592a87dcdcf9','addForeignKeyConstraint (x65)','Add all foreign keys to the database',NULL,'3.4.0',NULL,NULL);
    ```
9. Now you are ready for release



# Beanmapper

Beanmapper is a Java library for mapping dissimilar Java classes with similar names. The use
cases for Beanmapper are the following:
* mapping from forms to entities, because:
  * for security reasons you want to accept only a limited number of fields as input
  * the form fields are simplified to support frontend processing
* mapping from entities to results, because:
  * you want to simplify the result for frontend processing
  * you want to expose a limited number of fields for security reasons

## Maven dependency

In order to use Beanmapper in your project, simply add the following Maven dependency:

```xml
<dependency>
    <groupId>io.beanmapper</groupId>
    <artifactId>beanmapper</artifactId>
    <version>0.2.15</version>
</dependency>
```

## Getting started

You want to map two dissimilar classes with no hierarchical relation (save java.lang.Object), 
but with a fairly similar naming schema for the fields.

![Basic use case for Beanmapper](docs/images/01-basic-use-case.png)

```java
public class SourceClass {
   public Long id;
   public String name;
   public LocalDate date;
}
```

```java
public class TargetClass {
   public String name;
   public LocalDate date;
}
```

```java
BeanMapper beanMapper = new BeanMapper();
SourceClass source = new SourceClass();
source.id = 42L;
source.name = "Henk";
source.date = LocalDate.of(2015, 4, 1));
TargetClass target = beanMapper.map(source, TargetClass.class);
```

## What's more?

The library can help you with the following situations:
* nested dissimilar classes
* ignoring parts
* mapping to fields with other names, even if nested
* settings defaults if no value is found
* unwrapping class layers in order to flatten the structure
* works directly on the bean, no getters/setters required
* supports a combination of automated and manual processing
* adding conversion modules for data types

## What Beanmapper is not for

Beanmapper is *not* a library for deep-copying classes. Whenever Beanmapper can get away with a shallow
copy, it will do so. Deep-copying is reserved for dissimilar classes.

## Want to know more?

Find the rest of the documentation on [beanmapper.io](http://beanmapper.io).
