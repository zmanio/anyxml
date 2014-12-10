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

public class Location
{
    private XMLSource source;
    private Element element;
    private Document document;
    private Node node;
    private int offset = -1;
    private int line = 0;
    private int lineStartOffset = -1;
    private int column = 0;
    
    public Location (XMLSource source, int offset)
    {
        this.source = source;
        this.offset = offset;
    }
    
    public Location (Element e)
    {
        this.element = e;
    }
    
    public Location (Document document, Node node)
    {
        if (document == null)
            throw new IllegalArgumentException ("document is null");
        if (node == null)
            throw new IllegalArgumentException ("node is null");
        
        this.document = document;
        this.node = node;
    }
    
    public Location (Token token)
    {
        this (token.getSource (), token.getStartOffset ());
    }

    public int getOffset ()
    {
        return offset;
    }
    
    /** The line number */
    public int getLine ()
    {
        if (line == 0)
            calcLocation ();
        
        return line;
    }
    
    /** The column. Tab is 8 character wide */
    public int getColumn ()
    {
        if (line == 0)
            calcLocation ();
        
        return column;
    }

    /** Offset at which the current line starts in the document */
    public int getLineStartOffset ()
    {
        if (line == 0)
            calcLocation ();
        
        return lineStartOffset;
    }
    
    /** This method is called when an information is requested from the location */
    protected void calcLocation ()
    {
        if (source == null)
            calcLocationFromElement ();
        else
            calcLocationFromSource ();
    }

    /** This method is called when the location information comes from an XML source */
    protected void calcLocationFromSource ()
    {
        if (source == null)
        {
            return;
        }
        
        line = 1;
        column = 1;
        lineStartOffset = 0;
        
        moveToOffset (source, offset);
    }

    /**
     * This moves the line and column information by the text found in the source.
     */
    protected void moveToOffset (XMLSource source, int offset)
    {
        offset = Math.min (source.length (), offset);
        for (int i=0; i<offset; i++)
        {
            char c = source.charAt (i);
            //System.out.println (line+":"+column+" "+c+" ("+((int)c)+")");
            if (c == '\r' || c == '\n')
            {
                line ++;
                column = 1;
                if (c == '\r' && i+1 < source.length () && source.charAt (i+1) == '\n')
                    i ++;
                lineStartOffset = i + 1;
            }
            else if (c == '\t')
                column += (8 - (column % 8)) + 1;
            else
                column ++;
        }
    }

    protected void calcLocationFromElement ()
    {
        if (element == null && document == null)
        {
            return;
        }
        
        line = 1;
        column = 1;
        lineStartOffset = 0;
        offset = 0;

        // We'll need thousands of these, so I'm using a reusable object
        final ReusableXMLSource source = new ReusableXMLSource ();

        final Node terminator = (node == null ? element : node);
        XMLWriter writer = new XMLWriter (new Writer () {
            @Override
            public void close () throws IOException
            {
                // Do nothing
            }

            @Override
            public void flush () throws IOException
            {
                // Do nothing
            }

            @Override
            public void write (char[] cbuf, int off, int len)
                    throws IOException
            {
                // Do nothing
            }
        }) {
            @Override
            public void write (Node n, String xml)
                    throws IOException
            {
                if (n == terminator)
                    throw new NodeFoundException ();
                else
                {
                    source.source = xml;
                    moveToOffset (source, xml.length ());
                    offset += xml.length ();
                }
            }
        };

        Parent p;
        if (document == null)
        {
            p = element;
            while (p instanceof Child)
            {
                Child c = (Child)p;
                if (c.getParent () == null)
                    break;
                
                p = c.getParent ();
            }
        }
        else
        {
            p = document;
        }
        
        try
        {
            p.toXML (writer);
            
            nodeNotFound ();
        }
        catch (IOException e)
        {
            if (!(e instanceof NodeFoundException) )
                nodeNotFound ();
        }
    }
    
    /** This method is called when you specify a child node of an element
     * but when the location is requested, this node cannot be found.
     * 
     * <p>By default, this method just resets the location but you can override it
     * to throw an exception, if you like.
     */
    protected void nodeNotFound ()
    {
        line = 0;
        column = 0;
        offset = -1;
        lineStartOffset = -1;
    }

    public static class ReusableXMLSource implements XMLSource
    {
        public String source;

        public char charAt (int offset)
        {
            return source.charAt (offset);
        }

        public int length ()
        {
            return source.length ();
        }

        public String substring (int start, int end)
        {
            return source.substring (start, end);
        }
        
    }
    
    /** This is just a marker that the node has been found */
    public static class NodeFoundException extends IOException
    {
    	private static final long serialVersionUID = 1L;
    }
    
    @Override
    public String toString ()
    {
        return getLine () + ":" + getColumn ();
    }
}
