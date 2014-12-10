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

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import anyxml.Attribute;
import anyxml.Document;
import anyxml.Element;
import anyxml.Entity;
import anyxml.HTMLEntityResolver;
import anyxml.Node;
import anyxml.ProcessingInstruction;
import anyxml.Text;
import anyxml.XMLIOSource;
import anyxml.XMLInputStreamReader;
import anyxml.XMLParseException;
import anyxml.XMLParser;
import anyxml.XMLStringSource;
import anyxml.XMLTokenizer.Type;

public class XMLParserTest
{
    public static final String POM_XML = 
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
        "\r\n" + 
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\r\n" + 
        "  <modelVersion>4.0.0</modelVersion>\r\n" + 
        "\r\n" + 
        "  <!-- Ignore this <parent>\r\n" + 
        "    <groupId>org.codehaus.mojo</groupId>\r\n" + 
        "    <artifactId>mojo</artifactId>\r\n" + 
        "    <version>15</version>\r\n" + 
        "  </parent> -->\r\n" + 
        "  <parent>\r\n" + 
        "    <groupId>org.codehaus.mojo</groupId>\r\n" + 
        "    <artifactId>mojo</artifactId>\r\n" + 
        "    <version>16</version>\r\n" + 
        "  </parent>\r\n" + 
        "\r\n" + 
        "  <groupId>org.codehaus.mojo</groupId>\r\n" + 
        "  <artifactId>versions-maven-plugin</artifactId>\r\n" + 
        "  <version>1.0-SNAPSHOT</version>\r\n" + 
        "  <packaging>maven-plugin</packaging>\r\n" + 
        "</project>\r\n" + 
        "";
        
    @Test
    public void testRoundtrip () throws Exception
    {
        setUp (XMLTokenizerTest.XML);

        assertEquals (XMLTokenizerTest.XML, doc.toXML ());
    }
    
    @Test
    public void testCopy () throws Exception
    {
        setUp (XMLTokenizerTest.XML);
        
        assertEquals (doc.toXML (), doc.copy().toXML());
    }
    
    @Test
    public void testBOM () throws Exception
    {
        XMLParser parser = new XMLParser ();
        byte[] data = XMLTokenizerTest.XML.getBytes ("UnicodeBig");
        assertEquals (XMLTokenizerTest.XML.length () * 2 + 2, data.length);
        assertEquals (-2, data[0]);
        assertEquals (-1, data[1]);
        ByteArrayInputStream in = new ByteArrayInputStream (data);
        XMLIOSource source = new XMLIOSource (in);
        assertEquals (XMLTokenizerTest.XML.length (), source.length ());
        assertEquals ('<', source.charAt (0));
        doc = parser.parse (source);
    }
    
    @Test
    public void testNavigation () throws Exception
    {
        //System.out.println (XML);
        setUp (XMLTokenizerTest.XML);
        
        Element root = doc.getRootElement ();
        assertNotNull (root);
        
        Element e = root.getChild ("e");
        assertNotNull (e);
        assertEquals ("e", e.getName ());
    }
    
    @Test
    public void testNavigation2 () throws Exception
    {
        setUp (XMLTokenizerTest.XML);
        
        Element root = doc.getRootElement ();
        List<Element> l = root.getChildren ("e");
        assertNotNull (l);
        assertEquals (1, l.size ());
        assertEquals (root.getChild ("e"), l.get (0));
    }
    
    @Test
    public void testNavigation3 () throws Exception
    {
        setUp (XMLTokenizerTest.XML);
        
        Element root = doc.getRootElement ();
        List<Element> l = root.getChildren ("a");
        assertNotNull (l);
        assertEquals (2, l.size ());
    }
    
    @Test
    public void testNavigation4 () throws Exception
    {
        setUp (XMLTokenizerTest.XML);
        
        Element root = doc.getRootElement ();
        List<Element> l = root.getChildren ("a");
        Element a = l.get (0);
        Attribute attr = a.getAttribute ("x");
        assertEquals ("x", attr.getName ());
        assertEquals ("1", attr.getValue ());
    }
    
