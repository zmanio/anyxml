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


public class XMLParseException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
    private XMLSource source;
    private Token token;
    private Location location;
    private Element element;
    private Document doc;
    private Node node;
    
    public XMLParseException (String message, XMLSource source, int offset)
    {
        super (message);
        setSource (source, offset);
    }

    public XMLParseException (String message, Token token)
    {
        this (message);
        setToken (token);
    }
    
    public XMLParseException (String message)
    {
        super (message);
    }
    
    public XMLParseException (String message, Throwable cause)
    {
        super (message, cause);
    }
    
    public XMLParseException (String message, Document doc, Node node)
    {
        super (message);
        setNode (doc, node);
    }
    
    public void setNode (Document doc, Node node)
    {
        this.doc = doc;
        this.node = node;
        if (doc != null && node != null)
            location = new Location (doc, node);
    }

    public XMLParseException (String message, Node node)
    {
        super (message);
        
        if (node instanceof Element)
        {
            setElement ((Element)node);
        }
        else if (node instanceof BasicNode)
        {
            BasicNode n = (BasicNode)node;
            setToken (n.getToken ());
        }
    }

    public XMLParseException (String message, Element e)
    {
        super (message);
        setElement (e);
    }

    public XMLParseException setElement (Element e)
    {
        this.element = e;
        if (e != null)
            location = new Location (e);
        return this;
    }

    public XMLParseException setSource (XMLSource source, int offset)
    {
        this.source = source;
        if (source != null)
            location = new Location (source, offset);
        return this;
    }
    
    public XMLParseException setToken (Token token)
    {
        this.token = token;
        if (token != null)
        {
            setSource (token.getSource (), token.getStartOffset ());
        }
        return this;
    }

    public XMLSource getSource ()
    {
        return source;
    }
    
    public Token getToken ()
    {
        return token;
    }
    
    public Element getElement ()
    {
        return element;
    }
    
    public Document getDoc ()
    {
        if (doc == null)
            return element == null ? null : element.getDocument ();
        
        return doc;
    }
    
    public Node getNode ()
    {
        return node;
    }
    
    public Location getLocation ()
    {
        return location;
    }
    
    public int getLine ()
    {
        return location == null ? -1 : location.getLine ();
    }
    
    public int getColumn ()
    {
        return location == null ? -1 : location.getColumn ();
    }
    
    public int getOffset ()
    {
        return location == null ? -1 : location.getOffset ();
    }
    
    public int getLineStartOffset ()
    {
        return location == null ? -1 : location.getLineStartOffset ();
    }
    
    @Override
    public String getMessage ()
    {
        if (location == null)
            return super.getMessage ();
        
        return "Line "+getLine ()+", column " + getColumn () + ": " + super.getMessage ();
    }
    
    public String getMessageWithoutLocation ()
    {
        return super.getMessage ();
    }
}
