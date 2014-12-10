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
package anyxml.dtd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anyxml.BasicNode;
import anyxml.Node;
import anyxml.Token;
import anyxml.XMLWriter;
import anyxml.XMLTokenizer.Type;

public class DocTypeAttributeList extends BasicNode implements DocTypeNode
{
    private DocTypeElement element;
    private String elementName;
    private List<Node> nodes = new ArrayList<Node> ();
    
    public DocTypeAttributeList (Token token, String elementName)
    {
        super (token);
        this.elementName = elementName;
    }
    
    public DocTypeAttributeList (Type type, String elementName)
    {
        super (type, null);
        this.elementName = elementName;
    }
    
    public String getElementName ()
    {
        return elementName;
    }
    
    public void setElement (DocTypeElement element)
    {
        this.element = element;
    }
    
    public DocTypeElement getElement ()
    {
        return element;
    }
    
    public String getValue ()
    {
        return toXML ();
    }
    
    public DocTypeAttributeList toXML (XMLWriter writer) throws IOException
    {
        writer.write (this, "<!ATTLIST");
        if (!Character.isWhitespace (elementName.charAt (0)))
            writer.write (this, " ");
        writer.write (this, elementName);
        boolean needsWhitespace = true;
        if (Character.isWhitespace (elementName.charAt (elementName.length () - 1)))
            needsWhitespace = false;
        if (needsWhitespace && !nodes.isEmpty ())
        {
            Node n = nodes.get (0);
            if (n.getType () == Type.DTD_WHITESPACE)
                needsWhitespace = false;
        }
        if (needsWhitespace)
            writer.write (this, " ");
        
        for (Node n: nodes)
            n.toXML (writer);
        
        writer.write (this, ">");
        
        return this;
    }
    
    public DocTypeAttributeList addNode (Node node)
    {
        nodes.add (node);
        return this;
    }

}
