package jana.java;

import java.io.File;
import java.io.IOException;

public abstract class JJavaIntermediateRepresentationCompiler
{
	public abstract void initializeCompiler(JJavaClasspath aClasspath, File compilationOutputDirectory, JJavaDebugInformation javaDebugInformation) throws IOException;
	public abstract void compileIntermediateRepresentation(String aClassName);
}
