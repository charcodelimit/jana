package jana.metamodel;

import jana.metamodel.typesystem.JType;

import java.util.List;

/**
 * "A syntactic closure. Unlike in pure lambda calculus,
 * 	return type and local variables are made explicit."
 * @author chr
 *
 */
public abstract class JClosure 
{
	protected JType returnType;
	protected List<JType> parameterTypes;
	protected List<JVariableDeclaration> localVariables;
	protected List<JInstruction> instructions;
	
	public abstract String toSExpression();
	public abstract void toSExpression(StringBuffer aStringBuffer);
}
