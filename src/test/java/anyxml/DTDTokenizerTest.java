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

import org.junit.Test;

import anyxml.XMLStringSource;
import anyxml.dtd.DTDTokenizer;

public class DTDTokenizerTest
{
    @Test
    public void testSimpleDocType () throws Exception
    {
        check ("<!DOCTYPE x>",
            "Token (DOCTYPE, 0:9, \"<!DOCTYPE\")\r\n" + 
            "Token (DTD_WHITESPACE, 9:10, \" \")\r\n" + 
            "Token (TEXT, 10:11, \"x\")\r\n" + 
            "Token (DOCTYPE_END, 11:12, \">\")");
    }
    
    @Test
    public void testSimpleDocType2 () throws Exception
    {
        check ("<!DOCTYPE x[]>",
            "Token (DOCTYPE, 0:9, \"<!DOCTYPE\")\r\n" + 
            "Token (DTD_WHITESPACE, 9:10, \" \")\r\n" + 
            "Token (TEXT, 10:11, \"x\")\r\n" + 
            "Token (DOCTYPE_BEGIN_SUBSET, 11:12, \"[\")\r\n" + 
            "Token (DOCTYPE_END_SUBSET, 12:13, \"]\")\r\n" + 
            "Token (DOCTYPE_END, 13:14, \">\")");
    }
    
    @Test
    public void testSimpleDocType3 () throws Exception
    {
        check ("<!DOCTYPE x []>",
            "Token (DOCTYPE, 0:9, \"<!DOCTYPE\")\r\n" + 
            "Token (DTD_WHITESPACE, 9:10, \" \")\r\n" + 
            "Token (TEXT, 10:11, \"x\")\r\n" + 
            "Token (DTD_WHITESPACE, 11:12, \" \")\r\n" + 
            "Token (DOCTYPE_BEGIN_SUBSET, 12:13, \"[\")\r\n" + 
            "Token (DOCTYPE_END_SUBSET, 13:14, \"]\")\r\n" + 
            "Token (DOCTYPE_END, 14:15, \">\")");
    }
    
    @Test
    public void testSystemDocType () throws Exception
    {
        check ("<!DOCTYPE x SYSTEM \"test.dtd\">",
            "Token (DOCTYPE, 0:9, \"<!DOCTYPE\")\r\n" + 
            "Token (DTD_WHITESPACE, 9:10, \" \")\r\n" + 
            "Token (TEXT, 10:11, \"x\")\r\n" + 
            "Token (DTD_WHITESPACE, 11:12, \" \")\r\n" + 
            "Token (DOCTYPE_SYSTEM, 12:18, \"SYSTEM\")\r\n" + 
            "Token (DTD_WHITESPACE, 18:19, \" \")\r\n" + 
            "Token (DOCTYPE_QUOTED_TEXT, 19:29, \"\\\"test.dtd\\\"\")\r\n" + 
            "Token (DOCTYPE_END, 29:30, \">\")");
    }
    
    @Test
    public void testSystemDocType2 () throws Exception
    {
        String s = DTDParserTest.SYSTEM_DOCTYPE;
        check (s, s.indexOf ("<!DOCTYPE"),
            "Token (DOCTYPE, 63:72, \"<!DOCTYPE\")\n" + 
            "Token (DTD_WHITESPACE, 72:73, \" \")\n" + 
            "Token (TEXT, 73:76, \"sql\")\n" + 
            "Token (DTD_WHITESPACE, 76:77, \" \")\n" + 
            "Token (DOCTYPE_SYSTEM, 77:83, \"SYSTEM\")\r\n" + 
            "Token (DTD_WHITESPACE, 83:84, \" \")\r\n" + 
            "Token (DOCTYPE_QUOTED_TEXT, 84:93, \"\\\"sql.dtd\\\"\")\r\n" + 
            "Token (DOCTYPE_END, 93:94, \">\")\r\n" 
        );
    }
    
