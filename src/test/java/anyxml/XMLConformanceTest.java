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

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Assume;
import org.junit.Test;

import anyxml.Document;
import anyxml.EntityResolver;
import anyxml.XMLIOSource;
import anyxml.XMLParseException;
import anyxml.XMLParser;
import anyxml.XMLSource;

public class XMLConformanceTest
{
    // Download the test suite from http://www.w3.org/XML/Test/xmlconf-20031210.html
    // The download link is at the top of the document under "Test Archive".
    // The name is "xmlts20031210".
    // Unpack it in the same directory in which DecentXML is.
    public static final File XMLCONF_DIR = new File ("../xmlconf");
    // Path relative to XMLCONF_DIR
    public static final File XMLTEST_DIR = new File (XMLCONF_DIR, "xmltest");
    
    @Test public void valid_sa_001 () throws Exception { runValid ("valid/sa/001.xml"); }
    @Test public void valid_sa_002 () throws Exception { runValid ("valid/sa/002.xml"); }
    @Test public void valid_sa_003 () throws Exception { runValid ("valid/sa/003.xml"); }
    @Test public void valid_sa_004 () throws Exception { runValid ("valid/sa/004.xml"); }
    @Test public void valid_sa_005 () throws Exception { runValid ("valid/sa/005.xml"); }
    @Test public void valid_sa_006 () throws Exception { runValid ("valid/sa/006.xml"); }
    @Test public void valid_sa_007 () throws Exception { runValid ("valid/sa/007.xml"); }
    @Test public void valid_sa_008 () throws Exception { runValid ("valid/sa/008.xml"); }
    @Test public void valid_sa_009 () throws Exception { runValid ("valid/sa/009.xml"); }
    @Test public void valid_sa_010 () throws Exception { runValid ("valid/sa/010.xml"); }
    @Test public void valid_sa_011 () throws Exception { runValid ("valid/sa/011.xml"); }
    @Test public void valid_sa_012 () throws Exception { runValid ("valid/sa/012.xml"); }
    @Test public void valid_sa_013 () throws Exception { runValid ("valid/sa/013.xml"); }
    @Test public void valid_sa_014 () throws Exception { runValid ("valid/sa/014.xml"); }
    @Test public void valid_sa_015 () throws Exception { runValid ("valid/sa/015.xml"); }
    @Test public void valid_sa_016 () throws Exception { runValid ("valid/sa/016.xml"); }
    @Test public void valid_sa_017 () throws Exception { runValid ("valid/sa/017.xml"); }
    @Test public void valid_sa_018 () throws Exception { runValid ("valid/sa/018.xml"); }
    @Test public void valid_sa_019 () throws Exception { runValid ("valid/sa/019.xml"); }
    @Test public void valid_sa_020 () throws Exception { runValid ("valid/sa/020.xml"); }
    @Test public void valid_sa_021 () throws Exception { runValid ("valid/sa/021.xml"); }
    @Test public void valid_sa_022 () throws Exception { runValid ("valid/sa/022.xml"); }
    @Test public void valid_sa_023 () throws Exception { runValid ("valid/sa/023.xml"); }
    @Test public void valid_sa_024 () throws Exception { runValid ("valid/sa/024.xml"); }
    @Test public void valid_sa_025 () throws Exception { runValid ("valid/sa/025.xml"); }    @Test public void valid_sa_026 () throws Exception { runValid ("valid/sa/026.xml"); }
    @Test public void valid_sa_027 () throws Exception { runValid ("valid/sa/027.xml"); }
    @Test public void valid_sa_028 () throws Exception { runValid ("valid/sa/028.xml"); }
    @Test public void valid_sa_029 () throws Exception { runValid ("valid/sa/029.xml"); }
    @Test public void valid_sa_030 () throws Exception { runValid ("valid/sa/030.xml"); }
    @Test public void valid_sa_031 () throws Exception { runValid ("valid/sa/031.xml"); }
    @Test public void valid_sa_032 () throws Exception { runValid ("valid/sa/032.xml"); }
    @Test public void valid_sa_033 () throws Exception { runValid ("valid/sa/033.xml"); }
    @Test public void valid_sa_034 () throws Exception { runValid ("valid/sa/034.xml"); }
    @Test public void valid_sa_035 () throws Exception { runValid ("valid/sa/035.xml"); }
    @Test public void valid_sa_036 () throws Exception { runValid ("valid/sa/036.xml"); }
    @Test public void valid_sa_037 () throws Exception { runValid ("valid/sa/037.xml"); }
    @Test public void valid_sa_038 () throws Exception { runValid ("valid/sa/038.xml"); }
    @Test public void valid_sa_039 () throws Exception { runValid ("valid/sa/039.xml"); }
    @Test public void valid_sa_040 () throws Exception { runValid ("valid/sa/040.xml"); }
    @Test public void valid_sa_041 () throws Exception { runValid ("valid/sa/041.xml"); }
    @Test public void valid_sa_042 () throws Exception { runValid ("valid/sa/042.xml"); }
    @Test public void valid_sa_043 () throws Exception { runValid ("valid/sa/043.xml"); }
    @Test public void valid_sa_044 () throws Exception { runValid ("valid/sa/044.xml"); }
    @Test public void valid_sa_045 () throws Exception { runValid ("valid/sa/045.xml"); }
    @Test public void valid_sa_046 () throws Exception { runValid ("valid/sa/046.xml"); }
    @Test public void valid_sa_047 () throws Exception { runValid ("valid/sa/047.xml"); }
    @Test public void valid_sa_048 () throws Exception { runValid ("valid/sa/048.xml"); }
    @Test public void valid_sa_049 () throws Exception { runValid ("valid/sa/049.xml"); }
    @Test public void valid_sa_050 () throws Exception { runValid ("valid/sa/050.xml"); }
    @Test public void valid_sa_051 () throws Exception { runValid ("valid/sa/051.xml"); }
    @Test public void valid_sa_052 () throws Exception { runValid ("valid/sa/052.xml"); }
    @Test public void valid_sa_053 () throws Exception { runValid ("valid/sa/053.xml"); }
    @Test public void valid_sa_054 () throws Exception { runValid ("valid/sa/054.xml"); }
    @Test public void valid_sa_055 () throws Exception { runValid ("valid/sa/055.xml"); }
    @Test public void valid_sa_056 () throws Exception { runValid ("valid/sa/056.xml"); }
    @Test public void valid_sa_057 () throws Exception { runValid ("valid/sa/057.xml"); }
    @Test public void valid_sa_058 () throws Exception { runValid ("valid/sa/058.xml"); }
    @Test public void valid_sa_059 () throws Exception { runValid ("valid/sa/059.xml"); }
    @Test public void valid_sa_060 () throws Exception { runValid ("valid/sa/060.xml"); }
    @Test public void valid_sa_061 () throws Exception { runValid ("valid/sa/061.xml"); }
    @Test public void valid_sa_062 () throws Exception { runValid ("valid/sa/062.xml"); }
    @Test public void valid_sa_063 () throws Exception { runValid ("valid/sa/063.xml"); }
    @Test public void valid_sa_064 () throws Exception { runValid ("valid/sa/064.xml"); }
    @Test public void valid_sa_065 () throws Exception { runValid ("valid/sa/065.xml"); }
    @Test public void valid_sa_066 () throws Exception { runValid ("valid/sa/066.xml"); }
    @Test public void valid_sa_067 () throws Exception { runValid ("valid/sa/067.xml"); }
    @Test public void valid_sa_068 () throws Exception { runValid ("valid/sa/068.xml"); }
    @Test public void valid_sa_069 () throws Exception { runValid ("valid/sa/069.xml"); }
    @Test public void valid_sa_070 () throws Exception { runValid ("valid/sa/070.xml"); }
    @Test public void valid_sa_071 () throws Exception { runValid ("valid/sa/071.xml"); }
    @Test public void valid_sa_072 () throws Exception { runValid ("valid/sa/072.xml"); }
    @Test public void valid_sa_073 () throws Exception { runValid ("valid/sa/073.xml"); }
    @Test public void valid_sa_074 () throws Exception { runValid ("valid/sa/074.xml"); }
    @Test public void valid_sa_075 () throws Exception { runValid ("valid/sa/075.xml"); }
    @Test public void valid_sa_076 () throws Exception { runValid ("valid/sa/076.xml"); }
    @Test public void valid_sa_077 () throws Exception { runValid ("valid/sa/077.xml"); }
    @Test public void valid_sa_078 () throws Exception { runValid ("valid/sa/078.xml"); }
    @Test public void valid_sa_079 () throws Exception { runValid ("valid/sa/079.xml"); }
    @Test public void valid_sa_080 () throws Exception { runValid ("valid/sa/080.xml"); }
    @Test public void valid_sa_081 () throws Exception { runValid ("valid/sa/081.xml"); }
    @Test public void valid_sa_082 () throws Exception { runValid ("valid/sa/082.xml"); }
    @Test public void valid_sa_083 () throws Exception { runValid ("valid/sa/083.xml"); }
    @Test public void valid_sa_084 () throws Exception { runValid ("valid/sa/084.xml"); }
    @Test public void valid_sa_085 () throws Exception { runValid ("valid/sa/085.xml"); }
    @Test public void valid_sa_086 () throws Exception { runValid ("valid/sa/086.xml"); }
    @Test public void valid_sa_087 () throws Exception { runValid ("valid/sa/087.xml"); }
    @Test public void valid_sa_088 () throws Exception { runValid ("valid/sa/088.xml"); }
    @Test public void valid_sa_089 () throws Exception { runValid ("valid/sa/089.xml"); }
    @Test public void valid_sa_090 () throws Exception { runValid ("valid/sa/090.xml"); }
    @Test public void valid_sa_091 () throws Exception { runValid ("valid/sa/091.xml"); }
    @Test public void valid_sa_092 () throws Exception { runValid ("valid/sa/092.xml"); }
    @Test public void valid_sa_093 () throws Exception { runValid ("valid/sa/093.xml"); }
    @Test public void valid_sa_094 () throws Exception { runValid ("valid/sa/094.xml"); }
    @Test public void valid_sa_095 () throws Exception { runValid ("valid/sa/095.xml"); }
    @Test public void valid_sa_096 () throws Exception { runValid ("valid/sa/096.xml"); }
    @Test public void valid_sa_097 () throws Exception { runValid ("valid/sa/097.xml"); }
    @Test public void valid_sa_098 () throws Exception { runValid ("valid/sa/098.xml"); }
    @Test public void valid_sa_099 () throws Exception { runValid ("valid/sa/099.xml"); }
    @Test public void valid_sa_100 () throws Exception { runValid ("valid/sa/100.xml"); }
    @Test public void valid_sa_101 () throws Exception { runValid ("valid/sa/101.xml"); }
    @Test public void valid_sa_102 () throws Exception { runValid ("valid/sa/102.xml"); }
    @Test public void valid_sa_103 () throws Exception { runValid ("valid/sa/103.xml"); }
    @Test public void valid_sa_104 () throws Exception { runValid ("valid/sa/104.xml"); }
    @Test public void valid_sa_105 () throws Exception { runValid ("valid/sa/105.xml"); }
    @Test public void valid_sa_106 () throws Exception { runValid ("valid/sa/106.xml"); }
    @Test public void valid_sa_107 () throws Exception { runValid ("valid/sa/107.xml"); }
    @Test public void valid_sa_108 () throws Exception { runValid ("valid/sa/108.xml"); }
    @Test public void valid_sa_109 () throws Exception { runValid ("valid/sa/109.xml"); }
    @Test public void valid_sa_110 () throws Exception { runValid ("valid/sa/110.xml"); }
    @Test public void valid_sa_111 () throws Exception { runValid ("valid/sa/111.xml"); }
    @Test public void valid_sa_112 () throws Exception { runValid ("valid/sa/112.xml"); }
    @Test public void valid_sa_113 () throws Exception { runValid ("valid/sa/113.xml"); }
    @Test public void valid_sa_114 () throws Exception { runValid ("valid/sa/114.xml"); }
    @Test public void valid_sa_115 () throws Exception { runValid ("valid/sa/115.xml"); }
    @Test public void valid_sa_116 () throws Exception { runValid ("valid/sa/116.xml"); }
    @Test public void valid_sa_117 () throws Exception { runValid ("valid/sa/117.xml"); }
    @Test public void valid_sa_118 () throws Exception { runValid ("valid/sa/118.xml"); }
    @Test public void valid_sa_119 () throws Exception { runValid ("valid/sa/119.xml"); }
    @Test public void valid_not_sa_001 () throws Exception { runValid ("valid/not-sa/001.xml"); }
    @Test public void valid_not_sa_002 () throws Exception { runValid ("valid/not-sa/002.xml"); }
    @Test public void valid_not_sa_003 () throws Exception { runValid ("valid/not-sa/003.xml"); }
    @Test public void valid_not_sa_004 () throws Exception { runValid ("valid/not-sa/004.xml"); }
    @Test public void valid_not_sa_005 () throws Exception { runValid ("valid/not-sa/005.xml"); }
    @Test public void valid_not_sa_006 () throws Exception { runValid ("valid/not-sa/006.xml"); }
    @Test public void valid_not_sa_007 () throws Exception { runValid ("valid/not-sa/007.xml"); }
    @Test public void valid_not_sa_008 () throws Exception { runValid ("valid/not-sa/008.xml"); }
    @Test public void valid_not_sa_009 () throws Exception { runValid ("valid/not-sa/009.xml"); }
    @Test public void valid_not_sa_010 () throws Exception { runValid ("valid/not-sa/010.xml"); }
    @Test public void valid_not_sa_011 () throws Exception { runValid ("valid/not-sa/011.xml"); }
    @Test public void valid_not_sa_012 () throws Exception { runValid ("valid/not-sa/012.xml"); }
    @Test public void valid_not_sa_013 () throws Exception { runValid ("valid/not-sa/013.xml"); }
    @Test public void valid_not_sa_014 () throws Exception { runValid ("valid/not-sa/014.xml"); }
    @Test public void valid_not_sa_015 () throws Exception { runValid ("valid/not-sa/015.xml"); }
    @Test public void valid_not_sa_016 () throws Exception { runValid ("valid/not-sa/016.xml"); }
    @Test public void valid_not_sa_017 () throws Exception { runValid ("valid/not-sa/017.xml"); }
    @Test public void valid_not_sa_018 () throws Exception { runValid ("valid/not-sa/018.xml"); }
    @Test public void valid_not_sa_019 () throws Exception { runValid ("valid/not-sa/019.xml"); }
    @Test public void valid_not_sa_020 () throws Exception { runValid ("valid/not-sa/020.xml"); }
    @Test public void valid_not_sa_021 () throws Exception { runValid ("valid/not-sa/021.xml"); }
    @Test public void valid_not_sa_023 () throws Exception { runValid ("valid/not-sa/023.xml"); }
    @Test public void valid_not_sa_024 () throws Exception { runValid ("valid/not-sa/024.xml"); }
    @Test public void valid_not_sa_025 () throws Exception { runValid ("valid/not-sa/025.xml"); }
    @Test public void valid_not_sa_026 () throws Exception { runValid ("valid/not-sa/026.xml"); }
    @Test public void valid_not_sa_027 () throws Exception { runValid ("valid/not-sa/027.xml"); }
    @Test public void valid_not_sa_028 () throws Exception { runValid ("valid/not-sa/028.xml"); }
    @Test public void valid_not_sa_029 () throws Exception { runValid ("valid/not-sa/029.xml"); }
    @Test public void valid_not_sa_030 () throws Exception { runValid ("valid/not-sa/030.xml"); }
    // TODO Depend in external DTD @Test public void valid_not_sa_031 () throws Exception { runValid ("valid/not-sa/031.xml"); }
    @Test public void valid_ext_sa_001 () throws Exception { runValid ("valid/ext-sa/001.xml"); }
    @Test public void valid_ext_sa_002 () throws Exception { runValid ("valid/ext-sa/002.xml"); }
    @Test public void valid_ext_sa_003 () throws Exception { runValid ("valid/ext-sa/003.xml"); }
    @Test public void valid_ext_sa_004 () throws Exception { runValid ("valid/ext-sa/004.xml"); }
    @Test public void valid_ext_sa_005 () throws Exception { runValid ("valid/ext-sa/005.xml"); }
    @Test public void valid_ext_sa_006 () throws Exception { runValid ("valid/ext-sa/006.xml"); }
    @Test public void valid_ext_sa_007 () throws Exception { runValid ("valid/ext-sa/007.xml"); }
    @Test public void valid_ext_sa_008 () throws Exception { runValid ("valid/ext-sa/008.xml"); }
    @Test public void valid_ext_sa_009 () throws Exception { runValid ("valid/ext-sa/009.xml"); }
    @Test public void valid_ext_sa_010 () throws Exception { runValid ("valid/ext-sa/010.xml"); }
    // TODO Depends on external file @Test public void valid_ext_sa_011 () throws Exception { runValid ("valid/ext-sa/011.xml"); }
    @Test public void valid_ext_sa_012 () throws Exception { runValid ("valid/ext-sa/012.xml"); }
    @Test public void valid_ext_sa_013 () throws Exception { runValid ("valid/ext-sa/013.xml"); }
    @Test public void valid_ext_sa_014 () throws Exception { runValid ("valid/ext-sa/014.xml"); }
    
