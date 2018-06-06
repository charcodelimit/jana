package jana.metamodel;

import jana.metamodel.typesystem.JType;

import java.util.ArrayList;
import java.util.List;

/**
 * A generalization relation.
 * Making the generalization a first class object allows to deal with
 * different kinds of inheritance explicitly in the model
 * (multiple, single, specialization, implementation, ..)
 */
public abstract class JGeneralization 
{
	protected List<JType> superTypes;
	protected List<JType> subTypes;
	
	public JGeneralization()
	{
		this.subTypes = new ArrayList<JType>();
		this.superTypes = new ArrayList<JType>();
	}
		
	public void addSubType(JType aSubType)
	{
		this.subTypes.add(aSubType);
	}
	
	public void addSuperType(JType aSuperType)
	{
		this.superTypes.add(aSuperType);
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public abstract void toSExpression(StringBuffer aStringBuffer);
}
