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

import anyxml.XMLTokenizer.Type;

public class Text extends BasicNode implements TextNode
{
    private String text;
    
    public Text (Token token)
    {
        super (token);
    }
    
    public Text (Type type, String text)
    {
        super (type, type == Type.CDATA ? "<![CDATA[" + text + "]]>" : XMLUtils.escapeXMLText (text));
        this.text = text;
    }
    
    public Text (String text)
    {
        this (Type.TEXT, text);
        this.text = text;
    }

    public boolean isCDATA ()
    {
        return getType () == Type.CDATA;
    }
    
    public String getText ()
    {
        if (text == null)
        {
            String s = getValue ();
            if (isCDATA ())
                s = s.substring (9, s.length () - 3);
            text = s;
        }
        return text;
    }
    
    public Text setText (String text)
    {
        setValue (getType () == Type.CDATA ? "<![CDATA[" + text + "]]>" : XMLUtils.escapeXMLText (text));
        this.text = text;
        return this;
    }
    
    /** Get the contents of this text node without all whitespace before and after */ 
    public String getTrimmedText ()
    {
        return getText ().trim ();
    }
    
    /** Get the contents of this text node without all whitespace before and after
     * and with all whitespace between the words in the node reduced to a single space. */
    public String getNormalizedText ()
    {
        return getTrimmedText ().replaceAll ("\\s+", " ");
    }

    /** True, if this text node contains only whitespace */
    public boolean isWhitespace ()
    {
        String s = getText ();
        for (int i=0; i<s.length (); i++)
        {
            if (!Character.isWhitespace (s.charAt (i)))
                return false;
        }
        
        return true;
    }

    @Override
    public Text createClone ()
    {
        return new Text (getType (), null);
    }
    
    @Override
    public BasicNode copy (Node orig)
    {
        super.copy (orig);
        
        Text other = (Text)orig;
        
        this.text = other.text;
        
        return this;
    }
    
    @Override
    public Text copy ()
    {
        return (Text)super.copy ();
    }
}
