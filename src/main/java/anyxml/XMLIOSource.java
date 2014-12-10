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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * An XML source based on <code>InputStream</code> or <code>Reader</code>.
 * 
 * <p>This class uses <code>XMLInputStreamReader</code> to read from
 * an <code>InputStream</code>.
 * 
 * @author digulla
 * @see anyxml.XMLInputStreamReader
 * @see java.io.InputStream
 * @see java.io.Reader
 */
public class XMLIOSource extends XMLStringSource
{
    public XMLIOSource (InputStream in) throws IOException
    {
        this (new XMLInputStreamReader (in));
    }

    public XMLIOSource (Reader reader) throws IOException
    {
        super (toString (reader));
    }
    
    public XMLIOSource (File file) throws IOException
    {
        super (toString (file));
    }

    public XMLIOSource (URL url) throws IOException
    {
        super (toString (url));
    }
    
    public static String toString (URL url) throws IOException
    {
        return toString (url.openStream ());
    }

    public static String toString (File file) throws IOException
    {
        InputStream in = new FileInputStream (file);
        return toString (in);
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static String toString (InputStream in) throws IOException
    {
        Reader reader = null;
        IOException exception = null;
        String result = null;
        try
        {
            reader = new XMLInputStreamReader (new BufferedInputStream (in));
            result = toString (reader);
        }
        catch (IOException e)
        {
            exception = e;
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close ();
            }
            catch (IOException e)
            {
                if (exception == null)
                    exception = e;
            }
        }
        
        if (exception != null)
            throw exception;
        
        return result;
    }

    /** Helper method: Read everything from a <code>Reader</code> into a <code>String</code> */
    public static String toString (Reader reader) throws IOException
    {
        StringBuilder buffer = new StringBuilder (10240);
        char[] cbuf = new char[10240];
        int len;
        
        while ((len = reader.read (cbuf)) != -1)
        {
            buffer.append (cbuf, 0, len);
        }
        
        return buffer.toString ();
    }
}
