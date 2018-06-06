package jana.lang.java.soot.jimple;

import jana.java.JJavaRepository;
import jana.lang.java.JJavaClosure;
import jana.lang.java.JJavaMethodImplementation;
import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.local.JJavaSootJimpleBranchInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.local.JJavaSootJimpleLocalControlTransferInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.local.JJavaSootJimpleTrap;
import jana.lang.java.soot.jimple.instructions.controltransfer.local.cond.JJavaSootJimpleSwitchStmt;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.lang.java.typesystem.JJavaReferenceType;
import jana.lang.java.typesystem.JJavaType;
import jana.metamodel.JInstruction;
import jana.metamodel.JNamedElement;
import jana.metamodel.JVariableDeclaration;
import jana.metamodel.typesystem.JType;
import jana.util.logging.JLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Level;

import soot.Local;
import soot.SootClass;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.jimple.JimpleBody;
import soot.util.Chain;

public class JJavaSootJimpleClosure extends JJavaClosure
{	
	// enables logging of JIMPLE instructions for debug purposes 
	private static final boolean LOG_ALL_INSTRUCTIONS = false;
	
	private static JLogger logger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER);
	protected SortedSet<Integer> branchTargetIndices;
	protected SortedMap<Integer,String> branchTargetLabelMap;
	
	/**
	 * chr: more efficient version -- uses previous analysis results
	 * 
	 * @param aJimpleBody
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public JJavaSootJimpleClosure(JimpleBody aJimpleBody, JJavaMethodImplementation aMethodImplementation) throws Exception
	{
		Chain<Unit> unitChain = aJimpleBody.getUnits();
		
		this.branchTargetIndices = new TreeSet<Integer>();
		this.branchTargetLabelMap = new TreeMap<Integer,String>();
		
		// chr: look for signs of trouble
		assert(aMethodImplementation.getReturnType() instanceof JJavaType);
		assert(aMethodImplementation.getParameterTypes() != null);
		assert(aMethodImplementation.getThrownExceptions() != null);
		
		this.returnType = aMethodImplementation.getReturnType();
		this.parameterTypes = aMethodImplementation.getParameterTypes();
		this.thrownExceptions = aMethodImplementation.getThrownExceptions();
		
		this.initLocalVariables(aJimpleBody.getLocals());
		
		// No log level has been set, set to INFO
		if(logger.getLevel() == null) 
		{
			logger.setLevel(Level.INFO);
			logger.warn("No Log-Level has been set! Setting to Level.INFO.");
		}
		
		this.produceInstructions(unitChain);
		this.produceTrapInstructions(aJimpleBody.getTraps(), unitChain);
		this.convertInstructions();
	}
	
	/**
	 * chr: slow version -- uses the information provided by Soot to determine return and parameter types
	 * 	                    this information should have been computed already by BCEL
	 * 
	 * @param aJimpleBody
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public JJavaSootJimpleClosure(JimpleBody aJimpleBody) throws Exception
	{
		Chain<Unit> unitChain = aJimpleBody.getUnits();
	
		this.branchTargetIndices = new TreeSet<Integer>();
	
		// chr: infer return and parameter types from Soot's analysis
		this.returnType = JJavaSootType.produce(aJimpleBody.getMethod().getReturnType());
		this.initParameterTypes(aJimpleBody.getMethod().getParameterTypes());
		this.initExceptionTypes(aJimpleBody.getMethod().getExceptions());
		
		this.initLocalVariables(aJimpleBody.getLocals());

		// No log level has been set, set to INFO
		if(logger.getLevel() == null) 
		{
			logger.setLevel(Level.INFO);
			logger.warn("No Log-Level has been set! Setting to Level.INFO.");
		}
		
		this.produceInstructions(unitChain);
		this.produceTrapInstructions(aJimpleBody.getTraps(), unitChain);
	}
	
	protected void initParameterTypes(List<Type> aParameterTypeList) throws Exception
	{
		Type currentParameterType;
		
		this.parameterTypes = new ArrayList<JType>(aParameterTypeList.size());
		
		for( Iterator<Type> iter = aParameterTypeList.iterator(); iter.hasNext(); )
		{	
			currentParameterType = (Type) iter.next();
			
			this.parameterTypes.add(JJavaSootType.produce(currentParameterType));
		}
	}
	
	protected void initExceptionTypes(List<SootClass> anExceptionTypeList) throws Exception
	{
		Type currentExceptionType;
		SootClass currentExceptionClass;
		
		
		this.thrownExceptions = new ArrayList<JJavaReferenceType>(anExceptionTypeList.size());
		
		for( Iterator<SootClass> iter = anExceptionTypeList.iterator(); iter.hasNext(); )
		{
			currentExceptionClass = (SootClass) iter.next();
			currentExceptionType = (Type) currentExceptionClass.getType();
			this.thrownExceptions.add((JJavaReferenceType) JJavaSootType.produce(currentExceptionType));
		}
	}
	
	/**
	 * Initializes the list of local variable declarations
	 */
	protected void initLocalVariables(Chain<Local> aChain) throws Exception
	{
		this.localVariables = new ArrayList<JVariableDeclaration>(aChain.size());
		
		for(Local currentLocal : aChain)
			this.localVariables.add(new JJavaSootJimpleLocalVariableDeclaration(currentLocal));
	}
	
	/** 
	 * Produces Jimple instructions, by converting aChain of Jimple instructions 
	 * into a list of meta-model conform Jimple instructions.
	 * Initializes the fields this.instructions and this.branchTargetIndices
	 * @param aChain
	 */
	protected void produceInstructions(Chain<Unit> aChain) throws Exception
	{
		JJavaSootJimpleInstruction currentInstruction;
		JJavaSootJimpleBranchInstruction currentBranchInstruction;
		JJavaSootJimpleSwitchStmt currentSwitchInstruction;
		
		this.instructions = new ArrayList<JInstruction>(aChain.size());
		
		// collect branch target indices
		for( Unit currentUnit : aChain )
		{
			currentInstruction = JJavaSootJimpleInstruction.produce(currentUnit, aChain);
			this.instructions.add(currentInstruction);
			
			// Avoid SExpression conversion, as the number of instructions is usually quite high
			if(LOG_ALL_INSTRUCTIONS) // allow compile time optimizations
			{
				if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance too
					logger.debug(currentInstruction.toSExpression()); // you really wanted it, so here it is
			}
			
			if(currentInstruction instanceof JJavaSootJimpleBranchInstruction)
			{
				currentBranchInstruction = (JJavaSootJimpleBranchInstruction) currentInstruction;
				this.branchTargetIndices.addAll(currentBranchInstruction.getBranchTargetIndices());
				
				if(currentBranchInstruction instanceof JJavaSootJimpleSwitchStmt)
				{
					currentSwitchInstruction = (JJavaSootJimpleSwitchStmt) currentBranchInstruction;
					this.branchTargetIndices.add(currentSwitchInstruction.getDefaultTargetIndex());
				}
			}	
		}
	}
	
	/**
	 * Converts aChain of Jimple trap instructions into a list of meta-model conform Jimple instructions
	 * @param aTrapChain   the chain with the traps for this method
	 * @param aUnitChain   a chain of instructions
	 */
	protected void produceTrapInstructions(Chain<Trap> aTrapChain, Chain<Unit> aUnitChain) throws Exception
	{
		JJavaSootJimpleTrap convertedTrap;
		
		for( Trap currentTrap : aTrapChain )
		{
			convertedTrap = new JJavaSootJimpleTrap(currentTrap, aUnitChain);
			this.instructions.add(convertedTrap);
			
			if(Level.DEBUG.isGreaterOrEqual(logger.getLevel()))
				logger.debug(convertedTrap.toSExpression());
			
			this.branchTargetIndices.add(convertedTrap.getStartIndex());
			this.branchTargetIndices.add(convertedTrap.getEndIndex());
			this.branchTargetIndices.add(convertedTrap.getHandlerIndex());
		}
	}
	
	/**
	 * Converts branch-target indices into branch-target labels, 
	 * and adds branch-target labels to Jimple control-transfer instructions. 
	 */
	protected void convertInstructions() throws Exception
	{	
		int pos;
		String labelString;
		
		pos = 0;
		
		// create labels
		for( Integer branchTargetIndex : this.branchTargetIndices )
		{
			labelString = "label" + pos;

			this.branchTargetLabelMap.put(branchTargetIndex,labelString);
			
			pos++;
		}
	
		// free memory
		this.branchTargetIndices = null;
		
		JJavaSootJimpleLocalControlTransferInstruction currentLocalControlTransferInstruction;
		
		// update instructions
		for( JInstruction currentInstruction: this.instructions )
		{
			if( currentInstruction instanceof JJavaSootJimpleLocalControlTransferInstruction)
			{
				currentLocalControlTransferInstruction = (JJavaSootJimpleLocalControlTransferInstruction) currentInstruction;
				currentLocalControlTransferInstruction.initTargetLabels(this.branchTargetLabelMap);
			}
		}
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}

	/**
	 * (jimple-closure return-type parameter-types thrown-exceptions local-variables branch-target-label-map instructions)
	 * 
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("jimple-closure");
		
		aStringBuffer.append("\n ;; Return Type\n");
		aStringBuffer.append(" (");
		this.returnType.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		aStringBuffer.append("\n ;; Parameter Types\n");
		JNamedElement.elementListToSExpression( this.parameterTypes, aStringBuffer );
		
		aStringBuffer.append("\n ;; Thrown Exceptions\n");
		JNamedElement.elementListToSExpression( this.thrownExceptions, aStringBuffer );
		
		aStringBuffer.append("\n ;; Local Variables\n");
		JNamedElement.elementListToSExpression( this.localVariables, aStringBuffer );
		
		aStringBuffer.append("\n ;; Branch Targets\n");
		if( this.branchTargetLabelMap.size() == 0)
			aStringBuffer.append(" ()");
		else
		{
			aStringBuffer.append(" (list");
			for( Integer targetIndex : this.branchTargetLabelMap.keySet() )
			{	
				aStringBuffer.append(" '(");
				aStringBuffer.append('\"');
				aStringBuffer.append(this.branchTargetLabelMap.get(targetIndex));
				aStringBuffer.append('\"');
				aStringBuffer.append(" . ");
				aStringBuffer.append(targetIndex.toString());
				aStringBuffer.append(")");
			}
			aStringBuffer.append(")");
		}
		
		aStringBuffer.append("\n ;; Instructions\n");
		JNamedElement.elementListToSExpression( this.instructions, aStringBuffer );		
	}
}
