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
import anyxml.ProcessingInstruction;
import anyxml.Text;
import anyxml.XMLInputStreamReader;
import anyxml.XMLParser;
import anyxml.XMLStringSource;

public class DocumentExamplesTest
{
    /** Show how to parse a String into an XML document */
    @Test
    public void testParseExample () throws Exception
    {
        String xml = "<hello-world />";
        XMLParser parser = new XMLParser ();
        Document doc = parser.parse (new XMLStringSource (xml));
        assertEquals (xml, doc.toXML ());
    }
    
    /** Create a simple Hello-World document ... an ugly one at that */
    @Test
    public void testCreateDocument () throws Exception
    {
        Document doc = new Document ()
        .addNodes (
            new ProcessingInstruction ("xml", "\tversion='1.0' encoding='utf-8'"),
            new Element ("root")
            .addNodes (new Text ("Hello World!"))
        );
        
        // How Ugly!
        assertEquals ("<?xml\tversion='1.0' encoding='utf-8'?><root>Hello World!</root>", doc.toString ());
    }

    /** Using a couple of text elements, we can make the result much more readable */
    @Test
    public void testCreateDocument2 () throws Exception
    {
        Document doc = new Document ()
        .addNodes (
            new ProcessingInstruction ("xml", "version='1.0' encoding='utf-8'"),
            new Text ("\n\n"),
            new Element ("root")
            .addNodes (
                new Text ("\n    "),
                new Text ("Hello World!"),
                new Text ("\n")
            )
        );
        
        assertEquals ("<?xml version='1.0' encoding='utf-8'?>\n" +
        		"\n" +
        		"<root>\n" +
        		"    Hello World!\n" +
        		"</root>", doc.toString ());
    }
    
    /** Similar to the last one but we're using the helper methods in Document
     *  to create the XML declaration for us.
     */
    @Test
    public void testCreateDocument3 () throws Exception
    {
        Document doc = new Document ()
        .setEncoding (XMLInputStreamReader.ENCODING_UTF_8)
        .addNodes (
            new Text ("\n"),
            new Element ("root")
            .addNodes (
                new Text ("\n    "),
                new Text ("Hello World!"),
                new Text ("\n")
            )
        );
        
        assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<root>\n" +
            "    Hello World!\n" +
            "</root>", doc.toString ());
    }
    
    /** Try this with your favorite XML parser... */
    @Test
    public void testFormattingAttributes () throws Exception
    {
        Document doc = new Document ()
        .setEncoding (XMLInputStreamReader.ENCODING_UTF_8.toLowerCase ())
        .addNodes (
            new Element ("project")
            .addAttributes (
                new Attribute ("xmlns", "http://maven.apache.org/POM/4.0.0"),
                new Attribute ("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"),
                new Attribute ("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd")
                    .setPreSpace ("\n        ") // Can you do this?
            )
            .addNode (
                new Text ("\n")
            ),
            new Text ("\n")
        );
        
        assertEquals (
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
            "        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" + 
            "</project>\n" + 
            "", 
            doc.toString ());
    }
}
