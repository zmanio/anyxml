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

public class ProcessingInstruction extends BasicNode implements TextNode
{
    private String target;
    private String whitespace;
    private String text;
    
    public ProcessingInstruction (Token token)
    {
        super (token);
        
        int pos = 2;
        int pos2 = pos;
        String xml = token.getText ();
        while (pos2 < xml.length ())
        {
            char c = xml.charAt (pos2);
            if (Character.isWhitespace (c) || c == '?')
                break;
            pos2 ++;
        }
        
        if (pos == pos2)
            throw new XMLParseException ("Missing target name in processing instruction", token);
        
        target = xml.substring (pos, pos2);
        
        while (pos2 < xml.length ())
        {
            char c = xml.charAt (pos2);
            if (!Character.isWhitespace (c) || c == '?')
                break;
            pos2 ++;
        }
    
        pos = Math.min (pos2, xml.length () - 2);
        pos2 = xml.length () - 2;
        text = xml.substring (pos, pos2);
    }
    
    public ProcessingInstruction (String target)
    {
        this (target, null);
    }
    
    public ProcessingInstruction (String target, String text)
    {
        super (Type.PROCESSING_INSTRUCTION, null);
        
        if (target == null)
            throw new IllegalArgumentException ("target is null");
        if (target.trim ().length () == 0)
            throw new IllegalArgumentException ("target is blank or empty");
        if (text == null)
            text = "";
        if (text.contains ("?>"))
            throw new IllegalArgumentException ("text must not contain '?>'");
        
        this.target = target.trim ();
        this.whitespace = "";
        this.text = text;
        updateValue ();
    }

    protected void updateValue ()
    {
        int pos;
        for (pos = 0; pos < text.length (); pos ++)
        {
            char c = text.charAt (pos);
            if (!Character.isWhitespace (c))
                break;
        }

        if (pos == 0 && text.length () != 0)
            whitespace = " ";
        else
            whitespace = "";
        
        setValue ("<?" + target + whitespace + text + "?>");
    }
    
    public String getTarget ()
    {
        return target;
    }
    
    public void setTarget (String target)
    {
        this.target = target;
        updateValue ();
    }
    
    public String getText ()
    {
        return text;
    }
    
    public ProcessingInstruction setText (String text)
    {
        this.text = text;
        updateValue ();
        return this;
    }
    
    @Override
    public ProcessingInstruction createClone ()
    {
        return new ProcessingInstruction (target, text);
    }
    
    @Override
    public ProcessingInstruction copy (Node orig)
    {
        super.copy (orig);
        
        ProcessingInstruction other = (ProcessingInstruction)orig;
        
        this.target = other.target;
        this.text = other.text;
        this.whitespace = other.whitespace;
        
        return this;
    }
    
    @Override
    public ProcessingInstruction copy ()
    {
        return (ProcessingInstruction)super.copy ();
    }
}