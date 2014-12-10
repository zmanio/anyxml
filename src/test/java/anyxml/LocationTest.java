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

import org.junit.Test;

import anyxml.Document;
import anyxml.Element;
import anyxml.Location;
import anyxml.XMLParser;
import anyxml.XMLStringSource;

public class LocationTest
{
    @Test
    public void testLocation () throws Exception
    {
        Document doc = XMLParser.parse (XMLParserTest.POM_XML);
        Element parent = doc.getChild ("/project/parent");
        Location l = new Location (parent);
        
        assertEquals ("11:3", l.toString ());
    }
    
    @Test
    public void testSpaces () throws Exception
    {
        check (" ", 0, "1:1/0");
    }

    @Test
    public void testSpaces2 () throws Exception
    {
        check (" ", 1, "1:2/0");
    }
    
    @Test
    public void testNewLine () throws Exception
    {
        check ("\n", 1, "2:1/1");
    }
    
    @Test
    public void testNewLine2 () throws Exception
    {
        check ("\n\r\n\r\ra", 11, "5:2/5");
    }
    
    @Test
    public void testNewLine3 () throws Exception
    {
        check ("\n\r\n\r\ra", 1, "2:1/1");
    }
    
    @Test
    public void testNewLine4 () throws Exception
    {
        check ("\n\r\n\r\ra", 2, "3:1/3");
    }
    
    @Test
    public void testNewLine5 () throws Exception
    {
        check ("\n\r\n\r\ra", 3, "3:1/3");
    }
    
    @Test
    public void testNewLine6 () throws Exception
    {
        check ("\n\r\n\r\ra", 4, "4:1/4");
    }
    
    @Test
    public void testNewLine7 () throws Exception
    {
        check ("\n\r\n\r\ra", 5, "5:1/5");
    }
    
    @Test
    public void testNewLine8 () throws Exception
    {
        check ("\n\r\n\r\ra", 6, "5:2/5");
    }
    
    @Test
    public void testTab () throws Exception
    {
        check (" \t\ta", 0, "1:1/0");
    }
    
    @Test
    public void testTab2 () throws Exception
    {
        check (" \t\ta", 1, "1:2/0");
    }
    
    @Test
    public void testTab3 () throws Exception
    {
        check (" \t\ta", 2, "1:9/0");
    }
    
    @Test
    public void testTab4 () throws Exception
    {
        check (" \t\ta", 3, "1:17/0");
    }
    
    @Test
    public void testTab5 () throws Exception
    {
        check (" \t\ta", 4, "1:18/0");
    }
    
    private void check (String input, int offset, String expected)
    {
        XMLStringSource source = new XMLStringSource (input);
        Location l = new Location (source, offset);
        assertEquals (expected, l.toString ()+"/"+l.getLineStartOffset ());
    }
}
