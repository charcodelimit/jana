package jana.lang.java.bcel;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import org.apache.bcel.Constants;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.SourceFile;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

import jana.java.JJavaImaginaryProject;
import jana.java.JJavaProject;
import jana.java.JJavaRepository;
import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaAnnotation;
import jana.lang.java.JJavaClassifier;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.typesystem.JJavaBcelObjectType;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.JRoutineDeclaration;
import jana.metamodel.JVariableDeclaration;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

/**
 * a classifier can be a class, an interface or an enum
 * 
 * The analysis of inner classes is one of the performance bottlenecks when a class is analyzed.
 * 
 * @author chr
 *
 */
@RunWith(Enclosed.class)
public abstract class JJavaBcelClassifier extends JJavaClassifier implements Comparable<JJavaClassifier>
{	
	protected JJavaSignature aspectAnnotationSignature;
	
	protected JJavaBcelClassifier(JJavaSignature theSignature, JavaClass aJavaClass, JJavaBcelRepository aJJavaBcelRepository) throws Exception
	{
		super(theSignature);
		
		AnnotationEntry[] annotationEntries;
		JavaClass[] superClasses, interfaces; 
		Attribute[] attributes;
		Field[] fields;
		Method[] methods;
		
		this.repository = aJJavaBcelRepository;
		
		this.initParentPackage();
		
		annotationEntries = aJavaClass.getAnnotationEntries();
		this.addAnnotations(annotationEntries);
		annotationEntries = null;
		
		this.modifiers = new JJavaBcelClassModifiers( aJavaClass );
		
		superClasses = aJavaClass.getSuperClasses();
		interfaces = aJavaClass.getInterfaces();
		this.initGeneralization(superClasses, interfaces);
		superClasses = null;
		interfaces = null;
		
		attributes = aJavaClass.getAttributes();
		this.addInnerClasses(attributes);
		// Fast but probably less safe against changes in BCEL
		this.sourceFile = aJavaClass.getSourceFileName();
		//this.addSourceFileAttribute(attributes);
		attributes = null;
		
		fields = aJavaClass.getFields();
		this.addFields(fields);
		fields = null;
		
		methods = aJavaClass.getMethods();
		this.addMethods(methods);
		methods = null;
		
		this.discardClosureFactory();
	}

	/*
	 * find inner classes among the attributes
	 */	
	private void addInnerClasses(final Attribute[] classAttributes) throws Exception
	{	
		Attribute attrib;
		
		for(int i = 0; i < classAttributes.length; i++)
		{	
			attrib = classAttributes[i];
			
			if( attrib instanceof InnerClasses)
				addInnerClasses((InnerClasses) attrib);
		}
	}

	private void addInnerClasses(final InnerClasses innerClasses) throws ClassNotFoundException, IOException
	{	
		ConstantPool cp;
		InnerClass[] innerClassInstances;
		String innerClassBinaryName;
		JJavaSignature innerClassSignature;
	
		cp = innerClasses.getConstantPool();
		innerClassInstances = innerClasses.getInnerClasses();
	
		for( int i = 0; i < innerClassInstances.length; i++)
		{
			innerClassBinaryName = cp.getConstantString(innerClassInstances[i].getInnerClassIndex(), Constants.CONSTANT_Class);
			innerClassSignature = JJavaSignature.signatureForBinaryName(innerClassBinaryName);
		
			if(this.signature.isPrefixOf(innerClassSignature)
					&& !this.signature.equals(innerClassSignature))
			{
				this.nestedClassifiers.add( loadInnerClass(innerClassSignature) );
			}
		}
	}

	/**
	 * Attention! Inner classes are analyzed using the repository, even if the analysis should run on a FeeStarDustServer.
	 * 
	 * @param innerClassSignature
	 * @return
	 * @throws ClassNotFoundException
	 */
	private JJavaClassifier loadInnerClass(JJavaSignature innerClassSignature) throws ClassNotFoundException
	{
		JJavaClassifier innerClass;
		JJavaBcelRepository repository;
		
		repository = (JJavaBcelRepository) this.repository;
		
		try
		{
			innerClass = repository.analyzeClass(innerClassSignature.qualifiedName());
			innerClass.setParentClassifier(this);
		
			return innerClass;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new ClassNotFoundException("Could not find class: " + innerClassSignature.toString() + " because of " + e.toString(), e);
		}
	}
	
	/** SLOW -- BUT SAFE **/
	@SuppressWarnings("unused")
	private void addSourceFileAttribute(final Attribute[] classAttributes)
	{	
		Attribute attrib;
		
		for(int i = 0; i < classAttributes.length; i++)
		{	
			attrib = classAttributes[i];
			
			if( attrib instanceof SourceFile)
				this.sourceFile = ((SourceFile) attrib).getSourceFileName();
		}
	}
	
	private void addAnnotations(final AnnotationEntry[] annotations) throws Exception
	{
		this.annotations = new ArrayList<JJavaAnnotation>(annotations.length);
		
		for(int i=0; i < annotations.length; i++)
			this.annotations.add(new JJavaBcelAnnotation(annotations[i]));
	}
	
	private void addFields(final Field[] fields) throws Exception
	{
		this.attributes = new ArrayList<JVariableDeclaration>(fields.length);
	
		for(int i=0; i < fields.length; i++)
			this.attributes.add(new JJavaBcelFieldDeclaration(fields[i]));
	}	
	
