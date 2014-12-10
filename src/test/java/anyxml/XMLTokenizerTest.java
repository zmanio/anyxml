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

import anyxml.Token;
import anyxml.XMLParseException;
import anyxml.XMLStringSource;
import anyxml.XMLTokenizer;
import anyxml.validation.CharValidator;

public class XMLTokenizerTest
{
    public final static String XML = 
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "\n" +
        "<!-- Comment -->\n" +
        "<root>\n" +
        "    <!-- comment -->\n" +
        "    <e>\n" +
        "\ttext&lt;&gt;\n" +
        "\t<![CDATA[text<></e>]]>\n" +
        "    </e>\n" +
        "    <empty/>\n" +
        "    <empty />\n" +
        "    <a x='1' y=\"2\" f1='\"' f2=\"'\" />\n" +
        "    < a x='1' y=\"2\" f1='\"' f2=\"&quot;'\" >b</ a >\n" +
        "</root>\n";
    
    @Test
    public void testText () throws Exception
    {
        check ("a", "Token (TEXT, 0:1, \"a\")");
    }

    @Test
    public void testValidChars () throws Exception
    {
        check ("\r\t\n\u0080\u00a0\u01ff\u00ff\ufffd", "Token (TEXT, 0:8, \"\\r\\t\\n\\u0080\\u00a0\\u01ff\u00ff\\ufffd\")");
    }
    
    @Test
    public void testSpecialCharValidator () throws Exception
    {
        CharValidator v = new CharValidator () {
            @Override
            public String isValid (int codePoint)
            {
                if (0 <= codePoint && codePoint <= 0x10FFFF)
                    return null;
                
                return "Value of character must be [#x0000-#x10FFFF]: [#x"+Integer.toHexString (codePoint)+"]";
            }
        };
        String s = "\u0000\u0010\r\t\n\u0080\u00a0\u01ff\u00ff\ufffd\uffff";
        XMLTokenizer t = new XMLTokenizer (new XMLStringSource (s)).setCharValidator (v);
        check (t, "Token (TEXT, 0:11, \"\\u0000\\u0010\\r\\t\\n\\u0080\\u00a0\\u01ff\u00ff\\ufffd\\uffff\")");
    }    
    
    @Test
    public void testPI () throws Exception
    {
        check ("<?pi ? ? ?? ?>", "Token (PROCESSING_INSTRUCTION, 0:14, \"<?pi ? ? ?? ?>\")");
    }
    
    @Test
    public void testComment () throws Exception
    {
        check ("<!-- - xxx - -->", "Token (COMMENT, 0:16, \"<!-- - xxx - -->\")");
    }
    
    @Test
    public void testCDATA () throws Exception
    {
        check ("<![CDATA[ <>&;[[ <[!CDATA[  ]> ]] ]]]]]>", "Token (CDATA, 0:40, \"<![CDATA[ <>&;[[ <[!CDATA[  ]> ]] ]]]]]>\")");
    }
    
    @Test
    public void testElement1 () throws Exception
    {
        check ("<a>", 
                "Token (BEGIN_ELEMENT, 0:2, \"<a\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 2:3, \">\")");
    }
    
    @Test
    public void testElement2 () throws Exception
    {
        check ("< a >", 
                "Token (BEGIN_ELEMENT, 0:3, \"< a\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 3:5, \" >\")");
    }
    
    @Test
    public void testElement3 () throws Exception
    {
        check ("< a x=\"1\">", 
                "Token (BEGIN_ELEMENT, 0:3, \"< a\")\r\n" + 
                "Token (ATTRIBUTE, 3:9, \" x=\\\"1\\\"\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 9:10, \">\")");
    }
    
    @Test
    public void testElement4 () throws Exception
    {
        check ("< a x=\"1\"\n    y = '2'>", 
                "Token (BEGIN_ELEMENT, 0:3, \"< a\")\r\n" + 
                "Token (ATTRIBUTE, 3:9, \" x=\\\"1\\\"\")\r\n" + 
                "Token (ATTRIBUTE, 9:21, \"\\n    y = '2'\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 21:22, \">\")");
    }
    
    @Test
    public void testElement5 () throws Exception
    {
        check ("<a-b_c.d>", 
                "Token (BEGIN_ELEMENT, 0:8, \"<a-b_c.d\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 8:9, \">\")");
    }
    
    @Test
    public void testEndElement () throws Exception
    {
        check ("</a>", "Token (END_ELEMENT, 0:4, \"</a>\")");
    }
    