    @Test
    public void testDocumentType () throws Exception
    {
        setUp (XMLTokenizerTest.XML);
        
        assertEquals (Type.DOCUMENT, doc.getType ());
    }
    
    @Test
    public void testElementType () throws Exception
    {
        setUp (XMLTokenizerTest.XML);
        
        assertEquals (Type.ELEMENT, doc.getRootElement ().getType ());
    }
    
    @Test
    public void testElementChild () throws Exception
    {
        setUp ("<a/>");
        
        assertNull (doc.getRootElement ().getChild ("xxx"));
    }
    
    @Test
    public void testCreateElement () throws Exception
    {
        Element e = new Element ("e");
        assertEquals (-1, e.getStartOffset ());
        assertEquals (-1, e.getEndOffset ());
    }
    
    @Test
    public void testRemove () throws Exception
    {
        setUp (XMLTokenizerTest.XML);
        
        Node n;
        n = doc.removeNode (2);
        assertEquals (Type.COMMENT, n.getType ());
        n = doc.getNode (2);
        assertTrue (doc.removeNode (n));
        
        Element root = doc.getRootElement ();
        root.clearNodes ();
        
        root
        .addNode (new Text ("a"))
        .addNode (new Text ("c"))
        .addNode (1, new Text ("b"))
        ;
        
        assertEquals (
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
            "\n" + 
            "<root>abc</root>\n" + 
            "", doc.toXML ());
    }
    
    @Test
    public void testAttributeMap () throws Exception
    {
        setUp ("<a/>");
        
        Map<String, Attribute> map = doc.getRootElement ().getAttributeMap ();
        assertEquals ("{}", map.toString ());
    }
    
    @Test
    public void testAttributeMap2 () throws Exception
    {
        setUp ("<a />");
        
        Map<String, Attribute> map = doc.getRootElement ().getAttributeMap ();
        assertEquals ("{}", map.toString ());
    }
    
    @Test
    public void testAttributeMap3 () throws Exception
    {
        setUp ("<a x='1' y='2' />");
        
        Map<String, Attribute> map = doc.getRootElement ().getAttributeMap ();
        assertEquals ("{x= x='1', y= y='2'}", map.toString ());
    }
    
    @Test
    public void testAttributeMapSequence () throws Exception
    {
        setUp ("<a y='2' x='1' />");
        
        Map<String, Attribute> map = doc.getRootElement ().getAttributeMap ();
        assertEquals ("{y= y='2', x= x='1'}", map.toString ());
    }
    
    @Test
    public void testAttributes () throws Exception
    {
        setUp ("<a/>");
        
        List<Attribute> list = doc.getRootElement ().getAttributes ();
        assertEquals ("[]", list.toString ());
    }
    
    @Test
    public void testAttributes2 () throws Exception
    {
        setUp ("<a />");
        
        List<Attribute> list = doc.getRootElement ().getAttributes ();
        assertEquals ("[]", list.toString ());
    }
    
    @Test
    public void testAttributes3 () throws Exception
    {
        setUp ("<a y='2' x='1' />");
        
        List<Attribute> list = doc.getRootElement ().getAttributes ();
        assertEquals ("[ y='2',  x='1']", list.toString ());
    }
    
    @Test
    public void testAttributes4 () throws Exception
    {
        Attribute a = new Attribute ("a", "\"", '"');
        assertEquals (" a=\"&quot;\"", a.toXML ());
    }
    
    @Test
    public void testAttributes5 () throws Exception
    {
        Attribute a = new Attribute ("a", "'", '\'');
        assertEquals (" a='&apos;'", a.toXML ());
    }
    
    @Test
    public void testAttributes6 () throws Exception
    {
        Attribute a = new Attribute ("a", "\"'", '\'');
        assertEquals (" a='\"&apos;'", a.toXML ());
    }
    
