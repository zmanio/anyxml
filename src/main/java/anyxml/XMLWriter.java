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

import java.io.IOException;
import java.io.Writer;

public class XMLWriter extends Writer
{
    private final Writer writer;
    protected Node current;
    private String indent = null;
    private int level = 0;
    private boolean padCompact = false;

    public XMLWriter (Writer writer)
    {
        this.writer = writer;
    }

    @Override
    public void close () throws IOException
    {
        writer.close ();
    }

    @Override
    public void flush () throws IOException
    {
        writer.flush ();
    }

    @Override
    public void write (char[] cbuf, int off, int len) throws IOException
    {
        writer.write (cbuf, off, len);        
    }

    /**
     * If you want to see every node written to the underlying writer, this is the place.
     * 
     * @param node
     * @param s
     * @throws IOException
     */
    public void write (Node node, String s) throws IOException
    {
        current = node;
        writer.write (s);
    }
    
    public void writeAttributeValue (Node node, String value, char quoteChar) throws IOException
    {
        int start;
        int pos = 0;
        start = pos;
        
        String escapeQuote = (quoteChar == '"' ? "&quot;" : "&apos;");
        
        for ( ; pos < value.length (); pos ++)
        {
            char c = value.charAt (pos);
            String escape = null;
            
            if (c == quoteChar)
            {
                escape = escapeQuote;
            }
            else if (c == '&')
            {
                escape = EntityResolver.AMP_ESCAPE;
            }
            else if (c == '<')
            {
                escape = EntityResolver.LT_ESCAPE;
            }
            else if (c == '>')
            {
                escape = EntityResolver.GT_ESCAPE;
            }
            
            if (escape != null)
            {
                if (pos != start)
                    write (node, value.substring (start, pos));
                
                write (node, escape);
                start = pos + 1;
            }
        }
        
        if (start < pos)
            write (node, value.substring (start, pos));
    }
    
    /** Write all children of a node */
    public void writeChildNodes (NodeWithChildren node) throws IOException
    {
        for (Node n: node.getNodes ())
            n.toXML (this);
    }
    
    /** Write an element with all attributes and children */
    public void write (Element e) throws IOException
    {
        writeBeginElement (e);
        
        writeChildNodes (e);

        writeEndElement (e);
    }

    /** Write the end tag of an element */
    public void writeEndElement (Element e) throws IOException
    {
        if (!isCompact (e))
        {
            level --;
            if (indent != null && !hasSingleTextChild (e))
            {
                nl ();
                indent ();
            }
            
            write (e, "</");
            write (e, e.getEndName ());
            write (e, ">");
        }
    }
    
    /** Write the start tag of an element including the attributes. */
    public void writeBeginElement (Element e) throws IOException
    {
        if (indent != null && level > 0)
        {
            nl ();
            indent ();
        }
        
        write (e, "<");
        write (e, e.getBeginName ());
        
        writeAttributes (e);

        write (e, e.getPostSpace ());
        
        String end = ">";
        boolean compact = isCompact (e);
        if (compact)
        {
            if (padCompact && !" ".equals (e.getPostSpace ()))
            {
                end = " />";
            }
            else
            {
                end = "/>";
            }
        }
        
        write (e, end);
        
        if (compact)
        {
            if (indent != null)
            {
                Element parent = e.getParentElement ();
                if (parent != null)
                {
                    int index = parent.nodeIndexOf (e);
                    index ++;
                    if (index < parent.nodeCount ())
                    {
                        Node n = parent.getNode (index);
                        if (XMLUtils.isText (n))
                        {
                            Text t = (Text)n;
                            if (!t.isWhitespace ())
                            {
                                nl ();
                                indent ();
                            }
                        }
                    }
                }
            }
        }
        else
        {
            level ++;
            
            // If the element has more than a single text element...
            if (indent != null && !hasSingleTextChild (e))
            {
                // Indent if the first child not an element
                // This makes sure that text nodes before elements are correctly indented.
                if (e.hasNodes () && !XMLUtils.isElement (e.getNode (0)))
                {
                    nl ();
                    indent ();
                }
            }
        }
    }

    public boolean isCompact (Element e)
    {
        return (!e.hasNodes () && e.isCompactEmpty ());
    }

    public boolean hasSingleTextChild (Element e)
    {
        if (e.nodeCount () != 1)
            return false;
        
        Node node = e.getNode (0);
        if (!XMLUtils.isText (node))
            return false;
        
        Text t = (Text)node;
        return !t.isWhitespace ();
    }
    
    public void indent () throws IOException
    {
        for (int i=0; i<level; i++)
            writer.write (indent);
    }

    public void nl () throws IOException
    {
        writer.write ("\n");
    }
    
    public void writeAttributes (Element e) throws IOException
    {
        for (Node n: e.getAttributeMap ().values ())
            n.toXML (this);
    }

    public void setIndent (String indent)
    {
        this.indent = indent;
    }
    
    public String getIndent ()
    {
        return indent;
    }
    
    public void setPadCompact (boolean padCompact)
    {
        this.padCompact = padCompact;
    }
    
    /** If this is true, the writer makes sure that there is a single space before "/>" */
    public boolean isPadCompact ()
    {
        return padCompact;
    }
}
