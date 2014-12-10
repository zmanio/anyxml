package anyxml.mapping;

import anyxml.Node;

public class NopHandler implements INodeHandler
{
    public void handle (Node node)
    {
        // Do nothing
    }
    
    @Override
    public String toString ()
    {
        return getClass ().getSimpleName ();
    }
}
