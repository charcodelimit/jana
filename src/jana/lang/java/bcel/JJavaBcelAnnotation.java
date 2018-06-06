package jana.lang.java.bcel;
import java.io.IOException;
import java.util.TreeMap;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.ClassElementValue;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.EnumElementValue;
import org.apache.bcel.classfile.SimpleElementValue;

import jana.java.JJavaRepository;
import jana.lang.java.JJavaAnnotation;
import jana.lang.java.JJavaSignature;
import jana.util.JString;
import jana.util.logging.JLogger;

/**
 * Supports only simple annotation types.
 * Full support of all annotation types would require an implementation of
 * the full ElementValue hierarchy. This hierarchy is now collapsed to one node,
 * namely SimpleElementValue, which are represented as String.
 * 
 * @author chr
 *
 */
public class JJavaBcelAnnotation extends JJavaAnnotation
{
	public JJavaBcelAnnotation(AnnotationEntry anAnnotationEntry) throws Exception
	{
		// remove the L prefix that precedes Type names in the JVM constant pool
		super( JJavaSignature.signatureFor(fixBCEL_5_3_Bug(anAnnotationEntry.getAnnotationType())) );
		
		setElementValuePairs(anAnnotationEntry.getElementValuePairs());
	}
	
	private void setElementValuePairs(ElementValuePair[] theElementValuePairs) throws IOException
	{	
		String name, value;
		ElementValue elementValue;
		EnumElementValue enumElementValue;
		JJavaSignature signature;
		
		this.elementValuePairs = new TreeMap<String, String>();
		
		for(int i = 0; i < theElementValuePairs.length; i++)
		{
			name = theElementValuePairs[i].getNameString();
			
			if(name != null)
			{
				elementValue = theElementValuePairs[i].getValue();
			
				if(elementValue instanceof ArrayElementValue)
				{
					JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).warn("Use of unsupported combined annotation type " + this.getSignature().qualifiedName() + " !");
				}
				else 
				{
					value = JString.toQuotedString(theElementValuePairs[i].getValue().stringifyValue());
				
					if(elementValue instanceof SimpleElementValue)
					{
						this.elementValuePairs.put(name, value);
					}
					else if(elementValue instanceof ClassElementValue)
					{
						signature = JJavaSignature.signatureForBinaryName(elementValue.toString());
						this.elementValuePairs.put(name, signature.qualifiedName());
					}
					else if(elementValue instanceof EnumElementValue)
					{
						enumElementValue = (EnumElementValue) elementValue;
						signature = JJavaSignature.signatureForBinaryName(enumElementValue.getEnumTypeString().toString());
						this.elementValuePairs.put(name, signature.qualifiedName() + "#" + enumElementValue.getEnumValueString());
					}
					else
					{
						this.elementValuePairs.put(name, value);
						JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).debug("Type: " + elementValue.getClass().getSimpleName() + " Value: " + value);
					}
				}
			}
		}
			
	}
	
	/**
	 * BCEL 5.3 returns the raw constant pool entry instead of the annotation type name.
	 * This entry has the form L<TypeName>;
	 * 
	 * @param anAnnotationName
	 */
	private static String fixBCEL_5_3_Bug(String anAnnotationTypeName)
	{
		String substring;
		
		substring = anAnnotationTypeName.substring(1, anAnnotationTypeName.length() - 1);
		
		return substring.replace('/', '.');
	}
}
