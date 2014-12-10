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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import anyxml.Document;
import anyxml.EntityResolver;
import anyxml.Token;
import anyxml.XMLParseException;
import anyxml.XMLParser;
import anyxml.XMLSource;
import anyxml.XMLStringSource;
import anyxml.XMLTokenizer;
import anyxml.dtd.DocTypeAttributeList;
import anyxml.dtd.DocTypeElement;
import anyxml.dtd.DocTypeEntity;
import anyxml.dtd.DocType.DocTypeType;

public class DTDParserTest
{
    public final static String SYSTEM_DOCTYPE = 
        "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\r\n" + 
        "\r\n" + 
        "<!DOCTYPE sql SYSTEM \"sql.dtd\">\r\n" + 
        "<sql/>\r\n" + 
        "";
    
    public final static String PUBLIC_DOCTYPE = 
        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \n" + 
        " \"http://www.w3.org/TR/html4/strict.dtd\">" +
        "" +
        "<HTML />";
    
    public static String INTERNAL_DOCTYPE =
        "<?xml version=\"1.0\"?>\n" +
        "<!DOCTYPE sql [\n" +
        "   <!ENTITY name 'value' -- comment -->\n" +
        "   <!ENTITY % coreattrs\n" + 
        " \"id          ID             #IMPLIED  -- document-wide unique id --\n" + 
        "  class       CDATA          #IMPLIED  -- space-separated list of classes --\n" + 
        // TODO "  style       %StyleSheet;   #IMPLIED  -- associated style info --\n" + 
        // TODO "  title       %Text;         #IMPLIED  -- advisory title --\n" + 
        "  \">\n" + 
        "   <!ELEMENT sql ( ( a, b* | c ) | #PCDATA)>\n" +
        "   <!ATTLIST sql id ID #implied -- comment -->" +
        "]>\n" +
        "<sql>text</sql>\n" +
        "";
    // Add complex example from p.49

    public final static String SYSTEM_DOCTYPE_EXTENSION = 
        "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\r\n" + 
        "\r\n" + 
        "<!DOCTYPE sql SYSTEM \"sql.dtd\" [\n" +
        "   <!ENTITY sample 'Hello world!'>\n" +
        "]>\r\n" + 
        "<sql>&sample;</sql>\r\n" + 
        "";
    

    @Test
    public void testSystemDocType () throws Exception
    {
        Document doc = check (SYSTEM_DOCTYPE);
        assertNotNull (doc.getDocType ());
        assertEquals (DocTypeType.SYSTEM, doc.getDocType ().getDocTypeType ());
        assertEquals ("sql", doc.getDocType ().getName ());
        assertNull (doc.getDocType ().getPublicLiteral ());
        assertEquals ("sql.dtd", doc.getDocType ().getSystemLiteral ());
    }
    
    @Test
    public void testInternalDoctype () throws Exception
    {
        Document doc = check (INTERNAL_DOCTYPE);
        assertNotNull (doc.getDocType ());
        assertEquals (DocTypeType.INLINE, doc.getDocType ().getDocTypeType ());
        assertEquals ("sql", doc.getDocType ().getName ());
        assertNull (doc.getDocType ().getPublicLiteral ());
        assertNull (doc.getDocType ().getSystemLiteral ());
    }
    
    @Test
    public void testIntDTD_Element () throws Exception
    {
        Document doc = check (INTERNAL_DOCTYPE);
        DocTypeElement sql = doc.getDocType ().getElement ("sql");
        assertNotNull (sql);
        assertEquals ("( ( a, b* | c ) | #PCDATA)", sql.getContent ());
        assertEquals ("sql", sql.getName ());
        assertEquals ("Token (DOCTYPE_ELEMENT, 256:297, \"<!ELEMENT sql ( ( a, b* | c ) | #PCDATA)>\")", sql.getToken ().toString ());
        assertEquals ("<!ELEMENT sql ( ( a, b* | c ) | #PCDATA)>", sql.getValue ());
    }
    
