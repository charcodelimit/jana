package jana.lang.java.bcel;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.zip.CRC32;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

import jana.java.JJavaImaginaryProject;
import jana.java.JJavaRepository;
import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.JRoutineDeclaration;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

/*
 * Enums are can not contain nested classes or enums. (chr: well this is possible as in Java 1.6)
 * Enums may only declare fields.
 * 
 * The <init>() and <clinit>() methods
 * <init>() is required to initialize the enum object's name. 
 * The name is initialized with a call to the constructor of 
 * class java.lang.Enumeration, of which each enum type is an instance.
 * Also the methods values() and valueOf() are implemented by each Enum object.
 */
@RunWith(Enclosed.class)
public class JJavaBcelEnum extends JJavaBcelClassifier
{
	protected JJavaBcelEnum(JJavaSignature theSignature, JavaClass aJavaClass, JJavaBcelRepository aJJavaBcelRepository) throws Exception
	{
		super(theSignature, aJavaClass, aJJavaBcelRepository);
		
		this.classifierType = "java-enum-declaration";
	}

	protected void addMethodDeclarations(Method[] methods) throws Exception
	{
		this.routines = new ArrayList<JRoutineDeclaration>(methods.length);
		
		for(int i=0; i < methods.length; i++)
		{
			// if a method is native, abstract, or the method implementations should not be analyzed,
			// then produce a corresponding method declaration object
			if(methods[i].isNative() || methods[i].isAbstract() || (! this.repository.shouldAnalyzeMethodImplementations()))
			{
				this.routines.add(new JJavaBcelMethodDeclaration(methods[i], this));
			}
			else
			{
				this.routines.add(new JJavaBcelMethodImplementation(methods[i], this, i));
			}
		}
	}
		
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(JavaClass aJavaClass)
	{
		return aJavaClass.isEnum();
	}
	
		
	public static class JJavaBcelEnumTest
	{
		@Test
		public void enumTest() throws Exception
		{
			JavaClass cls;
			JJavaImaginaryProject jip;
			JJavaBcelClassifier jjbc;
			
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);

			cls = Repository.lookupClass(example.jana.classes.Day.class.getName());
			Repository.clearCache();
		
			jip = new JJavaImaginaryProject("");
			jjbc = JJavaBcelClassifier.produce(cls, new JJavaBcelRepository(jip));
			Assert.assertEquals(10, JJavaSignature.numSignatures());
			Assert.assertEquals(9, JJavaSootType.numTypes());
			
			StringReader sr = new StringReader(jjbc.toSExpression());
			int v;
			CRC32 crc = new CRC32();
			
			do
			{
				v = sr.read();
				crc.update(v);
			}
			while(v!= -1);
			
			Assert.assertEquals(1811549506L, crc.getValue());
			
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).debug(jjbc.toSExpression());
		}
	}
}
