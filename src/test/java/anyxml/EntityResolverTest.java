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

import org.junit.Test;

import anyxml.Entity;
import anyxml.EntityResolver;
import anyxml.HTMLEntityResolver;

public class EntityResolverTest
{
    @Test
    public void testLT () throws Exception
    {
        assertEquals ("<", new EntityResolver ().expand ("lt"));
    }
    
    @Test
    public void testLT2 () throws Exception
    {
        assertEquals ("<", new EntityResolver ().expand ("&lt;"));
    }
    
    @Test
    public void testGT () throws Exception
    {
        assertEquals (">", new EntityResolver ().expand ("gt"));
    }
    
    @Test
    public void testAmp () throws Exception
    {
        assertEquals ("&", new EntityResolver ().expand ("amp"));
    }
    
    @Test
    public void testQuot () throws Exception
    {
        assertEquals ("\"", new EntityResolver ().expand ("quot"));
    }
    
    @Test
    public void testApos () throws Exception
    {
        assertEquals ("'", new EntityResolver ().expand ("apos"));
    }
    
    @Test
    public void testExpandUndefined () throws Exception
    {
        assertNull (new EntityResolver ().expand ("&undefined;"));
    }
    
    @Test
    public void testDecimalEntity () throws Exception
    {
        assertEquals ("A", new EntityResolver ().expand ("&#65;"));
    }
    
    @Test
    public void testHexEntity () throws Exception
    {
        assertEquals ("A", new EntityResolver ().expand ("&#x41;"));
    }
    
    @Test
    public void testIllegalEntity1 () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#-1;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Value of numeric entity must be > 0: [&#-1;]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntity2 () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#ffffffff;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Value of decimal entity can't be parsed: [&#ffffffff;]", e.getMessage ());
        }
    }
    
    @Test
    public void testEntity_fffd () throws Exception
    {
        assertEquals ("\ufffd", new EntityResolver ().expand ("&#xfffd;"));
    }
    
    @Test
    public void testEntity_FFFD () throws Exception
    {
        assertEquals ("\ufffd", new EntityResolver ().expand ("&#xFFFD;"));
    }
    
    @Test
    public void testIllegalEntityD800 () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#xD800;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xd800]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntityD800_2 () throws Exception
    {
        new EntityResolver ().setCharValidator (null).expandNumericEntity ("&#xD800;");
    }
    
    @Test
    public void testIllegalEntityDFFF () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#xDFFF;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xdfff]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntityFFFE () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#xFFFE;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xfffe]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntityFFFF () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#xFFFF;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntity110000 () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#x110000;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#x110000]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntity110000_2 () throws Exception
    {
        try
        {
            new EntityResolver ().setCharValidator (null).expandNumericEntity ("&#x110000;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Value of numeric entity must be [#x0000-#x10FFFF]: [&#x110000;]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntityFFFFFFFF () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#x7FFFFFFF;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#x7fffffff]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntityFFFFFFFF_2 () throws Exception
    {
        try
        {
            new EntityResolver ().setCharValidator (null).expandNumericEntity ("&#x7FFFFFFF;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Value of numeric entity must be [#x0000-#x10FFFF]: [&#x7FFFFFFF;]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntity4 () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#x10000");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Entities must end with ';': [&#x10000]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntity5 () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("&#x;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Value of hexadecimal entity can't be parsed: [&#x;]", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEntity6 () throws Exception
    {
        try
        {
            new EntityResolver ().expandNumericEntity ("#x32;");
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("Entities must start with '&#' or '&#x': [#x32;]", e.getMessage ());
        }
    }
    
    @Test
    public void testParentEntity () throws Exception
    {
        assertEquals ("\u00a0", new EntityResolver (new HTMLEntityResolver ()).resolve ("nbsp"));
    }
    
    @Test
    public void testParentEntity2 () throws Exception
    {
        assertTrue (new EntityResolver (new HTMLEntityResolver ()).isDefined ("nbsp"));
    }
    
    @Test
    public void testParentEntity3 () throws Exception
    {
        assertNull (new EntityResolver (new HTMLEntityResolver ()).expand ("xxx"));
    }
    
    @Test
    public void testHtmlEntity () throws Exception
    {
        assertEquals ("\u00a0", new HTMLEntityResolver ().expand ("nbsp"));
    }
    
    @Test
    public void testEncodeXML ()
    {
        assertEquals ("&lt;&gt;&amp;&quot;&apos;a", new EntityResolver ().encode ("<>&\"'a"));
    }

    @Test
    public void testIllegalAdd () throws Exception
    {
        try
        {
            new EntityResolver ().add (null, null);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("name is null or empty", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAdd2 () throws Exception
    {
        try
        {
            new EntityResolver ().add (" ", null);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("name is null or empty", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAdd3 () throws Exception
    {
        try
        {
            new EntityResolver ().add ("a", null);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("replacementText is null", e.getMessage ());
        }
    }

    @Test
    public void testCreateDecimalEntity () throws Exception
    {
        Entity e = Entity.createDecimalEntity ('A');
        assertEquals ("&#65;", e.toXML ());
    }
    
    @Test
    public void testCreateDecimalEntity2 () throws Exception
    {
        try
        {
            Entity.createDecimalEntity (-1);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("unicode must be >= 0: -1 0xffffffff", e.getMessage ());
        }
    }
    
    @Test
    public void testCreateDecimalEntity3 () throws Exception
    {
        try
        {
            Entity.createDecimalEntity (Character.MAX_VALUE + 1);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("unicode must be <= 65535: 65536 0x10000", e.getMessage ());
        }
    }
    
    @Test
    public void testCreateHexEntity () throws Exception
    {
        Entity e = Entity.createHexEntity ('M');
        assertEquals ("&#x4d;", e.toXML ());
    }
    
    @Test
    public void testCreateHexEntity2 () throws Exception
    {
        try
        {
            Entity.createHexEntity (-1);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("unicode must be >= 0: -1 0xffffffff", e.getMessage ());
        }
    }
    
    @Test
    public void testCreateHexEntity3 () throws Exception
    {
        try
        {
            Entity.createHexEntity (Character.MAX_VALUE + 1);
            fail ("No exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals ("unicode must be <= 65535: 65536 0x10000", e.getMessage ());
        }
    }
    
    @Test
    public void testIsWhitespaceFalse () throws Exception
    {
        Entity e = new Entity ("lt");
        e.setResolver (new HTMLEntityResolver ());
        assertFalse (e.isWhitespace ());
    }
    
    @Test
    public void testIsWhitespaceFalse2 () throws Exception
    {
        Entity e = new Entity ("nbsp");
        e.setResolver (new HTMLEntityResolver ());
        assertFalse (e.isWhitespace ());
    }
    
    @Test
    public void testIsWhitespaceFalse3 () throws Exception
    {
        Entity e = new Entity ("&#13;");
        // Decimal entities can't be checked if there is no EntityResolver installed :/
        assertFalse (e.isWhitespace ());
    }
    
    @Test
    public void testIsWhitespaceTrue () throws Exception
    {
        Entity e = new Entity ("&#32;");
        e.setResolver (new EntityResolver ());
        assertEquals (" ", e.getText ());
        assertTrue (e.isWhitespace ());
    }
    
    @Test
    public void testIsWhitespaceTrue2 () throws Exception
    {
        Entity e = new Entity ("&tabspace;");
        e.setResolver (new HTMLEntityResolver ());
        String s = "\t ";
        e.getResolver ().add ("tabspace", s);
        assertEquals (s, e.getText ());
        assertTrue (e.isWhitespace ());
    }
}
