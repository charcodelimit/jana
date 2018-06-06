package jana.metamodel;

import java.util.List;

/**
 * "A concrete module. Examples are: namespaces, packages, etc."
 * a module declares types 
 * (which might be a virtual type that corresponds to a compilation unit)
 * it might also contain start instructions, like a call to a main method.
 */
public abstract class JModule extends JNamedElement implements JAbstractModule
{
	protected List<JReferenceTypeDefinition> typeDefinitions;
}
