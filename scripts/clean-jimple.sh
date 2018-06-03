#!/bin/sh
if [ $# -ne 2 ]
then
	 echo "Please provide the project-name and repository-directory as argument to the script!"
	 exit
fi	 
REPOSITORY_DIR=`dirname "$2"`
REPOSITORY_DIR=`cd "$REPOSITORY_DIR"; pwd`

echo "Cleaning Transformation Results in:" $REPOSITORY_DIR"/project-"$1

echo -e "Shall I proceed? [y/n]: \c"
read answer
if [ "$answer" != "y" ] && [ "$answer" != "Y" ]
then
	echo "Aborting"
	exit
fi

find $REPOSITORY_DIR/project-$1 -name "*.jimple" | xargs rm -v
find $REPOSITORY_DIR/project-$1 -name "*.dbg" | xargs rm -v
