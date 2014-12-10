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

import java.util.Collection;
import java.util.List;

/**
 * This is an interface for anything which can be a parent:
 * <code>Element</code> and <code>Document</code>.
 * 
 * @author digulla
 * @see anyxml.Element
 * @see anyxml.Document
 */
public interface Parent extends Node
{
    public Parent addNode (Node node);
    public Parent addNode (int index, Node node);
    public Parent addNodes (Node... nodes);
    public Parent addNodes (Collection<? extends Node> nodes);
    public Parent addNodes (int index, Node... nodes);
    public Parent addNodes (int index, Collection<? extends Node> nodes);
    
    /** Get a specific node from the list */
    public Node getNode (int index);
    
    /** Remove a node from the list
     * 
     * @return The removed node.
     */
    public Node removeNode (int index);
    
    /** Remove a node from the list 
     * 
     * @return true, if the node is in the list
     */
    public boolean removeNode (Node node);
    
    /** Remove all nodes */
    public Parent clearNodes ();
    
    /** Get the list of child nodes.
     * 
     *  <p>CAUTION: Changes to this list will modify the actual data structure!
     *  So don't do this unless you know what you're doing!
     */
    public List<Node> getNodes ();
    
    /** The index of the node in the node list or -1 if it isn't in the list */
    public int nodeIndexOf (Node node);
    
    /** The number of nodes in the list */
    public int nodeCount ();
    
    /** Does this node have children? */
    public boolean hasNodes ();
    
    /** Find a child element (not a node!) by path */
    public Element getChild (String path);
}
