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

public class AlphaFourTest extends AlphaThreeTest
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
		
		cls = Repository.lookupClass("example.jana.classes.SuperClassExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.InnerInterfaceTest");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));
		
		cls = Repository.lookupClass("example.jana.classes.InheritanceExample");
		Repository.clearCache();
		jjbc.add(JJavaBcelClassifier.produce(cls,jjbr));	
		
		return jjbc;
	}
	
	@Test
	public void unitTest() throws Exception
	{
		testAnalysis();
		
		// ToDo: check values!
		Assert.assertEquals(11, JJavaSignature.numSignatures());
		Assert.assertEquals(11, JJavaSootType.numTypes());
	}
	
	public static void main(String[] args)
	{
		AlphaFourTest att = new AlphaFourTest();
		att.runTest(args);	
	}
}
