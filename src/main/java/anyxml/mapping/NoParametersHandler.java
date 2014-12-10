package anyxml.mapping;

import java.lang.reflect.Method;

import anyxml.Node;

public class NoParametersHandler implements INodeHandler
{
    private final Object handler;
    private final Method method;
    
    public NoParametersHandler (Object handler, Method method)
    {
        this.handler = handler;
        this.method = method;
    }
    
    public void handle (Node node)
    {
        try
        {
            method.invoke (handler);
        }
        catch (Exception e)
        {
            throw new MappingException (node+": Error invoking "+method+" on "+handler);
        }
    }
    
    @Override
    public String toString ()
    {
        return super.toString () + "(handler="+handler+", method="+method+")";
    }
}
