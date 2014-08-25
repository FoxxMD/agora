GT Gamefest, The Platform!
======

Prerequisites
------

You have have these installed and configured before running the app:

* [Java JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Scala](http://www.scala-lang.org/)
* [SBT](http://www.scala-sbt.org/)
* [MySQL Server](http://dev.mysql.com/downloads/mysql/)
* [NodeJS Server](http://nodejs.org/download/)
    * [npm](https://www.npmjs.org/)

Configuration
------

### MySQL

1. Create a new database schema using MySQL Workbench (installed when you install MySQL server).
2. In `src/main/resources/jdbc.mysql.properties` find the Database Configuration section and replace values as needed

```
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost/[your_database_name]
username=[database_user]
password=[database_password]
```

3 . Open `create.sql` located at the directory root in MySQL Workbench and execute it against your database.

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

First Run
------

1. Open a terminal in the directory root
2. Install Grunt for the command line -- `npm install grunt-cli -g` (use `sudo` if on *nix)
3. Install Bower -- `npm install bower -g` (use `sudo` if on *nix)
2. Run `npm install` to download the components needed by npm for the app.
3. Run `bower install` to download the components needed to serve the front-end.

How To Run Server and Debug
------

1. Open a terminal in the directory root
  * Running *nix run `./sbt`
  * Running windows you need to start SBT from the location you installed it earlier. If you used the default directory this should work `java -Dsbt.log.noformat=true -Djline.terminal=jline.UnsupportedTerminal -Xmx512M -XX:MaxPermSize=256M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar "C:\Program Files (x86)\sbt\bin\sbt-launch.jar" `
2. Run `container:start` to start the Jetty container -- this is the back-end server. Once this is started you can access the API service.
3. Run `grunt server:dev` to start the front-end server. This serves up the client-side app and proxies to the API service.

To stop Jetty use `container:stop` or kill the SBT process. PROTIP: You can simple stop/start the jetty container when you want to recompile.