    @Test
    public void testXML () throws Exception
    {
        check (XML,
                "Token (PROCESSING_INSTRUCTION, 0:38, \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?>\")\r\n" + 
                "Token (TEXT, 38:40, \"\\n\\n\")\r\n" + 
                "Token (COMMENT, 40:56, \"<!-- Comment -->\")\r\n" + 
                "Token (TEXT, 56:57, \"\\n\")\r\n" + 
                "Token (BEGIN_ELEMENT, 57:62, \"<root\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 62:63, \">\")\r\n" + 
                "Token (TEXT, 63:68, \"\\n    \")\r\n" + 
                "Token (COMMENT, 68:84, \"<!-- comment -->\")\r\n" + 
                "Token (TEXT, 84:89, \"\\n    \")\r\n" + 
                "Token (BEGIN_ELEMENT, 89:91, \"<e\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 91:92, \">\")\r\n" + 
                "Token (TEXT, 92:108, \"\\n\\ttext&lt;&gt;\\n\\t\")\r\n" + 
                "Token (CDATA, 108:130, \"<![CDATA[text<></e>]]>\")\r\n" + 
                "Token (TEXT, 130:135, \"\\n    \")\r\n" + 
                "Token (END_ELEMENT, 135:139, \"</e>\")\r\n" + 
                "Token (TEXT, 139:144, \"\\n    \")\r\n" + 
                "Token (BEGIN_ELEMENT, 144:150, \"<empty\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 150:152, \"/>\")\r\n" + 
                "Token (TEXT, 152:157, \"\\n    \")\r\n" + 
                "Token (BEGIN_ELEMENT, 157:163, \"<empty\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 163:166, \" />\")\r\n" + 
                "Token (TEXT, 166:171, \"\\n    \")\r\n" + 
                "Token (BEGIN_ELEMENT, 171:173, \"<a\")\r\n" + 
                "Token (ATTRIBUTE, 173:179, \" x='1'\")\r\n" + 
                "Token (ATTRIBUTE, 179:185, \" y=\\\"2\\\"\")\r\n" + 
                "Token (ATTRIBUTE, 185:192, \" f1='\\\"'\")\r\n" + 
                "Token (ATTRIBUTE, 192:199, \" f2=\\\"'\\\"\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 199:202, \" />\")\r\n" + 
                "Token (TEXT, 202:207, \"\\n    \")\r\n" + 
                "Token (BEGIN_ELEMENT, 207:210, \"< a\")\r\n" + 
                "Token (ATTRIBUTE, 210:216, \" x='1'\")\r\n" + 
                "Token (ATTRIBUTE, 216:222, \" y=\\\"2\\\"\")\r\n" + 
                "Token (ATTRIBUTE, 222:229, \" f1='\\\"'\")\r\n" + 
                "Token (ATTRIBUTE, 229:242, \" f2=\\\"&quot;'\\\"\")\r\n" + 
                "Token (BEGIN_ELEMENT_END, 242:244, \" >\")\r\n" + 
                "Token (TEXT, 244:245, \"b\")\r\n" + 
                "Token (END_ELEMENT, 245:251, \"</ a >\")\r\n" + 
                "Token (TEXT, 251:252, \"\\n\")\r\n" + 
                "Token (END_ELEMENT, 252:259, \"</root>\")\r\n" + 
                "Token (TEXT, 259:260, \"\\n\")" +
                "");
    }
    
    @Test
    public void testEmptyToken () throws Exception
    {
        Token token = new Token ();
        assertEquals ("Token (null, 0:0, null)", token.toString ());
    }
    
