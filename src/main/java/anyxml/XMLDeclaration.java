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

/**
 * This class allows to manipulate the XML encoding at the beginning of an XML document.
 * 
 * @author DIGULAA
 *
 */
public class XMLDeclaration extends ProcessingInstruction
{
    private String versionSpace;
    private String version;
    private String versionEquals;
    private char versionQuote;
    private String encodingSpace;
    private String encoding;
    private String encodingEquals;
    private char encodingQuote;
    private String standaloneSpace;
    private boolean standalone;
    private String standaloneEquals;
    private char standaloneQuote;
    private boolean showStandaloneNo;
    private String postSpace;

    public XMLDeclaration (Token token)
    {
        super (token);
    }
    
    public XMLDeclaration (String version)
    {
        this (version, null, false);
    }
    
    public XMLDeclaration (String version, String encoding)
    {
        this (version, encoding, false);
    }
    
    public XMLDeclaration (String version, String encoding, boolean standalone)
    {
        super ("xml", null);
        
        checkVersion (version);
        if (encoding != null && encoding.trim ().length () == 0)
            throw new IllegalArgumentException ("encoding is blank");
        
        this.versionSpace = " ";
        this.versionEquals = "=";
        this.versionQuote = '"';
        this.version = version;
        this.encodingSpace = " ";
        this.encodingEquals = "=";
        this.encodingQuote = '"';
        this.encoding = encoding;
        this.standaloneSpace = " ";
        this.standaloneEquals = "=";
        this.standaloneQuote = '"';
        this.standalone = standalone;
        this.postSpace = "";
        
        updateText ();
    }

    /**
     * @param version
     */
    protected void checkVersion (String version)
    {
        if (version == null || version.trim ().length () == 0)
            throw new IllegalArgumentException ("version is null or blank");
        if (!"1.0".equals (version) && !"1.1".equals (version))
            throw new IllegalArgumentException ("only versions '1.0' and '1.1' are supported: ["+version+"]");
    }

    public String getVersionSpace ()
    {
        return versionSpace;
    }
    
    public XMLDeclaration setVersionSpace (String versionSpace)
    {
        checkSpace ("version", versionSpace);
        this.versionSpace = versionSpace;
        updateText ();
        return this;
    }
    
    protected void checkSpace (String name, String value)
    {
        if (value == null)
            throw new XMLParseException ("space before " + name + " field can't be null");
        if (value.length () == 0)
            throw new XMLParseException ("space before " + name + " field can't be empty");
        if (value.trim ().length () != 0)
            throw new XMLParseException ("space before " + name + " field must contain only whitespace: ["+value+"]");
    }

    public String getVersion ()
    {
        return version;
    }
    
    public XMLDeclaration setVersion (String version)
    {
        checkVersion (version);
        
        this.version = version;
        updateText ();
        return this;
    }
    
    public String getVersionEquals ()
    {
        return versionEquals;
    }
    
    public XMLDeclaration setVersionEquals (String versionEquals)
    {
        checkEquals ("version", versionEquals);
        this.versionEquals = versionEquals;
        updateText ();
        return this;
    }
    
    protected void checkEquals (String name, String value)
    {
        if (value == null)
            throw new XMLParseException ("equal sign after " + name + " field can't be null");
        if (value.length () == 0)
            throw new XMLParseException ("equal sign after " + name + " field can't be empty");
        if (!"=".equals (value.trim ()))
            throw new XMLParseException ("equal sign after " + name + " field must only contain whitespace and a single '=': ["+value+"]");
    }

    public char getVersionQuote ()
    {
        return versionQuote;
    }
    
    public XMLDeclaration setVersionQuote (char versionQuote)
    {
        checkQuote ("version", versionQuote);
        this.versionQuote = versionQuote;
        updateText ();
        return this;
    }
    
    private void checkQuote (String name, char value)
    {
        if (value != '"' && value != '\'')
            throw new XMLParseException ("The quote for "+name+" must be '\"' or '\\'': ["+value+"]");
    }

    public String getEncodingSpace ()
    {
        return encodingSpace;
    }
    
    public XMLDeclaration setEncodingSpace (String encodingSpace)
    {
        checkSpace ("encoding", encodingSpace);
        this.encodingSpace = encodingSpace;
        updateText ();
        return this;
    }
    
    public String getEncoding ()
    {
        return encoding;
    }
    
    public XMLDeclaration setEncoding (String encoding)
    {
        if (encoding != null && encoding.trim ().length () == 0)
            throw new IllegalArgumentException ("encoding is blank");
        
        this.encoding = encoding;
        updateText ();
        return this;
    }
    
    public String getEncodingEquals ()
    {
        return encodingEquals;
    }
    
    public XMLDeclaration setEncodingEquals (String encodingEquals)
    {
        checkEquals ("encoding", encodingEquals);
        this.encodingEquals = encodingEquals;
        updateText ();
        return this;
    }
    
