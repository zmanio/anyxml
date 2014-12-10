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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import anyxml.Attribute;
import anyxml.Document;
import anyxml.Element;
import anyxml.Node;
import anyxml.ProcessingInstruction;
import anyxml.Text;
import anyxml.XMLParseException;
import anyxml.XMLTokenizer.Type;

public class ElementTest
{
    @Test
    public void testElement () throws Exception
    {
        Element e = new Element (" xxx ");
        assertEquals ("< xxx />", e.toXML ());
    }
    
    @Test
    public void testSetName () throws Exception
    {
        Element e = new Element (" xxx ");
        e.setName (e.getName ().trim ());
        assertEquals ("<xxx/>", e.toXML ());
    }
    
    @Test
    public void testHasChildren () throws Exception
    {
        Element e = new Element ("root");
        assertFalse (e.hasNodes ());
        assertFalse (e.hasChildren ());
        
        e.addNode (new Text (" "));
        assertTrue (e.hasNodes ());
        assertFalse (e.hasChildren ());
        
        e.addNode (new Element ("e"));
        assertTrue (e.hasNodes ());
        assertTrue (e.hasChildren ());
    }
    
    @Test
    public void testAddIllegalNode () throws Exception
    {
        Element e = new Element ("a");
        Attribute a = new Attribute ("x", "1");
        try
        {
            e.addNode (a);
            fail ("No exception was thrown");
        }
        catch (XMLParseException ex)
        {
            assertEquals ("Line 1, column 1: The node ATTRIBUTE is not allowed here", ex.getMessage ());
        }
    }
    @Test
    public void testClearText () throws Exception
    {
        Element e = new Element ("a").setPostSpace (" ");
        e.clearText ();
        assertEquals ("<a />", e.toXML ());
    }
    
    @Test
    public void testClearText2 () throws Exception
    {
        Element e = new Element ("a").addNode (new Text (" "));
        e.clearText ();
        assertEquals ("<a/>", e.toXML ());
    }
    
    @Test
    public void testClearText2b () throws Exception
    {
        Element e = new Element ("a")
        .setCompactEmpty (false)
        .addNode (new Text (" "));
        e.clearText ();
        assertEquals ("<a></a>", e.toXML ());
    }
    
    @Test
    public void testClearText3 () throws Exception
    {
        Element e = new Element ("a")
        .addNodes (
            new Text (" "),
            new Element ("b").addNode (new Text ("a")),
            new Text (" ")
        );
        e.clearText ();
        assertEquals ("<a><b/></a>", e.toXML ());
    }
    
    @Test
    public void testClearText4 () throws Exception
    {
        Element e = new Element ("a")
        .addNodes (
            new Text (" "),
            new Element ("b").addNode (new Text ("a")),
            new Text (" ")
        );
        e.getChild ("b").clearText ();
        assertEquals ("<a> <b/> </a>", e.toXML ());
    }
    
    @Test
    public void testClearText5 () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (
            new Text("1"),
            new Text(Type.CDATA, "2"),
            new ProcessingInstruction ("dummy")
        );
        