    @Test
    public void testMissingName () throws Exception
    {
        try
        {
            check ("< ", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 3: Expected valid XML name for start tag", e.getMessage ());
        }
    }
    
    @Test
    public void testMissingCloseBracket () throws Exception
    {
        try
        {
            check ("<a /", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 4: Expected '/>'", e.getMessage ());
        }
    }
    
    @Test
    public void testMissingCloseBracket2 () throws Exception
    {
        try
        {
            check ("<a /<", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 4: Expected '/>'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEndElement () throws Exception
    {
        try
        {
            check ("</a", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 4: Expected '>'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalEndElement2 () throws Exception
    {
        try
        {
            check ("</a <", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 5: Expected '>' but found \"<\"", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalComment () throws Exception
    {
        try
        {
            check (" <!!", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<!--' or '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalComment2 () throws Exception
    {
        try
        {
            check (" <!-", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<!--'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalComment3 () throws Exception
    {
        try
        {
            check (" <!-!", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<!--'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalComment4 () throws Exception
    {
        try
        {
            check (" <!--", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 6: Expected '-->'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalComment5 () throws Exception
    {
        try
        {
            check (" <!---", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 6: Expected '-->'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalComment6 () throws Exception
    {
        try
        {
            check (" <!----", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 6: Expected '-->'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalComment7 () throws Exception
    {
        try
        {
            check (" <!----x", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 6: XML comments must not contain '--'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA () throws Exception
    {
        try
        {
            check (" <![", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA2 () throws Exception
    {
        try
        {
            check (" <![x", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA3 () throws Exception
    {
        try
        {
            check (" <![C", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA4 () throws Exception
    {
        try
        {
            check (" <![Cx", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA5 () throws Exception
    {
        try
        {
            check (" <![CD", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA6 () throws Exception
    {
        try
        {
            check (" <![CDx", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA7 () throws Exception
    {
        try
        {
            check (" <![CDA", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA8 () throws Exception
    {
        try
        {
            check (" <![CDAx", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA9 () throws Exception
    {
        try
        {
            check (" <![CDAT", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA10 () throws Exception
    {
        try
        {
            check (" <![CDATx", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA11 () throws Exception
    {
        try
        {
            check (" <![CDATA", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA12 () throws Exception
    {
        try
        {
            check (" <![CDATAx", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 2: Expected '<![CDATA['", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA13 () throws Exception
    {
        try
        {
            check (" <![CDATA[", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 11: Expected ']]>'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA14 () throws Exception
    {
        try
        {
            check (" <![CDATA[]", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 11: Expected ']]>' but found \"]\"", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalCDATA15 () throws Exception
    {
        try
        {
            check (" <![CDATA[]]", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 11: Expected ']]>' but found \"]]\"", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalPI () throws Exception
    {
        try
        {
            check ("xx  <?", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 5: Missing end of processing instruction", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalPI2 () throws Exception
    {
        try
        {
            check ("xx  <? ?", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 9: Expected '>' after '?'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute () throws Exception
    {
        try
        {
            check ("<a x", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 5: Expected '='", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute2 () throws Exception
    {
        try
        {
            check ("<a x=", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 6: Expected single or double quotes", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute3 () throws Exception
    {
        try
        {
            check ("<a x='", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 3: Missing end quote (') of attribute: \" x='\"", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute4 () throws Exception
    {
        try
        {
            check ("<a x='1", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 3: Missing end quote (') of attribute: \" x='1\"", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute5 () throws Exception
    {
        try
        {
            check ("<a x='1\"", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 3: Missing end quote (') of attribute: \" x='1\\\"\"", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute6 () throws Exception
    {
        try
        {
            check ("<a x='1>", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 8: Illegal character in attribute value: '>'", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute7 () throws Exception
    {
        try
        {
            check ("<a <", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 4: Expected valid XML name for attribute but found \"<\"", e.getMessage ());
        }
    }
    
    @Test
    public void testIllegalAttribute8 () throws Exception
    {
        try
        {
            check ("<a x<1234567890123456789***", "");
            fail ("No exception was thrown");
        }
        catch (XMLParseException e)
        {
            assertEquals ("Line 1, column 5: Expected '=' but found \"<1234567890123456789...\"", e.getMessage ());
        }
    }
    
    @Test
    public void testEmptyAttribute () throws Exception
    {
        check ("<a x=''>", 
            "Token (BEGIN_ELEMENT, 0:2, \"<a\")\r\n" + 
            "Token (ATTRIBUTE, 2:7, \" x=''\")\r\n" + 
            "Token (BEGIN_ELEMENT_END, 7:8, \">\")");
    }
    
    @Test
    public void testEntities () throws Exception
    {
        String xml = "<xml>&lt;a&gt;</xml>";
        
        XMLTokenizer tokenizer = new XMLTokenizer (new XMLStringSource (xml));
        assertTrue (tokenizer.isTreatEntitiesAsText ());
        tokenizer.setTreatEntitiesAsText (false);
        check (tokenizer,
            "Token (BEGIN_ELEMENT, 0:4, \"<xml\")\r\n" + 
            "Token (BEGIN_ELEMENT_END, 4:5, \">\")\r\n" + 
            "Token (ENTITY, 5:9, \"&lt;\")\r\n" + 
            "Token (TEXT, 9:10, \"a\")\r\n" + 
            "Token (ENTITY, 10:14, \"&gt;\")\r\n" + 
            "Token (END_ELEMENT, 14:20, \"</xml>\")");
    }
    
    private void check (String xml, String expected)
    {
        XMLTokenizer tokenizer = new XMLTokenizer (new XMLStringSource (xml));
        check (tokenizer, expected);
    }

    public static void check (XMLTokenizer tokenizer, String expected)
    {
        StringBuilder buffer = new StringBuilder ();
        Token token;
        String delim = "";
        
        while ((token = tokenizer.next ()) != null)
        {
            buffer.append (delim);
            delim = "\n";
            buffer.append (token.toString ());
        }
        
        expected = expected.replaceAll ("\r\n", "\n").trim ();
        assertEquals (expected, buffer.toString ());
    }
}
