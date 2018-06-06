package jana.lang.java.soot.jimple.values.references;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.JNamedElement;
import jana.metamodel.typesystem.JType;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;

/**
 * TODO Pull up into a separate class in the Java-metamodel
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleMethodRef extends JJavaSootJimpleReferenceValue
{
	private List<JType> argumentTypes;
	
	public JJavaSootJimpleMethodRef(Value aValue) throws Exception
	{
		this((SootMethodRef) aValue);
	}
	
	@SuppressWarnings("unchecked")
	public JJavaSootJimpleMethodRef(SootMethodRef aMethodReference) throws Exception
	{
		super(aMethodReference.returnType());
		
		this.valueType = "jimple-reference-value-method";
		this.value = aMethodReference.name();
		
		initArgumentTypes(aMethodReference.parameterTypes());
	}

	private void initArgumentTypes(List<Type> aList) throws Exception
	{
		Type argumentType;
		
		if(aList == null)
			this.argumentTypes = new ArrayList<JType>();
		else
			this.argumentTypes = new ArrayList<JType>(aList.size());
		
		for(Iterator<Type> iter = aList.iterator(); iter.hasNext(); )
		{
			argumentType = (Type) iter.next();
			this.argumentTypes.add(JJavaSootType.produce(argumentType));
		}
	}
	
	public List<JType> getArgumentTypes()
	{
		return this.argumentTypes;
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-reference-value-method method-name return-type argument-types)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.valueType);
		
		aStringBuffer.append(" \"");
		aStringBuffer.append( this.value );
		aStringBuffer.append("\"");
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		JNamedElement.elementListToSExpression( this.argumentTypes, aStringBuffer );
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof SootMethodRef);
	}
}
