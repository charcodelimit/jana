package jana.lang.java;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

/*
 * a Java signature is a uniquely identifiable KEY, 
 * sometimes signature.equals(name), 
 * but signature has to be unambiguous and non-empty,
 * furthermore should a signature stay the same over the whole lifetime of the object
 * 
 * Using a HashMap reduces for the number of created signatures significantly (factor 20)!
 * 
 * As memoizing signature Strings both in JJavaSignature, as well as in JJavaDictionary results 
 * in a redundant state one should be careful that JJavaDictionary and JJavaSignature stay in synch.
 * Thats why JJavaRepository calls JJavaSignature.initialize() and consequently resets <emph>signatures</emph> as well.
 */
@RunWith(Enclosed.class)
public class JJavaSignature implements Serializable 
{
	/**
	 * Generated by Eclipse
	 */
	private static final long serialVersionUID = 8453348046483644577L;

	// chr: transient static ist doppelt gemoppelt, da static fields per-default nicht sarialisiert werden
	protected transient static HashMap<String, JJavaSignature> signatures;

	protected String signature; 
	protected static char PACKAGE_SEPARATOR_CHAR = '.';
	protected static String PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);
	protected static String CLASSFILE_FILENAME_EXTENSION = ".class";
	
	protected JJavaSignature()
	{
	}
	
	/**
	 * Reset the signature cache.
	 */
	public static void initialize()
	{
		if(signatures != null)
			System.out.print("s");
		
		signatures = null;
	}
	
	/*
	 * Signatures have the form ([:alnum:]+ [.])*[:alnum:]+ 
	 */
	private JJavaSignature(String aSignature) throws IOException
	{	
		if(!isValidSignature(aSignature))
			throw new IOException("Invalid Signature " + aSignature);
		
		if(aSignature.indexOf(PACKAGE_SEPARATOR) == -1 )
		{
			this.signature = aSignature;
		}
		else if( aSignature.indexOf(PACKAGE_SEPARATOR) > 0 &&
				 aSignature.lastIndexOf(PACKAGE_SEPARATOR) < aSignature.length() )
		{	
			this.signature = aSignature;
		}
	}
	
	public String unqualifiedName()
	{
		int index;
		
		index = signature.lastIndexOf(".");
		
		if(index == -1)
			return signature;
		else	
			return signature.substring(index+1);
	}
	
	public String qualifiedName()
	{
		return signature;
	}
	
	public String asPackageDirectoryName()
	{
		if(this.signature.contains(PACKAGE_SEPARATOR))
			return this.signature.replace(PACKAGE_SEPARATOR_CHAR, File.separatorChar);
		else
			return this.signature;
	}
	
	public String asClassfileName()
	{
		return this.asPackageDirectoryName() + CLASSFILE_FILENAME_EXTENSION;
	}
	
	public String firstSignatureElement()
	{
		int index;
		
		index = signature.indexOf(PACKAGE_SEPARATOR);
		
		if(index == -1)
			return "";
		else
			return signature.substring(0, index); 
	}
	
	public String previousSignatureElement()
	{
		int index;
		
		index = signature.lastIndexOf(PACKAGE_SEPARATOR);
		
		if(index==-1)
			return "";
		else
			return signature.substring(0,index);
	}
	
	public JJavaSignature append( String aName ) throws IOException
	{
		return new JJavaSignature( this.signature + PACKAGE_SEPARATOR + aName);	
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (java-signature qualified-name)
	 * 
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("java-signature");
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(this.qualifiedName());
		aStringBuffer.append("\"");
	}
	
	/*
	 * a signature is primitive if it is not composed from several signature elements
	 * e.g. "foo"
	 */
	public boolean isPrimitive()
	{
		return !this.signature.contains(PACKAGE_SEPARATOR);
	}
	
	public boolean isPrefixOf(JJavaSignature aSignature) 
	{
		return aSignature.signature.startsWith(this.signature);
	}
	
	public boolean equals(Object otherObject)
	{
		if(otherObject instanceof JJavaSignature)
		{
			JJavaSignature other;
			other = (JJavaSignature) otherObject;
			
			return this.signature.equals(other.signature);
		}
		
		return false;
	}
	
	public int hashCode()
	{
		return this.signature.hashCode();
	}
	
	/**
	 * Use this memoizing constructor to create new instances!
	 * 
	 * @param aQualifiedClassName
	 * @return
	 * @throws IOException
	 */
	public static JJavaSignature signatureFor(String aQualifiedClassName) throws IOException
	{
		JJavaSignature signature;
		
		if( signatures == null )
			signatures = new HashMap<String, JJavaSignature>();
		
		if(!signatures.containsKey(aQualifiedClassName))
		{
			signature = new JJavaSignature(aQualifiedClassName);
			signatures.put(aQualifiedClassName, signature);
		}
		else
		{
			signature = signatures.get(aQualifiedClassName);
		}
		
		return signature;
	}
		
	/**
	 * Used in the test case.
	 * ALWAYS use the memoizing constructor signatureFor !
	 * 
	 * @param aQualifiedName
	 * @return
	 */
	private final static JJavaSignature signatureWithoutMemoization(String aQualifiedName) throws IOException
	{
		return new JJavaSignature(aQualifiedName);
	}

	public static int numSignatures()
	{
		if(signatures!=null)
			return signatures.size();
		else
			return 0;
	}

	public static boolean isValidSignature(String aSignature)
	{
		return aSignature.indexOf(PACKAGE_SEPARATOR) == -1 || 
			   ( aSignature.indexOf(PACKAGE_SEPARATOR) > 0 && aSignature.lastIndexOf(PACKAGE_SEPARATOR) < aSignature.length());
	}

	public static JJavaSignature signatureForBinaryName(String binaryName) throws IOException
	{
		String className;
		int end;
		
		end = binaryName.length();
		
		if(binaryName.charAt(0) == 'L' && binaryName.charAt( end - 1) == ';')
			className = binaryName.substring( 1, end - 1);
		else
			className = binaryName;
				
		className = className.replace(File.separatorChar, PACKAGE_SEPARATOR_CHAR);
	
		return JJavaSignature.signatureFor(className);
	}
	
	public static String asString()
	{
		StringBuffer sb = new StringBuffer();
		
		int count,size;
		
		count = 0;
		size = signatures.keySet().size();
		for( String qualifiedName : signatures.keySet() )
		{
			sb.append(qualifiedName);
			if(count < size - 1)
				sb.append(",");
			count++;
		}
		
		return sb.toString();
	}
	
	public static class JJavaSignatureTest
	{ 		
		@Test
		public void signatureTests() throws IOException
		{
			JJavaSignature s1,s2;
				
			s1 = JJavaSignature.signatureWithoutMemoization("a.b");
			Assert.assertEquals("b", s1.unqualifiedName());
			Assert.assertEquals("a", s1.firstSignatureElement());
			
			s2 = JJavaSignature.signatureWithoutMemoization("a");
			Assert.assertTrue(s2.isPrimitive());
			Assert.assertTrue(s2.isPrefixOf(s1));
			
			s1 = JJavaSignature.signatureWithoutMemoization("example.jana.classes.InnerClassExample$1");
			Assert.assertEquals("InnerClassExample$1", s1.unqualifiedName() );
			Assert.assertEquals("example.jana.classes.InnerClassExample$1", s1.qualifiedName() );
			Assert.assertEquals("example/jana/classes/InnerClassExample$1", s1.asPackageDirectoryName() );
			Assert.assertEquals("example/jana/classes/InnerClassExample$1.class", s1.asClassfileName() );
		}
	}
}