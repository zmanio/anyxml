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

import static org.junit.Assert.*;

import org.junit.Test;

import anyxml.Attribute;
import anyxml.BasicNode;
import anyxml.Node;
import anyxml.NodeFilter;
import anyxml.Text;
import anyxml.XMLUtils;
import anyxml.XMLTokenizer.Type;

public class OtherTest
{
    @Test
    public void testVirginNode () throws Exception
    {
        try
        {
            new BasicNode (null, null);
            fail ("No exception was thrown");
        }
        catch (NullPointerException ex)
        {
            assertEquals ("type is null", ex.getMessage ());
        }
    }
    
    @Test
    public void testVirginNode2 () throws Exception
    {
        try
        {
            new BasicNode (null);
            fail ("No exception was thrown");
        }
        catch (NullPointerException ex)
        {
            assertEquals ("token is null", ex.getMessage ());
        }
    }
    
    @Test
    public void testVirginNode3 () throws Exception
    {
        BasicNode node = new BasicNode (Type.TEXT, null);
        assertEquals (-1, node.getStartOffset ());
        assertEquals (-1, node.getEndOffset ());
        assertEquals ("", node.getValue ());
    }
    
    @Test
    public void testEscapeXML () throws Exception
    {
        assertEquals ("&amp;&lt;&gt;", XMLUtils.escapeXMLText ("&<>"));
        assertEquals ("&lt;&gt;&amp;", XMLUtils.escapeXMLText ("<>&"));
        assertEquals ("&amp;amp;", XMLUtils.escapeXMLText (XMLUtils.escapeXMLText ("&")));
    }
    
    @Test
    public void testUnescape () throws Exception
    {
        assertEquals ("&<>", XMLUtils.unescapeXMLText ("&amp;&lt;&gt;"));
        assertEquals ("<>&", XMLUtils.unescapeXMLText ("&lt;&gt;&amp;"));
    }
    
    @Test
    public void testNodeFilter () throws Exception
    {
        assertTrue (new NodeFilter<Node> ().matches (null));
    }

    @Test
    public void testCDATA () throws Exception
    {
        Text t = new Text (Type.CDATA, "xxx");
        
        assertEquals ("<![CDATA[xxx]]>", t.toXML ());
        assertEquals ("xxx", t.getText ());
        assertEquals ("<![CDATA[xxx]]>", t.getValue ());
    }
    
    @Test
    public void testIsText () throws Exception
    {
        assertTrue (XMLUtils.isTextType (Type.DTD_WHITESPACE));
    }
    
    @Test
    public void testIsAttribute ()
    {
        assertTrue (XMLUtils.isAttribute (new Attribute ("a", "")));
    }
    
    @Test
    public void testIsAttribute2 ()
    {
        assertTrue (XMLUtils.isAttributeType (Type.CUSTOM_ATTRIBUTE));
    }
    
    @Test
    public void testIsAttribute3 ()
    {
        assertFalse (XMLUtils.isAttributeType (Type.CDATA));
    }
    
    @Test
    public void testIsAttribute4 () throws Exception
    {
        Attribute a = new Attribute ("a", "b") {
            {
                setType (Type.CUSTOM_ATTRIBUTE);
            }
        };
        assertTrue (XMLUtils.isAttribute (a));
    }
    
    @Test
    public void testIsElement () throws Exception
    {
        assertTrue (XMLUtils.isElementType (Type.CUSTOM_ELEMENT));
    }
    
}
