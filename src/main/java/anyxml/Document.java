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
import java.util.Collection;

import anyxml.XMLTokenizer.Type;
import anyxml.dtd.DocType;

/**
 * This class represents an XML document.
 * 
 * <p>If you add a {@code ProcessingInstruction} as the first node of the document,
 * it will be converted into an {@code XMLDeclaration}.
 * 
 * <p>Note: The automatic creation of {@code XMLDeclaration} is not perfect; if you
 * manipulate the list of nodes yourself (for example, via {@code getNodes().add()}),
 * then you're on your own.
 * 
 * @author DIGULAA
 *
 */
public class Document extends NodeWithChildren implements Parent
{
    private Element rootNode;
    private XMLDeclaration xmlDeclaration;
    private DocType docType;
    private Namespaces namespaces;
    
    public Document ()
    {
        // Do nothing
    }
    
    public Document (Node... nodes)
    {
        addNodes (nodes);
    }
    
    @Override
    public Document addNode (Node node)
    {
        super.addNode (node);
        return this;
    }
    
    public Document addNode (int index, Node node)
    {
        if (node instanceof Element)
        {
            Element e = (Element)node;
            if (rootNode != null)
            {
                Token startToken = e.getStartToken ();
                if (startToken == null)
                    throw new XMLParseException ("Only one root element allowed per document", rootNode);
                else
                    throw new XMLParseException ("Only one root element allowed per document", startToken);
            }
            
            rootNode = e;
        }
        else if (node instanceof DocType)
        {
            setDocType ((DocType)node);
        }
        else if (node == null)
            throw new NullPointerException ("node is null");
        
        if (index == 0 && xmlDeclaration != null)
            throw new XMLParseException ("It is not allowed to have content before the XML declaration", this, xmlDeclaration);
        
        switch (node.getType ()) //@COBEX
        {
        case COMMENT:
        case ELEMENT:
        case PROCESSING_INSTRUCTION:
        case CUSTOM_ELEMENT:
        case DOCTYPE:
            break;
        
        case TEXT: {
            Text t = (Text)node;
            if (t.isWhitespace ())
                break;
            }
            // Fall through
            
        default:
            Node sibling = null;
            if (nodeCount () > 0)
            {
                if (index == nodeCount ())
                    index --;
                sibling = getNode (index);
            }
            if (node instanceof BasicNode && ((BasicNode)node).getToken () != null)
            {
                throw new XMLParseException (node.getType ()+" node is not allowed here", ((BasicNode)node).getToken ());
            }
            throw new XMLParseException (node.getType ()+" node is not allowed here", this, sibling);
        }
        
        super.addNode (index, node);
        
        if (node.getType () == Type.PROCESSING_INSTRUCTION)
        {
            ProcessingInstruction pi = (ProcessingInstruction)node;
            
            if (XMLDeclaration.isXMLDeclaration (pi))
            {
                if (index == 0)
                    parseXMLDeclaration ();
                else
                {
                    String msg = "An XML declaration is only allowed as the first node in the document";
                    if (pi.getToken () != null)
                        throw new XMLParseException (msg, pi.getToken ());
                    
                    throw new XMLParseException (msg, this, pi);
                }
            }
        }
        
        return this;
    }
    
    @Override
    public Document addNodes (Collection<? extends Node> nodes)
    {
        super.addNodes (nodes);
        return this; 
    }
    
    @Override
    public Document addNodes (int index, Collection<? extends Node> nodes)
    {
        super.addNodes (index, nodes);
        return this;
    }
    
    @Override
    public Document addNodes (Node... nodes)
    {
        super.addNodes (nodes);
        return this; 
    }
    
    @Override
    public Document addNodes (int index, Node... nodes)
    {
        super.addNodes (index, nodes);
        return this; 
    }
    
    @Override
    public Node removeNode (int index)
    {
        Node n = getNode (index);
        if (n.equals (xmlDeclaration))
            clearXMLDeclaration ();
        else if (n.equals (rootNode))
            rootNode = null;
        
        return super.removeNode (index);
    }

    @Override
    public boolean removeNode (Node n)
    {
        if (n != null)
        {
            if (n.equals (xmlDeclaration))
                clearXMLDeclaration ();
            else if (n.equals (rootNode))
                rootNode = null;
        }

        return super.removeNode (n);
    }
    
    public Element getChild (String path)
    {
        return getChild (path, null);
    }
    
