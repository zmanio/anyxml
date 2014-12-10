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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import anyxml.XMLIOSource;
import anyxml.XMLInputStreamReader;
import anyxml.XMLParseException;

public class XMLInputStreamReaderTest
{
    public final static String TEST_UTF8_1 = "";
    public final static String TEST_UTF8_2 = "<?xml?>"; // Illegal: Version is missing
    public final static String TEST_UTF8_3 = "<?xml encoding='utf-8'?>"; // Illegal: Version is missing
    public final static String TEST_UTF8_4 = "<?xml version='1.0' encoding='utf-8'?>"; // Illegal: Version is missing
    public final static String TEST_Latin1_1 = "<?xml version='1.0' encoding='ISO-8859-1'?>";
    public final static String TEST_Latin1_2 = "<?xml version=\"1.0\" encoding=\"Latin-1\"?>";
    public final static byte[] TEST_BOM_UTF8 = new byte[] { (byte)0xef, (byte)0xbb, (byte)0xbf, (byte)0xfc };
    public final static byte[] TEST_BOM_UTF16_BE = new byte[] { (byte)0xfe, (byte)0xff, 0, (byte)0xfc };
    public final static byte[] TEST_BOM_UTF16_LE = new byte[] { (byte)0xff, (byte)0xfe, (byte)0xfc, 0 };
    public final static byte[] TEST_BOM_UTF32_BE = new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0, 0, 0, (byte)0xfc };
    public final static byte[] TEST_BOM_UTF32_LE = new byte[] { (byte)0xff, (byte)0xfe, 0, 0, (byte)0xfc, 0, 0, 0 };
    public final static String TEST_XML = "<?xml version=\"1.0\"?>\n\n<root>\n" +
    		"<p>\u00a0\u00fc</p>" +
    		"<p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Curabitur sit amet lectus congue" +
    		" libero elementum porttitor. Integer ut mi. Morbi pellentesque neque et felis. Etiam porta" +
    		" varius nunc. Duis justo lectus, dictum vitae, porttitor et, tristique nec, diam." +
    		" Suspendisse at erat eget leo tempor tincidunt. Praesent laoreet, tellus scelerisque" +
    		" convallis luctus, nibh quam malesuada justo, nec blandit libero diam sit amet est." +
    		" Mauris pretium odio a leo. Integer congue. Nunc vitae sem egestas odio dapibus" +
    		" ultricies. Phasellus tortor erat, molestie in, bibendum in, venenatis ut, purus." +
    		" Sed diam. Vestibulum a purus ut lacus mattis faucibus.</p>\n" +
    		"\n" +
    		"<p>Praesent tortor tellus, tempus sit amet, bibendum vitae, blandit vel, nulla. Quisque" +
    		" pharetra sem eget sapien. Duis at elit sed nunc adipiscing porta. Nulla nunc eros," +
    		" porta vel, consectetuer interdum, tempor vitae, felis. Aenean tempor elit. Aliquam" +
    		" placerat. In hac habitasse platea dictumst. Integer eleifend justo vel nulla. Quisque" +
    		" aliquam vulputate dui. Phasellus eget justo. Duis iaculis, leo vel fermentum" +
    		" scelerisque, mauris dui condimentum nulla, sed sagittis nunc felis sed odio. Nam" +
    		" condimentum, eros vitae hendrerit semper, dolor quam imperdiet ante, sit amet" +
    		" pellentesque arcu sem rutrum erat. Nulla nec sapien id arcu consequat cursus." +
    		" Suspendisse lectus mi, rutrum semper, commodo ut, consectetuer id, pede. Curabitur" +
    		" diam leo, posuere ut, dictum in, pellentesque at, enim. Quisque et lacus. Donec" +
    		" lacinia consequat orci.</p>\n" +
    		"</root>";
    
    @Test
    public void testUTF8_1 () throws Exception
    {
        byte[] data = TEST_UTF8_1.getBytes ("UTF-8");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        assertEquals ("UTF-8", r.getXmlEncoding ());
        assertEquals (TEST_UTF8_1, toString (r));
    }
    
    @Test
    public void testUTF8_2 () throws Exception
    {
        byte[] data = TEST_UTF8_2.getBytes ("UTF-8");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        try
        {
            r.getXmlEncoding ();
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 7: Missing version in XML declaration", e.getMessage ());
        }
        
        r.close ();
    }
    
    @Test
    public void testUTF8_3 () throws Exception
    {
        byte[] data = TEST_UTF8_3.getBytes ("UTF-8");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        try
        {
            r.getXmlEncoding ();
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 24: Missing version in XML declaration", e.getMessage ());
        }
    }

    @Test
    public void testUTF8_4 () throws Exception
    {
        byte[] data = TEST_UTF8_4.getBytes ("UTF-8");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        assertEquals ("utf-8", r.getXmlEncoding ());
        assertEquals ("UTF-8", r.getJavaEncoding ());
        assertEquals (TEST_UTF8_4, toString (r));
    }
    
    @Test
    public void testUTF8_5 () throws Exception
    {
        byte[] data = TEST_XML.getBytes ("UTF-8");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        assertEquals ("UTF-8", r.getXmlEncoding ());
        assertEquals ("UTF-8", r.getJavaEncoding ());
        assertEquals (TEST_XML, toString (r));
    }
    
    @Test
    public void testLatin1_1 () throws Exception
    {
        byte[] data = TEST_Latin1_1.getBytes ("ISO-8859-1");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        assertEquals ("ISO-8859-1", r.getXmlEncoding ());
        assertEquals ("ISO-8859-1", r.getJavaEncoding ());
        assertEquals (TEST_Latin1_1, toString (r));
    }
    
    @Test
    public void testLatin1_2 () throws Exception
    {
        byte[] data = TEST_Latin1_2.getBytes ("ISO-8859-1");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        assertEquals ("Latin-1", r.getXmlEncoding ());
        assertEquals ("ISO-8859-1", r.getJavaEncoding ());
        assertEquals (TEST_Latin1_2, toString (r));
    }
    
    @Test
    public void testLength () throws Exception
    {
        assertTrue ("Length: "+TEST_XML.length (), TEST_XML.length () > 1024);
    }
    
    @Test
    public void testUnicodeBig () throws Exception
    {
        byte[] data = "\u00fc".getBytes ("UnicodeBig");
        assertEquals ("[-2, -1, 0, -4]", Arrays.toString (data));
    }
    
    @Test
    public void testUnicodeLittle () throws Exception
    {
        byte[] data = "\u00fc".getBytes ("UnicodeLittle");
        assertEquals ("[-1, -2, -4, 0]", Arrays.toString (data));
    }
    
    @Test
    public void testUTF16_1 () throws Exception
    {
        byte[] data = TEST_XML.getBytes ("UnicodeBig");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        assertEquals ("UTF-16", r.getXmlEncoding ());
        assertEquals ("UnicodeBig", r.getJavaEncoding ());
        assertEquals (TEST_XML, toString (r));
    }
    
    @Test
    public void testUTF16_2 () throws Exception
    {
        byte[] data = TEST_XML.getBytes ("UnicodeLittle");
        XMLInputStreamReader r = new XMLInputStreamReader (new ByteArrayInputStream (data));
        assertEquals ("UTF-16", r.getXmlEncoding ());
        assertEquals ("UnicodeLittle", r.getJavaEncoding ());
        assertEquals (TEST_XML, toString (r));
    }
    
    @Test
    public void testIllegalEncoding () throws Exception
    {
        XMLInputStreamReader r = new XMLInputStreamReader (
                new ByteArrayInputStream ("<?xml version='1.0' encoding='xxx'?>".getBytes ()));
        try
        {
            r.determineEncoding ();
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 31: Error parsing XML declaration: Unsupported encoding 'xxx'", e.getMessage ());
        }
    }

    @Test
    public void testMissingOpeningQuote () throws Exception
    {
        XMLInputStreamReader r = new XMLInputStreamReader (
                new ByteArrayInputStream ("<?xml version='1.0' encoding=xxx?>".getBytes ()));
        try
        {
            r.determineEncoding ();
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 30: Missing opening quote for encoding", e.getMessage ());
        }
    }
    
    @Test
    public void testMissingClosingQuote () throws Exception
    {
        XMLInputStreamReader r = new XMLInputStreamReader (
                new ByteArrayInputStream ("<?xml version='1.0' encoding='xxx?>".getBytes ()));
        try
        {
            r.determineEncoding ();
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 31: Missing closing quote for encoding", e.getMessage ());
        }
    }
    
    @Test
    public void testMissingClosingQuote2 () throws Exception
    {
        XMLInputStreamReader r = new XMLInputStreamReader (
                new ByteArrayInputStream ("<?xml version='1.0' encoding=\"xxx?>".getBytes ()));
        try
        {
            r.determineEncoding ();
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 31: Missing closing quote for encoding", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCharacter () throws Exception
    {
        XMLInputStreamReader r = new XMLInputStreamReader (
                new ByteArrayInputStream ("<?xml version='1.0' encoding=\"xxx?<".getBytes ()));
        try
        {
            r.determineEncoding ();
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 35: Found illegal character in XML header: '<'", e.getMessage ());
        }
    }
    
    @Test
    public void testIOException () throws Exception
    {
        XMLInputStreamReader r = new XMLInputStreamReader (new IOExceptionInputStream ());
        try
        {
            r.read ();
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 1: Error parsing XML declaration: Test Error", e.getMessage ());
        }
    }
    
    private String toString (XMLInputStreamReader r) throws IOException
    {
        String result = XMLIOSource.toString (r);
        r.close ();
        return result;
    }
}
