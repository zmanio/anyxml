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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.junit.Test;

import anyxml.Document;
import anyxml.XMLIOSource;
import anyxml.XMLParseException;
import anyxml.XMLParser;
import anyxml.XMLStringBufferSource;
import anyxml.XMLStringBuilderSource;

public class XMLSourceTest
{
    @Test
    public void testXMLStringBufferSource () throws Exception
    {
        StringBuffer buffer = new StringBuffer ("<x a='1'>n</x>");
        XMLParser parser = new XMLParser ();
        Document doc = parser.parse (new XMLStringBufferSource (buffer));
        assertEquals (buffer.toString (), doc.toXML ());
    }

    @Test
    public void testXMLStringBuilderSource () throws Exception
    {
        StringBuilder buffer = new StringBuilder ("<x a='1'>n</x>");
        XMLParser parser = new XMLParser ();
        Document doc = parser.parse (new XMLStringBuilderSource (buffer));
        assertEquals (buffer.toString (), doc.toXML ());
    }
    
    @Test
    public void testXMLIOSource1 () throws Exception
    {
        XMLIOSource source = new XMLIOSource (new ByteArrayInputStream (XMLInputStreamReaderTest.TEST_XML.getBytes ("utf-8")));
        XMLParser parser = new XMLParser ();
        Document doc = parser.parse (source);
        assertEquals (XMLInputStreamReaderTest.TEST_XML, doc.toXML ());
    }

    @Test
    public void testXMLIOSource2 () throws Exception
    {
        XMLIOSource source = new XMLIOSource (new StringReader (XMLInputStreamReaderTest.TEST_XML));
        XMLParser parser = new XMLParser ();
        Document doc = parser.parse (source);
        assertEquals (XMLInputStreamReaderTest.TEST_XML, doc.toXML ());
    }
    
    @Test
    public void testFileSource () throws Exception
    {
        XMLIOSource source = new XMLIOSource (new File ("pom.xml"));
        XMLParser parser = new XMLParser ();
        Document doc = parser.parse (source);
        assertEquals ("DecentXML", doc.getChild ("/project/name").getText ());
    }
    
    @Test
    public void testURLSource () throws Exception
    {
        File f = new File ("pom.xml").getAbsoluteFile ();
        XMLIOSource source = new XMLIOSource (f.toURI ().toURL());
        XMLParser parser = new XMLParser ();
        Document doc = parser.parse (source);
        assertEquals ("DecentXML", doc.getChild ("/project/name").getText ());
    }
    
    @Test
    public void testExceptionDuringRead () throws Exception
    {
        try
        {
            XMLIOSource.toString (new IOExceptionInputStream ());
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Error parsing XML declaration: Test Error", e.getMessage ());
        }
    }
    
    @Test
    public void testExceptionDuringClose () throws Exception
    {
        InputStream reader = new InputStream () {
            @Override
            public void close () throws IOException
            {
                throw new IOException ("close failed");
            }

            @Override
            public int read () throws IOException
            {
                return -1;
            }
        };
        
        try
        {
            XMLIOSource.toString (reader);
            fail ("No exception was thrown");
        }
        catch (IOException e) // Break of symmetry: Other places throw a XMLParseException
        {
            assertEquals ("close failed", e.getMessage ());
        }
    }
    
    @Test
    public void testExceptionShadowing () throws Exception
    {
        InputStream reader = new InputStream () {
            @Override
            public void close () throws IOException
            {
                // This exception must not shadow the exception in read()
                throw new IOException ("close failed");
            }
            
            @Override
            public int read () throws IOException
            {
                throw new IOException ("read failed");
            }
        };
        
        try
        {
            XMLIOSource.toString (reader);
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Error parsing XML declaration: read failed", e.getMessage ());
        }
    }
    
    @Test
    public void testToString () throws Exception
    {
        assertEquals ("", XMLIOSource.toString (new ByteArrayInputStream (new byte [0])));
    }
}
