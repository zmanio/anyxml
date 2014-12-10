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
import anyxml.Document;
import anyxml.Element;
import anyxml.Namespace;
import anyxml.XMLParser;


public class NamespaceTest
{
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + 
                    "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n" + 
                    "         xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\r\n" + 
                    "  < rdf:Description rdf:about=\"http://de.wikipedia.org/wiki/Resource_Description_Framework\">\r\n" + 
                    "    <dc:title>Resource Description Framework</dc:title>\r\n" + 
                    "    <dc:publisher>Wikipedia - Die freie Enzyklop-die</dc:publisher>\r\n" + 
                    "  </rdf:Description>\r\n" + 
                    "</ rdf:RDF >\r\n" + 
                    "";

    @Test
    public void testRoundtrip () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        assertEquals (XML, doc.toXML ());
    }
    
    @Test
    public void testPrefix () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Namespace rdf = doc.getNamespace ("rdf");
        assertNotNull ("Namespace rdf not found", rdf);
        assertEquals ("rdf", rdf.getPrefix ());
        assertEquals ("http://www.w3.org/1999/02/22-rdf-syntax-ns#", rdf.getURI ());
        
        Namespace dc = doc.getNamespace ("dc");
        assertNotNull ("Namespace dc not found", dc);
        assertEquals ("dc", dc.getPrefix ());
        assertEquals ("http://purl.org/dc/elements/1.1/", dc.getURI ());
        
        assertEquals (rdf, doc.getRootElement ().getNamespace ());
    }
    
    @Test
    public void testGetChildWithoutNS () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Element description = doc.getChild ("RDF/Description");
        assertNotNull (description);
    }
    
    @Test
    public void testGetChildWithNS () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Namespace rdf = doc.getNamespace ("rdf");
        Element description = doc.getChild ("RDF/Description", rdf);
        assertNotNull (description);
    }
    
    @Test
    public void testGetChildWithNSMixed () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Namespace rdf = doc.getNamespace ("rdf");
        Element description = doc.getChild ("rdf:RDF/Description", rdf);
        assertNotNull (description);
    }
    
    @Test
    public void testGetChildWithNSMixed2 () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Element description = doc.getChild ("rdf:RDF/rdf:Description/dc:title");
        assertNotNull (description);
    }
    
    @Test
    public void testGetChildWithNSMixed3 () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Element description = doc.getChild ("RDF/Description/dc:title");
        assertNotNull (description);
    }
    
    @Test
    public void testGetChildWithWrongNS () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Namespace dc = doc.getNamespace ("dc");
        Element e = doc.getChild ("dc:RDF");
        assertNull (e);
        e = doc.getChild ("/dc:RDF");
        assertNull (e);
        e = doc.getChild ("RDF", dc);
        assertNull (e);
        e = doc.getChild ("/RDF", dc);
        assertNull (e);
        e = doc.getChild ("RDF/dc:Description");
        assertNull (e);
        e = doc.getChild ("RDF/Description", dc);
        assertNull (e);
        e = doc.getChild ("/RDF/dc:Description");
        assertNull (e);
        e = doc.getChild ("/RDF/Description", dc);
        assertNull (e);
    }
    
    @Test
    public void testGetChildWithWrongNS2 () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Namespace dc = doc.getNamespace ("dc");
        Element root = doc.getRootElement ();
        Element e;
        e = root.getChild ("/dc:RDF");
        assertNull (e);
        e = root.getChild ("RDF", dc);
        assertNull (e);
        e = root.getChild ("/RDF", dc);
        assertNull (e);
        e = root.getChild ("RDF/dc:Description");
        assertNull (e);
        e = root.getChild ("RDF/Description", dc);
        assertNull (e);
        e = root.getChild ("/RDF/dc:Description");
        assertNull (e);
        e = root.getChild ("/RDF/Description", dc);
        assertNull (e);
        e = root.getChild ("dc:Description");
        assertNull (e);
        e = root.getChild ("Description", dc);
        assertNull (e);
    }

    @Test
    public void testAttributePrefix () throws Exception
    {
        Document doc = XMLParser.parse (XML);
        
        Element description = doc.getChild ("RDF/Description");
        Attribute a = description.getAttribute ("rdf:about");
        assertNotNull (a);
        // TODO Is this correct??
        assertEquals ("rdf:about", a.getName ());
        String url = "http://de.wikipedia.org/wiki/Resource_Description_Framework";
        assertEquals (url, a.getValue ());
        
        a = description.getAttribute ("about");
        assertNotNull (a);
        assertEquals (url, a.getValue ());
        
        a = description.getAttribute ("about", doc.getNamespace ("rdf"));
        assertNotNull (a);
        assertEquals (url, a.getValue ());
    }
    
}
