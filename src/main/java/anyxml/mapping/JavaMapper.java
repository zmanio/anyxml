package anyxml.mapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import anyxml.Document;
import anyxml.Element;
import anyxml.Node;
import anyxml.TreeIterator;
import anyxml.XMLUtils;

public class JavaMapper
{
    private Map<String, INodeHandler> handlers = new HashMap<String, INodeHandler> ();
    private INodeHandler textHandler;

    public JavaMapper setHandler (Object handler)
    {
        handlers.clear ();
        
        Class<?> c = handler.getClass ();
        //System.out.println (c);
        Set<Method> methods = new HashSet<Method> (Arrays.asList (c.getMethods ()));
        
        Method textMethod = locateTextHandler (methods);
        textHandler = TextHandlerFactory.create (handler, textMethod);
        //System.out.println (textHandler);
        
        for (Method m: methods)
        {
            if (Object.class.equals (m.getDeclaringClass ()))
                continue;
            
            String[] names = getNames (m);
            INodeHandler elementHandler = ElementHandlerFactory.create (handler, m);
            
            for (String name: names)
            {
                INodeHandler existing = handlers.get (name);
                if (existing != null)
                {
                    throw new MappingException ("The handler "+handler+" contains two methods to handle elements with the name "+name);
                }
                
                handlers.put (name, elementHandler);
            }
        }
        
        if (handlers.isEmpty () && textMethod == null)
        {
            throw new MappingException ("No usable methods found; maybe they aren't public? Handler: "+handler);
        }
        
        return this;
    }

    private String[] getNames (Method m)
    {
        ElementName ann = m.getAnnotation (ElementName.class);
        if (ann != null)
            return ann.value ();
        
        return new String[] { m.getName () };
    }

    private Method locateTextHandler (Set<Method> methods)
    {
        for (Method m: methods)
        {
            //System.out.println (m);
            //System.out.println (m.getAnnotation (TextHandler.class));
            if (m.getAnnotation (TextHandler.class) != null)
            {
                methods.remove (m);
                return m;
            }
        }
        
        for (Method m: methods)
        {
            //System.out.println (m);
            if ("text".equals (m.getName ()) && m.getAnnotation (ElementName.class) == null)
            {
                methods.remove (m);
                return m;
            }
        }
        
        return null;
    }

    public void apply (Document doc)
    {
        for (TreeIterator iter = doc.iterator (); iter.hasNext (); )
        {
            Node node = iter.next ();
            if (XMLUtils.isText (node))
            {
                textHandler.handle (node);
            }
            else if (XMLUtils.isElement (node))
            {
                String name = ((Element)node).getName ();
                INodeHandler handler = handlers.get (name);
                if (handler != null)
                    handler.handle (node);
            }
            // TODO what about other nodes?
        }
    }
    
}
