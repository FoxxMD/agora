#!/bin/sh

# This script will install all the prerequities for running the site.
# Hopefully...

sudo apt-get install default-jre
sudo apt-get install default-jdk

#Install Scala
sudo apt-get remove scala-library scala
wget http://www.scala-lang.org/files/archive/scala-2.11.1.deb
sudo dpkg -i scala-2.11.1.deb
sudo apt-get update
sudo apt-get install scala

#Install SBT
wget http://dl.bintray.com/sbt/debian/sbt-0.13.5.deb
sudo dpkg -i sbt-0.13.5.deb
sudo apt-get update
sudo apt-get install sbt

# Install MySQL
echo "Installing MySQL..."
sudo apt-get install mysql-server

# Configure and seed MySQL
echo "Attempting to configure and seed MySQL"
echo "Setting up database and tables"
mysql --user root -p gtgamfest_scal < create.sql
echo "Seeding databases"
mysql --user root -p gtgamfest_scal < seed.sql

echo "Success!"
