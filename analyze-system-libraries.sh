#!/bin/sh
java -Xms512M -Xmx1024M -cp lib/fee.jar:lib/bcel-5.3.jar:lib/custom/sootclasses-2.3.0-chr.jar:lib/log4j-1.2.15.jar:lib/commons-cli-1.1.jar:lib/foil.jar fee.AnalyzeJavaSystemLibraries $@
