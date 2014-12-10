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

/** A piece of XML. */
public class Token
{
    private XMLSource source;
    private Type type;
    private int startOffset;
    private int endOffset;
    
    public void setSource (XMLSource source)
    {
        this.source = source;
    }

    public XMLSource getSource ()
    {
        return source;
    }

    /** Return the string of text which this token represents in the XMLSource
     *
     * @return the text or <code>null</code> if there is no source
     */
    public String getText ()
    {
        return getSource() == null ? null : getSource().substring (getStartOffset(), getEndOffset());
    }
    
    /** Return the text with all special characters (like line feed, new line, null bytes, characters
     * in the unicode range) escaped.
     * 
     * <p>The result of this method can use used directly in a Java String.
     * 
     * @return the text (without quotes) or <code>null</code> if there is no source
     */
    public String getEscapedText ()
    {
        return TextUtils.escapeJavaString (getText ());
    }
    
    @Override
    public String toString ()
    {
        return "Token (" + getType () + ", " + getStartOffset () + ":" + getEndOffset () + ", " + getEscapedText () + ")";
    }

    public void setType (Type type)
    {
        this.type = type;
    }

    public Type getType ()
    {
        return type;
    }

    public void setStartOffset (int startOffset)
    {
        this.startOffset = startOffset;
    }

    /** The position in the source at which the token begins */
    public int getStartOffset ()
    {
        return startOffset;
    }

    public void setEndOffset (int endOffset)
    {
        this.endOffset = endOffset;
    }

    /** The position after the last character of the token (matching the definition of 
     * <code>String.substring(start,end)</code> */
    public int getEndOffset ()
    {
        // Avoid exceptions when using half-initialized tokens
        return endOffset < startOffset ? startOffset : endOffset;
    }

    public String getPrefixWhiteSpace ()
    {
        int pos = getStartOffset ();
        int N = getEndOffset ();
        while (pos < N)
        {
            char c = source.charAt (pos);
            if (!Character.isWhitespace (c))
                break;
            pos ++;
        }
        return pos == 0 ? "" : source.substring (getStartOffset (), pos);
    }
}