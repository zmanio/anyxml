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
package anyxml.dtd;

import anyxml.Token;
import anyxml.XMLParseException;
import anyxml.XMLSource;
import anyxml.XMLTokenizer;

/** A parser for a DTD (inline or reference). */
public class DTDTokenizer extends XMLTokenizer
{
    /** >= 0 as long as we're inside of a DOCTYPE */
    protected int docTypeLevel;

    /** Create an DTD tokenizer. startPosition must point at "<!DOCTYPE" in the source. */
    public DTDTokenizer (XMLSource source, int startPosition)
    {
        super (source);
        this.pos = startPosition;
    }
    
    /** The current position in the XML source */
    public int getOffset ()
    {
        return pos;
    }
    
    /** Fetch the next token from the source. Returns <code>null</code> if
     * there the complete doctype declaration has been read.
     * 
     * <p>If {@code next()} returns {@code null}, the position of the
     * tokenizer will be just after the end of the DTD declaration.
     * 
     * @return The next token or <code>null</code> if the DTD has been read.
     */
    public Token next ()
    {
        if (pos >= source.length () || docTypeLevel < 0)
            return null;

        Token token = createToken ();
        
        char c = source.charAt (pos);
        switch (c)
        {
        case '[':
            pos ++;
            token.setType (Type.DOCTYPE_BEGIN_SUBSET);
            docTypeLevel ++;
            break;

        case ']':
            pos ++;
            token.setType (Type.DOCTYPE_END_SUBSET);
            docTypeLevel --;
            break;

        case '(':
            pos ++;
            token.setType (Type.DOCTYPE_BEGIN_GROUP);
            docTypeLevel ++;
            break;
            
        case ')':
            pos ++;
            token.setType (Type.DOCTYPE_END_GROUP);
            docTypeLevel --;
            break;
            
        case '?':
            pos ++;
            token.setType (Type.DOCTYPE_ZERO_OR_ONE);
            break;
            
        case '*':
            pos ++;
            token.setType (Type.DOCTYPE_ZERO_OR_MORE);
            break;
            
        case '+':
            pos ++;
            token.setType (Type.DOCTYPE_ONE_OR_MORE);
            break;
            
        case '|':
            pos ++;
            token.setType (Type.DOCTYPE_ALTERNATIVE);
            break;
            
        case ',':
            pos ++;
            token.setType (Type.DOCTYPE_SEQUENCE);
            break;
            
        case '<':
            pos ++;
            parseDocTypeMarkupDeclaration (token);
            if (token.getType () != Type.DOCTYPE)
                docTypeLevel ++;
            break;

        case '>':
            pos ++;
            token.setType (Type.DOCTYPE_END);
            docTypeLevel --;
            break;

        case '"':
        case '\'':
            pos ++;
            parseDocTypeQuotedText (token);
            break;

        case '-':
            pos ++;
            parseDocTypeComment (token);
            break;
            
        case '#':
            pos ++;
            parseDocTypeConstant (token);
            break;
            
        case '%':
            pos ++;
            token.setType (Type.DOCTYPE_PARAMETER_ENTITY);
            break;
            
        case ';':
            pos ++;
            token.setType (Type.DOCTYPE_PARAMETER_ENTITY_END);
            break;
            
        default:
            if (Character.isWhitespace (c))
            {
                token.setType (Type.DTD_WHITESPACE);
                skipWhiteSpace ();
            }
            else
            {
                pos ++;
                parseDocTypeText (token);
            }
        break;
        }
        
        token.setEndOffset (pos);
        return token;
    }
    
    /** #implied, #pcdata, ... */
    protected void parseDocTypeConstant (Token token)
    {
        int errorOffset = pos - 1;
        
        char c = 0;
        if (pos < source.length ())
            c = source.charAt (pos);
        
        String expected = null;
        switch (c)
        {
        case 'i':
        case 'I':
            expected = "#IMPLIED";
            token.setType (Type.DOCTYPE_IMPLIED);
            break;
            
        case 'p':
        case 'P':
            expected = "#PCDATA";
            token.setType (Type.DOCTYPE_PCDATA);
            break;
        
        case 'r':
        case 'R':
            expected = "#REQUIRED";
            token.setType (Type.DOCTYPE_REQUIRED);
            break;
            
        case 'f':
        case 'F':
            expected = "#FIXED";
            token.setType (Type.DOCTYPE_FIXED);
            break;
            
        }
        
        String s = null;
        if (expected != null)
        {
            int pos2 = errorOffset + expected.length ();
            if (pos2 < source.length ())
            {
                pos = pos2;
                s = source.substring (errorOffset, pos2);
            }
        }
        
        if (expected == null || s == null)
            throw new XMLParseException ("Expected '#IMPLIED' or '#PCDATA'"+lookAheadForErrorMessage ("but found", errorOffset, 20), source, errorOffset);
        
        if (!expected.equalsIgnoreCase (s))
            throw new XMLParseException ("Expected '"+expected+"'", source, errorOffset);
    }

