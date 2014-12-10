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

public class DocTypeNotation extends BasicNode implements DocTypeNode
{
    private String name;
    private char quoteChar = '"';
    private String text;
    private List<Node> nodes = new ArrayList<Node> ();
    private String systemLiteral;
    private String publicIDLiteral;
    
    public DocTypeNotation (Token token, String name)
    {
        super (token);
        this.name = name;
    }
    
    public DocTypeNotation (Type type, String name)
    {
        super (type, null);
        this.name = name;
    }

    public String getName ()
    {
        return name;
    }
    
    public void setName (String name)
    {
        this.name = name;
    }
    
    public String getText ()
    {
        return text;
    }
    
    public void setText (String text)
    {
        this.text = text;
    }
    
    public String getSystemLiteral ()
    {
        return systemLiteral;
    }
    
    public void setSystemLiteral (String systemLiteral)
    {
        this.systemLiteral = systemLiteral;
    }
    
    public boolean isSystem ()
    {
        return systemLiteral != null;
    }
    
    public String getPublicIDLiteral ()
    {
        return publicIDLiteral;
    }
    
    public void setPublicIDLiteral (String publicIDLiteral)
    {
        this.publicIDLiteral = publicIDLiteral;
    }
    
    public boolean isPublic ()
    {
        return publicIDLiteral != null;
    }
    
    public String getValue ()
    {
        return toXML ();
    }
    
    public DocTypeNotation toXML (XMLWriter writer) throws IOException
    {
        writer.write (this, "<!NOTATION");
        
        if (nodes.isEmpty ())
        {
            writer.write (this, " ");
            writer.write (this, name);
            writer.write (this, " ");
            writer.write (this, Character.toString (quoteChar));
            writer.write (this, text);
            writer.write (this, Character.toString (quoteChar));
        }
        else
        {
            // TODO Sync nodes with setters
            for (Node n: nodes)
                n.toXML (writer);
        }
        
        writer.write (this, ">");
        
        return this;
    }
    
    public DocTypeNotation addNode (Node node)
    {
        nodes.add (node);
        return this;
    }

}
