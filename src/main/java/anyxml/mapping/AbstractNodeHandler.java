package anyxml.mapping;

import java.lang.reflect.Method;

import anyxml.Node;

public abstract class AbstractNodeHandler implements INodeHandler
{
    protected final Object handler;
    protected final Method method;
    
    public AbstractNodeHandler (Object handler, Method method)
    {
        this.handler = handler;
        this.method = method;
    }
    
    public void handle (Node node)
    {
        try
        {
            invoke (node);
        }
        catch (Exception e)
        {
            throw new MappingException (node+": Error invoking "+method+" on "+handler, e);
        }
    }
    
    public abstract void invoke (Node node) throws Exception;
    
    @Override
    public String toString ()
    {
        return super.toString () + "(handler="+handler+", method="+method+")";
    }
}