    @Test
    public void testIntDTD_AttList () throws Exception
    {
        Document doc = check (INTERNAL_DOCTYPE);
        DocTypeElement sql = doc.getDocType ().getElement ("sql");
        List<DocTypeAttributeList> alists = doc.getDocType ().getAttributeList ("sql");
        assertNotNull (alists);
        assertEquals (1, alists.size ());
        assertEquals (alists, sql.getAttLists ());
        
        DocTypeAttributeList attList = alists.get (0);
        assertEquals (sql, attList.getElement ());
    }
    
    @Test
    public void testIntDTD_Entity () throws Exception
    {
        Document doc = check (INTERNAL_DOCTYPE);
        DocTypeEntity e = doc.getDocType ().getEntity ("name");
        String s = "<!ENTITY name 'value' -- comment -->";
        assertEquals (s, e.toXML ());
        assertEquals (s, e.getValue ());
        assertEquals ("name", e.getName ());
        assertEquals ("value", e.getText ());
        
        EntityResolver r = doc.getDocType ().getEntityResolver ();
        assertEquals ("value", r.resolve ("name"));
    }
    
    @Test
    public void testIntDTD_ParameterEntity () throws Exception
    {
        Document doc = check (INTERNAL_DOCTYPE);
        DocTypeEntity e = doc.getDocType ().getEntity ("coreattrs");
        assertNull ("Parameter entity coreattrs was added to the normal entities", e);
        e = doc.getDocType ().getParameterEntity ("coreattrs");
        assertNotNull ("No parameter entity 'coreattrs'", e);
        String s = "<!ENTITY % coreattrs\n" + 
        		" \"id          ID             #IMPLIED  -- document-wide unique id --\n" + 
        		"  class       CDATA          #IMPLIED  -- space-separated list of classes --\n" + 
        		"  \">";
        assertEquals (s, e.toXML ());
        assertEquals (s, e.getValue ());
        assertEquals ("coreattrs", e.getName ());
        assertEquals ("id          ID             #IMPLIED  -- document-wide unique id --\n" + 
        		"  class       CDATA          #IMPLIED  -- space-separated list of classes --\n" + 
        		"  ", e.getText ());
        // TODO Maybe parse attribute names and types, etc., into a usable structure, too.
    }
    
    @Test
    public void testPublicDoctype () throws Exception
    {
        Document doc = check (PUBLIC_DOCTYPE);
        assertNotNull (doc.getDocType ());
        assertEquals (DocTypeType.PUBLIC, doc.getDocType ().getDocTypeType ());
        assertEquals ("HTML", doc.getDocType ().getName ());
        assertEquals ("-//W3C//DTD HTML 4.01//EN", doc.getDocType ().getPublicLiteral ());
        assertEquals ("http://www.w3.org/TR/html4/strict.dtd", doc.getDocType ().getSystemLiteral ());
        
        assertEquals (0, doc.getDocType ().getElements ().size ());
    }
    
    @Test
    public void testNoStartToken () throws Exception
    {
        XMLParser parser = new XMLParser () {
            @Override
            protected XMLTokenizer createDTDTokenizer (XMLSource source, final int startOffset)
            {
                return new XMLTokenizer (source) {
                    {
                        pos = startOffset;
                    }
                    @Override
                    public Token next ()
                    {
                        return null;
                    }
                };
            }
        };
        try
        {
            parser.parse (new XMLStringSource (INTERNAL_DOCTYPE));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 2, column 1: Expected '<!DOCTYPE'", e.getMessage ());
        }
    }
    
    @Test
    public void testWrongStartToken () throws Exception
    {
        XMLParser parser = new XMLParser () {
            @Override
            protected XMLTokenizer createDTDTokenizer (XMLSource source, final int startOffset)
            {
                return new XMLTokenizer (source) {
                    {
                        pos = startOffset;
                    }
                    @Override
                    public Token next ()
                    {
                        Token token = createToken ();
                        token.setEndOffset (pos + 5);
                        token.setType (Type.TEXT);
                        return token;
                    }
                };
            }
        };
        try
        {
            parser.parse (new XMLStringSource (INTERNAL_DOCTYPE));
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 2, column 1: Expected '<!DOCTYPE' but found '<!DOC'", e.getMessage ());
        }
    }
    
    public Document check (String xml)
    {
        Document doc = XMLParser.parse (xml);
        assertEquals (xml, doc.toXML ());
        return doc;
    }
}