        e.clearText ();
        assertEquals ("", e.getText ());
    }

    @Test
    public void testClearNodes () throws Exception
    {
        Element e = new Element ("e");
        
        e.clearNodes ();
        assertEquals ("<e/>", e.toXML ());
    }
    
    @Test
    @SuppressWarnings("deprecation")
    public void testClearChildNodes () throws Exception
    {
        Element e = new Element ("e");
        
        e.clearChildNodes ();
        assertEquals ("<e/>", e.toXML ());
    }
    
    @Test
    public void testClearChildren () throws Exception
    {
        Element e = new Element ("e");
        
        e.clearChildren ();
        assertEquals ("<e/>", e.toXML ());
    }
    
    @Test
    public void testClearChildren2 () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (new Text ("a"), new Element ("b"));
        
        e.clearChildren ();
        assertEquals ("<e>a</e>", e.toXML ());
    }

    @Test
    public void testGetText () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (
            new Text ("1"),
            new Element ("a")
            .addNodes (new Text ("2")),
            new Text ("3")
        );
        
        assertEquals ("123", e.getText ());
    }

    @Test
    public void testGetText2 () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (
            new ProcessingInstruction ("dummy")
        );
        
        assertEquals ("", e.getText ());
    }
    
    @Test
    public void testGetParentElement () throws Exception
    {
        Element e = new Element ("e");
        assertNull (e.getParentElement ());
    }
    
    @Test
    public void testElementNullName () throws Exception
    {
        try
        {
            new Element ((String)null);
            fail ("No exception was thrown");
        }
        catch (NullPointerException ex)
        {
            assertEquals ("name is null", ex.getMessage ());
        }
    }
    
    @Test
    public void testElementEmptyName () throws Exception
    {
        try
        {
            new Element ("");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals ("name is blank", ex.getMessage ());
        }
    }
    
    @Test
    public void testElementEmptyName2 () throws Exception
    {
        try
        {
            new Element (" ");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals ("name is blank", ex.getMessage ());
        }
    }
    
    @Test
    @SuppressWarnings("deprecation")
    public void testNodePath () throws Exception
    {
        Element e = new Element ("e");
        assertEquals ("e", e.getNodePath ());
    }
    
    @Test
    public void testChildPath () throws Exception
    {
        Element e = new Element ("e");
        assertEquals ("e", e.getChildPath ());
    }
    
    @Test
    public void testChildPath2 () throws Exception
    {
        Element e = new Element ("e");
        Element e2 = new Element ("e2");
        e.addNode (e2);
        assertEquals ("e/e2", e2.getChildPath ());
    }

    @Test
    public void testChildPath3 () throws Exception
    {
        Document doc = new Document ();
        Element e = new Element ("e");
        doc.addNode (e);
        assertEquals ("/e", e.getChildPath ());
    }
    
    @Test
    public void testChildPath4 () throws Exception
    {
        Document doc = new Document (
            new Element ("e")
            .addNode (new Element ("e2"))
        );
        Element e = doc.getRootElement ().getChild (0);
        assertEquals ("/e/e2", e.getChildPath ());
    }
    
    @Test
    public void testChildPath5 () throws Exception
    {
        Document doc = new Document (
            new Element ("e")
            .addNodes (
                new Element ("e2"),
                new Element ("e2"),
                new Element ("e2")
            )
        );
        Element e = doc.getRootElement ().getChild (2);
        assertEquals ("/e/e2[2]", e.getChildPath ());
    }
    
    @Test
    public void testGetChild () throws Exception
    {
        Element e = new Element ("e");
        assertEquals (e, e.getChild (""));
    }
    
    @Test
    public void testGetChild2 () throws Exception
    {
        Element e = new Element ("e");
        assertEquals (e, e.getChild ("."));
    }
    
    @Test
    public void testGetChild3 () throws Exception
    {
        Element e = new Element ("e");
        assertNull (e.getChild ("/e"));
    }
    
    @Test
    public void testGetChild4 () throws Exception
    {
        Element e = new Element ("e");
        assertNull (e.getChild ("x"));
    }
    
    @Test
    public void testGetChild5 () throws Exception
    {
        Element e = new Element ("e");
        Element e2 = new Element ("e2");
        e.addNode (e2);
        assertEquals (e2, e.getChild ("e2"));
    }
    
    @Test
    public void testGetChild6 () throws Exception
    {
        Element e = new Element ("e");
        Element e2 = new Element ("e2");
        e.addNode (e2);
        Element e3 = new Element ("e3");
        e2.addNode (e3);
        assertEquals (e3, e.getChild ("e2/e3"));
    }
    
    @Test
    public void testGetChild7 () throws Exception
    {
        Element e = new Element ("e");
        Element e2 = new Element ("e2");
        e.addNode (e2);
        Element e3 = new Element ("e3");
        e2.addNode (e3);
        assertEquals (e3, e.getChild ("e2[0]/e3"));
    }
    
    @Test
    public void testGetChild8 () throws Exception
    {
        Element e = new Element ("e");
        Element e2 = new Element ("e2");
        e.addNode (e2);
        Element e3_1 = new Element ("e3");
        e2.addNode (e3_1);
        Element e3_2 = new Element ("e3");
        e2.addNode (e3_2);
        assertEquals (e3_2, e.getChild ("e2/e3[1]"));
    }
    
    @Test
    public void testGetChild9 () throws Exception
    {
        Document doc = new Document ();
        Element e = new Element ("e");
        doc.addNode (e);
        Element e2 = new Element ("e2");
        e.addNode (e2);
        Element e3_1 = new Element ("e3");
        e2.addNode (e3_1);
        Element e3_2 = new Element ("e3");
        e2.addNode (e3_2);
        assertEquals (e3_2, e.getChild ("/e/e2/e3[1]"));
    }
    
    @Test
    public void testGetChild10 () throws Exception
    {
        Document doc = new Document ();
        Element e = new Element ("e");
        doc.addNode (e);
        Element e2 = new Element ("e2");
        e.addNode (e2);
        Element e3_1 = new Element ("e3");
        e2.addNode (e3_1);
        Element e3_2 = new Element ("e3");
        e2.addNode (e3_2);
        assertEquals (e3_2, e.getChild ("/e/e2/e3[1]"));
    }
    
    @Test
    public void testGetChild11 () throws Exception
    {
        Document doc = new Document ();
        Element e = new Element ("e");
        doc.addNode (e);
        Element e2 = new Element ("e2");
        e.addNode (e2);
        Element e3_1 = new Element ("e3");
        e2.addNode (e3_1);
        Element e3_2 = new Element ("e3");
        e2.addNode (e3_2);
        assertEquals (e, e3_2.getChild ("/e"));
    }
    
    @Test
    public void testGetChild12 () throws Exception
    {
        Element e = new Element ("e");
        try
        {
            e.getChild (0);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child 0, node has only 0 children", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetChild13 () throws Exception
    {
        Element e = new Element ("e");
        try
        {
            e.getChild (-1);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child -1, node has only 0 children", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetChild14 () throws Exception
    {
        Element e = new Element ("e").addNode (new Element ("e2"));
        try
        {
            e.getChild (2);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child 2, node has only 1 child", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetChild15 () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (
            new Element ("e2"),
            new Element ("e2")
        );
        try
        {
            e.getChild (3);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child 3, node has only 2 children", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetChild16 () throws Exception
    {
        Element e = new Element ("e").addNode (new Element ("e2"));
        assertEquals (e.getNode (0), e.getChild (0));
    }
    
    @Test
    public void testGetChild17 () throws Exception
    {
        Document doc = new Document ();
        assertNull (doc.getChild (null));
    }
    
    @Test
    public void testGetChild18 () throws Exception
    {
        Document doc = new Document (new Element ("e"));
        assertNull (doc.getChild ("a"));
    }
    
    @Test
    public void testGetChild19 () throws Exception
    {
        Document doc = new Document (new Element ("e").addNode (new Element ("e2")));
        assertNull (doc.getChild ("a/e2"));
    }
    
    @Test
    public void testGetChild20 () throws Exception
    {
        Document doc = new Document (
            new Element ("e")
            .addNodes (
                new Text (" "),
                new Element ("e2"),
                new Text (" "),
                new Element ("e2"),
                new Text (" ")
            )
        );
        Element e = doc.getChild ("e/e2[1]");
        assertNotNull (e);
        assertEquals ("/e/e2[1]", e.getChildPath ());
    }
    
    @Test
    public void testGetChild21 () throws Exception
    {
        Document doc = new Document (
            new Element ("e")
            .addNodes (
                new Text (" "),
                new Element ("e2"),
                new Text (" "),
                new Element ("e2"),
                new Text (" ")
            )
        );
        Element e = doc.getChild ("e").getChild (1);
        assertNotNull (e);
        assertEquals ("/e/e2[1]", e.getChildPath ());
    }
    
    @Test
    public void testIndexOf () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (
            new Text (" "),
            new Element ("e2")
        );
        assertEquals (-1, e.nodeIndexOf (null));
        assertEquals (-1, e.nodeIndexOf (new Text (" ")));
        assertEquals (0, e.nodeIndexOf (e.getNode (0)));
        assertEquals (1, e.nodeIndexOf (e.getNode (1)));
    }

    @Test
    public void testIndexOf2 () throws Exception
    {
        Element e = new Element ("e");
        assertEquals (-1, e.nodeIndexOf (new Text (" ")));
    }

    @Test
    public void testIndexOf3 () throws Exception
    {
        Element e = new Element ("e");
        assertEquals (-1, e.childIndexOf (new Element ("a")));
    }
    
    @Test
    public void testGetNode () throws Exception
    {
        Element e = new Element ("e");
        try
        {
            e.getNode (0);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child 0, node has only 0 child nodes", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetNode2 () throws Exception
    {
        Element e = new Element ("e");
        try
        {
            e.getNode (-1);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child -1, node has only 0 child nodes", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetNode3 () throws Exception
    {
        Element e = new Element ("e").addNode (new Element ("e2"));
        try
        {
            e.getNode (2);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child 2, node has only 1 child nodes", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetNode4 () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (
            new Element ("e2"),
            new Element ("e2")
        );
        try
        {
            e.getNode (3);
            fail ("No exception was thrown");
        }
        catch (IndexOutOfBoundsException ex)
        {
            assertEquals ("Cannot return child 3, node has only 2 child nodes", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetNode5 () throws Exception
    {
        Element e = new Element ("e")
        .addNodes (
            new Element ("e2"),
            new Element ("e2")
        );

        assertNotNull (e.getNode (1));
    }
    
    @Test
    public void testAddNodesCollection () throws Exception
    {
        Element parent = new Element ("parent");
        List<Node> l = new ArrayList<Node> ();
        l.add (new Element ("child2"));
        parent.addNodes (0, l);
        
        l.clear ();
        l.add (new Element ("child1"));
        parent.addNodes (0, l);
        
        assertEquals ("<parent><child1/><child2/></parent>", parent.toXML ());
    }
    
    
    @Test
    public void testCopy () throws Exception
    {
        Element e = new Element ("e")
        .setAttribute ("a", "v")
        .addNodes (
            new Element ("e2"),
            new Element ("e2")
        );
        
        Element copy = e.copy ();
        assertEquals (e.toXML (), copy.toXML ());
    }
}
