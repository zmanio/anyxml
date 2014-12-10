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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import anyxml.XMLTokenizer.Type;
import static anyxml.XMLUtils.isElement;
import static anyxml.XMLUtils.isText;

/**
 * An element in a XML document.
 * 
 * <p>Elements can have attributes and they have children like a document.
 * 
 * @author digulla
 * @see anyxml.Document
 * @see anyxml.Attribute
 */
public class Element extends NodeWithChildren implements Child, TextNode
{
    private Token startToken;
    private Namespace namespace;
    private String beginName;
    private String postSpace = "";
    private String endName;
    private String name;
    private Parent parent;
    private Map<String, Attribute> attributeMap;
    private boolean compactEmpty;

    public Element (Token token)
    {
        this.startToken = token;
        this.beginName = token.getText ().substring (1);
        this.name = this.beginName.trim ();
        setNamespace (null);
    }

    public Element (String name)
    {
        this (null, name);
    }

    public Element (Parent parent, String name)
    {
        this (parent, name, null);
    }
    
    public Element (String name, Namespace ns)
    {
        this (null, name, ns);
    }
    
    public Element (Parent parent, String name, Namespace ns)
    {
        if (name == null)
            throw new NullPointerException ("name is null");
        if (name.trim ().length () == 0)
            throw new IllegalArgumentException ("name is blank");
        
        this.parent = parent;
        this.name = name;
        compactEmpty = true;
        setNamespace (ns);
        
        if (parent != null)
            parent.addNode (this);
    }

    public Token getStartToken ()
    {
        return startToken;
    }

    public int getStartOffset ()
    {
        return startToken == null ? -1 : startToken.getStartOffset ();
    }

    public int getEndOffset ()
    {
        return startToken == null ? -1 : startToken.getEndOffset ();
    }

    public String getBeginName ()
    {
        return beginName == null ? name : beginName;
    }

    public Element setBeginName (String beginName)
    {
        this.beginName = beginName;
        return this;
    }

    public String getEndName ()
    {
        return endName == null ? name : endName;
    }

    /** The string to be put into the end tag. This can contain whitespace around the name */
    public Element setEndName (String endName)
    {
        this.endName = endName;
        return this;
    }

    /** Space before the closing bracket of the element */
    public String getPostSpace ()
    {
        return postSpace;
    }
    
    public Element setPostSpace (String postSpace)
    {
        this.postSpace = postSpace;
        return this;
    }
    
    public Element setName (String name)
    {
        this.name = name;
        this.beginName = null;
        this.endName = null;
        return this;
    }

    public String getName ()
    {
        return name;
    }

    public Parent getParent ()
    {
        return parent;
    }

    public Element setParent (Parent parent)
    {
        this.parent = parent;
        return this;
    }
    
    public Element addAttributes (Attribute... attributes)
    {
        for (Attribute node: attributes)
            addAttribute (node);
        return this;
    }

    public Element addAttribute (String name, String value)
    {
        return addAttribute (new Attribute (name, value));
    }
    
    public Element addAttribute (Attribute a)
    {
        if (attributeMap == null)
        {
            attributeMap = new LinkedHashMap<String, Attribute> ();
        }
        
        String name = a.getName ();
        if (attributeMap.containsKey (name))
        {
            Token token = a.getToken ();
            if (token != null)
                throw new XMLParseException ("There is already an attribute with the name "+name, token);
            
            throw new XMLParseException ("There is already an attribute with the name "+name, this);
        }

        attributeMap.put (name, a);
        
        if (name.startsWith (Namespace.NS_PREFIX))
        {
            Namespaces namespaces = null;
            Document doc = getDocument ();
            if (doc != null)
                namespaces = doc.getNamespaces ();
            
            if (namespaces != null)
            {
                namespaces.addNamespace (new Namespace (name.substring (Namespace.NS_PREFIX.length ()), a.getValue ()));
            }
        }
        
        return this;
    }

    public Element setAttribute (Attribute a)
    {
        Attribute existing = getAttribute (a.getName ());
        if (existing == null)
            addAttribute (a);
        else
            existing.copy (a);
        return this;
    }

    public List<Attribute> getAttributes ()
    {
        if (attributeMap == null)
            return Collections.emptyList ();
        
        return new ArrayList<Attribute> (attributeMap.values ());
    }

    public Map<String, Attribute> getAttributeMap ()
    {
        if (attributeMap == null)
            return Collections.emptyMap ();
        
        return attributeMap;
    }

    public Attribute getAttribute (String name, Namespace ns)
    {
        if (ns == null || ns.getPrefix ().length () == 0)
            return getAttribute (name);
        
        return getAttribute (ns.getPrefix ()+":"+name);
    }
    
    public Attribute getAttribute (String name)
    {
        Attribute a = getAttributeMap ().get (name);
        if (a != null || name.contains (":"))
            return a;
        
        // Try with attribute name without ns prefix
        for (Attribute a2: getAttributeMap ().values ())
        {
            String aName = a2.getName ();
            int pos = aName.indexOf (':');
            if (pos == -1)
                continue;
            
            aName = aName.substring (pos + 1);
            if (name.equals (aName))
                return a2;
        }
        
        return null;
    }

