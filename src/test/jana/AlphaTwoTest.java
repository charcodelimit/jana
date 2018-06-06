package test.jana;

import java.util.List;

import jana.java.JJavaExistingProject;
import jana.java.JJavaProject;
import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.JJavaBcelClassifier;
import jana.lang.java.soot.typesystem.JJavaSootType;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.junit.Assert;
import org.junit.Test;

/***
 * Check JJavaSignature! if this test is run in a batch of tests JJavaSignature.numSignatures() will return 52.
 * If run alone the value is 53!
 * 
 * @author chr
 *
 */
public class AlphaTwoTest extends JTest
{
	public List<JJavaBcelClassifier> testAnalysis() throws Exception
	{
		JavaClass cls;
		JJavaBcelRepository jjbr;
		java.util.List<JJavaBcelClassifier> jjbc;
		JJavaProject prj;
		
		prj = new JJavaExistingProject("fee-examples","test-repository");
		
		jjbc = new java.util.ArrayList<JJavaBcelClassifier>();
		jjbr = new JJavaBcelRepository(prj);
		
		cls = Repository.lookupClass("example.jana.classes.ArithmeticExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.ArrayExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.ConstantTypes");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.Day");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.ExceptionExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.IfExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.InnerClassExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.ObjectExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.SwitchStatementExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.SynchronizationExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.TestInterface");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		return jjbc;
	}
	
	@Test
	public void unitTest() throws Exception
	{
		testAnalysis();
		
		// ToDo: check values!
		// System.out.println(JJavaSignature.asString());
		Assert.assertEquals(43, JJavaSignature.numSignatures());
		Assert.assertEquals(50, JJavaSootType.numTypes());
	}
	
	public static void main(String[] args)
	{
		try
		{
			AlphaTwoTest att = new AlphaTwoTest();
			att.setUp();
			att.runTest(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