    @Test
    public void testAttributes7 () throws Exception
    {
        Attribute a = new Attribute ("a", "\"'", '\'');
        a.setQuoteChar ('"');
        assertEquals (" a=\"&quot;'\"", a.toXML ());
    }
    
    @Test
    public void testAttributes8 () throws Exception
    {
        Attribute a = new Attribute ("a", "x");
        assertEquals (" a=\"x\"", a.toXML ());
        assertEquals ('\"', a.getQuoteChar ());
        a.setQuoteChar ('\'');
        assertEquals ('\'', a.getQuoteChar ());
        assertEquals (" a='x'", a.toXML ());
    }
    
    @Test
    public void testAttributesNameNull () throws Exception
    {
        try
        {
            new Attribute (null, null);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals ("name is null", ex.getMessage ());
        }
    }
    
    @Test
    public void testAttributesValueNull () throws Exception
    {
        try
        {
            new Attribute ("a", null);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals ("value is null", ex.getMessage ());
        }
    }
    
    @Test
    public void testIllegalQuoteChar () throws Exception
    {
        try
        {
            new Attribute ("a", "\"", 'x');
            fail ("No exception was thrown");
        }
        catch (XMLParseException ex)
        {
            assertEquals ("Illegal quote charater: \"x\" (120)", ex.getMessage ());
        }
    }
    
    @Test
    public void testGetAttribute () throws Exception
    {
        setUp ("<a/>");
        
        Attribute a = doc.getRootElement ().getAttribute ("x");
        assertNull (a);
    }
    
    @Test
    public void testGetAttribute2 () throws Exception
    {
        setUp ("<a />");
        
        Attribute a = doc.getRootElement ().getAttribute ("x");
        assertNull (a);
    }
    
    @Test
    public void testGetAttribute3 () throws Exception
    {
        setUp ("<a y='2' x='1' />");
        
        Attribute a = doc.getRootElement ().getAttribute ("x");
        assertEquals (" x='1'", a.toString ());
    }
    
    @Test
    public void testGetAttributeValue () throws Exception
    {
        setUp ("<a/>");
        
        String a = doc.getRootElement ().getAttributeValue ("x");
        assertNull (a);
    }
    
    @Test
    public void testGetAttributeValue2 () throws Exception
    {
        setUp ("<a />");
        
        String a = doc.getRootElement ().getAttributeValue ("x");
        assertNull (a);
    }
    
    @Test
    public void testGetAttributeValue3 () throws Exception
    {
        setUp ("<a y='2' x='1' />");
        
        String a = doc.getRootElement ().getAttributeValue ("x");
        assertEquals ("1", a.toString ());
    }
    
    @Test
    public void testGetAttributeValue4 () throws Exception
    {
        setUp ("<a y='2' x='&lt;&gt;&amp;&quot;&apos;' />");
        
        String a = doc.getRootElement ().getAttributeValue ("x");
        assertEquals ("<>&\"'", a.toString ());
    }
    
    @Test
    public void testIsCompactEmpty () throws Exception
    {
        setUp ("<a/>");
        assertTrue (doc.getRootElement ().isCompactEmpty ());
    }
    
    @Test
    public void testIsCompactEmpty2 () throws Exception
    {
        setUp ("<a />");
        assertTrue (doc.getRootElement ().isCompactEmpty ());
    }
    
    @Test
    public void testIsCompactEmpty3 () throws Exception
    {
        setUp ("<a></a>");
        assertFalse (doc.getRootElement ().isCompactEmpty ());
        assertEquals ("<a></a>", doc.toXML ());
    }
    
    @Test
    public void testIsCompactEmpty4 () throws Exception
    {
        setUp ("<a />");
        doc.getRootElement ().addNode (new Element ("e"));
        assertFalse (doc.getRootElement ().isCompactEmpty ());
    }
    