    public Element setAttribute (String name, String value)
    {
        return setAttribute (name, value, null);
    }
    
    public Element setAttribute (String name, String value, Namespace ns)
    {
        Attribute a = getAttribute (name, ns);
        if (a == null)
        {
            a = new Attribute (name, value, ns);
            addAttribute (a);
        }
        else
        {
            a.setValue (value);
        }
        return this;
    }
    
    public Element checkMandatoryAttribute (String name)
    {
        Attribute a = getAttribute (name);
        if (a == null)
            throw new XMLParseException ("Element "+getName ()+" has no attribute "+name);
        return this;
    }
    
    public Element removeAttribute (String name)
    {
        if (attributeMap != null)
            attributeMap.remove (name);
        
        return this;
    }
    
    public String getAttributeValue (String name)
    {
        return getAttributeValue (name, null);
    }
    
    public String getAttributeValue (String name, Namespace ns)
    {
        Attribute a = getAttribute (name, ns);
        return a == null ? null : a.getValue ();
    }

    @Override
    public Element addNode (Node node)
    {
        super.addNode (node);
        return this;
    }
    
    public Element addNode (int index, Node node)
    {
        switch (node.getType ()) //@COBEX
        {
        case CDATA:
        case COMMENT:
        case CUSTOM_ELEMENT:
        case ELEMENT:
        case TEXT:
        case ENTITY:
            break;

        case PROCESSING_INSTRUCTION:
        {
            ProcessingInstruction pi = (ProcessingInstruction)node;
            if (XMLDeclaration.isXMLDeclaration (pi))
            {
                if (pi.getToken () != null)
                    throw new XMLParseException ("The XML declaration must be the first node of the document", pi.getToken ());
                
                throw new XMLParseException ("The XML declaration must be the first node of the document", this);
            }
            break;
        }

        default:
            throw new XMLParseException ("The node "+node.getType ()+" is not allowed here", this);
        }
        
        super.addNode (index, node);
        return this;
    }
    
    @Override
    public Element addNodes (Collection<? extends Node> nodes)
    {
        super.addNodes (nodes);
        return this; 
    }
    
    @Override
    public Element addNodes (int index, Collection<? extends Node> nodes)
    {
        super.addNodes (index, nodes);
        return this;
    }
    
    @Override
    public Element addNodes (Node... nodes)
    {
        super.addNodes (nodes);
        return this; 
    }
    
    @Override
    public Element addNodes (int index, Node... nodes)
    {
        super.addNodes (index, nodes);
        return this; 
    }
    
    public Element setCompactEmpty (boolean compactEmpty)
    {
        this.compactEmpty = compactEmpty;
        return this;
    }

    public boolean isCompactEmpty ()
    {
        return compactEmpty && !hasNodes ();
    }

    public Element toXML (XMLWriter writer) throws IOException
    {
        writer.write (this);
        return this;
    }

    public Type getType ()
    {
        return Type.ELEMENT;
    }

    public Element getChild (int index)
    {
        int count = 0;
        
        if (hasNodes ())
        {
            for (Node n: getNodes ())
            {
                if (isElement (n))
                {
                    if (index == count)
                        return (Element)n;
                    
                    count ++;
                }
            }
        }
        
        throw new IndexOutOfBoundsException ("Cannot return child "+index
            +", node has only "+count+(count == 1 ? " child" : " children"));
    }
    
    public boolean hasChildren ()
    {
        if (!hasNodes ())
            return false;
        
        for (Node node: getNodes ())
        {
            if (isElement (node))
                return true;
        }
        
        return false;
    }

    public final static NodeFilter<Element> ELEMENT_FILTER = new NodeFilter<Element> () {
        @Override
        public boolean matches (Node n)
        {
            return n instanceof Element;
        }
    };

    public List<Element> getChildren ()
    {
        return getNodes (ELEMENT_FILTER);
    }

    public List<Element> getChildren (final String name)
    {
        return getChildren (name, null);
    }
    
    public List<Element> getChildren (final String name, final Namespace ns)
    {
        NodeFilter<Element> nameFilter = new NodeFilter<Element> () {
            @Override
            public boolean matches (Node n)
            {
                if (!isElement (n))
                    return false;
                
                Element e = (Element)n;
                
                boolean match = true;
                if (match && name != null && !e.getName ().equals (name))
                    match = false;
                if (match && ns != null && !ns.equals (getNamespace ()))
                    match = false;
                
                return match;
            }
        };
        return getNodes (nameFilter);
    }

    /** @deprecated Use Element.clearChildren() instead */
    public Element clearChildNodes ()
    {
        return clearChildren ();
    }
    
    public Element clearChildren ()
    {
        if (!hasNodes ())
            return this;
        
        for (Iterator<Node> iter = getNodes ().iterator (); iter.hasNext (); )
        {
            Node n = iter.next ();
            if (isElement (n))
                iter.remove ();
        }
        
        return this;
    }

