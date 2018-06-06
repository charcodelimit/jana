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

public class BetaOneTest extends AlphaFiveTest
{
	public List<JJavaBcelClassifier> testAnalysis() throws Exception
	{
		JavaClass cls;
		java.util.List<JJavaBcelClassifier> jjbc;
		JJavaBcelRepository jjbr;
		JJavaProject prj;
		
		prj = new JJavaExistingProject("fee-examples","test-repository");
		
		jjbc = new java.util.ArrayList<JJavaBcelClassifier>();
		jjbr = new JJavaBcelRepository(prj);
		
		cls = Repository.lookupClass("java.lang.Float");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("java.lang.Double");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));	
		
		cls = Repository.lookupClass("java.math.BigDecimal");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("java.math.BigInteger");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		return jjbc;
	}
	
	@Test
	public void unitTest() throws Exception
	{
		testAnalysis();
		
		// ToDo: check values!
		Assert.assertEquals(57, JJavaSignature.numSignatures());
		Assert.assertEquals(56, JJavaSootType.numTypes());
	}
	
	public static void main(String[] args)
	{
		try
		{
			BetaOneTest bot = new BetaOneTest();
			bot.setUp();
			bot.runTest(args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