    @Test
    public void testGetChildNodes () throws Exception
    {
        setUp ("<a />");
        assertEquals ("[]", doc.getRootElement ().getNodes ().toString ());
    }
    
    @Test
    public void testGetChildNodes2 () throws Exception
    {
        setUp ("<a> </a>");
        assertEquals ("[ ]", doc.getRootElement ().getNodes ().toString ());
    }
    
    @Test
    public void testRemoveChildNode () throws Exception
    {
        setUp ("<a> <b/></a>");
        Node n = doc.getRootElement ().removeNode (0);
        assertNotNull (n);
        assertEquals ("<a><b/></a>", doc.toXML ());
    }

    @Test
    public void testRemoveChildNode2 () throws Exception
    {
        setUp ("<a> <b/></a>");
        Node n = doc.getRootElement ().removeNode (1);
        assertNotNull (n);
        assertEquals ("<a> </a>", doc.toXML ());
    }
    
    @Test
    public void testRemoveChildNode3 () throws Exception
    {
        setUp ("<a> <b/></a>");
        Node n = doc.getRootElement ().getNode (0);
        assertTrue (doc.getRootElement ().removeNode (n));
        assertEquals ("<a><b/></a>", doc.toXML ());
    }
    
    @Test
    public void testRemoveChildNode4 () throws Exception
    {
        Element e = new Element ("e");
        assertFalse (e.removeNode (null));
    }
    
    @Test
    public void testGetChildren () throws Exception
    {
        setUp ("<a />");
        assertEquals ("[]", doc.getRootElement ().getChildren ().toString ());
    }
    
    @Test
    public void testGetChildren2 () throws Exception
    {
        setUp ("<a> <b/></a>");
        assertEquals ("[<b/>]", doc.getRootElement ().getChildren ().toString ());
    }
    
    @Test
    public void testGetText () throws Exception
    {
        setUp ("<a />");
        assertEquals ("", doc.getRootElement ().getText ());
    }
    
    @Test
    public void testGetText2 () throws Exception
    {
        setUp ("<a>  x \n y  </a>");
        assertEquals ("  x \n y  ", doc.getRootElement ().getText ());
    }
    
    @Test
    public void testGetText3 () throws Exception
    {
        setUp ("<a>a<b>x</b>a</a>");
        assertEquals ("axa", doc.getRootElement ().getText ());
    }
    
    @Test
    public void testGetTrimmedText () throws Exception
    {
        setUp ("<a>  x \n y  </a>");
        assertEquals ("x \n y", doc.getRootElement ().getTrimmedText ());
    }
    
    @Test
    public void testGetNormalizedText () throws Exception
    {
        setUp ("<a> <b> x \n y </b> </a>");
        assertEquals ("x y", doc.getRootElement ().getNormalizedText ());
    }
    
    @Test
    public void testNothingFound () throws Exception
    {
        setUp (POM_XML);

        Element match = root.getChild ("xxx");
        assertNull (match == null ? "" : match.toString (), match);
    }
    
    @Test
    public void testProject () throws Exception
    {
        setUp (POM_XML);

        assertEquals ("project", root.getName ());
        assertEquals (42, root.getStartToken ().getStartOffset());
    }
    
    @Test
    public void testParent () throws Exception
    {
        setUp (POM_XML);

        Element match = root.getChild ("parent");
        assertEquals (437, match.getStartOffset());
        assertEquals (562, match.getEndOffset());
        
        assertEquals (
                "<parent>\r\n" + 
                "    <groupId>org.codehaus.mojo</groupId>\r\n" + 
                "    <artifactId>mojo</artifactId>\r\n" + 
                "    <version>16</version>\r\n" + 
                "  </parent>", 
                match.getStartToken ().getSource ().substring (match.getStartOffset (), match.getEndOffset ()));
    }
    