    public Element getChild (String path, Namespace ns)
    {
        if (getRootElement () == null)
            return null;

        if (path.startsWith ("/"))
            path = path.substring (1);
        
        int pos = path.indexOf ('/');
        if (pos == -1)
        {
            if (ns == null)
            {
                pos = path.indexOf (':');
            }
            
            if (path.equals (getRootElement ().getName ())
                && (ns == null || ns.equals (getRootElement ().getNamespace ()))
            )
                return getRootElement ();
            else
                return null;
        }
        
        String name = path.substring (0, pos);
        int pos2 = name.indexOf (':');
        Namespace ns2 = null;
        if (pos2 != -1)
        {
            String prefix = name.substring (0, pos2);
            name = name.substring (pos2 + 1);
            ns2 = getNamespaces ().getNamespace (prefix);
            if (ns2 == null)
                throw new XMLParseException ("Namespace prefix '"+prefix+"' is not defined");
        }
        if (!name.equals (getRootElement ().getName ())
            || (ns2 != null && !ns2.equals (getRootElement ().getNamespace ()))
        )
            return null;
        
        return getRootElement ().getChild (path.substring (pos+1), ns);
    }
    
    public Type getType ()
    {
        return Type.DOCUMENT;
    }
    
    public Element getRootElement ()
    {
        return rootNode;
    }
    
    public Document setRootNode (Element rootNode)
    {
        if (this.rootNode != null)
        {
            removeNode (this.rootNode);
        }
        
        this.rootNode = null;
        
        if (rootNode.getParent () != null)
        {
            rootNode.getParent ().removeNode (rootNode);
        }
        
        super.addNode (rootNode);
        
        return this;
    }
    
    public XMLDeclaration getXmlDeclaration ()
    {
        return xmlDeclaration;
    }
    
    public void setXmlDeclaration (XMLDeclaration xmlDeclaration)
    {
        this.xmlDeclaration = xmlDeclaration;
    }
    
    public String getVersion ()
    {
        return xmlDeclaration == null ? null : xmlDeclaration.getVersion ();
    }
    
    public Document setVersion (String version)
    {
        createXMLDeclaration ().setVersion (version == null ? "1.0" : version);
        return this;
    }
    
    protected XMLDeclaration createXMLDeclaration ()
    {
        if (xmlDeclaration == null)
        {
            addNode (0, new XMLDeclaration ("1.0"));
            addNode (1, new Text ("\n"));
        }
        return xmlDeclaration;
    }

    public String getEncoding ()
    {
        return xmlDeclaration == null ? null : xmlDeclaration.getEncoding ();
    }
    
    public Document setEncoding (String encoding)
    {
        createXMLDeclaration ().setEncoding (encoding);
        return this;
    }
    
    public boolean isStandalone ()
    {
        return xmlDeclaration == null ? false : xmlDeclaration.isStandalone ();
    }
    
    public void setStandalone (boolean standalone)
    {
        createXMLDeclaration ().setStandalone (standalone);
    }

    @Override
    public Document toXML (XMLWriter writer) throws IOException
    {
        super.toXML (writer);
        return this;
    }
    
    /** Parse a possible XML declaration and fill the internal fields with the data. */
    public void parseXMLDeclaration ()
    {
        if (getNodes ().isEmpty ())
            return;

        Node n = getNode (0);
        if (n.getType () != Type.PROCESSING_INSTRUCTION
            || n == xmlDeclaration)
            return;
        
        ProcessingInstruction pi = (ProcessingInstruction)n;
        if (xmlDeclaration != null)
        {
            xmlDeclaration.parseXMLDeclaration (pi.getValue ());
        }
        else
        {
            try
            {
                xmlDeclaration = XMLDeclaration.parseXMLDeclaration (pi);
            }
            catch (XMLParseException e)
            {
                XMLParseException ex = new XMLParseException (e.getMessageWithoutLocation (), e);
                if (pi.getToken () == null)
                    ex.setNode (this, pi);
                else
                    ex.setSource (pi.getToken ().getSource (), pi.getStartOffset () + e.getOffset ());
                throw ex;
            }
            getNodes ().set (0, xmlDeclaration);
        }
    }

    protected void clearXMLDeclaration ()
    {
        xmlDeclaration = null;
    }

    public DocType getDocType ()
    {
        return docType;
    }
    
    public void setDocType (DocType docType)
    {
        this.docType = docType;
    }
    
    public Namespaces getNamespaces ()
    {
        if (namespaces == null)
            namespaces = new Namespaces ();
        
        return namespaces;
    }
    
    public void setNamespaces (Namespaces namespaces)
    {
        this.namespaces = namespaces;
    }

    public Namespace getNamespace (String prefix)
    {
        if (prefix == null || prefix.length () == 0)
            return Namespace.NO_NAMESPACE;
        
        if ("xml".equals (prefix))
            return Namespace.XML_NAMESPACE;
        
        return getNamespaces ().getNamespace (prefix);
    }

    public Document createClone ()
    {
        return new Document ();
    }
    
    public Document copy (Node orig)
    {
        super.copy (orig);
        
        Document other = (Document)orig;
        
        this.xmlDeclaration = other.xmlDeclaration.copy ();
        this.docType = other.docType;
        if (other.namespaces != null)
            this.namespaces = other.namespaces.copy ();
        
        return this;
    }

    public Document copy ()
    {
        return (Document)super.copy ();
    }
    
    public TreeIterator iterator ()
    {
        return new TreeIterator (this);
    }
}
