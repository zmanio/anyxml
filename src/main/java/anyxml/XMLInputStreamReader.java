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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Determine the encoding of a stream of bytes according to the
 * XML spec and return a Reader which converts the byte stream
 * into Unicode as it is read.
 * 
 * @author digulla
 * @see java.io.Reader
 */
public class XMLInputStreamReader extends Reader
{
    /** The constant "UTF-8" */
    public final static String ENCODING_UTF_8 = "UTF-8";
    /** The constant "ISO-8859-1", also called "Latin-1" */
    public final static String ENCODING_ISO_8859_1 = "ISO-8859-1";
    /** An alias for ENCODING_ISO_8859_1 */
    public final static String ENCODING_ISO_Latin_1 = ENCODING_ISO_8859_1;
    
    /** A map to convert standard XML encodings into Java encodings.
     * 
     *  <p>The map is mutable; you can put your own encodings in here if you need them.
     */
    public static Map<String, String> ENCODING_MAP = new HashMap<String, String> ();
    static
    {
        ENCODING_MAP.put ("Utf-8", "UTF-8");
        ENCODING_MAP.put ("utf-8", "UTF-8");
        ENCODING_MAP.put ("Latin-1", "ISO-8859-1");
    }
    /** A map to convert standard Java encodings into XML encodings.
     * 
     *  <p>The map is mutable; you can put your own encodings in here if you need them.
     */
    public static Map<String, String> JAVA_TO_XML_ENCODING_MAP = new HashMap<String, String> ();
    static
    {
        JAVA_TO_XML_ENCODING_MAP.put ("UnicodeBig", "UTF-16");
        JAVA_TO_XML_ENCODING_MAP.put ("UnicodeLittle", "UTF-16");
    }
    
    private InputStream in;
    private String xmlEncoding;
    private String javaEncoding;
    private Reader delegate;
    
    public XMLInputStreamReader (InputStream in)
    {
        this.in = in;
    }
    
    public final static Pattern VERSION_PATTERN = Pattern.compile ("\\bversion\\s*=\\s*[\"'][0-9.]+[\"']");
    
