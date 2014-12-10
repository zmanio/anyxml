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

import anyxml.XMLTokenizer.Type;

/**
 * Utility methods when working with XML.
 * 
 * @author digulla
 * @since 1.1
 */
public class XMLUtils //@COBEX
{
    /** Escape '&lt;', '&gt;' and '&amp;' */
    public static String escapeXMLText (String text)
    {
        if (text == null)
            return null;
        
        // TODO Replace with StringBuilder
        return text
            .replaceAll ("&", EntityResolver.AMP_ESCAPE)
            .replaceAll ("<", EntityResolver.LT_ESCAPE)
            .replaceAll (">", EntityResolver.GT_ESCAPE)
        ;
    }
    
    /** Unescape '&lt;', '&gt;' and '&amp;' */
    public static String unescapeXMLText (String text)
    {
        if (text == null)
            return null;
        
        // TODO Replace with StringBuilder
        return text
            .replaceAll (EntityResolver.AMP_ESCAPE, "&")
            .replaceAll (EntityResolver.LT_ESCAPE, "<")
            .replaceAll (EntityResolver.GT_ESCAPE, ">")
        ;
    }
    
    /** Unescape '&lt;', '&gt;', '&amp;', '&quot;' and '&apos;' */
    public static String unescapeXMLAttributeValue (String text)
    {
        if (text == null)
            return null;
        
        // TODO Replace with StringBuilder
        return unescapeXMLText (text)
            .replaceAll (EntityResolver.QUOT_ESCAPE, "\"")
            .replaceAll (EntityResolver.APOS_ESCAPE, "'")
        ;
    }
    
    public static boolean isElement (Node n)
    {
        return n == null ? false : isElementType (n.getType ());
    }
    
    public static boolean isElementType (Type t)
    {
        return t == Type.ELEMENT
            || t == Type.CUSTOM_ELEMENT
        ;
    }
    
    public static boolean isAttribute (Node n)
    {
        return n == null ? false : isAttributeType (n.getType ());
    }
    
    public static boolean isAttributeType (Type t)
    {
        return t == Type.ATTRIBUTE
            || t == Type.CUSTOM_ATTRIBUTE
        ;
    }
    
    public static boolean isText (Node n)
    {
        return n == null ? false : isTextType (n.getType ());
    }
    
    public static boolean isTextType (Type t)
    {
        return t == Type.TEXT
            || t == Type.CDATA
            || t == Type.DTD_WHITESPACE
        ;
    }
    
}
