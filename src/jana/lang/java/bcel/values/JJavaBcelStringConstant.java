package jana.lang.java.bcel.values;

import java.io.IOException;

import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.typesystem.JJavaBcelObjectType;
import jana.metamodel.values.JValue;
import jana.util.JString;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantValue;

public class JJavaBcelStringConstant extends JJavaBcelConstantValue 
{
	protected String constantValue; // INV constantValue != null
	protected final String typeString = "java.lang.String";
	
	public JJavaBcelStringConstant(ConstantValue constantValue) throws IOException
	{
		super(constantValue);
		
		this.type = new JJavaBcelObjectType(JJavaSignature.signatureFor(this.typeString));
	}
	
	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ConstantString;
	}
	
	protected void initialize(Constant aConstant, ConstantPool theConstantPool) 
	{
		ConstantString cs;
		
		cs = (ConstantString) aConstant;
		this.constantValue = JString.toQuotedString(cs.getBytes(theConstantPool));
	}

	@Override
	protected boolean compareValues(JValue aValue) 
	{
		if(aValue instanceof JJavaBcelStringConstant) 
		{
			JJavaBcelStringConstant other = (JJavaBcelStringConstant) aValue;
			
			return this.getValue().equals(other.getValue());
		}
		
		return false;
	}

	public String getValue()
	{
		return constantValue;
	}
	
	@Override
	public int hashCode() 
	{
		return constantValue.hashCode();
	}
	
	public String toString()
	{
		return getValue().toString();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}

	public void toSExpression(StringBuffer aStringBuffer) 
	{
		aStringBuffer.append("java-constant-string-value");
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(getValue());
		aStringBuffer.append("\"");
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}	
}
