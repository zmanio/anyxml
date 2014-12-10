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

import static org.junit.Assert.*;

import org.junit.Test;

import anyxml.Comment;
import anyxml.Token;
import anyxml.XMLParseException;
import anyxml.XMLStringSource;

public class CommentTest
{
    @Test
    public void testNull () throws Exception
    {
        Comment c = new Comment ((String)null);
        assertEquals ("<!-- -->", c.toXML ());
    }
    
    @Test
    public void testEmpty () throws Exception
    {
        Comment c = new Comment ("");
        assertEquals ("<!-- -->", c.toXML ());
    }
    
    @Test
    public void testBlank () throws Exception
    {
        Comment c = new Comment (" ");
        assertEquals ("<!-- -->", c.toXML ());
    }
    
    @Test
    public void testSimple () throws Exception
    {
        Comment c = new Comment ("a");
        assertEquals ("<!--a-->", c.toXML ());
    }
    
    @Test
    public void testSpaces () throws Exception
    {
        String text = " a\n<>b!!-!>\t";
        Comment c = new Comment (text);
        assertEquals ("<!--"+text+"-->", c.toXML ());
        assertEquals (text, c.getText ());
    }
    
    @Test
    public void testMinusMinus () throws Exception
    {
        try
        {
            new Comment ("--");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("The text of a comment must not contain '--': [--]", e.getMessage ());
        }
    }
    
    @Test
    public void testToken () throws Exception
    {
        Token t = new Token ();
        t.setSource (new XMLStringSource ("<!-- Comment -->"));
        t.setStartOffset (0);
        t.setEndOffset (t.getSource ().length ());
        Comment c = new Comment (t);
        assertEquals (" Comment ", c.getText ());
    }
}