    // TODO Unbalanced parentheses in external DTD
    //@Test public void invalid_002 () throws Exception { run ("invalid/002.xml"); }
    //@Test public void invalid_005 () throws Exception { run ("invalid/005.xml"); }
    //@Test public void invalid_006 () throws Exception { run ("invalid/006.xml"); }
    
    // TODO Not sure what's wrong here
    //@Test public void invalid_not_sa_022 () throws Exception { run ("invalid/not-sa/022.xml"); }

    @Test public void not_wf_sa_001 () throws Exception { runInvalid ("not-wf/sa/001.xml", "Line 3, column 1: Expected valid XML name for attribute but found \"?\\r\\n<a</a>\\r\\n</doc>\\r\\n\""); }
    @Test public void not_wf_sa_002 () throws Exception { runInvalid ("not-wf/sa/002.xml", "Line 2, column 2: Expected valid XML name for start tag but found \".doc></.doc>\\r\\n</doc>...\""); }
    @Test public void not_wf_sa_003 () throws Exception { runInvalid ("not-wf/sa/003.xml", "Line 1, column 6: Missing target name in processing instruction"); }
    @Test public void not_wf_sa_004 () throws Exception { runInvalid ("not-wf/sa/004.xml", "Line 1, column 6: Missing end of processing instruction"); }
    @Test public void not_wf_sa_005 () throws Exception { runInvalid ("not-wf/sa/005.xml", "Line 1, column 6: Missing end of processing instruction"); }
    @Test public void not_wf_sa_006 () throws Exception { runInvalid ("not-wf/sa/006.xml", "Line 1, column 21: XML comments must not contain '--'"); }
    @Test public void not_wf_sa_007 () throws Exception { runInvalid ("not-wf/sa/007.xml", "Line 1, column 10: Illegal character in entity: [ ] (20)"); }
    @Test public void not_wf_sa_008 () throws Exception { runInvalid ("not-wf/sa/008.xml", "Line 1, column 6: Entity name doesn't begin with a valid character: '&.entity;'"); }
    @Test public void not_wf_sa_009 () throws Exception { runInvalid ("not-wf/sa/009.xml", "Line 1, column 6: Value of decimal entity can't be parsed: [&#RE;]"); }
    @Test public void not_wf_sa_010 () throws Exception { runInvalid ("not-wf/sa/010.xml", "Line 1, column 9: Illegal character in entity: [ ] (20)"); }
    @Test public void not_wf_sa_011 () throws Exception { runInvalid ("not-wf/sa/011.xml", "Line 1, column 8: Expected '=' but found \"></doc>\\r\\n\""); }
    @Test public void not_wf_sa_012 () throws Exception { runInvalid ("not-wf/sa/012.xml", "Line 1, column 9: Expected single or double quotes"); }
    @Test public void not_wf_sa_013 () throws Exception { runInvalid ("not-wf/sa/013.xml", "Line 1, column 13: Illegal character in attribute value: '>'"); }
    @Test public void not_wf_sa_014 () throws Exception { runInvalid ("not-wf/sa/014.xml", "Line 1, column 10: Illegal character in attribute value: '<'"); }
    @Test public void not_wf_sa_015 () throws Exception { runInvalid ("not-wf/sa/015.xml", "Line 1, column 9: Expected single or double quotes"); }
    @Test public void not_wf_sa_016 () throws Exception { runInvalid ("not-wf/sa/016.xml", "Line 1, column 14: Expected valid XML name for attribute but found \"\\\"v2\\\"></doc>\\r\\n\""); }
    @Test public void not_wf_sa_017 () throws Exception { runInvalid ("not-wf/sa/017.xml", "Line 2, column 1: Expected ']]>'"); }
    @Test public void not_wf_sa_018 () throws Exception { runInvalid ("not-wf/sa/018.xml", "Line 1, column 6: Expected '<![CDATA['"); }
    @Test public void not_wf_sa_019 () throws Exception { runInvalid ("not-wf/sa/019.xml", "Line 1, column 8: Expected valid XML name for end tag but found \">\\r\\n\""); }
    @Test public void not_wf_sa_020 () throws Exception { runInvalid ("not-wf/sa/020.xml", "Line 1, column 12: Missing ';' after '&': \"& B\\\"></doc>\\r\\n\""); }
    @Test public void not_wf_sa_021 () throws Exception { runInvalid ("not-wf/sa/021.xml", "Line 1, column 11: Missing ';' after '&': \"&b\\\"></doc>\\r\\n\""); }
    @Test public void not_wf_sa_022 () throws Exception { runInvalid ("not-wf/sa/022.xml", "Line 1, column 10: Missing ';' after '&': \"&#123:\\\"></doc>\\r\\n\""); }
    @Test public void not_wf_sa_023 () throws Exception { runInvalid ("not-wf/sa/023.xml", "Line 1, column 6: Expected valid XML name for attribute but found \"12=\\\"34\\\"></doc>\\r\\n\""); }
    @Test public void not_wf_sa_024 () throws Exception { runInvalid ("not-wf/sa/024.xml", "Line 2, column 2: Expected valid XML name for start tag but found \"123></123>\\r\\n</doc>\\r\\n\""); }
    @Test public void not_wf_sa_025 () throws Exception { runInvalid ("not-wf/sa/025.xml", "Line 1, column 8: Please replace the '>' of ']]>' in character data with '&gt;'"); }
    @Test public void not_wf_sa_026 () throws Exception { runInvalid ("not-wf/sa/026.xml", "Line 1, column 9: Please replace the '>' of ']]>' in character data with '&gt;'"); }
    @Test public void not_wf_sa_027 () throws Exception { runInvalid ("not-wf/sa/027.xml", "Line 4, column 1: Expected '-->'"); }
    @Test public void not_wf_sa_028 () throws Exception { runInvalid ("not-wf/sa/028.xml", "Line 2, column 1: Missing end of processing instruction"); }
    @Test public void not_wf_sa_029 () throws Exception { runInvalid ("not-wf/sa/029.xml", "Line 1, column 12: Please replace the '>' of ']]>' in character data with '&gt;'"); }
    @Test public void not_wf_sa_030 () throws Exception { runInvalid ("not-wf/sa/030.xml", "Line 1, column 19: Illegal character found in text. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xc]"); }
    @Test public void not_wf_sa_031 () throws Exception { runInvalid ("not-wf/sa/031.xml", "Line 1, column 24: Illegal character found in processing instruction. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xc]"); }
    @Test public void not_wf_sa_032 () throws Exception { runInvalid ("not-wf/sa/032.xml", "Line 1, column 24: Illegal character found in comment. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xc]"); }
    @Test public void not_wf_sa_033 () throws Exception { runInvalid ("not-wf/sa/033.xml", "Line 1, column 9: Illegal character found in text. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#x1b]"); }
    @Test public void not_wf_sa_034 () throws Exception { runInvalid ("not-wf/sa/034.xml", "Line 1, column 5: Expected whitespace, '>' or '/>' after element name"); }
    @Test public void not_wf_sa_035 () throws Exception { runInvalid ("not-wf/sa/035.xml", "Line 1, column 10: Expected valid XML name for start tag but found \"2 but not in XML</do...\""); }
    @Test public void not_wf_sa_036 () throws Exception { runInvalid ("not-wf/sa/036.xml", "Line 1, column 12: TEXT node is not allowed here"); }
    @Test public void not_wf_sa_037 () throws Exception { runInvalid ("not-wf/sa/037.xml", "Line 2, column 1: ENTITY node is not allowed here"); }
    @Test public void not_wf_sa_038 () throws Exception { runInvalid ("not-wf/sa/038.xml", "Line 1, column 21: There is already an attribute with the name x"); }
    @Test public void not_wf_sa_039 () throws Exception { runInvalid ("not-wf/sa/039.xml", "Line 1, column 6: End element 'aa' at line 1, column 9 doesn't match with 'a'"); }
    @Test public void not_wf_sa_040 () throws Exception { runInvalid ("not-wf/sa/040.xml", "Line 2, column 1: Only one root element allowed per document"); }
    @Test public void not_wf_sa_041 () throws Exception { runInvalid ("not-wf/sa/041.xml", "Line 2, column 1: Only one root element allowed per document"); }
    @Test public void not_wf_sa_042 () throws Exception { runInvalid ("not-wf/sa/042.xml", "Line 1, column 12: Expected '>' but found \"/>\\r\\n\""); }
    @Test public void not_wf_sa_043 () throws Exception { runInvalid ("not-wf/sa/043.xml", "Line 1, column 7: TEXT node is not allowed here"); }
    @Test public void not_wf_sa_044 () throws Exception { runInvalid ("not-wf/sa/044.xml", "Line 1, column 7: Only one root element allowed per document"); }
    @Test public void not_wf_sa_045 () throws Exception { runInvalid ("not-wf/sa/045.xml", "Line 2, column 3: Expected '/>'"); }
    @Test public void not_wf_sa_046 () throws Exception { runInvalid ("not-wf/sa/046.xml", "Line 2, column 3: Expected '/>'"); }
    @Test public void not_wf_sa_047 () throws Exception { runInvalid ("not-wf/sa/047.xml", "Line 2, column 4: Expected '/>'"); }
    @Test public void not_wf_sa_048 () throws Exception { runInvalid ("not-wf/sa/048.xml", "Line 3, column 1: CDATA node is not allowed here"); }
    @Test public void not_wf_sa_049 () throws Exception { runInvalid ("not-wf/sa/049.xml", "Line 1, column 1: End element 'a' at line 3, column 13 doesn't match with 'doc'"); }
    @Test public void not_wf_sa_050 () throws Exception { runInvalid ("not-wf/sa/050.xml", "No root element found"); }
    @Test public void not_wf_sa_051 () throws Exception { runInvalid ("not-wf/sa/051.xml", "Line 2, column 1: CDATA node is not allowed here"); }
    @Test public void not_wf_sa_052 () throws Exception { runInvalid ("not-wf/sa/052.xml", "Line 2, column 1: ENTITY node is not allowed here"); }
    @Test public void not_wf_sa_053 () throws Exception { runInvalid ("not-wf/sa/053.xml", "Line 1, column 1: End element 'DOC' at line 1, column 6 doesn't match with 'doc'"); }
    @Test public void not_wf_sa_054 () throws Exception { runInvalid ("not-wf/sa/054.xml", "Line 2, column 37: Expected whitespace after public ID literal but found \">\\r\\n]>\\r\\n<doc></doc>\\r\\n\" (Token (DOCTYPE_END, 53:54, \">\"))"); }
    @Test public void not_wf_sa_055 () throws Exception { runInvalid ("not-wf/sa/055.xml", "Line 2, column 2: Expected '!' but found \"doc></doc>\\r\\n\""); }
    @Test public void not_wf_sa_056 () throws Exception { runInvalid ("not-wf/sa/056.xml", "Line 1, column 15: Expected '>', got Token (DOCTYPE_COMMENT, 14:29, \"-- a comment --\")"); }
    // TODO This isn't SGML; comments can't exist in declarations.  @Test public void not_wf_sa_057 () throws Exception { runInvalid ("not-wf/sa/057.xml", ""); }
    @Test public void not_wf_sa_058 () throws Exception { runInvalid ("not-wf/sa/058.xml", "Line 3, column 22: Unexpected token Token (DOCTYPE_SEQUENCE, 64:65, \",\")"); }
    @Test public void not_wf_sa_059 () throws Exception { runInvalid ("not-wf/sa/059.xml", "Line 3, column 26: Expected #IMPLIED or quoted text: Token (TEXT, 68:70, \"v1\")"); }
    // TODO Invalid type NAME defined in ATTLIST. @Test public void not_wf_sa_060 () throws Exception { runInvalid ("not-wf/sa/060.xml", "Invalid type NAME defined in ATTLIST."); }
    @Test public void not_wf_sa_061 () throws Exception { runInvalid ("not-wf/sa/061.xml", "Line 2, column 29: Expected whitespace after public ID literal but found \"\\\"e.ent\\\">\\r\\n]>\\r\\n<doc><...\" (Token (DOCTYPE_QUOTED_TEXT, 45:52, \"\\\"e.ent\\\"\"))"); }
    @Test public void not_wf_sa_062 () throws Exception { runInvalid ("not-wf/sa/062.xml", "Line 2, column 13: Expected whitespace after entity name but found \"\\\"some text\\\">\\r\\n]>\\r\\n<d...\" (Token (DOCTYPE_QUOTED_TEXT, 29:40, \"\\\"some text\\\"\"))"); }
    @Test public void not_wf_sa_063 () throws Exception { runInvalid ("not-wf/sa/063.xml", "Line 2, column 1: Expected '<!ATTLIST', '<!DOCTYPE', '<!ELEMENT' or '<!ENTITY'"); }
    @Test public void not_wf_sa_064 () throws Exception { runInvalid ("not-wf/sa/064.xml", "Line 3, column 21: Expected whitespace after attribute type but found \"\\\"foo\\\">\\r\\n]>\\r\\n<doc></d...\" (Token (DOCTYPE_QUOTED_TEXT, 63:68, \"\\\"foo\\\"\"))"); }
    @Test public void not_wf_sa_065 () throws Exception { runInvalid ("not-wf/sa/065.xml", "Line 3, column 17: Expected whitespace after attribute name but found \"(foo|bar) #IMPLIED>\\r...\" (Token (DOCTYPE_BEGIN_GROUP, 59:60, \"(\"))"); }
    @Test public void not_wf_sa_066 () throws Exception { runInvalid ("not-wf/sa/066.xml", "Line 3, column 27: Expected whitespace after list of alternatives but found \"#IMPLIED>\\r\\n]>\\r\\n<doc>...\" (Token (DOCTYPE_IMPLIED, 69:77, \"#IMPLIED\"))"); }
    @Test public void not_wf_sa_067 () throws Exception { runInvalid ("not-wf/sa/067.xml", "Line 3, column 23: Expected whitespace after list of alternatives but found \"\\\"foo\\\">\\r\\n]>\\r\\n<doc></d...\" (Token (DOCTYPE_QUOTED_TEXT, 65:70, \"\\\"foo\\\"\"))"); }
    @Test public void not_wf_sa_068 () throws Exception { runInvalid ("not-wf/sa/068.xml", "Line 3, column 26: Expected whitespace after attribute type but found \"(foo) #IMPLIED>\\r\\n]>\\r...\" (Token (DOCTYPE_BEGIN_GROUP, 68:69, \"(\"))"); }
    @Test public void not_wf_sa_069 () throws Exception { runInvalid ("not-wf/sa/069.xml", "Line 4, column 30: Space is required before an NDATA entity annotation"); }
    @Test public void not_wf_sa_070 () throws Exception { runInvalid ("not-wf/sa/070.xml", "Line 1, column 41: XML comments must not contain '--'"); }
    // TODO Test for endless loop in entity expansion @Test public void not_wf_sa_071 () throws Exception { runInvalid ("not-wf/sa/071.xml", ""); }
    // TODO No error; entities are only checked when a resolver is installed  @Test public void not_wf_sa_072 () throws Exception { runInvalid ("not-wf/sa/072.xml", ""); }
    // TODO No error; entities are only checked when a resolver is installed  @Test public void not_wf_sa_073 () throws Exception { runInvalid ("not-wf/sa/073.xml", ""); }
    // TODO Not-wellformed content of entity @Test public void not_wf_sa_074 () throws Exception { runInvalid ("not-wf/sa/074.xml", ""); }
    // TODO Test for endless loop in entity expansion @Test public void not_wf_sa_075 () throws Exception { runInvalid ("not-wf/sa/075.xml", ""); }
    // TODO Reference to unknown entity foo @Test public void not_wf_sa_076 () throws Exception { runInvalid ("not-wf/sa/076.xml", ""); }
    // TODO Reference to unknown entity bar @Test public void not_wf_sa_077 () throws Exception { runInvalid ("not-wf/sa/077.xml", ""); }
    // TODO Undefined reference in default attribute of ATTLIST @Test public void not_wf_sa_078 () throws Exception { runInvalid ("not-wf/sa/078.xml", ""); }
    // TODO Test for endless loop in entity expansion @Test public void not_wf_sa_079 () throws Exception { runInvalid ("not-wf/sa/079.xml", ""); }
    // TODO Test for endless loop in entity expansion @Test public void not_wf_sa_080 () throws Exception { runInvalid ("not-wf/sa/080.xml", ""); }
    // TODO Illegal use of external entity @Test public void not_wf_sa_081 () throws Exception { runInvalid ("not-wf/sa/081.xml", ""); }
    // TODO Illegal use of external entity @Test public void not_wf_sa_082 () throws Exception { runInvalid ("not-wf/sa/082.xml", ""); }
    // TODO Illegal use of external entity @Test public void not_wf_sa_083 () throws Exception { runInvalid ("not-wf/sa/083.xml", ""); }
    // TODO Illegal use of external entity @Test public void not_wf_sa_084 () throws Exception { runInvalid ("not-wf/sa/084.xml", ""); }
    // TODO Illegal use of external entity @Test public void not_wf_sa_085 () throws Exception { runInvalid ("not-wf/sa/085.xml", ""); }
    // TODO Illegal use of external entity @Test public void not_wf_sa_086 () throws Exception { runInvalid ("not-wf/sa/086.xml", ""); }
    @Test public void not_wf_sa_087 () throws Exception { runInvalid ("not-wf/sa/087.xml", "Line 2, column 37: Expected '>' after notation declaration but found \"\\\"null.ent\\\">\\r\\n]>\\r\\n<do...\""); }
    @Test public void not_wf_sa_088 () throws Exception { runInvalid ("not-wf/sa/088.xml", "Line 6, column 12: Illegal character in attribute value: '>'"); }
    @Test public void not_wf_sa_089 () throws Exception { runInvalid ("not-wf/sa/089.xml", "Line 2, column 33: Parameter entities are always parsed; NDATA annotations are not permitted"); }
    // TODO There is a '<' in the attribute 'a' because of reference expansion @Test public void not_wf_sa_090 () throws Exception { runInvalid ("not-wf/sa/090.xml", ""); }
    @Test public void not_wf_sa_091 () throws Exception { runInvalid ("not-wf/sa/091.xml", "Line 3, column 33: Parameter entities are always parsed; NDATA annotations are not permitted"); }
    // TODO The replacement text for &e; is illegal here because &#38; is expanded immediately @Test public void not_wf_sa_092 () throws Exception { runInvalid ("not-wf/sa/092.xml", ""); }
    @Test public void not_wf_sa_093 () throws Exception { runInvalid ("not-wf/sa/093.xml", "Line 1, column 6: Value of decimal entity can't be parsed: [&#X58;]"); }
    @Test public void not_wf_sa_094 () throws Exception { runInvalid ("not-wf/sa/094.xml", "Line 1, column 21: Missing version in XML declaration"); }
    @Test public void not_wf_sa_095 () throws Exception { runInvalid ("not-wf/sa/095.xml", "Line 1, column 7: Version must be before encoding"); }
    @Test public void not_wf_sa_096 () throws Exception { runInvalid ("not-wf/sa/096.xml", "Line 1, column 20: Expecting whitespace between attributes of XML declaration"); }
    @Test public void not_wf_sa_097 () throws Exception { runInvalid ("not-wf/sa/097.xml", "Line 1, column 19: Quote mismatch: Expected [\"], found [']"); }
    @Test public void not_wf_sa_098 () throws Exception { runInvalid ("not-wf/sa/098.xml", "Line 1, column 21: Found a second version attribute"); }
    @Test public void not_wf_sa_099 () throws Exception { runInvalid ("not-wf/sa/099.xml", "Line 1, column 21: Expected 'version' but found 'valid=\"'"); }
    @Test public void not_wf_sa_100 () throws Exception { runInvalid ("not-wf/sa/100.xml", "Line 1, column 37: Allowed values for standalone are 'yes' and 'no', found 'YES'"); }
    @Test public void not_wf_sa_101 () throws Exception { runInvalid ("not-wf/sa/101.xml", "Line 1, column 31: Error parsing XML declaration: Unsupported encoding ' UTF-8'"); }
    @Test public void not_wf_sa_102 () throws Exception { runInvalid ("not-wf/sa/102.xml", "Line 1, column 23: Missing version in XML declaration"); }
    // TODO &#60; gets expanded to '<' which makes the document illegal @Test public void not_wf_sa_103 () throws Exception { runInvalid ("not-wf/sa/103.xml", ""); }
    @Test public void not_wf_sa_104 () throws Exception { runInvalid ("not-wf/sa/104.xml", "Line 4, column 6: Error while expanding entity &e;: Line 1, column 1: Unexpected end-of-file while parsing children of element foo"); }
    @Test public void not_wf_sa_105 () throws Exception { runInvalid ("not-wf/sa/105.xml", "Line 2, column 1: CDATA node is not allowed here"); }
    @Test public void not_wf_sa_106 () throws Exception { runInvalid ("not-wf/sa/106.xml", "Line 2, column 1: ENTITY node is not allowed here"); }
    @Test public void not_wf_sa_107 () throws Exception { runInvalid ("not-wf/sa/107.xml", "Line 2, column 1: Expected '<!ATTLIST', '<!DOCTYPE', '<!ELEMENT' or '<!ENTITY'"); }
    @Test public void not_wf_sa_108 () throws Exception { runInvalid ("not-wf/sa/108.xml", "Line 2, column 1: Expected '<![CDATA['"); }
    @Test public void not_wf_sa_109 () throws Exception { runInvalid ("not-wf/sa/109.xml", "Line 4, column 1: ENTITY node is not allowed here"); }
    @Test public void not_wf_sa_110 () throws Exception { runInvalid ("not-wf/sa/110.xml", "Line 5, column 1: ENTITY node is not allowed here"); }
    @Test public void not_wf_sa_111 () throws Exception { runInvalid ("not-wf/sa/111.xml", "Line 4, column 6: Expected valid XML name for attribute but found \"&e;></doc>\\r\\n\""); }
    @Test public void not_wf_sa_112 () throws Exception { runInvalid ("not-wf/sa/112.xml", "Line 2, column 1: Expected '<![CDATA['"); }
    @Test public void not_wf_sa_113 () throws Exception { runInvalid ("not-wf/sa/113.xml", "Line 2, column 17: Missing ';' after '&': \"&\\\">\\r\\n]>\\r\\n<doc></doc>...\""); }
    @Test public void not_wf_sa_114 () throws Exception { runInvalid ("not-wf/sa/114.xml", "Line 2, column 15: Missing ';' after '&': \"&\\\">\\r\\n]>\\r\\n<doc></doc>...\""); }
    // TODO &#38; expands to & @Test public void not_wf_sa_115 () throws Exception { runInvalid ("not-wf/sa/115.xml", ""); }
    // TODO &e; expands to "&#9" which is illegal because it's just a partial entity @Test public void not_wf_sa_116 () throws Exception { runInvalid ("not-wf/sa/116.xml", ""); }
    // TODO &#38; expands to & @Test public void not_wf_sa_117 () throws Exception { runInvalid ("not-wf/sa/117.xml", ""); }
    // TODO Entity reference expansion is not recursive @Test public void not_wf_sa_118 () throws Exception { runInvalid ("not-wf/sa/118.xml", ""); }
    // TODO &e; expands to "&" which is illegal because it's just a partial entity @Test public void not_wf_sa_119 () throws Exception { runInvalid ("not-wf/sa/119.xml", ""); }
    // TODO &e; expands to "&" which is illegal because it's just a partial entity @Test public void not_wf_sa_120 () throws Exception { runInvalid ("not-wf/sa/120.xml", ""); }
    @Test public void not_wf_sa_121 () throws Exception { runInvalid ("not-wf/sa/121.xml", "Line 2, column 10: Expected '#IMPLIED' or '#PCDATA' but found \"#DEFAULT \\\"default\\\">\\r...\""); }
    // TODO Invalid syntax mixed connectors are used. (???) @Test public void not_wf_sa_122 () throws Exception { runInvalid ("not-wf/sa/122.xml", ""); }
    @Test public void not_wf_sa_123 () throws Exception { runInvalid ("not-wf/sa/123.xml", "Line 5, column 1: Unexpected EOF after '<!DOCTYPE'"); }
    // TODO Invalid format of Mixed-content declaration. @Test public void not_wf_sa_124 () throws Exception { runInvalid ("not-wf/sa/124.xml", ""); }
    // TODO Invalid syntax extra set of parenthesis not necessary @Test public void not_wf_sa_125 () throws Exception { runInvalid ("not-wf/sa/125.xml", ""); }
    // TODO Invalid syntax Mixed-content must be defined as zero or more. @Test public void not_wf_sa_126 () throws Exception { runInvalid ("not-wf/sa/126.xml", ""); }
    // TODO Invalid syntax Mixed-content must be defined as zero or more @Test public void not_wf_sa_127 () throws Exception { runInvalid ("not-wf/sa/127.xml", ""); }
    // TODO Check content of <!ELEMENT @Test public void not_wf_sa_128 () throws Exception { runInvalid ("not-wf/sa/128.xml", "Invalid CDATA syntax"); }
    @Test public void not_wf_sa_129 () throws Exception { runInvalid ("not-wf/sa/129.xml", "Line 2, column 16: Expected '-' but found \" - (#PCDATA)>\\r\\n]>\\r\\n<...\""); }
    // TODO Invalid syntax for Element Type Declaration @Test public void not_wf_sa_130 () throws Exception { runInvalid ("not-wf/sa/130.xml", ""); }
    @Test public void not_wf_sa_131 () throws Exception { runInvalid ("not-wf/sa/131.xml", "Line 2, column 23: Expected '-' but found \"(foo)>\\r\\n]>\\r\\n<doc></d...\""); }
    // TODO Invalid syntax mixed connectors used @Test public void not_wf_sa_132 () throws Exception { runInvalid ("not-wf/sa/132.xml", ""); }
    // TODO Illegal whitespace before optional character causes syntax error @Test public void not_wf_sa_133 () throws Exception { runInvalid ("not-wf/sa/133.xml", ""); }
    // TODO Illegal whitespace before optional character causes syntax error @Test public void not_wf_sa_134 () throws Exception { runInvalid ("not-wf/sa/134.xml", ""); }
    @Test public void not_wf_sa_135 () throws Exception { runInvalid ("not-wf/sa/135.xml", "Line 2, column 18: Expected some text but found \"& b)?>\\r\\n]>\\r\\n<doc></d...\""); }
    // TODO Tag omission is invalid in XML @Test public void not_wf_sa_136 () throws Exception { runInvalid ("not-wf/sa/136.xml", ""); }
    @Test public void not_wf_sa_137 () throws Exception { runInvalid ("not-wf/sa/137.xml", "Line 2, column 14: Expected whitespace after element name but found \"(#PCDATA)>\\r\\n]>\\r\\n<doc...\" (Token (DOCTYPE_BEGIN_GROUP, 30:31, \"(\"))"); }
    // TODO Invalid syntax for content particle @Test public void not_wf_sa_138 () throws Exception { runInvalid ("not-wf/sa/138.xml", ""); }
    // TODO The element-content model should not be empty @Test public void not_wf_sa_139 () throws Exception { runInvalid ("not-wf/sa/139.xml", ""); }
    // Character '&#x309a;' is a CombiningChar, not a Letter, and so may not begin a name 
    @Test public void not_wf_sa_140 () throws Exception { runInvalid ("not-wf/sa/140.xml", "Line 4, column 6: Error while expanding entity &e;: Line 1, column 2: Expected valid XML name for start tag but found \"\\u309a></\\u309a>\""); }
    // Character #x0E5C is not legal in XML names 
    @Test public void not_wf_sa_141 () throws Exception { runInvalid ("not-wf/sa/141.xml", "Line 4, column 6: Error while expanding entity &e;: Line 1, column 3: Expected whitespace, '>' or '/>' after element name"); }
    @Test public void not_wf_sa_142 () throws Exception { runInvalid ("not-wf/sa/142.xml", "Line 4, column 6: Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#x0]"); }
    @Test public void not_wf_sa_143 () throws Exception { runInvalid ("not-wf/sa/143.xml", "Line 4, column 6: Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#x1f]"); }
    @Test public void not_wf_sa_144 () throws Exception { runInvalid ("not-wf/sa/144.xml", "Line 4, column 6: Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_145 () throws Exception { runInvalid ("not-wf/sa/145.xml", "Line 4, column 6: Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xd800]"); }
    @Test public void not_wf_sa_146 () throws Exception { runInvalid ("not-wf/sa/146.xml", "Line 4, column 6: Illegal value for numeric entity. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#x110000]"); }
    @Test public void not_wf_sa_147 () throws Exception { runInvalid ("not-wf/sa/147.xml", "Line 2, column 1: An XML declaration is only allowed as the first node in the document"); }
    @Test public void not_wf_sa_148 () throws Exception { runInvalid ("not-wf/sa/148.xml", "Line 2, column 1: An XML declaration is only allowed as the first node in the document"); }
    @Test public void not_wf_sa_149 () throws Exception { runInvalid ("not-wf/sa/149.xml", "Line 3, column 2: Expected '!' but found \"?xml version=\\\"1.0\\\"?>...\""); }
    @Test public void not_wf_sa_150 () throws Exception { runInvalid ("not-wf/sa/150.xml", "Line 2, column 1: The XML declaration must be the first node of the document"); }
    @Test public void not_wf_sa_151 () throws Exception { runInvalid ("not-wf/sa/151.xml", "Line 3, column 1: An XML declaration is only allowed as the first node in the document"); }
    @Test public void not_wf_sa_152 () throws Exception { runInvalid ("not-wf/sa/152.xml", "Line 1, column 24: Missing version in XML declaration"); }
    // TODO Entity expands to XML declaration @Test public void not_wf_sa_153 () throws Exception { runInvalid ("not-wf/sa/153.xml", ""); }
    @Test public void not_wf_sa_154 () throws Exception { runInvalid ("not-wf/sa/154.xml", "Line 1, column 1: Expected '<?xml'"); }
    @Test public void not_wf_sa_155 () throws Exception { runInvalid ("not-wf/sa/155.xml", "Line 1, column 1: Expected '<?xml'"); }
    @Test public void not_wf_sa_156 () throws Exception { runInvalid ("not-wf/sa/156.xml", "Line 2, column 1: The XML declaration must be the first node of the document"); }
    @Test public void not_wf_sa_157 () throws Exception { runInvalid ("not-wf/sa/157.xml", "Line 2, column 1: The XML declaration must be the first node of the document"); }
    @Test public void not_wf_sa_158 () throws Exception { runInvalid ("not-wf/sa/158.xml", "Line 3, column 37: Expected '>' after notation declaration but found \"\\\"\\\">\\r\\n<!ATTLIST #NOTA...\""); }
    @Test public void not_wf_sa_159 () throws Exception { runInvalid ("not-wf/sa/159.xml", "Line 3, column 26: Missing ';' after '&': \"& Michael]]>\\\">\\r\\n]>\\r\\n...\""); }
    // TODO Violates the PEs in Internal Subset WFC by using a PE reference within a declaration @Test public void not_wf_sa_160 () throws Exception { runInvalid ("not-wf/sa/160.xml", ""); }
    // TODO Violates the PEs in Internal Subset WFC by using a PE reference within a declaration @Test public void not_wf_sa_161 () throws Exception { runInvalid ("not-wf/sa/161.xml", ""); }
    // TODO Violates the PEs in Internal Subset WFC by using a PE reference within a declaration @Test public void not_wf_sa_162 () throws Exception { runInvalid ("not-wf/sa/162.xml", ""); }
    @Test public void not_wf_sa_163 () throws Exception { runInvalid ("not-wf/sa/163.xml", "Line 4, column 3: TEXT node is not allowed here"); }
    @Test public void not_wf_sa_164 () throws Exception { runInvalid ("not-wf/sa/164.xml", "Line 4, column 3: Expected '>', got Token (DOCTYPE_PARAMETER_ENTITY, 63:64, \"%\")"); }
    @Test public void not_wf_sa_165 () throws Exception { runInvalid ("not-wf/sa/165.xml", "Line 2, column 9: Expected whitespace after '<!ENTITY' but found \"% e \\\"\\\">\\r\\n<!ELEMENT d...\" (Token (DOCTYPE_PARAMETER_ENTITY, 25:26, \"%\"))"); }
    @Test public void not_wf_sa_166 () throws Exception { runInvalid ("not-wf/sa/166.xml", "Line 1, column 6: Illegal character found in text. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_167 () throws Exception { runInvalid ("not-wf/sa/167.xml", "Line 1, column 6: Illegal character found in text. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xfffe]"); }
    @Test public void not_wf_sa_168 () throws Exception { runInvalid ("not-wf/sa/168.xml", "Line 1, column 6: Illegal character found in text. Character after first in surrogate pair is not between 0xDC00 and 0xDFFF: 3c"); }
    @Test public void not_wf_sa_169 () throws Exception { runInvalid ("not-wf/sa/169.xml", "Line 1, column 6: Illegal character found in text. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xdc00]"); }
    // Four byte UTF-8 encodings can encode UCS-4 characters which are beyond the range of legal XML characters (and can't be expressed in Unicode surrogate pairs). This document holds such a character.
    // TODO Java probably swallows this :/ @Test public void not_wf_sa_170 () throws Exception { runInvalid ("not-wf/sa/170.xml", ""); }
    @Test public void not_wf_sa_171 () throws Exception { runInvalid ("not-wf/sa/171.xml", "Line 1, column 6: Illegal character found in comment. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_172 () throws Exception { runInvalid ("not-wf/sa/172.xml", "Line 1, column 6: Illegal character found in processing instruction. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_173 () throws Exception { runInvalid ("not-wf/sa/173.xml", "Line 1, column 9: Illegal character found in attribute value. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_174 () throws Exception { runInvalid ("not-wf/sa/174.xml", "Line 1, column 15: Illegal character found in CDATA. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_175 () throws Exception { runInvalid ("not-wf/sa/175.xml", "Line 3, column 15: Illegal character found in quoted text. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_176 () throws Exception { runInvalid ("not-wf/sa/176.xml", "Line 4, column 1: Unexpected end-of-file while parsing children of element doc"); }
    @Test public void not_wf_sa_177 () throws Exception { runInvalid ("not-wf/sa/177.xml", "Line 4, column 7: Illegal character found in text. Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]"); }
    @Test public void not_wf_sa_178 () throws Exception { runInvalid ("not-wf/sa/178.xml", "Line 5, column 14: Illegal character in attribute value: '>'"); }
    @Test public void not_wf_sa_179 () throws Exception { runInvalid ("not-wf/sa/179.xml", "Line 2, column 12: Couldn't find closing quote"); }
    // TODO The Entity Declared WFC requires entities to be declared before they are used in an attribute list declaration @Test public void not_wf_sa_180 () throws Exception { runInvalid ("not-wf/sa/180.xml", ""); }
    @Test public void not_wf_sa_181 () throws Exception { runInvalid ("not-wf/sa/181.xml", "Line 5, column 6: Error while expanding entity &e;: Line 1, column 10: Expected ']]>'"); }
    // TODO Internal parsed entities must match the content  production to be well formed @Test public void not_wf_sa_182 () throws Exception { runInvalid ("not-wf/sa/182.xml", ""); }
    // TODO Mixed content declarations may not include content particles @Test public void not_wf_sa_183 () throws Exception { runInvalid ("not-wf/sa/183.xml", ""); }
    // TODO In mixed content models, element names must not be parenthesized @Test public void not_wf_sa_184 () throws Exception { runInvalid ("not-wf/sa/184.xml", ""); }
    // TODO Tests the Entity Declared WFC. Note: a nonvalidating parser is permitted not to report this WFC violation, since it would need to read an external parameter entity to distinguish it from a violation of the Standalone Declaration VC @Test public void not_wf_sa_185 () throws Exception { runInvalid ("not-wf/sa/185.xml", ""); }
    @Test public void not_wf_sa_186 () throws Exception { runInvalid ("not-wf/sa/186.xml", "Line 5, column 9: Expected whitespace between attributes of element a but found Token (ATTRIBUTE, 95:100, \"d=\\\"e\\\"\")"); }

    private void runInvalid (String fileName, String exceptionMessage) throws IOException
    {
        try
        {
            XMLParser p = new XMLParser ();
            p.setEntityResolver (new EntityResolver ());
            Document doc = run (p, fileName);
            fail ("No exception was thrown:\n"+doc.toXML ());
        }
        catch (XMLParseException e)
        {
            if (!exceptionMessage.equals (e.getMessage ()))
            {
                System.err.println ("--- "+fileName+" -----------------------------------------");
                e.printStackTrace ();
            }
            
            String s = "";
            try
            {
                s = XMLIOSource.toString (new File (XMLTEST_DIR, fileName));
            }
            catch (Exception ignore)
            {
                // Ignore
            }
            assertEquals ("Document:\n"+s, exceptionMessage, e.getMessage ());
        }
    }

    private static boolean printedWarning;
    
    private Document runValid (String fileName) throws Exception
    {
        if (!checkTestSuite ())
            return null;
        
        XMLParser p = new XMLParser ();
        p.setEntityResolver (new EntityResolver ());
        
        File f = new File (XMLTEST_DIR, fileName);
        if (!f.exists ())
            fail ("Test "+fileName+" not found");
        
        XMLSource s = new XMLIOSource (f);
        String expected = s.substring (0, s.length ());
        try
        {
            Document doc = p.parse (s); // Try parsing with all checks enabled
            
            p.setExpandEntities (false);
            doc = p.parse (s);
            
            assertEquals (expected, doc.toXML ()); // Try parsing while preserving the original structure
            
            return run (p, fileName);
        }
        catch (Exception e)
        {
            System.err.println ("--- "+fileName+" -----------------------------------------");
            System.err.println (expected);
            throw e;
        }
    }
    
    private Document run (XMLParser p, String fileName) throws IOException
    {
        Assume.assumeTrue(checkTestSuite ());
        
        File f = new File (XMLTEST_DIR, fileName);
        if (!f.exists ())
            fail ("Test "+fileName+" not found");
        
        XMLSource s = new XMLIOSource (f);
        String expected = s.substring (0, s.length ());
        Document doc = p.parse (s);
        
        assertEquals (expected, doc.toXML ());
        
        return doc;
    }

    public boolean checkTestSuite ()
    {
        if (!XMLCONF_DIR.exists ())
        {
            if (!printedWarning)
            {
                System.err.println ("Warning: Couldn't find W3C XML Conformance Test Suite. Skipping these tests.");
                System.err.println ("If you want to run them, download the suite from");
                System.err.println ("http://www.w3.org/XML/Test/xmlconf-20031210.html");
                System.err.println ("The name is \"xmlts20031210.zip\". You can find the link at the top of the document.");
                System.err.println ("Unpack it to "+XMLCONF_DIR.getAbsolutePath ());
            }
            printedWarning = true;
            return false;
        }
        
        return true;
    }
    
    // To get the list of tests above, run this method
    public static void main (String[] args)
    {
        processDir ("valid/sa", false);
        processDir ("valid/not-sa", false);
        processDir ("valid/ext-sa", false);
        processDir ("not-wf/sa", true);
    }
    /**
     * @param dir
     */
    private static void processDir (String relPath, boolean invalid)
    {
        File dir = new File (XMLTEST_DIR, relPath);
        for (File f: dir.listFiles ())
        {
            if (f.isDirectory ())
                continue;
            
            if (f.getName ().endsWith (".xml"))
            {
                String name = f.getName ();
                name = relPath.replace ('/', '_')
                    .replace ('\\', '_')
                    .replace ('-', '_')
                    + "_" + name.substring (0, name.length () - 4);
                System.out.println ("    @Test public void "+name+" () throws Exception { "
                        +(invalid ? "runInvalid" : "run")+" (\""+relPath+"/"+f.getName ()+"\""
                        +(invalid ? ", \"\"" : "")+"); }");
            }
        }
    }
}
