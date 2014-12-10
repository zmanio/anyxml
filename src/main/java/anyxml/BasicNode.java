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
import java.io.StringWriter;

import anyxml.XMLTokenizer.Type;

/**
 * The parent class for all nodes in the XML document.
 * 
 * @author digulla
 *
 */
public class BasicNode implements Node
{
    private Token token;
    private Type type;
    private String value;
    
    public BasicNode (Token token)
    {
        if (token == null)
            throw new NullPointerException ("token is null");
        
        this.token = token;
        this.type = token.getType();
    }
    
    public BasicNode (Type type, String text)
    {
        if (type == null)
            throw new NullPointerException ("type is null");
        if (text == null)
            text = "";
        
        this.type = type;
        this.value = text;
    }

    // protected so Attribute and Element can be extended and the
    // type can be set to CUSTOM_ATTRIBUTE/ELEMENT.
    protected void setType (Type type)
    {
        this.type = type;
    }
    
    public Type getType ()
    {
        return type;
    }
    
    /** Get the token (mainly for error handling) */
    public Token getToken ()
    {
        return token;
    }
    
    /** The start offset of the node in the XML source or -1 */
    public int getStartOffset ()
    {
        return token == null ? -1 : token.getStartOffset ();
    }
    
    public int getEndOffset ()
    {
        return token == null ? -1 : token.getEndOffset ();
    }
    
    public String getValue ()
    {
        if (value == null)
            return token.getText ();
            
        return value;
    }
    
    public void setValue (String value)
    {
        this.value = value;
    }
    
    /** Append the content of this node to <code>writer</code> */
    public BasicNode toXML (XMLWriter writer) throws IOException
    {
        writer.write (this, getValue ());
        return this;
    }
    
    /** Convert this node to a string. */
    public String toXML ()
    {
        return toXML (this);
    }
    
    @Override
    public String toString ()
    {
        return toXML ();
    }

    /** Helper method for <code>String toXML()</code> to handle the
     *  <code>IOException</code> that <code>StringWriter</code>
     *  will never throw. 
     */
    public static String toXML (Node n)
    {
        StringWriter buffer = new StringWriter ();
        XMLWriter writer = new XMLWriter (buffer);
        try
        {
            n.toXML (writer);
        }
        catch (IOException e) //@COBEX Note: this code will never be executed
        {
            throw new RuntimeException ("Should not happen", e); //@COBEX
        }
        return buffer.toString ();
    }
    
    public BasicNode createClone ()
    {
        return new BasicNode (type, value);
    }
    
    public BasicNode copy (Node orig)
    {
        BasicNode other = (BasicNode)orig;
        
        this.token = other.token;
        this.type = other.type;
        this.value = other.value;
        
        return this;
    }
    
    public BasicNode copy ()
    {
        BasicNode n = createClone ();
        n.copy (this);
        return n;
    }
}
