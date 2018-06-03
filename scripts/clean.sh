#!/bin/bash
if [ $# -ne 2 ]
then
	echo "Please provide the project-name and repository-directory as argument to the script!"
        exit
fi

PROGRAM_DIR=`dirname "$0"`
PROGRAM_DIR=`cd "$PROGRAM_DIR"; pwd`

echo "Cleaning Files in:" $2"/project-"$1

sh $PROGRAM_DIR/clean-bin.sh $1 $2
sh $PROGRAM_DIR/clean-jimple.sh $1 $2
