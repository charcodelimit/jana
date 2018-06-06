package jana.metamodel;

import jana.metamodel.typesystem.JType;

import java.util.List;

/**
 * "A declaration of name, argument-types and return-type of a routine."
 */
public abstract class JRoutineDeclaration extends JNamedElement
{
	protected List<JType> parameterTypes;
	protected JType returnType;
	
	public String getName()
	{
		return name;
	}
}
