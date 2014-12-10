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

import anyxml.XMLTokenizer.Type;

public class Comment extends BasicNode implements TextNode
{
    private String text;
    
    public Comment (Token token)
    {
        super (token);
    }
    
    public Comment (String text)
    {
        super (Type.COMMENT, null);
        setText (text);
    }
    
    public Comment setText (String text)
    {
        if (text == null || text.length () == 0)
            text = " ";
        if (text.contains ("--"))
            throw new XMLParseException ("The text of a comment must not contain '--': ["+text+"]");
        
        setValue ("<!--" + text + "-->");
        this.text = text;
        return this;
    }

    public String getText ()
    {
        if (text == null)
        {
            text = getValue ();
            text = text.substring (4, text.length () - 3);
        }
        
        return text;
    }

    @Override
    public Comment createClone ()
    {
        return new Comment (text);
    }
    
    @Override
    public Comment copy ()
    {
        return (Comment)super.copy ();
    }
}
