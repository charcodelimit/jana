package jana.metamodel;


/**
 * a routine is a declaration with a body that describes the closure 
 * that binds the values of the declared argument types to names. 
 */
public abstract class JRoutineImplementation extends JRoutineDeclaration 
{
	protected JClosure body;
}
