package jana.metamodel;

import jana.metamodel.typesystem.JType;

/**
 * a variable declaration reserves a name to which a value can be bound
 */
public abstract class JVariableDeclaration extends JNamedElement 
{
	protected JType type;
}
