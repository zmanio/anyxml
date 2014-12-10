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
import anyxml.Comment;
import anyxml.Document;
import anyxml.Element;
import anyxml.Node;
import anyxml.ProcessingInstruction;
import anyxml.Text;
import anyxml.XMLInputStreamReader;
import anyxml.XMLParseException;
import anyxml.XMLTokenizer.Type;

public class DocumentTest
{
    @Test
    public void testDocument () throws Exception
    {
        Document doc = new Document ();
        assertNull (doc.getRootElement ());
        assertEquals ("", doc.toXML ());
    }
    
    @Test
    public void testDocument2 () throws Exception
    {
        Document doc = new Document ();
        Element root = new Element ("root");
        doc.setRootNode (root);
        assertEquals ("<root/>", doc.toXML ());
    }
    
    @Test
    public void testDocument3 () throws Exception
    {
        Document doc = new Document ();
        Document doc2 = new Document ();
        Element root = new Element ("root");
        
        doc.setRootNode (root);
        assertEquals (root, doc.getRootElement ());
        assertEquals (root, doc.getNode (0));
        assertEquals (doc, root.getParent ());
        
        doc2.setRootNode (root);
        assertEquals ("", doc.toXML ());
        assertFalse (doc.hasNodes ());
        assertNull (doc.getRootElement ());
        assertEquals ("<root/>", doc2.toXML ());
        assertEquals (root, doc2.getRootElement ());
        assertEquals (root, doc2.getNode (0));
        assertEquals (doc2, root.getParent ());
    }
    
    @Test
    public void testDocument4 () throws Exception
    {
        Document doc = new Document ();
        Element root = new Element ("root");
        Element root2 = new Element ("root2");
        
        doc.setRootNode (root);
        assertEquals (root, doc.getRootElement ());
        assertEquals (root, doc.getNode (0));
        assertEquals (doc, root.getParent ());
        
        doc.setRootNode (root2);
        assertEquals ("<root2/>", doc.toXML ());
        assertTrue (doc.hasNodes ());
        assertEquals (root2, doc.getRootElement ());
        assertNull (root.getParent ());
        assertEquals (doc, root2.getParent ());
    }
    
    @Test
    public void testDocument5 () throws Exception
    {
        Document doc = new Document ();
        Element root = new Element ("root");
        Element root2 = new Element ("root");
        doc.setRootNode (root);
        
        assertFalse (doc.removeNode (root2));
    }
    
    @Test
    public void testDocument6 () throws Exception
    {
        Document doc = new Document ().addNodes (new Text (" "), new Text (" "));
        try
        {
            doc.addNodes (1, new Attribute ("x", "1"));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: ATTRIBUTE node is not allowed here", e.getMessage ());
        }
    }
    
    @Test
    public void testDocument7 () throws Exception
    {
        Document doc = new Document ();
        List<Node> l = new ArrayList<Node> ();
        Element root = new Element ("root");
        l.add (root);
        doc.addNodes (l);
        
        assertEquals (root, doc.getRootElement ());
        assertEquals (doc, root.getParent ());
    }

    @Test
    public void testDocument8 () throws Exception
    {
        Document doc = new Document ();
        Element root = new Element ("root");
        doc.setRootNode (root);

        assertEquals (root, doc.removeNode (0));
        assertNull (doc.getRootElement ());
        assertNull (root.getParent ());
    }

    @Test
    public void testDocument9 () throws Exception
    {
        Document doc = new Document ();
        Element root = new Element ("root");
        doc.setRootNode (root);

        assertTrue (doc.removeNode (root));
        assertNull (doc.getRootElement ());
        assertNull (root.getParent ());
    }
    
    @Test
    public void testDocument10 () throws Exception
    {
        Document doc = new Document ();
        doc.setVersion ("1.0");
        Node n = doc.getNode (0);
        assertEquals (Type.PROCESSING_INSTRUCTION, n.getType ());
        doc.removeNode (0);
    }
    
    @Test
    public void testDocument11 () throws Exception
    {
        Document doc = new Document ();
        doc.setVersion ("1.0");
        Node n = doc.getNode (0);
        assertEquals (Type.PROCESSING_INSTRUCTION, n.getType ());
        doc.removeNode (n);
    }

    @Test
    public void testDocument12 () throws Exception
    {
        Document doc = new Document ();
        assertFalse (doc.removeNode (null));
    }

    @Test
    public void testDocument13 () throws Exception
    {
        Document doc = new Document ();
        doc.parseXMLDeclaration ();
    }
    
    @Test
    public void testAddNullNode () throws Exception
    {
        try
        {
            new Document ().addNode (null);
            fail ("No exception was thrown");
        }
        catch (NullPointerException ex)
        {
            assertEquals ("node is null", ex.getMessage ());
        }
    }
    
    /**
     * Show various ways to build an XML document from scratch
     */
    @Test
    public void testBuildDocument () throws Exception
    {
        Document doc = new Document (
            new ProcessingInstruction ("xml", "version=\"1.0\" encoding=\"utf-8\""),
            new Text ("\n\n"),
            new Comment (" Comment "),
            new Text ("\n")
        );
        
        Element root = new Element ("root")
            .addNode (new Text ("\n    "))
            .addNode (new Comment (" comment "))
            .addNode (new Text ("\n    "))
            ;
        doc.addNode (root);
        
        root.addNodes (
            new Element ("e")
            .addNodes (
                new Text ("\n\ttext<>\n\t"),
                new Text (Type.CDATA, "text<></e>"),
                new Text ("\n    ")
            ),
            new Text ("\n    "),
            new Element ("empty"),
            new Text ("\n    "),
            new Element ("empty "),
            new Text ("\n    ")
        );
        
        new Element (root, "a")
        .addAttributes (
            new Attribute ("x", "1", '\''),
            new Attribute ("y", "2"),
            new Attribute ("f1", "\""),
            new Attribute ("f2", "'")
        )
        .setPostSpace (" ");
        
        List<Node> l = new ArrayList<Node> ();
        l.add (new Text ("\n    "));
        Element e = new Element ("a");
        l.add (e);
        
        root.addNodes (l);

        e.addAttribute (new Attribute ("x", "1", '\''));
        e.addAttribute (new Attribute ("y", "2", '"'));
        e.addAttribute (new Attribute ("f1", "\""));
        e.addAttribute (new Attribute ("f2", "\"'"));
        e.setPostSpace (" ");
        
        e.addNode (new Text ("b"));
        
        e.setBeginName (" a");
        e.setEndName (" a ");

        root.addNode (new Text ("\n"));
        doc.addNode (new Text ("\n"));
        
        assertEquals (XMLTokenizerTest.XML, doc.toXML ());
    }
    
    @Test
    public void testIllegalText () throws Exception
    {
        Document doc = new Document ();
        doc.addNode (new Element ("e"));
        doc.setEncoding (XMLInputStreamReader.ENCODING_ISO_Latin_1);
        
        try
        {
            doc.addNode (2, new Text ("xxx"));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 2, column 1: TEXT node is not allowed here", e.getMessage ());
        }
    }

}