    public String getText ()
    {
        if (!hasNodes ())
            return "";
        
        StringBuilder buffer = new StringBuilder ();
        for (Node n: getNodes ())
        {
            if (isText (n))
                buffer.append (((Text)n).getText ());
            else if (isElement (n))
                buffer.append (((Element)n).getText ());
            else if (n.getType () == Type.ENTITY)
                buffer.append (((Entity)n).getText ());
        }
        
        return buffer.toString ();
    }

    public String getTrimmedText ()
    {
        return getText ().trim ();
    }

    public String getNormalizedText ()
    {
        return getTrimmedText ().replaceAll ("\\s+", " ");
    }

    public Element setText (String text)
    {
        clearText ();
        addNode (0, new Text (text));
        return this;
    }

    public Element clearText ()
    {
        if (!hasNodes ())
            return this;
        
        for (Iterator<Node> iter = getNodes ().iterator (); iter.hasNext ();)
        {
            Node n = iter.next ();
            if (isText (n))
            {
                iter.remove ();
            }
            else if (isElement (n))
            {
                ((Element)n).clearText ();
            }
        }
        return this;
    }
    
    public Element getParentElement ()
    {
        if (getParent () == null
            || getParent ().getType () == Type.DOCUMENT
        )
            return null;
        
        return (Element)getParent ();
    }
    
    /** @deprecated Use Element.getChildPath() instead */
    public String getNodePath ()
    {
        return getChildPath ();
    }
    
    public String getChildPath ()
    {
        if (getParent () == null)
            return getName ();
        
        Element p = getParentElement ();
        if (p == null)
            return "/" + getName ();
        
        int index = p.childIndexOf (this);
        if (index == 0)
            return p.getChildPath () + "/" + getName ();
        
        return p.getChildPath () + "/" + getName () + "[" + index + "]";
    }
    
    public int childIndexOf (Element element)
    {
        int index = 0;
        for (Node n: getNodes ())
        {
            if (n.equals (element))
                return index;
            
            if (isElement (n))
                index ++;
        }
        
        return -1;
    }

    public Document getDocument ()
    {
        Parent p = getParent ();
        while (p != null)
        {
            if (p.getType () == Type.DOCUMENT)
                return (Document)p;
            
            p = ((Child)p).getParent ();
        }
        
        return null;
    }
    
    public Element getChild (String path)
    {
        return getChild (path, null);
    }
    
    public Element getChild (String path, Namespace ns)
    {
        if (path.startsWith ("/"))
        {
            Document doc = getDocument ();
            return doc == null ? null : doc.getChild (path, ns);
        }
        
        if ((path.length () == 0 || ".".equals (path))
            && (ns == null || ns.equals (getNamespace ()))
        )
            return this;
        
        if (!hasNodes ())
            return null;
        
        String[] pathSegments = path.split ("/");
        Element current = this;
        
        for (int i = 0; i < pathSegments.length; i++)
        {
            String name = pathSegments[i];
            int pos = name.indexOf (':');
            Namespace ns2 = null;
            if (pos >= 0)
            {
                String prefix = name.substring (0, pos);
                name = name.substring (pos + 1);
                ns2 = getDocument ().getNamespace (prefix);
                if (ns2 == null)
                    throw new XMLParseException ("Namespace prefix '"+prefix+"' is not defined");
            }
            pos = name.indexOf ('[');
            int index = 0;
            if (pos != -1)
            {
                int pos2 = name.indexOf (']', pos);
                index = Integer.parseInt (name.substring (pos + 1, pos2));
                name = name.substring (0, pos);
            }
            
            int count = 0;
            boolean found = false;
            for (Node n: current.getNodes ())
            {
                if (n instanceof Element)
                {
                    Element e = (Element)n;
                    if (name.equals (e.getName ())
                        && (ns2 == null || ns2.equals (e.getNamespace ())))
                    {
                        if (count == index)
                        {
                            current = e;
                            found = true;
                            break;
                        }
                        count ++;
                    }
                }
            }
            
            if (!found)
                return null;
        }
        
        if (ns != null && !ns.equals (current.getNamespace ()))
            return null;
        
        return current;
    }

    public Element createClone ()
    {
        return new Element (name);
    }
    
    public Element copy (Node orig)
    {
        super.copy (orig);
        
        Element other = (Element)orig;
        
        if (other.attributeMap != null)
        {
            for (Attribute a: other.attributeMap.values ())
            {
                addAttribute (a.copy ());
            }
        }
        
        this.beginName = other.beginName;
        this.compactEmpty = other.compactEmpty;
        this.endName = other.endName;
        this.name = other.name;
        // Ignore parent
        this.postSpace = other.postSpace;
        this.startToken = other.startToken;
        
        return this;
    }

    public Element copy ()
    {
        return (Element)super.copy ();
    }
    
    public void setNamespace (Namespace namespace)
    {
        if (namespace == null)
        {
            namespace = Namespace.NO_NAMESPACE;
        }
        this.namespace = namespace;
    }

    public Namespace getNamespace ()
    {
        return namespace;
    }

    public void remove() {
    	if( null == getParent() ) {
    		return;
    	}
    	
    	getParent().removeNode( this );
    }
}
