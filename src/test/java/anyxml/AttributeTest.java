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
import anyxml.Element;


public class AttributeTest
{
    @Test
    public void testSetAttribute () throws Exception
    {
        Element e = new Element ("e");
        e.setAttribute ("x", "y");
        assertEquals ("<e x=\"y\"/>", e.toXML ());
        e.setAttribute ("x", "a");
        assertEquals ("<e x=\"a\"/>", e.toXML ());
    }
    
    @Test
    public void testSetAttribute2 () throws Exception
    {
        Element e = new Element ("e");
        e.setAttribute ("x", "y");
        e.setAttribute ("a", "1");
        assertEquals ("<e x=\"y\" a=\"1\"/>", e.toXML ());
        e.setAttribute (new Attribute ("x", "a"));
        assertEquals ("<e x=\"a\" a=\"1\"/>", e.toXML ());
    }
    
    @Test
    public void testEscaping () throws Exception
    {
        Element e = new Element ("e");
        e.setAttribute ("x", "<>&\"\'");
        
        assertEquals ("<e x=\"&lt;&gt;&amp;&quot;'\"/>", e.toXML ());
    }
    
    @Test
    public void testKeepOrder () throws Exception
    {
        Element e = new Element ("e");
        e.setAttribute ("b", "1");
        e.setAttribute ("c", "2");
        e.setAttribute ("a", "3");
        assertEquals ("<e b=\"1\" c=\"2\" a=\"3\"/>", e.toXML ());
        
        e.removeAttribute ("b");
        assertEquals ("<e c=\"2\" a=\"3\"/>", e.toXML ());

        e.setAttribute ("b", "1");
        assertEquals ("<e c=\"2\" a=\"3\" b=\"1\"/>", e.toXML ());
        
        e.setAttribute ("a", "4");
        assertEquals ("<e c=\"2\" a=\"4\" b=\"1\"/>", e.toXML ());
    }
    
    @Test
    public void testRemove () throws Exception
    {
        Element e = new Element ("e");
        e.removeAttribute ("b");
    }
}