    public char getEncodingQuote ()
    {
        return encodingQuote;
    }
    
    public XMLDeclaration setEncodingQuote (char encodingQuote)
    {
        this.encodingQuote = encodingQuote;
        updateText ();
        return this;
    }
    
    public String getStandaloneSpace ()
    {
        return standaloneSpace;
    }
    
    public XMLDeclaration setStandaloneSpace (String standaloneSpace)
    {
        checkSpace ("standalone", standaloneSpace);
        this.standaloneSpace = standaloneSpace;
        updateText ();
        return this;
    }
    
    public boolean isStandalone ()
    {
        return standalone;
    }
    
    public XMLDeclaration setStandalone (boolean standalone)
    {
        this.standalone = standalone;
        updateText ();
        return this;
    }
    
    public String getStandaloneEquals ()
    {
        return standaloneEquals;
    }
    
    public XMLDeclaration setStandaloneEquals (String standaloneEquals)
    {
        checkEquals ("standalone", standaloneEquals);
        this.standaloneEquals = standaloneEquals;
        updateText ();
        return this;
    }
    
    public char getStandaloneQuote ()
    {
        return standaloneQuote;
    }
    
    public XMLDeclaration setStandaloneQuote (char standaloneQuote)
    {
        this.standaloneQuote = standaloneQuote;
        updateText ();
        return this;
    }
    
    public boolean isShowStandaloneNo ()
    {
        return showStandaloneNo;
    }
    
    public XMLDeclaration setShowStandaloneNo (boolean showStandaloneNo)
    {
        this.showStandaloneNo = showStandaloneNo;
        updateText ();
        return this;
    }
    
    public String getPostSpace ()
    {
        return postSpace;
    }
    
    public XMLDeclaration setPostSpace (String postSpace)
    {
        if (postSpace == null)
            postSpace = "";
        else if (postSpace.trim ().length () != 0)
            throw new XMLParseException ("Space after the last field must not contain anything but whitespace");
        this.postSpace = postSpace;
        updateText ();
        return this;
    }
    
    protected void updateText ()
    {
        setText (buildText (version, encoding, standalone));
    }
 
    protected String buildText (String version, String encoding, boolean standalone)
    {
        StringBuilder buffer = new StringBuilder (32);

        buffer.append (versionSpace);
        buffer.append ("version");
        buffer.append (versionEquals);
        buffer.append (versionQuote);
        buffer.append (version);
        buffer.append (versionQuote);
        
        // The XML spec demands an encoding field if standalone="yes"
        // But the XML test suite has a valid example which omits the encoding.
        String s = encoding;
        if (s != null)
        {
            buffer.append (encodingSpace);
            buffer.append ("encoding");
            buffer.append (encodingEquals);
            buffer.append (encodingQuote);
            buffer.append (s);
            buffer.append (encodingQuote);
        }
        
        s = (standalone ? "yes" : (showStandaloneNo ? "no" : null));
        if (s != null)
        {
            buffer.append (standaloneSpace);
            buffer.append ("standalone");
            buffer.append (standaloneEquals);
            buffer.append (standaloneQuote);
            buffer.append (s);
            buffer.append (standaloneQuote);
        }
        
        buffer.append (postSpace);
        
        return buffer.toString ();
    }
    
    public static boolean isXMLDeclaration (ProcessingInstruction pi)
    {
        return "xml".equalsIgnoreCase (pi.getTarget ());
    }
    
    public static XMLDeclaration parseXMLDeclaration (ProcessingInstruction pi)
    {
        XMLDeclaration decl = new XMLDeclaration ("1.0");
        decl.parseXMLDeclaration (pi.getValue ());
        return decl;
    }
    
