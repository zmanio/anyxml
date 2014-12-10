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

import org.junit.Test;

import anyxml.XMLDeclaration;

public class XMLDeclarationTest
{
    @Test
    public void testVersion () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.0");
        assertEquals ("<?xml version=\"1.0\"?>", decl.toXML ());
    }
    
    @Test
    public void testVersionSpace () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.0");
        decl.setVersionSpace ("\t");
        assertEquals ("<?xml\tversion=\"1.0\"?>", decl.toXML ());
    }
    
    @Test
    public void testVersionQuote () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.0");
        decl.setVersionQuote ('\'');
        assertEquals ("<?xml version='1.0'?>", decl.toXML ());
    }
    
    @Test
    public void testVersionEquals () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.0");
        decl.setVersionEquals (" = ");
        assertEquals ("<?xml version = \"1.0\"?>", decl.toXML ());
    }
    
    @Test
    public void testVersion11 () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.1");
        assertEquals ("<?xml version=\"1.1\"?>", decl.toXML ());
    }
    
    @Test
    public void testEncoding () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.1", "iso-8859-1");
        assertEquals ("<?xml version=\"1.1\" encoding=\"iso-8859-1\"?>", decl.toXML ());
    }
    
    @Test
    public void testEncodingSpace () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.1", "iso-8859-1");
        decl.setEncodingSpace ("\t");
        assertEquals ("<?xml version=\"1.1\"\tencoding=\"iso-8859-1\"?>", decl.toXML ());
    }

    @Test
    public void testEncodingQuote () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.1", "iso-8859-1");
        decl.setEncodingQuote ('\'');
        assertEquals ("<?xml version=\"1.1\" encoding='iso-8859-1'?>", decl.toXML ());
    }
    
    @Test
    public void testParsingSimple () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.0");
        String text = "<?xml version=\"1.0\"?>";
        decl.parseXMLDeclaration (text);
        
        assertEquals (text, decl.toXML ());
        assertEquals (" ", decl.getVersionSpace ());
    }
    
    @Test
    public void testParsingComplex () throws Exception
    {
        XMLDeclaration decl = new XMLDeclaration ("1.0");
        String text = "<?xml\t \tversion\t= '1.1'\t encoding\t\t= \t'iso-8859-1'\t\tstandalone =\t'no' \t?>";
        decl.parseXMLDeclaration (text);
        
        assertEquals (text, decl.toXML ());
        assertEquals ("\t \t", decl.getVersionSpace ());
        assertEquals ("\t= ", decl.getVersionEquals ());
        assertEquals ('\'', decl.getVersionQuote ());
        assertEquals ("1.1", decl.getVersion ());
        
        assertEquals ("\t ", decl.getEncodingSpace ());
        assertEquals ("\t\t= \t", decl.getEncodingEquals ());
        assertEquals ('\'', decl.getEncodingQuote ());
        assertEquals ("iso-8859-1", decl.getEncoding ());

        assertEquals ("\t\t", decl.getStandaloneSpace ());
        assertEquals (" =\t", decl.getStandaloneEquals ());
        assertEquals ('\'', decl.getStandaloneQuote ());
        assertEquals (false, decl.isStandalone ());
        assertEquals (true, decl.isShowStandaloneNo ());
    }
    
    @Test
    public void testNullVersion () throws Exception
    {
        try
        {
            new XMLDeclaration ((String)null);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("version is null or blank", e.getMessage ());
        }
    }
    
    @Test
    public void testEmptyVersion () throws Exception
    {
        try
        {
            new XMLDeclaration ("");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("version is null or blank", e.getMessage ());
        }
    }
    
    @Test
    public void testBlankVersion () throws Exception
    {
        try
        {
            new XMLDeclaration (" ");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("version is null or blank", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalVersion () throws Exception
    {
        try
        {
            new XMLDeclaration ("0");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("only versions '1.0' and '1.1' are supported: [0]", e.getMessage ());
        }
    }
    
    @Test
    public void testNullEncoding () throws Exception
    {
        assertEquals ("<?xml version=\"1.0\"?>", new XMLDeclaration ("1.0", null).toXML ());
    }
    
    @Test
    public void testEmptyEncoding () throws Exception
    {
        try
        {
            new XMLDeclaration ("1.0", "");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("encoding is blank", e.getMessage ());
        }
    }
    
    @Test
    public void testBlankEncoding () throws Exception
    {
        try
        {
            new XMLDeclaration ("1.0", " ");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("encoding is blank", e.getMessage ());
        }
    }
}
