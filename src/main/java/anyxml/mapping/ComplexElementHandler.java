package anyxml.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import anyxml.Attribute;
import anyxml.Element;
import anyxml.Node;

public class ComplexElementHandler extends AbstractNodeHandler
{
    private static interface ParameterProvider
    {
        Object value (Element node);
    }
    
    private static class StringParameterProvider implements ParameterProvider
    {
        private final String name;

        public StringParameterProvider (String name)
        {
            this.name = name;
        }

        public Object value (Element node)
        {
            return node.getAttributeValue (name);
        }
    }
    
    private static class IntegerParameterProvider implements ParameterProvider
    {
        private final String name;
        
        public IntegerParameterProvider (String name)
        {
            this.name = name;
        }
        
        public Object value (Element node)
        {
            String value = node.getAttributeValue (name);
            if (value == null)
                return null;
            
            try
            {
                return new Integer (value);
            }
            catch (NumberFormatException e)
            {
                throw new MappingException ("Failed to convert the attribute "+name+"=\""+value+"\" to integer", e);
            }
        }
    }
    
    private static class BooleanParameterProvider implements ParameterProvider
    {
        private final String name;
        private final String[] trueValues; 
        private final String[] falseValues; 
        
        public BooleanParameterProvider (String name, AttributeMapping ann)
        {
            this.name = name;
            
            if (   ann.trueValues ().length == 0 
                && ann.falseValues ().length == 0)
            {
                trueValues = new String[] { "1", "on", "true", "y", "yes" };
                falseValues = null;
            }
            else
            {
                trueValues = (ann.trueValues ().length == 0) ? null : ann.trueValues ();
                falseValues = (ann.falseValues ().length == 0) ? null : ann.falseValues ();
            }
            
            if (trueValues != null)
                Arrays.sort (trueValues);
            if (falseValues != null)
                Arrays.sort (falseValues);
        }
        
        public Object value (Element node)
        {
            String value = node.getAttributeValue (name).toLowerCase();
            if (value == null)
                return null;
            
            if (trueValues != null)
            {
                if (Arrays.binarySearch (trueValues, value) >= 0)
                    return Boolean.TRUE;
            }
            if (falseValues != null)
            {
                if (Arrays.binarySearch (falseValues, value) >= 0)
                    return Boolean.FALSE;
            }
            
            if (trueValues != null && falseValues != null)
            {
                throw new MappingException ("Failed to convert the attribute "+name+"=\""+value+"\" to boolean. " +
                		"Allowed values for true are: "+Arrays.toString (trueValues)+". " +
                		"Allowed values for false are: "+Arrays.toString (trueValues));
            }
            
            return (trueValues == null) ? Boolean.TRUE : Boolean.FALSE;
        }
    }
    
    private static class AttributeParameterProvider implements ParameterProvider
    {
        private final String name;
        
        public AttributeParameterProvider (String name)
        {
            this.name = name;
        }
        
        public Object value (Element node)
        {
            return node.getAttribute (name);
        }
    }
    
    private static class RequiredWrapper implements ParameterProvider
    {
        private final String name;
        private final ParameterProvider delegate;

        public RequiredWrapper (String name, ParameterProvider delegate)
        {
            this.name = name;
            this.delegate = delegate;
        }

        public Object value (Element node)
        {
            if (null == node.getAttribute (name))
                throw new MappingException ("Required attribute \""+name+"\" is missing: "+node);
            
            return delegate.value (node);
        }
    }
    
    private ParameterProvider[] parameterProviders;
    
    public ComplexElementHandler (Object handler, Method method)
    {
        super (handler, method);
        
        processArguments ();
    }

    private void processArguments ()
    {
        final int N = method.getParameterTypes ().length;
        parameterProviders = new ParameterProvider[N];
        
        Annotation[][] annotations = method.getParameterAnnotations ();
        for (int i=0; i<N; i++)
        {
            AttributeMapping ann = getAnnotation (annotations[i], AttributeMapping.class);
            Class<?> type = method.getParameterTypes ()[i];
            //System.out.println (type);
            if (ann == null)
            {
                if (Element.class.isAssignableFrom (type))
                {
                    parameterProviders[i] = new ParameterProvider () {
                        public Object value (Element node)
                        {
                            return node;
                        }
                    };
                }
            }
            else
            {
                String name = ann.value ();
                if (name == null || name.length () == 0)
                    name = ann.name ();
                if (name == null || name.length () == 0)
                    throw new MappingException ("Missing attribute name in annotation AttributeName for "+method);
                
                boolean required = ann.required ();
                
                if (String.class.equals (type))
                    parameterProviders[i] = new StringParameterProvider (name);
                else if (Attribute.class.isAssignableFrom (type))
                    parameterProviders[i] = new AttributeParameterProvider (name);
                else if (int.class.equals (type))
                {
                    parameterProviders[i] = new IntegerParameterProvider (name);
                    required = true;
                }
                else if (boolean.class.equals (type))
                {
                    parameterProviders[i] = new BooleanParameterProvider (name, ann);
                    required = true;
                }
                else if (Integer.class.equals (type))
                    parameterProviders[i] = new IntegerParameterProvider (name);
                else if (Boolean.class.equals (type))
                    parameterProviders[i] = new IntegerParameterProvider (name);
                
                if (required && parameterProviders[i] != null)
                    parameterProviders[i] = new RequiredWrapper (name, parameterProviders[i]);
            }
            
            if (parameterProviders[i] == null)
                throw new MappingException ("Can't create parameter provider for parameter "+(i+1)+" ("+type+") of "+method);
            
        }
    }

    @SuppressWarnings ("unchecked")
    private <T extends Annotation> T getAnnotation (Annotation[] annotations, Class<T> key)
    {
        for (Annotation a: annotations)
        {
            if (key.equals (a.annotationType ()))
                return (T)a;
        }
        return null;
    }

    @Override
    public void invoke (Node node) throws Exception
    {
        Element element = (Element)node;
        
        final int N = method.getParameterTypes ().length;
        Object[] parameters = new Object[N];
        for (int i=0; i<N; i++)
        {
            parameters[i] = parameterProviders[i].value (element);
        }
        
        try
        {
            method.invoke (handler, parameters);
        }
        catch (Exception e)
        {
            String msg = toString (parameters);
            throw new MappingException ("Error invoking "+method+" with the parameters:"+msg, e);
        }
    }

    private String toString (Object[] parameters)
    {
        StringBuilder buffer = new StringBuilder ();
        
        for (int i=0; i<parameters.length; i++)
        {
            buffer.append ('\n');
            buffer.append (i);
            buffer.append (": ");
            
            Object p = parameters[i];
            if (p == null)
            {
                buffer.append ("null");
            }
            else
            {
                buffer.append ("[");
                buffer.append (p.getClass ());
                buffer.append ("] ");
                buffer.append (p.toString ());
            }
        }
        
        return buffer.toString ();
    }
}
