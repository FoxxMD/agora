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

sudo apt-get install nodejs
sudo apt-get install npm
# Symlink node
sudo ln -s /usr/bin/nodejs /usr/bin/node

# Install dev dependencies
sudo npm install -g bower
sudo npm install -g grunt-cli
sudo npm install -g grunt
sudo npm install -g npm-install-missing --save
sudo npm install
# Make sure we get all dependencies, node ain't great about that
npm-install-missing
bower install

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
