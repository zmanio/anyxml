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
 * 
 */
package anyxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class contains all the code necessary to implement nodes
 * which can have child nodes (like Document or Element).
 * 
 * @author digulla
 *
 */
public abstract class NodeWithChildren implements Parent
{
    private List<Node> nodes;

    public NodeWithChildren addNode (Node node)
    {
        addNode (nodes == null ? 0 : nodes.size (), node);
        return this;
    }

    public NodeWithChildren addNode (int index, Node node)
    {
        if (nodes == null)
            nodes = new ArrayList<Node> ();

        nodes.add (index, node);

        if (node instanceof Child)
        {
            Child child = (Child)node;
            child.setParent (this);
        }

        return this;
    }

    public NodeWithChildren addNodes (Collection<? extends Node> nodes)
    {
        return addNodes (this.nodes == null ? 0 : this.nodes.size (), nodes);
    }

    public NodeWithChildren addNodes (int index, Collection<? extends Node> nodes)
    {
        for (Node node: nodes)
            addNode (index++, node);
        return this;
    }

    public NodeWithChildren addNodes (Node... nodes)
    {
        return addNodes (this.nodes == null ? 0 : this.nodes.size (), nodes);
    }

    public NodeWithChildren addNodes (int index, Node... nodes)
    {
        for (Node node: nodes)
            addNode (index++, node);
        return this;
    }


    public List<Node> getNodes ()
    {
        if (nodes == null)
            return Collections.emptyList ();
        
        return nodes;
    }

    public boolean hasNodes ()
    {
        return nodeCount () != 0;
    }
    
    public int nodeCount ()
    {
        return nodes == null ? 0 : nodes.size ();
    }
    
    public int nodeIndexOf (Node node)
    {
        if (nodes == null)
            return -1;
        
        return nodes.indexOf (node);
    }
    
    public Node getNode (int index)
    {
        if (nodes == null)
            throw new IndexOutOfBoundsException ("Cannot return child "+index+", node has only "+0+" child nodes");
        else if (index < 0 || index >= nodes.size ())
            throw new IndexOutOfBoundsException ("Cannot return child "+index+", node has only "+nodes.size ()+" child nodes");
        
        return nodes.get (index);
    }

    public Node removeNode (int index)
    {
        Node n = nodes.remove (index);
        
        if (n instanceof Child)
            ((Child)n).setParent (null);
        
        return n;
    }

    public boolean removeNode (Node n)
    {
        if (nodes == null)
            return false;
        
        if (nodes.remove (n))
        {
            if (n instanceof Child)
            {
                ((Child)n).setParent (null);
            }
            
            return true;
        }
        
        return false;
    }

    @SuppressWarnings("unchecked")
	public <T> List<T> getNodes (NodeFilter<T> filter)
    {
        if (nodes == null)
            return Collections.emptyList ();
        
        List<T> result = new ArrayList<T> (nodes.size ());
        for (Node n: nodes)
        {
            if (filter.matches (n))
            {
                result.add ((T)n);
            }
        }
        return result;
    }
    
    public NodeWithChildren clearNodes ()
    {
        if (nodes != null)
            nodes.clear ();
        
        return this;
    }
    
    public String toXML ()
    {
        return BasicNode.toXML (this);
    }

    public NodeWithChildren toXML (XMLWriter writer) throws IOException
    {
        writer.writeChildNodes (this);
        return this;
    }

    @Override
    public String toString ()
    {
        return toXML ();
    }

    public NodeWithChildren copy (Node orig)
    {
        NodeWithChildren other = (NodeWithChildren)orig;
        
        if (other.nodes != null)
        {
            for (Node n: other.nodes)
            {
                Node copy = n.createClone ();
                copy.copy (n);
                
                addNode (copy);
            }
        }
        
        return this;
    }
    
    public NodeWithChildren copy ()
    {
        NodeWithChildren n = (NodeWithChildren)createClone ();
        n.copy (this);
        return n;
    }
}