    @Test
    public void testParentVersion () throws Exception
    {
        setUp (POM_XML);

        Element match = root.getChild ("parent");
        Element version = match.getChild ("version");
        assertEquals (528, version.getStartToken ().getStartOffset());
        assertEquals ("<version>16</version>", version.toXML ());
        assertEquals ("16", version.getNormalizedText ());
    }
    
    @Test
    public void testReplaceParentVersion () throws Exception
    {
        setUp (POM_XML);

        Element match = root.getChild ("parent");
        Element version = match.getChild ("version");
        version.setText ("17");
        
        assertEquals (POM_XML.replaceAll ("16", "17"), doc.toXML ());
    }
    
    @Test
    public void testParentXXX () throws Exception
    {
        setUp (POM_XML);

        Element parent = root.getChild ("parent");
        Element match = parent.getChild ("xxx");
        assertNull (match == null ? "" : match.toString (), match);
    }
    
    @Test
    public void testProjectArtifactId () throws Exception
    {
        setUp (POM_XML);

        Element artifactId = root.getChild ("artifactId");
        assertEquals ("versions-maven-plugin", artifactId.getNormalizedText ());
    }
    
    @Test
    public void testNoAttributeXXX () throws Exception
    {
        setUp ("<a />");
        Attribute a = root.getAttribute ("xxx");
        assertNull (a);
    }
    
