package jana.lang.java;

import java.io.IOException;

/**
 * Type names don't use the JJavaDictionary, as they are represented by symbols in cl-jana
 * Don't memoize the basic types as they are initialized only once statically!
 * 
 * @author chr
 *
 */
public class JJavaBasicTypeSignature extends JJavaSignature
{
	private static final long serialVersionUID = 4233009099336521867L;

	protected JJavaBasicTypeSignature(String aSignature) throws IOException
	{
		if(aSignature.indexOf(PACKAGE_SEPARATOR) == -1 )
		{
			this.signature = aSignature;
		}
		else if( aSignature.indexOf(PACKAGE_SEPARATOR) > 0 &&
				 aSignature.lastIndexOf(PACKAGE_SEPARATOR) < aSignature.length() )
		{	
			this.signature = aSignature;
		}
		else
		{
			throw new IOException("Invalid Signature " + aSignature);
		}
	}
	
	public static JJavaSignature signatureFor(String aSignature) throws IOException
	{
		JJavaSignature signature;
		signature = new JJavaBasicTypeSignature(aSignature);
		
		return signature;
	}

}
