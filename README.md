GT Gamefest, The Platform!
======

Prerequisites
------

You must have these installed and configured before running the app:

* [Java JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Scala](http://scala-lang.org/) -- [2.11.1](http://scala-lang.org/download/2.11.1.html)
* [SBT](http://www.scala-sbt.org/) -- [0.13.5](https://dl.bintray.com/sbt/native-packages/sbt/0.13.5/)
* [MySQL Server](http://dev.mysql.com/downloads/mysql/)

**Note: If you are running ubuntu or a flavor of nix with `apt-get` you can try running `install.sh` to install and configure all prereqs and MySQL as well as skip "First Run"**

Configuration
------

### MySQL

1. Run `create.sql` located at the directory root and execute it against your database.
2. Run `seed.sql` also at the directory root to hydrate your new db.

### PATH Variables

Note: If you are running a nix environment these do not apply to you.

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

How To Run Server and Debug
------

1. Open a terminal in the directory root
  * Running *nix run `./sbt`
  * Running windows you need to start SBT from the location you installed it earlier. If you used the default directory this should work `java -Dsbt.log.noformat=true -Djline.terminal=jline.UnsupportedTerminal -Xmx512M -XX:MaxPermSize=256M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar "C:\Program Files (x86)\sbt\bin\sbt-launch.jar" `
2. Run `container:start` to start the Jetty container -- this is the back-end server. Once this is started you can access the API service.

To stop Jetty use `container:stop` or kill the SBT process. PROTIP: You can simple stop/start the jetty container when you want to recompile.


How To create app for Production
------

1. To package the back-end run start sbt and run `assembly`, this will create a fat JAR executable in your `/target` folder.


