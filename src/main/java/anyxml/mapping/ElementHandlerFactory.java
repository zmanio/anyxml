package anyxml.mapping;

import java.lang.reflect.Method;

public class ElementHandlerFactory
{

    public static INodeHandler create (Object handler, Method method)
    {
        if (method == null)
            return new NopHandler ();
        
        if (method.getParameterTypes ().length == 0)
            return new NoParametersHandler (handler, method);
        
        if (method.getParameterTypes ().length >= 1)
            return new ComplexElementHandler (handler, method);
        
        throw new MappingException ("Can't create an element handler for "+method);
    }
    
}
