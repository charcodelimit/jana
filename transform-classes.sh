#!/bin/sh

if [ $# -eq 2 ]
then
	echo "Transforming classes in the Project '$1' in Repository '$2'"
	echo -e "Shall I proceed? [y/n]: \c"
	read answer
	if [ "$answer" != "y" ] && [ "$answer" != "Y" ]
	then
		echo "Aborting"
		exit
	fi
else
	echo "Usage: transform-classes.sh <project-name> <project-repository-pathname>"
	exit
fi	

application="./cl-jada-ccl-linux-x86.bin"

if [ ! -x $application ]; then	
	application="./cl-jada-ccl-win32.exe"
fi

if [ ! -x $application ]; then	
	application="./cl-jada-ccl-osx-ppc.app"
fi     

if [ ! -x $application ]; then	
	application="./cl-jada-ccl-osx-x86.app"
fi

if [ ! -x $application ]; then	
	echo "Can't find cl-jada executable!"
	exit
fi     

exec $application --project-name $1 --repository-directory $2
