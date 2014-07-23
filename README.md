GT Gamefest, The Platform!
======

Prerequisites
------

You have have these installed and configured before running the app:

* [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Scala](http://www.scala-lang.org/)
* [SBT](http://www.scala-sbt.org/)
* [MySQL Server](http://dev.mysql.com/downloads/mysql/)
* [NodeJS Server](http://nodejs.org/download/)
    * [npm](https://www.npmjs.org/)
    * [bower](http://bower.io/) -- Installed thru npm

Configuration
------

### MySQL

1. Create a new database schema using MySQL Workbench (installed when you install MySQL server).
2. In `PROJECTROOT/conf/application.conf` find the Database Configuration section and replace values as needed

```
db.default.driver=com.mysql.jdbc.Driver
db.default.url="jdbc:mysql://localhost/[your_database_name]"
db.default.user="[database_user]"
db.default.password="[database_password]"
```

3 . Open `create.sql` located at `PROJECTROOT` in MySQL Workbench and execute it against your database.

### PATH Variables

Note: If you are running a *nix environment these do not apply to you.

Add variables for Java, Scala, nodejs, and npm to your PATH environmental variable

1. Right Click My Computer -> Properties -> Advanced System Settings -> Environment Variables
2. In the **System Variables** area find the PATH variable and edit it. Below are the locations of the variables needed **for my system**, they may differ for yours.

```
C:\Program Files\nodejs;
C:\Users\[YourAccount]\AppData\Roaming\npm;
C:\Program Files (x86)\scala\bin;
C:\Program Files\Java\jdk1.8.0_05\bin\java.exe;
C:\Program Files\Java\jdk1.8.0_05\bin;
```

### SecureSocial (Optional)

This configuration is only needed if you plan on registering new users.

1. In `PROJECTROOT/conf/securesocial.conf` find the settings for smtp and replace values as is necessary


First Run
------

1. Open a terminal in your PROJECTROOT and run the command `activator` . This will initialize SBT which will then resolve dependencies for the project.
2. Run `npm install` to download the components needed by npm for the app. This will install `grunt`, a front-end task-runner, and `karma`, a unit testing suite.
3. Run `bower install` to download the components needed to serve the front-end. `bower` is a front-end package manager. It will download bootstrap, angular, etc.

How To Run Server/Develop with Activator
------

There are two options for running the web server, either can be used depending on the style of development you want.

### Activator GUI

Use this method if you do not have an IDE

1. Open a terminal in your PROJECTROOT and run the command `activator ui`  
2. From the window opened in your browser use the *Run* option to start the webserver.
3. Navigate to http://localhost:9000 to compile the app and initiate execution. 

From the Activator GUI you also have the option to inspect, edit, and test code. It's a very basic IDE but it works well.

### Activator CLI

Use this method if you are hooked into an IDE like Sublime or don't want to use the GUI.

1. Open a terminal in your PROJECTROOT and run the command `activator` 
2. Once Activator has finished loading the project execute the command `run` to start the webserver.
3. Navigate to http://localhost:9000 to compile the app and initiate execution.

**Regardless of which method you use the webserver is designed to recompile/refresh on any file changes**. This allows you to develop in whatever style you'd like.