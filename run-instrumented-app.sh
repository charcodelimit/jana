#!/bin/sh

if [ $# -ge 2 ]
then
	echo "Running the Instrumented Application of Project '$1' in Repository '$2'"
else
	echo "Usage: run-instrumented-app.sh <project-name> <project-repository-pathname> [java-options]" 
	exit
fi	

java -cp $2/project-$1/$1-final.jar:lib/rtv.jar $3 $4 $5 $6 $7 $8 $9