    protected void parseDocTypeComment (Token token)
    {
        expect ('-');
        
        while (pos < source.length ())
        {
            char c = source.charAt (pos);
            if (c == '-')
            {
                c = nextChar ("Expected '--'");
                if (c == '-')
                {
                    pos ++;
                    break;
                }
            }
            pos ++;
        }
        
        token.setType (Type.DOCTYPE_COMMENT);
    }

    protected void parseDocTypeText (Token token)
    {
        token.setType (Type.TEXT);

        pos --;
        while (pos < source.length () && getCharValidator ().isNameChar (source.charAt (pos)))
            pos ++;
        
        String s = source.substring (token.getStartOffset (), pos);
        if (s.length () == 0)
            throw new XMLParseException ("Expected some text"+lookAheadForErrorMessage ("but found", token.getStartOffset (), 20), token);
        
        // TODO How about "<!DOCTYPE SYSTEM ..."?
        if ("SYSTEM".equals (s))
            token.setType (Type.DOCTYPE_SYSTEM);
        else if ("PUBLIC".equals (s))
            token.setType (Type.DOCTYPE_PUBLIC);
        else if ("NDATA".equals (s))
            token.setType (Type.DOCTYPE_NDATA);
    }

    protected void parseDocTypeQuotedText (Token token)
    {
        token.setType (Type.DOCTYPE_QUOTED_TEXT);
        int errorPos = pos - 1;
        char quoteChar = source.charAt (errorPos);
        boolean insideEntity = false;
        int entityStartPos = pos - 1;
        
        while (pos < source.length ())
        {
            char c = source.charAt (pos);
            if (c == quoteChar) break;
            
            if (c == '&')
            {
                entityStartPos = pos;
                insideEntity = true;
            }
            else if (c == ';')
            {
                if (insideEntity)
                {
                    verifyEntity (entityStartPos, pos + 1);
                }
                
                insideEntity = false;
            }
            else
            {
                String msg = getCharValidator ().isValid (source, pos);
                if (msg != null)
                    throw new XMLParseException ("Illegal character found in quoted text. "+msg, source, pos);
            }
            
            skipChar (c);
        }
        
        if (insideEntity)
        {
            throw new XMLParseException ("Missing ';' after '&': "+lookAheadForErrorMessage (null, entityStartPos, 20), source, entityStartPos);
        }

        if (pos >= source.length ())
        {
            throw new XMLParseException ("Couldn't find closing quote", getSource (), errorPos);
        }
        else
        {
            // Skip closing quote
            pos ++;
        }
    }

    /** "<!DOCTYPE", "<!ELEMENT", "<!ATTLIST", "<!ENTITY", "<!--...-->" */
    protected void parseDocTypeMarkupDeclaration (Token token)
    {
        int errorPos = pos - 1;
        expect ('!');
        
        char c = nextChar ("Unexpeted end of file while reading doctype markup declaration");
        if (c  == 'E')
        {
            c = nextChar ("Unexpeted end of file while reading doctype markup declaration");
            if (c == 'L')
            {
                nextChars ("<!ELEMENT", errorPos, "Expected '<!ELEMENT'");
                token.setType (Type.DOCTYPE_ELEMENT);
            }
            else if (c == 'N')
            {
                nextChars ("<!ENTITY", errorPos, "Expected '<!ENTITY'");
                token.setType (Type.DOCTYPE_ENTITY);
            }
            else
                throw new XMLParseException ("Expected '<!ELEMENT' or '<!ENTITY'", source, errorPos);
        }
        else if (c == 'A')
        {
            nextChars ("<!ATTLIST", errorPos, "Expected '<!ATTLIST'");
            token.setType (Type.DOCTYPE_ATTLIST);
        }
        else if (c == 'N')
        {
            nextChars ("<!NOTATION", errorPos, "Expected '<!NOTATION'");
            token.setType (Type.DOCTYPE_NOTATION);
        }
        else if (c == '-') // Comment
        {
            // Very ugly; the level will be incremented in the calling routine :(
            docTypeLevel --;
            parseComment (token);
            return;
        }
        else if (c == 'D')
        {
            nextChars ("<!DOCTYPE", errorPos, "Expected '<!DOCTYPE'");
            token.setType (Type.DOCTYPE);
        }
        else
            throw new XMLParseException ("Expected '<!ATTLIST', '<!DOCTYPE', '<!ELEMENT' or '<!ENTITY'", source, errorPos);
    }


}
