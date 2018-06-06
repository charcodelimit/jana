package jana.metamodel;

/**
 * "An instruction as defined on a virtual machine that implements the operational semantics of the modeled language."
 * @author chr
 *
 */
public abstract class JInstruction 
{
	// decouples the symbolic names for instruction types from the class names
	protected String instructionType; 
	protected int lineNumber;
	
	public abstract String toSExpression();
}
