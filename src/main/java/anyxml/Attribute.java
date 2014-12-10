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

import anyxml.XMLTokenizer.Type;

/**
 * This class represents an attribute of an <code>Element</code>
 * 
 * @author digulla
 * @see anyxml.Element
 */
public class Attribute extends BasicNode
{
    private Namespace namespace;
    /** Space before the name */
    private String preSpace = " ";
    private String name;
    /** Space around the equals sign (incl. the sign) */
    private String equalsSpace = "=";
    private String rawValue;
    private String value;
    private char quoteChar;
    
    /** Create an attribute from a Token.
     * 
     *  <p>The token must include the space before the name and end with the closing quote.
     *    
     * @param token
     */
    public Attribute (Token token)
    {
        super (token);
        
        String s = token.getText ();
        int N = s.length ();
        int pos = 0;
        int start = pos;
        
        while (Character.isWhitespace (s.charAt (pos)) && pos < N)
            pos ++;
        
        if (pos != start)
            this.preSpace = s.substring(start, pos);

        start = pos;
        while (pos < N)
        {
            char c = s.charAt (pos);
            if (Character.isWhitespace (c) || c == '=')
                break;
            
            pos ++;
        }
        
        this.name = s.substring (start, pos);
        
        start = pos;
        while (pos < N)
        {
            char c = s.charAt (pos);
            if (!(Character.isWhitespace (c) || c == '=') )
                break;
            
            pos ++;
        }
        
        this.equalsSpace = s.substring (start, pos);
        
        this.quoteChar = s.charAt (pos);
        this.rawValue = s.substring (pos + 1, N - 1);
        this.value = XMLUtils.unescapeXMLAttributeValue (rawValue);
        
        setNamespace (null);
    }
    
    /** Create an attribute with a certain name and value.
     *
     *  <p>The quote to use is determined automatically depending on the content of the value
     */
    public Attribute (String name, String value)
    {
        this (name, value, null, '\0');
    }

    /** Create an attribute with a certain name and value.
     *
     *  <p>The quote to use is determined automatically depending on the content of the value
     */
    public Attribute (String name, String value, Namespace namespace)
    {
        this (name, value, namespace, '\0');
    }
    
    /** Create an attribute with a certain name and value plus a preference
     *  for the quote character that should be used.
     *  
     *  <p>If the quote character exists in the value, it is ignored.
     */
    public Attribute (String name, String value, char quoteChar)
    {
        this (name, value, null, quoteChar);
    }
    
    /** Create an attribute with a certain name and value plus a preference
     *  for the quote character that should be used.
     *  
     *  <p>If the quote character exists in the value, it is ignored.
     */
    public Attribute (String name, String value, Namespace namespace, char quoteChar)
    {
        super (Type.ATTRIBUTE, null);
        
        if (name == null)
            throw new IllegalArgumentException ("name is null");
        if (name.trim ().length () == 0)
            throw new IllegalArgumentException ("name is blank");
        if (value == null)
            throw new IllegalArgumentException ("value is null");
        
        this.name = name;
        this.value = value;
        setNamespace (namespace);
        this.quoteChar = checkQuoteChar (value, quoteChar);
    }

    /** Return the name of the attribute */
    public String getName ()
    {
        return name;
    }
    
    public void setName (String name)
    {
        this.name = name;
    }
    
    /** Return the value of the attribute */
    public String getValue ()
    {
        return value;
    }
    
    public void setValue (String value)
    {
        if (value == null)
            throw new IllegalArgumentException ("value is null");
        
        this.value = value;
        this.rawValue = null;
    }
    
    public Namespace getNamespace ()
    {
        return namespace;
    }
    
    public void setNamespace (Namespace namespace)
    {
        if (namespace == null)
            namespace = Namespace.NO_NAMESPACE;
        
        this.namespace = namespace;
    }
    
    public int getQuoteChar ()
    {
        return quoteChar;
    }
    
    public Attribute setQuoteChar (char quoteChar)
    {
        this.quoteChar = checkQuoteChar (getValue (), quoteChar);
        return this;
    }

    public String getPreSpace ()
    {
        return preSpace;
    }
    
    public Attribute setPreSpace (String preSpace)
    {
        if (preSpace == null)
            preSpace = " ";
        else if (preSpace.trim ().length () != 0)
            throw new IllegalArgumentException ("Space prefix must not contain anything besides whitespace: "+TextUtils.escapeJavaString(preSpace));
        
        this.preSpace = preSpace;
        return this;
    }
    
    public String getEqualsSpace ()
    {
        return equalsSpace;
    }
    
    public Attribute setEqualsSpace (String equalsSpace)
    {
        if (equalsSpace == null)
            equalsSpace = "=";
        else if (!"=".equals (equalsSpace.trim ()))
            throw new IllegalArgumentException ("Space around equals sign must not contain anything besides whitespace: "+TextUtils.escapeJavaString(equalsSpace));
        
        this.equalsSpace = equalsSpace;
        return this;
    }
    
    public static char checkQuoteChar (String value, char quoteChar)
    {
        if (quoteChar == '\0')
        {
            if (!value.contains ("\""))
                quoteChar = '"';
            else if (!value.contains ("'"))
                quoteChar = '\'';
            else
                quoteChar = '"';
        }
        else if (quoteChar != '"' && quoteChar != '\'')
        {
            char[] buffer = new char[1];
            buffer[0] = (char)quoteChar;
            String s = new String (buffer);
            throw new XMLParseException ("Illegal quote charater: "+TextUtils.escapeJavaString (s)+" ("+(int)quoteChar+")");
        }
        return quoteChar;
    }
    
    @Override
    public BasicNode toXML (XMLWriter writer) throws IOException
    {
        writer.write (this, getPreSpace ());
        String prefix = getNamespace ().getPrefix ();
        if (prefix.length () != 0)
        {
            writer.write (this, prefix);
            writer.write (this, ":");
        }
        writer.write (this, getName ());
        writer.write (this, getEqualsSpace ());
        
        char[] buffer = new char[1];
        buffer[0] = (char)quoteChar;
        String s = new String (buffer);
        writer.write (this, s);
        if (rawValue != null)
            writer.write (this, rawValue);
        else
            writer.writeAttributeValue (this, getValue (), quoteChar);
        writer.write (this, s);
        
        return this;
    }
    
    @Override
    public Attribute createClone ()
    {
        return new Attribute (name, value);
    }
    
    @Override
    public Attribute copy (Node orig)
    {
        super.copy (orig);
        
        Attribute other = (Attribute)orig;
        
        this.equalsSpace = other.equalsSpace;
        this.name = other.name;
        this.preSpace = other.preSpace;
        this.quoteChar = other.quoteChar;
        this.value = other.value;
        this.rawValue = other.rawValue;
        
        return this;
    }
    
    @Override
    public Attribute copy ()
    {
        return (Attribute)super.copy ();
    }
}
