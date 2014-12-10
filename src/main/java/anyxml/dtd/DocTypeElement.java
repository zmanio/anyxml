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
package anyxml.dtd;

import java.util.List;

import anyxml.BasicNode;
import anyxml.Token;
import anyxml.XMLTokenizer.Type;

public class DocTypeElement extends BasicNode
{
    private String name;
    private String content;
    private List<DocTypeAttributeList> attLists;
    
    public DocTypeElement (Token token, String name, String content)
    {
        super (token);
        this.name = name.trim ();
        this.content = content.trim ();
    }
    
    public DocTypeElement (Type type, String name, String content)
    {
        super (type, toXML (name, content));
        this.name = name.trim ();
        this.content = content.trim ();
    }
    
    public String getName ()
    {
        return name;
    }
    
    public void setName (String name)
    {
        this.name = name;
        updateValue ();
        this.name = name.trim ();
    }
    
    public String getContent ()
    {
        return content;
    }
    
    public void setContent (String content)
    {
        this.content = content;
        updateValue ();
        this.content = content.trim ();
    }
    
    protected void updateValue ()
    {
        setValue (toXML (name, content));
    }
    
    public static String toXML (String name, String content)
    {
        StringBuilder buffer = new StringBuilder ();
        
        buffer.append ("<!ELEMENT");
        if (!Character.isWhitespace (name.charAt (0)))
            buffer.append (" ");
        buffer.append (name);
        if (!Character.isWhitespace (name.charAt (name.length () - 1)) && !Character.isWhitespace (content.charAt (0)))
            buffer.append (" ");
        buffer.append (content);
        buffer.append ('>');
        
        return buffer.toString ();
    }

    public void setAttLists (List<DocTypeAttributeList> value)
    {
        this.attLists = value;
    }
    
    public List<DocTypeAttributeList> getAttLists ()
    {
        return attLists;
    }
}