    protected void parseXMLDeclaration (String text)
    {
        XMLSource source = new XMLStringSource (text);
        
        if (!text.startsWith ("<?xml"))
            throw new XMLParseException ("Expected '<?xml'", source, 0);
        
        int pos = 5;
        int end = text.length () - 2;
        if (!text.endsWith ("?>"))
            throw new XMLParseException ("Expected '?>'", source, end);
        
        version = null;
        encoding = null;
        standalone = false;
        boolean sawStandalone = false;
        String attrName;
        
        while (pos < end)
        {
            if (!Character.isWhitespace (text.charAt (pos)))
                throw new XMLParseException ("Expecting whitespace between attributes of XML declaration", source, pos);
            
            int spaceStart = pos;
            while (pos < end && Character.isWhitespace (text.charAt (pos)))
                pos ++;
            
            if (pos == end)
            {
                postSpace = text.substring (spaceStart, pos);
                break;
            }
            
            char startChar = text.charAt (pos);
            if (startChar == 'v')
            {
                expect (source, pos, attrName="version");
                if (version != null)
                    throw new XMLParseException ("Found a second version attribute", source, pos);
                versionSpace = text.substring (spaceStart, pos);
                pos += 7;
            }
            else if (startChar == 'e')
            {
                expect (source, pos, attrName="encoding");
                if (encoding != null)
                    throw new XMLParseException ("Found a second encoding attribute", source, pos);
                if (version == null)
                    throw new XMLParseException ("Version must be before encoding", source, pos);
                encodingSpace = text.substring (spaceStart, pos);
                pos += 8;
            }
            else if (startChar == 's')
            {
                expect (source, pos, attrName="standalone");
                if (sawStandalone)
                    throw new XMLParseException ("Found a second standalone attribute", source, pos);
                sawStandalone = true;
                standaloneSpace = text.substring (spaceStart, pos);
                pos += 10;
            }
            else
                throw new XMLParseException ("Expected 'version', 'encoding' or 'standalone'", source, pos);
            
            spaceStart = pos;
            while (pos < end && Character.isWhitespace (text.charAt (pos)))
                pos ++;
            
            if (pos == end || text.charAt (pos) != '=')
                throw new XMLParseException ("Expected '=' after "+attrName, source, pos);
            pos ++;
            
            while (pos < end && Character.isWhitespace (text.charAt (pos)))
                pos ++;

            if (pos == end)
                throw new XMLParseException ("Expected value for "+attrName, source, pos);

            if (startChar == 'v')
            {
                versionEquals = text.substring (spaceStart, pos);
            }
            else if (startChar == 'e')
            {
                encodingEquals = text.substring (spaceStart, pos);
            }
            else if (startChar == 's')
            {
                standaloneEquals = text.substring (spaceStart, pos);
            }
            
            char quoteChar = text.charAt (pos);
            if (quoteChar != '"' && quoteChar != '\'')
                throw new XMLParseException ("Expected single or double quotes around value of "+attrName+" but found '"+quoteChar+"' ("+Integer.toString (quoteChar, 16)+")", source, pos);
            
            pos ++;
            char otherQuoteChar = (quoteChar == '"' ? '\'' : '"');
            int valueStart = pos;
            while (pos < end)
            {
                char c = text.charAt (pos);
                if (c == quoteChar)
                    break;
                
                if (c == otherQuoteChar)
                    throw new XMLParseException ("Quote mismatch: Expected ["+quoteChar+"], found ["+c+"]", source, pos);
                pos ++;
            }
            
            if (pos == end)
                throw new XMLParseException ("Missing closing quote after value for "+attrName, source, pos);
            
            String value = text.substring (valueStart, pos);
            pos ++;

            if (value.trim ().length () == 0)
                throw new XMLParseException ("Value for "+attrName+" is empty", source, valueStart-1);

            if (startChar == 'v')
            {
                try
                {
                    checkVersion (value);
                }
                catch (IllegalArgumentException e)
                {
                    throw new XMLParseException (e.getMessage (), e).setSource (source, valueStart-1);
                }
                version = value;
                versionQuote = quoteChar;
            }
            else if (startChar == 'e')
            {
                encoding = value;
                encodingQuote = quoteChar;
            }
            else if (startChar == 's')
            {
                if ("no".equals (value) || "yes".equals (value))
                    standalone = "yes".equals (value);
                else
                    throw new XMLParseException ("Allowed values for standalone are 'yes' and 'no', found '"+value+"'", source, pos);
                
                standaloneQuote = quoteChar;
                showStandaloneNo = !standalone;
            }
        }

        if (version == null)
            throw new XMLParseException ("Missing version attribute", source, 0);

        updateText ();
    }

    private void expect (XMLSource source, int pos, String expected)
    {
        int end = Math.min (pos + expected.length (), source.length ());
        String s = source.substring (pos, end);
        if (!expected.equals (s))
            throw new XMLParseException ("Expected '"+expected+"' but found '"+s+"'", source, pos);
    }
    
    @Override
    public XMLDeclaration createClone ()
    {
        return new XMLDeclaration (version);
    }
    
    @Override
    public XMLDeclaration copy (Node orig)
    {
        super.copy (orig);
        
        XMLDeclaration other = (XMLDeclaration)orig;
        
        this.encoding = other.encoding;
        this.encodingEquals = other.encodingEquals;
        this.encodingQuote = other.encodingQuote;
        this.encodingSpace = other.encodingSpace;
        this.postSpace = other.postSpace;
        this.showStandaloneNo = other.showStandaloneNo;
        this.standalone = other.standalone;
        this.standaloneEquals = other.standaloneEquals;
        this.standaloneQuote = other.standaloneQuote;
        this.standaloneSpace = other.standaloneSpace;
        this.version = other.version;
        this.versionEquals = other.versionEquals;
        this.versionQuote = other.versionQuote;
        this.versionSpace = other.versionSpace;
        
        return this;
    }
    
    @Override
    public XMLDeclaration copy ()
    {
        return (XMLDeclaration)super.copy ();
    }
}
