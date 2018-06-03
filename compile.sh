#!/bin/sh

if [ $# -ge 2 ]
then
	echo "Compiling files in Project '$1' in Repository '$2'"
else
	echo "Usage: analyze.sh <project-name> <project-repository-pathname>" 
        exit
fi      


sh fee.sh -c -r $2 -cli -p $1 $3 $4 $5 $6 $7 $8 $9
