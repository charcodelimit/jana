css: style.css

Jana -- Java Analysis Framework
===============================

What is Jana?
-------------

Jana is a framework for analyzing Java programs.
We use this framework for finding bugs due to missing 
synchronization in concurrent Java programs.

Our analysis can detect  if you consistently 
use the `synchronized` statement in your application 
to prevent inconsistent states of your objects.
This allows you to detect faults like data-races that are hard to
detect through e.g. unit-testing with JUnit.

Usually these faults lead to bugs that occur randomly 
when the program is run on a different machine than yours, 
for example, a computer with a multi-core CPU 
at the customer's site.


Jana consists of three parts: 
 * the front-end extension FEE, 
 * the analysis and transformation framework CL-JANA, 
 * and a run-time verification framework called RTV.

What Results can I Expect?
--------------------------

The output of running your instrumented application with the runtime monitor RTV
will look like this example of analyzing the [Hibernate Framework]:

	rtv.lockset.monitor.ConservativeMonitor

	Conflicting access without synchronization through locks
	 of Field: cirrus.hibernate.impl.RelationalDatabaseSession.entries
	Access occured in method: 
	 protected void cirrus.hibernate.impl.RelationalDatabaseSession.finalize()
	 by Thread: Finalizer

	Conflicting access without synchronization through locks
	 of Field: cirrus.hibernate.impl.RelationalDatabaseSession.conn
	Access occured in method: 
	 protected void cirrus.hibernate.impl.RelationalDatabaseSession.finalize()
	 by Thread: Finalizer

	Conflicting access without synchronization through locks
	 of Field: cirrus.hibernate.helpers.IdentityMap$Key.key
	Access occured in method: 
	 public int cirrus.hibernate.helpers.IdentityMap$Key.hashCode()
	 by Thread: Thread-1

It tells you which instance-variables in your application may have an inconsistent state at run-time.
If the correctness of your application depends on the values stored in these fields, it is time
to add synchronization, or check if you synchronized them with other means besides the
`synchronized` statement.

You may also get *no* result as output. 

This actually means, Jana could not find evidence that your program needs additional 
synchronization to work correctly. 
That includes the case, where your application uses only one thread.


Usage
-----

We provide shell scripts to analyze, transform, compile, and 
run the instrumented program.

The analysis results from running the instrumented program, can be found in the file
`analysis-results.txt`.

Usually you may want to run your program with the JUnit Test-Cases that you wrote,
in order to achieve the greatest possible coverage. 
In this case, you have to make sure that you include JUnit into the classpath, when
you add your application to the repository.

{:shell:    lang=sh code_background_color='#efefff'}

### Creating a New Project ### {#new-project}

	$ sh prepare-repository.sh -r <PATH-TO-YOUR-REPOSITORY> -p <PROJECT-NAME> \
	$ --classpath <YOUR-CLASSPATH> --jar <YOUR-APPLICATION>
{:shell}	

The argument `<YOUR-APPLICATION>` is the .jar file containing the .class files of your
application.

### Analyzing Your Project ###

    $ sh fee.sh -a -r <PATH-TO-YOUR-REPOSITORY> -p <PROJECT-NAME>
{:shell}

If you create a repository from scratch, please analyze the java-class libraries first
(takes about 1/2h and saves you lots of time later).
Otherwise you *MUST* copy all the files of the dummy project `java` from the test-repository
provided with Jana to your own repository. 
For example, with the command: 

    $ cp -r test-repository/project-java* <PATH-TO-YOUR-REPOSITORY>
{:shell}
    
### Transforming your Project ###

    $ sh transform-classes.sh <PROJECT-NAME> <PATH-TO-YOUR-REPOSITORY>
{:shell}
    
This step adds the runtime-monitoring framework to your application.
Using the shell-script is probably the easiest way.
You may also take a look at the file `jana-example-usage.lisp`
on how to transform your application from within your lisp REPL.

### Compiling Your Transformed Project ###

    $ sh fee.sh -c -r <PATH-TO-YOUR-REPOSITORY> -p <PROJECT-NAME>
{:shell}
    
You may want to supply the path to the libraries used by your application
with the command-line switch `--classpath`.
If you did this correctly in [step 1](#new-project), 
this should not be necessary.

### Running Your Application ###

    $ sh run-instrumented-app.sh <PROJECT-NAME> <PATH-TO-YOUR-REPOSITORY> \
    $ classname `[optional ARGS for your application]`
{:shell}
    
The transformed classes can be found in a .jar file called `<PROJECT-NAME>`-final.jar in 
the folder where your application is stored in the repository. 
If you want to use this, please don't forget to add `lib/rtv.jar` to your classpath!


Example
-------

To run the example provided with JANA, run the following commands on a POSIX compliant
shell:

    $ sh analyze.sh fee-examples
    $ sh transform-classes.sh fee-examples
    $ sh compile.sh fee-examples 
    $ sh run-instrumented-app.sh fee-examples test-repository \
    $ example.jana.classes.ArrayExample
{:shell}    

Requirements {#requirements}
------------

Jana requires 1 GByte of Memory and a sufficiently recent CPU with 
at least 1GHz if you don't want to wait too long.
Less memory may work too, but it requires editing the shell 
scripts accordingly.

Other Requirements:

    * POSIX Compliant Shell (e.g. Linux, MacOS X, Windows (CygWin))
    * Java JRE >= 1.6
    * CLTL2 Compliant Common-Lisp implementation
    
Jana is known to work with [GNU CLISP], [CCL], [LispWorks].
The default configuration is GNU CLISP. 
Please adjust the file `lisp` in this directory accordingly,
when you use another Common Lisp implementation.
        

Known to Work
-------------

Jana has been successfully tested with the following configuration:
 
    * Sun Java SDK 1.6
    * Linux with Kernel 2.6.22
    * Clozure Common Lisp (CCL) 1.4, and GNU CLISP 2.48
    
[GNU CLISP]: http://clisp.cons.org
[CCL]: http://trac.clozure.com/ccl
[LispWorks]: http://www.lispworks.com
[Hibernate Framework]: http://www.hibernate.org

