package anyxml.mapping;

import java.lang.reflect.Method;

import anyxml.Node;
import anyxml.Text;

public class TextHandlerFactory
{
    public static class StringTextHandler extends AbstractNodeHandler
    {
        public StringTextHandler (Object handler, Method method)
        {
            super (handler, method);
        }

        @Override
        public void invoke (Node node) throws Exception
        {
            String text = ((Text)node).getText ();
            method.invoke (handler, text);
        }
    }

    public static class TextObjectTextHandler extends AbstractNodeHandler
    {
        public TextObjectTextHandler (Object handler, Method method)
        {
            super (handler, method);
        }
        
        @Override
        public void invoke (Node node) throws Exception
        {
            method.invoke (handler, node);
        }
    }
    
    public static INodeHandler create (Object handler, Method method)
    {
        if (method == null)
            return new NopHandler ();
        
        if (method.getParameterTypes ().length == 0)
            return new NoParametersHandler (handler, method);
        
        if (method.getParameterTypes ().length == 1)
        {
            Class<?> type = method.getParameterTypes ()[0];
            
            if (String.class.equals (type))
                return new StringTextHandler (handler, method);
            if (Text.class.isAssignableFrom (type))
                return new TextObjectTextHandler (handler, method);
        }
        
        throw new MappingException ("Can't create a text handler for "+method);
    }
}