    @Test
    public void testInlineDocType () throws Exception
    {
        check (DTDParserTest.INTERNAL_DOCTYPE, DTDParserTest.INTERNAL_DOCTYPE.indexOf ("<!DOCTYPE"),
            "Token (DOCTYPE, 22:31, \"<!DOCTYPE\")\n" + 
            "Token (DTD_WHITESPACE, 31:32, \" \")\n" + 
            "Token (TEXT, 32:35, \"sql\")\n" + 
            "Token (DTD_WHITESPACE, 35:36, \" \")\n" + 
            "Token (DOCTYPE_BEGIN_SUBSET, 36:37, \"[\")\n" + 
            "Token (DTD_WHITESPACE, 37:41, \"\\n   \")\n" + 
            "Token (DOCTYPE_ENTITY, 41:49, \"<!ENTITY\")\n" + 
            "Token (DTD_WHITESPACE, 49:50, \" \")\n" + 
            "Token (TEXT, 50:54, \"name\")\n" + 
            "Token (DTD_WHITESPACE, 54:55, \" \")\n" + 
            "Token (DOCTYPE_QUOTED_TEXT, 55:62, \"'value'\")\n" + 
            "Token (DTD_WHITESPACE, 62:63, \" \")\n" + 
            "Token (DOCTYPE_COMMENT, 63:76, \"-- comment --\")\n" + 
            "Token (DOCTYPE_END, 76:77, \">\")\n" + 
            "Token (DTD_WHITESPACE, 77:81, \"\\n   \")\n" + 
            "Token (DOCTYPE_ENTITY, 81:89, \"<!ENTITY\")\n" + 
            "Token (DTD_WHITESPACE, 89:90, \" \")\n" + 
            "Token (DOCTYPE_PARAMETER_ENTITY, 90:91, \"%\")\n" + 
            "Token (DTD_WHITESPACE, 91:92, \" \")\n" + 
            "Token (TEXT, 92:101, \"coreattrs\")\n" + 
            "Token (DTD_WHITESPACE, 101:103, \"\\n \")\n" + 
            "Token (DOCTYPE_QUOTED_TEXT, 103:251, \"\\\"id          ID             #IMPLIED  -- document-wide unique id --\\n  class       CDATA          #IMPLIED  -- space-separated list of classes --\\n  \\\"\")\n" + 
            "Token (DOCTYPE_END, 251:252, \">\")\n" + 
            "Token (DTD_WHITESPACE, 252:256, \"\\n   \")\n" + 
            "Token (DOCTYPE_ELEMENT, 256:265, \"<!ELEMENT\")\n" + 
            "Token (DTD_WHITESPACE, 265:266, \" \")\n" + 
            "Token (TEXT, 266:269, \"sql\")\n" + 
            "Token (DTD_WHITESPACE, 269:270, \" \")\n" + 
            "Token (DOCTYPE_BEGIN_GROUP, 270:271, \"(\")\n" + 
            "Token (DTD_WHITESPACE, 271:272, \" \")\n" + 
            "Token (DOCTYPE_BEGIN_GROUP, 272:273, \"(\")\n" + 
            "Token (DTD_WHITESPACE, 273:274, \" \")\n" + 
            "Token (TEXT, 274:275, \"a\")\n" + 
            "Token (DOCTYPE_SEQUENCE, 275:276, \",\")\n" + 
            "Token (DTD_WHITESPACE, 276:277, \" \")\n" + 
            "Token (TEXT, 277:278, \"b\")\n" + 
            "Token (DOCTYPE_ZERO_OR_MORE, 278:279, \"*\")\n" + 
            "Token (DTD_WHITESPACE, 279:280, \" \")\n" + 
            "Token (DOCTYPE_ALTERNATIVE, 280:281, \"|\")\n" + 
            "Token (DTD_WHITESPACE, 281:282, \" \")\n" + 
            "Token (TEXT, 282:283, \"c\")\n" + 
            "Token (DTD_WHITESPACE, 283:284, \" \")\n" + 
            "Token (DOCTYPE_END_GROUP, 284:285, \")\")\n" + 
            "Token (DTD_WHITESPACE, 285:286, \" \")\n" + 
            "Token (DOCTYPE_ALTERNATIVE, 286:287, \"|\")\n" + 
            "Token (DTD_WHITESPACE, 287:288, \" \")\n" + 
            "Token (DOCTYPE_PCDATA, 288:295, \"#PCDATA\")\n" + 
            "Token (DOCTYPE_END_GROUP, 295:296, \")\")\n" + 
            "Token (DOCTYPE_END, 296:297, \">\")\n" + 
            "Token (DTD_WHITESPACE, 297:301, \"\\n   \")\n" + 
            "Token (DOCTYPE_ATTLIST, 301:310, \"<!ATTLIST\")\n" + 
            "Token (DTD_WHITESPACE, 310:311, \" \")\n" + 
            "Token (TEXT, 311:314, \"sql\")\n" + 
            "Token (DTD_WHITESPACE, 314:315, \" \")\n" + 
            "Token (TEXT, 315:317, \"id\")\n" + 
            "Token (DTD_WHITESPACE, 317:318, \" \")\n" + 
            "Token (TEXT, 318:320, \"ID\")\n" + 
            "Token (DTD_WHITESPACE, 320:321, \" \")\n" + 
            "Token (DOCTYPE_IMPLIED, 321:329, \"#implied\")\n" + 
            "Token (DTD_WHITESPACE, 329:330, \" \")\n" + 
            "Token (DOCTYPE_COMMENT, 330:343, \"-- comment --\")\n" + 
            "Token (DOCTYPE_END, 343:344, \">\")\n" + 
            "Token (DOCTYPE_END_SUBSET, 344:345, \"]\")\n" + 
            "Token (DOCTYPE_END, 345:346, \">\")"
        );
    }
    
    private void check (String xml, String expected)
    {
        check (xml, 0, expected);
    }
    
    private void check (String xml, int offset, String expected)
    {
        DTDTokenizer tokenizer = new DTDTokenizer (new XMLStringSource (xml), offset);
        XMLTokenizerTest.check (tokenizer, expected);
    }


}