    @Test
    public void testNoAttributeXXX2 () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("xxx");
        assertNull (a);
    }
    
    @Test
    public void testSetAttributeName () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        a.setName ("y");
        assertEquals ("<a y='1' />", doc.toXML ());
    }
    
    @Test
    public void testSetAttributeValue () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        a.setValue ("2");
        assertEquals ("<a x='2' />", doc.toXML ());
    }
    
    @Test
    public void testSetAttributeValue2 () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        a.setValue ("\"x\"");
        assertEquals ("<a x='\"x\"' />", doc.toXML ());
    }
    
    @Test
    public void testSetAttributeValue3 () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        a.setValue ("'b'");
        assertEquals ("<a x='&apos;b&apos;' />", doc.toXML ());
    }
    
    @Test
    public void testSetAttributeValue4 () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        a.setValue ("'\"");
        assertEquals ("<a x='&apos;\"' />", doc.toXML ());
    }
    
    @Test
    public void testAttributeToXML () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        assertEquals (" x='1'", a.toXML ());
    }
    
    @Test
    public void testAttributeType () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        assertEquals (Type.ATTRIBUTE, a.getType ());
    }
    
    @Test
    public void testAttributeOffsets () throws Exception
    {
        setUp ("<a x='1' />");
        Attribute a = root.getAttribute ("x");
        assertEquals (2, a.getStartOffset ());
        assertEquals (8, a.getEndOffset ());
    }
    
    @Test
    public void testNoRootElement () throws Exception
    {
        try
        {
            setUp ("<?xml version='1.0'?>");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("No root element found", e.getMessage ());
        }
    }
    
    @Test
    public void testTwoRootElements () throws Exception
    {
        try
        {
            setUp ("<?xml version='1.0'?><a /><b/>");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 27: Only one root element allowed per document", e.getMessage ());
        }
    }
    
    @Test
    public void testUnexpectedEOF () throws Exception
    {
        try
        {
            setUp ("<?xml version='1.0'?>\n<a");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 2, column 3: Missing '>' of start tag", e.getMessage ());
        }
    }
    
    @Test
    public void testUnexpectedEOF2 () throws Exception
    {
        try
        {
            setUp ("<?xml version='1.0'?>\n<a>");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 2, column 1: Unexpected end-of-file while parsing children of element a", e.getMessage ());
        }
    }
    
    @Test
    public void testElementMismatch () throws Exception
    {
        try
        {
            setUp ("<?xml version='1.0'?>\n<a></b>");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 2, column 1: End element 'b' at line 2, column 4 doesn't match with 'a'", e.getMessage ());
        }
    }
    
    @Test
    public void testElementMismatch2 () throws Exception
    {
        try
        {
            setUp ("<?xml version='1.0'?>\n<a>\n<b></b>\n<b>\n<b></b>\n</a>");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 4, column 1: End element 'a' at line 6, column 1 doesn't match with 'b'", e.getMessage ());
        }
    }
    
    @Test
    public void testDocumentXMLDecl1 () throws Exception
    {
        try
        {
            setUp ("<?xml   version = '2.1' ?>\n<root />");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 19: only versions '1.0' and '1.1' are supported: [2.1]", e.getMessage ());
        }
    }

    @Test
    public void testDocumentXMLDecl2 () throws Exception
    {
        setUp ("<?xml   version = '1.1'  encoding = 'Utf-8' ?>\n<root />");
        assertEquals ("1.1", doc.getVersion ());
        assertEquals ("Utf-8", doc.getEncoding ());
        assertFalse (doc.isStandalone ());
    }
    
    @Test
    public void testDocumentXMLDecl3 () throws Exception
    {
        setUp ("<?xml   version = '1.1'  encoding = 'Utf-8'  standalone = 'yes' ?>\n<root />");
        assertEquals ("1.1", doc.getVersion ());
        assertEquals ("Utf-8", doc.getEncoding ());
        assertTrue (doc.isStandalone ());
    }
    
    @Test
    public void testDocumentXMLDecl4 () throws Exception
    {
        setUp ("<?xml   version = '1.1'  encoding = 'Utf-8' standalone = 'no' ?>\n<root />");
        assertEquals ("1.1", doc.getVersion ());
        assertEquals ("Utf-8", doc.getEncoding ());
        assertFalse (doc.isStandalone ());
    }
    
    @Test
    public void testDocumentXMLDecl5 () throws Exception
    {
        Document doc = new Document ();
        try
        {
            doc.addNode (new ProcessingInstruction ("xml"));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Missing version attribute", e.getMessage ());
        }
    }
    
    @Test
    public void testDocumentXMLDecl6 () throws Exception
    {
        Document doc = new Document ();
        try
        {
            doc.addNode (new ProcessingInstruction ("xml", "encoding='utf-8'"));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Version must be before encoding", e.getMessage ());
        }
    }
    
    @Test
    public void testDocumentXMLDecl7 () throws Exception
    {
        Document doc = new Document ();
        try
        {
            doc.addNode (new ProcessingInstruction ("xml", "version='1.1' encoding=''"));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Value for encoding is empty", e.getMessage ());
        }
    }
    
    @Test
    public void testDocumentXMLDecl8 () throws Exception
    {
        Document doc = new Document ();
        try
        {
            doc.addNode (new ProcessingInstruction ("xml", "version='1.1' encoding='utf-8' standalone='' "));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Value for standalone is empty", e.getMessage ());
        }
    }

    @Test
    public void testDocumentXMLDecl9 () throws Exception
    {
        Document doc = new Document ();
        try
        {
            doc.addNode (new ProcessingInstruction ("xml", " version='1.1' encoding='utf-8' standalone='xxx' "));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Allowed values for standalone are 'yes' and 'no', found 'xxx'", e.getMessage ());
        }
    }
    
    @Test
    public void testDocumentXMLDecl10 () throws Exception
    {
        Document doc = new Document ();
        doc.addNode (new Element ("e"));
        doc.setVersion ("1.1");
        
        assertEquals ("<?xml version=\"1.1\"?>\n<e/>", doc.toXML ());
    }
    
    @Test
    public void testDocumentXMLDecl11 () throws Exception
    {
        Document doc = new Document ();
        doc.addNode (new Element ("e"));
        doc.setEncoding (XMLInputStreamReader.ENCODING_ISO_Latin_1);
        
        assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<e/>", doc.toXML ());
    }
    
    @Test
    public void testDocumentXMLDecl12 () throws Exception
    {
        Document doc = new Document ();
        doc.addNode (new Element ("e"));
        doc.setStandalone (true);
        
        // The spec demands that an encoding is set if standalone is specified
        // but the W3C test suite has examples which omit the encoding in this case.
        //assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<e/>", doc.toXML ());
        assertEquals ("<?xml version=\"1.0\" standalone=\"yes\"?>\n<e/>", doc.toXML ());
    }
    
    @Test
    public void testDocumentXMLDecl13 () throws Exception
    {
        Document doc = new Document ();
        doc.addNode (new Element ("e"));
        doc.setStandalone (true);
        doc.setEncoding (XMLInputStreamReader.ENCODING_ISO_Latin_1);
        
        assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n<e/>", doc.toXML ());
    }
    
    @Test
    public void testDocumentXMLDecl14 () throws Exception
    {
        Document doc = new Document ();
        doc.addNode (new Element ("e"));
        doc.setEncoding (XMLInputStreamReader.ENCODING_ISO_Latin_1);
        
        try
        {
            doc.addNode (0, new Text ("xxx"));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: It is not allowed to have content before the XML declaration", e.getMessage ());
        }
    }

    @Test
    public void testDocumentXMLDecl15 () throws Exception
    {
        Document doc = new Document ();
        doc.addNode (new Element ("e"));
        doc.setVersion (null);
        
        assertEquals ("<?xml version=\"1.0\"?>\n<e/>", doc.toXML ());
    }
    
    @Test
    public void testEntityResolver () throws Exception
    {
        XMLParser parser = new XMLParser ();

        // Default is not to expand entites; in fact they are treated as text
        assertNull (parser.getEntityResolver ());
        assertFalse (parser.isExpandEntities ());
        assertTrue (parser.isTreatEntitiesAsText ());
        
        parser.setEntityResolver (new HTMLEntityResolver ());
        
        // Make sure this enables entity resolution
        assertTrue (parser.isExpandEntities ());
        assertFalse (parser.isTreatEntitiesAsText ());
        
        doc = parser.parse (new XMLStringSource (
            "<?xml version=\"1.0\"?>\n" +
            "<xml>&lt;a&gt;&nbsp;</xml>\n"
        ));
        
        Element root = doc.getRootElement ();
        assertEquals ("<a>\u00a0", root.getText ());
        Text entity = (Text)root.getNode (0);
        assertEquals ("&lt;", entity.getValue ());
        assertEquals ("<", entity.getText ());
    }

    @Test
    public void testWithoutEntityResolver () throws Exception
    {
        XMLParser parser = new XMLParser ();
        parser.setEntityResolver (new HTMLEntityResolver ());
        parser.setExpandEntities (false);
        
        assertFalse (parser.isExpandEntities ());
        assertFalse (parser.isTreatEntitiesAsText ());
        
        doc = parser.parse (new XMLStringSource (
                "<?xml version=\"1.0\"?>\n" +
                "<xml>&lt;a&gt;&nbsp;</xml>\n"
        ));
        
        Element root = doc.getRootElement ();
        assertEquals ("<a>\u00a0", root.getText ());
        Entity entity = (Entity)root.getNode (0);
        assertEquals ("lt", entity.getName ());
        assertEquals ("&lt;", entity.getValue ());
        assertEquals ("<", entity.getText ());
        
        entity = (Entity)root.getNode (3);
        assertEquals ("nbsp", entity.getName ());
        assertEquals ("&nbsp;", entity.getValue ());
        assertEquals ("\u00a0", entity.getText ());
    }
    
    private Document doc;
    private Element root;

    public void setUp (String xml)
    {
        XMLParser parser = new XMLParser ();
        doc = parser.parse (new XMLStringSource (xml));
        root = doc.getRootElement ();
    }
    
    @After
    public void tearDown ()
    {
        doc = null;
        root = null;
    }
}
