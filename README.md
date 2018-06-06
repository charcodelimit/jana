# Jana - A Java Analysis Framework

## What is Jana?

Jana is a framework for analyzing Java programs.
This framework can be used for finding bugs due to missing
synchronization in concurrent Java programs.

The analysis can detect if you consistently
use the `synchronized` statement in your application
to prevent inconsistent states of your objects.
This allows you to detect faults like data-races that are hard to
detect through other means, like unit-testing with JUnit.

Usually these faults lead to bugs that occur randomly
when the program is run on a multicore CPU.

Jana consists of three parts:
 * the front-end extension FEE,
 * the static analysis and transformation framework CL-JANA,
 * and a run-time verification framework called RTV.

### What Results can I Expect?

The output of running your instrumented application with the runtime monitor RTV
will look like this example of analyzing the [Hibernate Framework] (0.9.10 Beta):

```
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
```

It tells you which instance-variables in your application may have an inconsistent state at run-time.
If the correctness of your application depends on the values stored in these fields, it is time
to add synchronization, or check if you synchronized them with other means besides the
`synchronized` statement.

You may also get *no* result as output.

This actually means, Jana could not find evidence that your program needs additional
synchronization to work correctly.
That includes the case, where your application uses only one thread.

## Building Jana

Building Jana requires min. a Java 1.6 JDK and the [Ant] build tool.

```shell
$ ant CleanAll
$ ant
```

## Usage

We provide shell scripts to analyze, transform, compile, and
run the instrumented program.

The analysis results from running the instrumented program, can be found in the file
`analysis-results.txt`.

Usually you may want to run your program with the JUnit Test-Cases that you wrote,
in order to achieve the greatest possible coverage.
In this case, you have to make sure that you include JUnit into the classpath, when
you add your application to the repository.

### Creating a New Project

```shell
$ sh prepare-repository.sh -r <PATH-TO-YOUR-REPOSITORY> -p <PROJECT-NAME> \
$ --classpath <YOUR-CLASSPATH> --jar <YOUR-APPLICATION>
```

The argument `<YOUR-APPLICATION>` is the .jar file containing the .class files of your
application.

### Analyzing Your Project

```shell
$ sh fee.sh -a -r <PATH-TO-YOUR-REPOSITORY> -p <PROJECT-NAME>
```

If you create a repository from scratch, please analyze the java-class libraries first
(takes about 1/2h and saves you lots of time later).
Otherwise you *MUST* copy all the files of the dummy project `java` from the test-repository
provided with Jana to your own repository.
For example, with the command:

```shell
$ cp -r test-repository/project-java* <PATH-TO-YOUR-REPOSITORY>
```

### Transforming your Project

```shell
$ sh transform-classes.sh <PROJECT-NAME> <PATH-TO-YOUR-REPOSITORY>
```

This step adds the runtime-monitoring framework to your application.
Using the shell-script is probably the easiest way.
You may also take a look at the file `jana-example-usage.lisp`
on how to transform your application from within your lisp REPL.

### Compiling Your Transformed Project

```shell
$ sh fee.sh -c -r <PATH-TO-YOUR-REPOSITORY> -p <PROJECT-NAME>
```

You may want to supply the path to the libraries used by your application
with the command-line switch `--classpath`.
If you did this correctly in step [Creating a New Project],
this should not be necessary.

### Running Your Application

```shell
$ sh run-instrumented-app.sh <PROJECT-NAME> <PATH-TO-YOUR-REPOSITORY> \
$ classname `[optional ARGS for your application]`
```

The transformed classes can be found in a .jar file called `<PROJECT-NAME>`-final.jar in
the folder where your application is stored in the repository.
If you want to use this, please don't forget to add `lib/rtv.jar` to your classpath!


## Example

To run the example provided with JANA, run the following commands on a POSIX compliant
shell:

```shell
$ sh analyze.sh fee-examples test-repository
$ sh transform-classes.sh fee-examples test-repository
$ sh compile.sh fee-examples test-repository
$ sh run-instrumented-app.sh fee-examples test-repository \
$ example.jana.classes.ArrayExample
```

## Requirements

Jana requires min. 1 GByte of Memory.
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

## Known to Work

Jana has been successfully tested with the following configuration:

    * Sun Java SDK 1.6
    * Linux with Kernel 2.6.22
    * Clozure Common Lisp (CCL) 1.4, and GNU CLISP 2.48

[GNU CLISP]: http://clisp.cons.org
[CCL]: http://trac.clozure.com/ccl
[LispWorks]: http://www.lispworks.com
[Hibernate Framework]: http://www.hibernate.org
[Ant]: http://ant.apache.org
