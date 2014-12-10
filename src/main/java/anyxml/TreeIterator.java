/*
 * Copyright (c) 2008, Aaron Digulla
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *     * Neither the name of Aaron Digulla nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package anyxml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import anyxml.XMLTokenizer.Type;

public class TreeIterator implements Iterator<Node>
{
    private String path = null;
    private List<NodeWithChildren> stack = new ArrayList<NodeWithChildren> ();
    private int[] cursorStack = new int[100];

    public TreeIterator (NodeWithChildren start)
    {
        push (start);
    }

    public String getPath ()
    {
        if (path == null)
        {
            NodeWithChildren n = currentNode ();
            if (n.getType () == Type.DOCUMENT)
                path = "/";
            else
                path = ((Element)n).getChildPath ();
        }
        
        return path;
    }
    
    protected void push (NodeWithChildren node)
    {
        stack.add (node);
        setCursor (0);
        path = null;
    }
    
    protected NodeWithChildren pop ()
    {
        NodeWithChildren node = stack.remove (stack.size () - 1);
        path = null;
        return node;
    }
    
    public NodeWithChildren currentNode ()
    {
        return stack.get (stack.size () - 1);
    }
    
    protected void setCursor (int i)
    {
        cursorStack[stack.size () - 1] = i;
    }
    
    protected void incCursor ()
    {
        cursorStack[stack.size () - 1] ++;
    }
    
    protected int getCursor ()
    {
        return cursorStack[stack.size () - 1];
    }

    public boolean hasNext ()
    {
        while (true)
        {
            if (stack.isEmpty ())
                return false;
        
            if (getCursor () == currentNode ().nodeCount ())
            {
                pop ();
                continue;
            }
            
            return true;
        }
    }

    public Node next ()
    {
        Node n = currentNode ().getNode (getCursor ());
        incCursor ();
        
        if (XMLUtils.isElement (n))
        {
            Element e = (Element)n;
            if (e.hasNodes ())
                push (e);
        }
        
        return n;
    }

    public void remove ()
    {
        throw new UnsupportedOperationException ("remove() is not supported");
    }
}