	/*
 	 * create method implementation objects
 	 */
	protected void addMethodDeclarations(final Method[] methods) throws Exception
	{
		JJavaBcelMethodImplementation implementation;
		
		this.routines = new ArrayList<JRoutineDeclaration>(methods.length);
	
		for(int i=0; i < methods.length; i++)
		{	
			// if a method is native, abstract, no body has been declared, 
			// or the method implementations should not be analyzed,
			// then produce a corresponding method declaration object
			if( methods[i].isNative() || methods[i].isAbstract() || methods[i].getCode() == null || (! this.repository.shouldAnalyzeMethodImplementations()))
			{
				this.routines.add(new JJavaBcelMethodDeclaration(methods[i], this));
			}
			else
			{
				implementation = new JJavaBcelMethodImplementation(methods[i], this, i);
				
				if(implementation.getMethodBody() != null)
					this.routines.add(implementation);
			}
		}
	}
	
	protected void initGeneralization(final JavaClass[] superClasses, final JavaClass[] interfaces) throws Exception
	{	
		JJavaSignature signature;
		JJavaBcelInherits inheritsR = new JJavaBcelInherits();
		JJavaBcelImplements implementsR = new JJavaBcelImplements();
		
		if(superClasses != null)
		{
			for( int i = 0; i < superClasses.length; i++)
			{
				signature = JJavaBcelSignature.signatureFor(superClasses[i]);
				inheritsR.addSuperClass(signature);
				this.repository.addSupertype(signature.qualifiedName());
			}
		}
		
		if(interfaces != null)
		{
			for( int i = 0; i < interfaces.length; i++)
			{
				signature = JJavaBcelSignature.signatureFor(interfaces[i]);
				implementsR.addInterface(signature);
			}
		}
		
		this.generalizationRelation = inheritsR;
		this.implementsRelation = implementsR;
	}
	
	public JJavaBcelObjectType getType()
	{
		return new JJavaBcelObjectType(this.getSignature());
	}
	
	public List<JJavaAnnotation> getAnnotations() {
		return this.annotations;
	}
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 * because JJavaClassifier is an abstract class it returns false.
	 */
	protected static boolean modelsType(JavaClass aJavaClass)
	{
		return false;
	}
	
	protected void addMethods(Method[] methods) throws Exception
	{
		addMethodDeclarations(methods);
	}
	
	/*
	 * instance creation based on the entity represented 
	 * by the JavaClass
	 */
	public static JJavaBcelClassifier produce(JavaClass aJavaClass, JJavaBcelRepository aJJavaBcelRepository) throws Exception
	{	
		JJavaSignature signature;
		
		signature = JJavaBcelSignature.signatureFor(aJavaClass);
		return produce(signature, aJavaClass, aJJavaBcelRepository);
	}

	private static JJavaBcelClassifier produce(JJavaSignature theSignature, JavaClass aJavaClass, JJavaBcelRepository aJJavaBcelRepository) throws Exception
	{
		if( JJavaBcelClass.modelsType(aJavaClass) )
			return new JJavaBcelClass(theSignature, aJavaClass, aJJavaBcelRepository);
		
		if( JJavaBcelInterface.modelsType(aJavaClass) )
			return new JJavaBcelInterface(theSignature, aJavaClass, aJJavaBcelRepository);
		
		if( JJavaBcelEnum.modelsType(aJavaClass) )
			return new JJavaBcelEnum(theSignature, aJavaClass, aJJavaBcelRepository);
		
		if( JJavaBcelAnnotationType.modelsType(aJavaClass) )
			return new JJavaBcelAnnotationType(theSignature, aJavaClass, aJJavaBcelRepository );
		
		throw new Exception("Unsupported Java Type: " + aJavaClass.toString());
	}
	
	public boolean isAspect() throws IOException
	{
		if( this.aspectAnnotationSignature == null ) 
			this.aspectAnnotationSignature = JJavaSignature.signatureFor( open.weaver.annotations.Aspect.class.getName() );
		
		annotations = this.getAnnotations();
		
		for( JJavaAnnotation annotation : annotations )
		{
			if( annotation.getSignature().equals(this.aspectAnnotationSignature) )
				return true;
		}
		
		return false;
	}
	
	/**
	 * Sort classes by name
	 */
	public int compareTo(JJavaClassifier aClassifier)
	{
		return this.signature.qualifiedName().compareTo(aClassifier.qualifiedName());
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		super.toSExpression(aStringBuffer);
	}	
	
	public static class BcelClassifierTest
	{
		@Test
		public void testInnerClass() throws Exception
		{
			JavaClass cls;
			JJavaBcelClassifier jjbc;
			
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);
			
			cls = Repository.lookupClass(example.jana.classes.InnerClassExample.class.getName());
			Repository.clearCache();
			
			JJavaProject dummyProject = new JJavaImaginaryProject(".");
			jjbc = JJavaBcelClassifier.produce(cls, new JJavaBcelRepository(dummyProject));
			
			Assert.assertEquals(19, JJavaSignature.numSignatures());
			Assert.assertEquals(17, JJavaSootType.numTypes());
			
			StringReader sr = new StringReader(jjbc.toSExpression());
			int v;
			CRC32 crc = new CRC32();
			
			do
			{
				v = sr.read();
				crc.update(v);
			}
			while(v!= -1);
			
			Assert.assertEquals(3520027455L, crc.getValue());
			
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).debug(jjbc.toSExpression());
		}
	}
}