    /** Determine the encoding of the stream.
     * 
     *  <p>It is safe to call this method more than once from a single thread.
     */
    public void determineEncoding ()
    {
        if (xmlEncoding != null)
            return;
        
        String s = "";
        int encodingOffset = 0;
        
        byte[] header = new byte[1024];
        int headerLength;
        try
        {
            headerLength = in.read (header);
        }
        catch (IOException e)
        {
            XMLStringSource source = new XMLStringSource ("");
            throw new XMLParseException ("Error parsing XML declaration: "+e.getMessage (), e)
            .setSource (source, 0);
        }
        
        String encoding = ENCODING_UTF_8;
        int skip = 0;
        if (headerLength < 2)
        {
            // Do nothing
        }
        else if ((header[0] == (byte)0xFE && header[1] == (byte)0xFF))
        {
            encoding = "UnicodeBig";
            skip = 2;
        }
        else if ((header[0] == (byte)0xFF && header[1] == (byte)0xFE))
        {
            encoding = "UnicodeLittle";
            skip = 2;
            
            if (headerLength >= 4)
            {
                if (header[2] == 0 && header[3] == 0)
                {
                    // UTF-32 Little Endian
                    throw new XMLParseException ("UTF 32 Little Endian not supported, yet. Patches welcome.");
                }
            }
        }
        else if (headerLength < 3)
        {
            // Do nothing
        }
        else if ((header[0] == (byte)0xEF && header[1] == (byte)0xBB) && header[2] == (byte)0xBF)
        {
            encoding = ENCODING_UTF_8;
            skip = 3;
        }
        else if (headerLength < 4)
        {
            // Do nothing
        }
        else if ((header[0] == (byte)0x00 && header[1] == (byte)0x00) && header[2] == (byte)0xFE && header[3] == (byte)0xFF)
        {
            // UTF-32 Big Endian
            throw new XMLParseException ("UTF 32 Big Endian not supported, yet. Patches welcome.");
        }
        else if (header[0] == '<' && header[1] == '?' && header[2] == 'x' && header[3] == 'm')
        {
            int pos = 4;
            for ( ; pos<headerLength; pos ++)
            {
                int c = header[pos] & 0xFF;
                if (c == '>')
                    break;
                
                if (Character.isLetter (c))
                    continue;
                if (Character.isDigit (c))
                    continue;
                if (Character.isWhitespace (c))
                    continue;
                if (c == '?' || c == '"' || c == '\'' || c == '=' || c == '-' || c == '_' || c == '.')
                    continue;
                
                XMLStringSource source = null;
                try
                {
                    source = new XMLStringSource (new String (header, ENCODING_ISO_8859_1));
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new XMLParseException ("Error parsing XML declaration: Unsupported encoding "+e.getMessage (), e); //@COBEX
                }
                throw new XMLParseException ("Found illegal character in XML header: '"+(char)c+"'")
                .setSource (source, pos);
            }
            
            try
            {
                s = new String (header, 0, pos, ENCODING_UTF_8);
            }
            catch (UnsupportedEncodingException e) //@COBEX Note: this code will never be executed
            {
                throw new XMLParseException ("Error parsing XML declaration: Unsupported encoding "+e.getMessage (), e); //@COBEX
            }
            //System.out.println (s);

            Matcher matcher = VERSION_PATTERN.matcher (s);
            if (!matcher.find ())
                throw new XMLParseException ("Missing version in XML declaration")
                .setSource (new XMLStringSource (s), pos);

            String pattern = "encoding=";
            pos = s.indexOf (pattern);
            if (pos != -1)
            {
                pos = pos + pattern.length ();
                int c = s.charAt (pos);
                if (c != '\'' && c != '"')
                    throw new XMLParseException ("Missing opening quote for encoding")
                    .setSource (new XMLStringSource (s), pos);
                
                pos ++;
                int pos2 = s.indexOf (c, pos);
                if (pos2 == -1)
                    throw new XMLParseException ("Missing closing quote for encoding")
                    .setSource (new XMLStringSource (s), pos);
                
                encoding = s.substring (pos, pos2);
                encodingOffset = pos;
            }
        }
        
        //System.out.println ("encoding="+encoding);
        // FIXME Setting skip to 0 here seems to have no impact :( Why doesn't XMLParserTest.testBOM fail??
        
        JoinedInputStream jin = new JoinedInputStream ()
        .add (new ByteArrayInputStream (header, skip, headerLength - skip))
        .add (in)
        ;

        setXmlEncoding (encoding);
        try
        {
            delegate = new InputStreamReader (jin, javaEncoding);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new XMLParseException ("Error parsing XML declaration: Unsupported encoding '"+javaEncoding+"'", e)
            .setSource (new XMLStringSource (s), encodingOffset);
        }
    }

    /** Return the encoding of the stream */
    public String getXmlEncoding ()
    {
        determineEncoding ();
        return xmlEncoding;
    }
    
    /** If you know the encoding, you can override it here.
     * 
     *  <p>When you do that, the reader will ignore the encoding in the stream
     *  if there is one.
     */
    public void setXmlEncoding (String xmlEncoding)
    {
        this.xmlEncoding = xmlEncoding;
        javaEncoding = ENCODING_MAP.get (xmlEncoding);
        if (javaEncoding == null)
            javaEncoding = xmlEncoding;
        if (JAVA_TO_XML_ENCODING_MAP.containsKey (this.xmlEncoding))
            this.xmlEncoding = JAVA_TO_XML_ENCODING_MAP.get (this.xmlEncoding);
    }
    
    /** Get the Java name of the XML encoding of the stream. */
    public String getJavaEncoding ()
    {
        determineEncoding ();
        return javaEncoding;
    }
    
    @Override
    public void close () throws IOException
    {
        if (delegate == null)
            in.close ();
        else
            delegate.close ();
    }

    @Override
    public int read (char[] cbuf, int off, int len) throws IOException
    {
        determineEncoding ();
        return delegate.read (cbuf, off, len);
    }
}
