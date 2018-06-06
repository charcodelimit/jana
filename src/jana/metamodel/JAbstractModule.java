package jana.metamodel;

//import java.util.List;
//import java.util.Collection;

/**
 * With multiple inheritance,this would be an abstract class.
 * an abstract module has parent modules and child modules.
 */
@SuppressWarnings("unchecked")
public interface JAbstractModule 
{
	/**
	 * returns an ordered collection of the parent modules.
	 */
	//public abstract List getParentModules();
	
	/**
	 * returns <i>all</i> child modules of a module.
	 * If the child modules could not be determined for some
	 * reason, an exception is thrown.
	 */
	//public abstract Collection getChildModules() throws Exception;
}
