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

import anyxml.validation.CharValidator;

/**
 * This class allows to chop an XMLSource into tokens.
 * 
 * <p>You can use it to parse XML yourself or use the XMLParser to
 * let it parse XML into a Document.
 * 
 * @author digulla
 * @see anyxml.XMLSource
 * @see anyxml.XMLParser
 * @see anyxml.Document
 */
public class XMLTokenizer
{
    /** Types of tokens the tokenizer can return */
    public static enum Type {
        /** A piece of text with the entities still intact */
        TEXT,
        /** A CDATA segment (including the CDATA marker) */
        CDATA,
        /** Whitespace in a doctype */
        DTD_WHITESPACE,
        /** A processing instruction with the begin and end tag */
        PROCESSING_INSTRUCTION,
        /** A comment (including the begin and end tag */
        COMMENT,
        /** This is the name part of the start tag */
        BEGIN_ELEMENT,
        /** An attribute in the start tag */
        ATTRIBUTE,
        /** A custom attribute. Use this if you extended Attribute and need to distinguish
         *  the nodes from common attributes. 
         */
        CUSTOM_ATTRIBUTE,
        /** The token which terminates the start tag. It's value is either '>' or '/>' if it's an empty element */
        BEGIN_ELEMENT_END,
        /** The end element */
        END_ELEMENT,
        /** A document node */
        DOCUMENT,
        /** Node-type for elements after they have been parsed */
        ELEMENT,
        /** A custom element. Use this if you extended Element and need to distinguish the nodes
         *  from common elements.
         */
        CUSTOM_ELEMENT,
        /** An entity */
        ENTITY,
        /** <!DOCTYPE */
        DOCTYPE,
        /** "SYSTEM" */
        DOCTYPE_SYSTEM,
        /** "PUBLIC" */
        DOCTYPE_PUBLIC,
        /** "NDATA" */
        DOCTYPE_NDATA,
        /** <!ELEMENT */
        DOCTYPE_ELEMENT,
        /** <!ATTLIST */
        DOCTYPE_ATTLIST,
        /** <!ENTITY */
        DOCTYPE_ENTITY,
        /** <!NOTATION */
        DOCTYPE_NOTATION,
        /** Something between quotes in a doctype */
        DOCTYPE_QUOTED_TEXT,
        /** "[" */
        DOCTYPE_BEGIN_SUBSET,
        /** "]" */
        DOCTYPE_END_SUBSET,
        /** ">" */
        DOCTYPE_END,
        /** "-- comment --" inside of a doctype */
        DOCTYPE_COMMENT,
        /** "(" */
        DOCTYPE_BEGIN_GROUP,
        /** ")" */
        DOCTYPE_END_GROUP,
        /** "|" */
        DOCTYPE_ALTERNATIVE,
        /** "?" */
        DOCTYPE_ZERO_OR_ONE,
        /** "*" */
        DOCTYPE_ZERO_OR_MORE,
        /** "+" */
        DOCTYPE_ONE_OR_MORE,
        /** "%" */
        DOCTYPE_PARAMETER_ENTITY,
        /** ";" */
        DOCTYPE_PARAMETER_ENTITY_END,
        /** "#PCDATA" */
        DOCTYPE_PCDATA,
        /** "#IMPLIED" */
        DOCTYPE_IMPLIED,
        /** "#REQUIRED" */
        DOCTYPE_REQUIRED,
        /** "#FIXED" */
        DOCTYPE_FIXED,
        /** "," */
        DOCTYPE_SEQUENCE,
        /** "CDATA" */
        DOCTYPE_CDATA;
    }
    
    protected final XMLSource source;
    /** The current position in the source */
    protected int pos;
    /** true if we're currently inside of a start tag */
    protected boolean inStartElement;
    /** Should the tokenizer return entities or treat them as text? Default is true. */
    private boolean treatEntitiesAsText = true;
    /** The character validator for this tokenizer. */
    private CharValidator charValidator = new CharValidator ();
    /** The entity resolver to use to expand and verify entities. */
    private EntityResolver entityResolver;
    
    public XMLTokenizer (XMLSource source)
    {
        this.source = source;
    }
    
    public XMLTokenizer setTreatEntitiesAsText (boolean treatEntitiesAsText)
    {
        this.treatEntitiesAsText = treatEntitiesAsText;
        return this;
    }
    
    public boolean isTreatEntitiesAsText ()
    {
        return treatEntitiesAsText;
    }
    
    public CharValidator getCharValidator ()
    {
        return charValidator;
    }

    public XMLTokenizer setCharValidator (CharValidator charValidator)
    {
        if (charValidator == null)
            throw new IllegalArgumentException ("charValidator is null");
        
        this.charValidator = charValidator;
        return this;
    }

    public EntityResolver getEntityResolver ()
    {
        return entityResolver;
    }
    
    public XMLTokenizer setEntityResolver (EntityResolver resolver)
    {
        this.entityResolver = resolver;
        return this;
    }
    
    /** Fetch the next token from the source. Returns <code>null</code> if
     * there are no more tokens in the input.
     * 
     * @return The next token or <code>null</code> at EOF
     */
    public Token next ()
    {
        if (pos >= source.length ())
            return null;
        
        Token token = createToken ();
        
        char c = source.charAt (pos);
        if (inStartElement)
        {
            skipWhiteSpace ();
            
            c = source.charAt (pos);
            if (c == '>')
            {
                pos ++;
                token.setType (Type.BEGIN_ELEMENT_END);
                inStartElement = false;
            }
            else if (c == '/') // Empty element
            {
                pos ++;
                if (pos >= source.length () || source.charAt (pos) != '>')
                    throw new XMLParseException ("Expected '/>'", source, pos-1);
                
                pos ++;
                
                token.setType (Type.BEGIN_ELEMENT_END);
                inStartElement = false;
            }
            else
            {
                parseAttribute (token);
            }
        }
        else if (c == '<')
        {
            pos ++;
            parseBeginSomething (token);
        }
        else if (!treatEntitiesAsText && c == '&')
        {
            pos ++;
            parseEntity (token);
        }
        else
        {
            parseText (token);
        }
        
        token.setEndOffset (pos);
        
        return token;
    }

    /** All tokens are created here.
     * 
     * <p>Use this method to create custom tokens with
     * additional information.
     * 
     * @return a new, pre-initialized token
     */
    protected Token createToken ()
    {
        Token token = new Token ();
        token.setSource (source);
        token.setStartOffset (pos);
        return token;
    }

    public XMLSource getSource ()
    {
        return source;
    }
    
    /** Get the current parsing position (for error handling, for example).
     * 
     * <p>This value is not very accurate because the tokenizer might be
     * anywhere in the stream.
     */
    public int getOffset ()
    {
        return pos;
    }
    
    /** Set the current parsing position. You can use this to restart
     *  parsing after an error or to jump around in the input. */
    public void setOffset (int offset)
    {
        this.pos = offset;
    }
    
    /** Read one of "&lt;tag", "&lt;?pi", "&lt;!--", "&lt;![CDATA[" or a end tag. */
    protected void parseBeginSomething (Token token)
    {
        if (pos >= source.length ())
            throw new XMLParseException ("Unexpected end of input. Expected start or end tag, processing instruction, comment or CDATA", source, pos);
        
        char c = source.charAt (pos);
        switch (c)
        {
        case '?':
            pos ++;
            parseProcessingInstruction (token);
            break;
            
        case '!':
            pos ++;
            parseExcalamation (token);
            break;
        
        case '/':
            pos ++;
            parseEndElement (token);
            break;
        
        default:
            parseBeginElement (token);
            break;
        }
    }

    /** Read the name of an element.
     * 
     *  <p>The resulting token will contain the '&lt;' plus any whitespace between
     *  it and the name plus the name itself but no whitespace after the name.
     */
    protected void parseBeginElement (Token token)
    {
        token.setType (Type.BEGIN_ELEMENT);
        inStartElement = true;
        
        skipWhiteSpace ();
        
        int nameStartOffset = pos;
        
        parseName ("start tag");
        
        if (pos == nameStartOffset)
            throw new XMLParseException ("Missing element name", token);
        
        if (pos >= source.length ())
            throw new XMLParseException ("Missing '>' of start tag", source, pos);
        
        char c = source.charAt (pos);
        if (!charValidator.isWhitespace (c) && c != '/' && c != '>')
            throw new XMLParseException ("Expected whitespace, '>' or '/>' after element name", source, pos);
    }

    /**
     * Read an end tag.
     * 
     * <p>The resulting token will contain the '&lt;/' and '&gt;' plus the
     * name plus any whitespace between those three.
     */
    protected void parseEndElement (Token token)
    {
        token.setType (Type.END_ELEMENT);
        
        skipWhiteSpace ();
        
        parseName ("end tag");
        
        skipWhiteSpace ();
        
        expect ('>');
    }

    /** Parse "&lt;!--" or  "&lt;![CDATA[" */
    protected void parseExcalamation (Token token)
    {
        char c = source.charAt (pos);
        if (c == '-')
        {
            pos ++;
            parseComment (token);
        }
        else if (c == '[')
        {
            pos ++;
            parseCData (token);
        }
        else if (c == 'D')
        {
            pos ++;
            parseDocType (token);
        }
        else
            throw new XMLParseException ("Expected '<!--' or '<![CDATA['", source, pos-2);
    }

    /** Parse a doctype declaration
    *
    *  <p>The resulting token will contain "<!DOCTYPE"
    */
    protected void parseDocType (Token token)
    {
        token.setType (Type.DOCTYPE);
        nextChars ("<!DOCTYPE", pos - 3, "Expected '<!DOCTYPE'");
    }

    /** Parse a CDATA element.
     * 
     *  <p>The resulting token will contain the "&lt;![CDATA[" plus the
     *  terminating "]]&gt;".
     */
    protected void parseCData (Token token)
    {
        token.setType (Type.CDATA);
        
        nextChars ("<![CDATA[", pos - 3, "Expected '<![CDATA['");

        while (true)
        {
            if (pos >= source.length ())
                throw new XMLParseException ("Expected ']]>'", source, pos);
            
            char c = source.charAt (pos);
            if (c == ']')
            {
                int errorPos = pos;
                pos ++;
                if (pos+1 >= source.length ())
                    throw new XMLParseException ("Expected ']]>'"+lookAheadForErrorMessage ("but found", errorPos, 20), source, errorPos);
                
                c = source.charAt (pos);
                if (c != ']')
                    continue;
                
                c = source.charAt (pos + 1);
                if (c == '>')
                {
                    pos += 2;
                    break;
                }
            }
            else
            {
                String msg = charValidator.isValid (source, pos);
                if (msg != null)
                    throw new XMLParseException ("Illegal character found in CDATA. "+msg, source, pos);
                
                skipChar (c);
            }
        }
    }

    /** Read a comment.
     * 
     *  <p>The resulting token will contain the "&lt;!--" plus the
     *  terminating "--&gt;".
     */
    protected void parseComment (Token token)
    {
        token.setType (Type.COMMENT);
        
        if (pos >= source.length () || source.charAt (pos) != '-')
            throw new XMLParseException ("Expected '<!--'", source, pos-3);
        
        pos ++;
        
        while (true)
        {
            if (pos >= source.length ())
                throw new XMLParseException ("Expected '-->'", source, pos);
            
            char c = source.charAt (pos);
            if (c == '-')
            {
                pos ++;
                if (pos >= source.length ())
                    throw new XMLParseException ("Expected '-->'", source, pos-1);
                
                c = source.charAt (pos ++);
                if (c != '-')
                    continue;
                
                if (pos >= source.length ())
                    throw new XMLParseException ("Expected '-->'", source, pos-2);
                
                c = source.charAt (pos ++);
                if (c != '>')
                    throw new XMLParseException ("XML comments must not contain '--'", source, pos-3);

                break;
            }
            else
            {
                String msg = charValidator.isValid (source, pos);
                if (msg != null)
                    throw new XMLParseException ("Illegal character found in comment. "+msg, source, pos);
                
                skipChar (c);
            }
        }
    }

    /** Read a processing instruction.
     * 
     *  <p>The resulting token will contain the "&lt;?" plus the
     *  terminating "?&gt;".
     */
    protected void parseProcessingInstruction (Token token)
    {
        token.setType (Type.PROCESSING_INSTRUCTION);
        int errorPos = pos - 2;
        
        while (true)
        {
            if (pos >= source.length ())
                throw new XMLParseException ("Missing end of processing instruction", source, errorPos);
            
            char c = source.charAt (pos);
            if (c == '?')
            {
                pos ++;
                if (pos >= source.length ())
                    throw new XMLParseException ("Expected '>' after '?'", source, pos);
                
                if (source.charAt (pos) == '>')
                {
                    pos ++;
                    break;
                }
            }
            else
            {
                String msg = charValidator.isValid (source, pos);
                if (msg != null)
                    throw new XMLParseException ("Illegal character found in processing instruction. "+msg, source, pos);

                skipChar (c);
            }
        }
    }

    /** Read the attribute of an element.
     * 
     *  <p>The resulting token will contain the name, "=" plus the
     *  quotes and the value.
     */
    protected void parseAttribute (Token token)
    {
        token.setType (Type.ATTRIBUTE);

        parseName ("attribute");
        
        if (pos == token.getStartOffset())
            throw new XMLParseException ("Expected attribute name", source, pos);
        
        skipWhiteSpace ();
        expect ('=');
        skipWhiteSpace ();
        
        char c = 0;
        if (pos < source.length ())
            c = source.charAt (pos);
        if (c != '\'' && c != '"')
            throw new XMLParseException ("Expected single or double quotes", source, pos);
        
        char endChar = c;
        boolean insideEntity = false;
        int errorPos = pos;
        
        while (true)
        {
            pos ++;
            if (pos >= source.length ())
            {
                int i = Math.min (20, source.length () - token.getStartOffset ());
                throw new XMLParseException ("Missing end quote ("+endChar+") of attribute: "
                        +lookAheadForErrorMessage (null, token.getStartOffset (), i), token);
            }
            
            c = source.charAt (pos);
            if (c == endChar)
                break;
            
            if (c == '<' || c == '>')
                throw new XMLParseException ("Illegal character in attribute value: '"+c+"'", source, pos);
            
            if (c == '&')
            {
                insideEntity = true;
                errorPos = pos;
            }
            else if (c == ';')
            {
                verifyEntity (errorPos, pos+1);
                insideEntity = false;
            }
            else
            {
                String msg = charValidator.isValid (source, pos);
                if (msg != null)
                    throw new XMLParseException ("Illegal character found in attribute value. "+msg, source, pos);
                
                skipChar (c);
                pos --;
            }
        }
        
        if (insideEntity)
        {
            throw new XMLParseException ("Missing ';' after '&': "+lookAheadForErrorMessage (null, errorPos, 20), source, errorPos);
        }
        
        // Skip end-char
        pos ++;
    }

    /** Read an XML name */
    protected void parseName (String objectName)
    {
        int startPos = pos;
        
        if (pos < source.length () && charValidator.isNameStartChar (source.charAt (pos)))
        {
            pos ++;
        
            while (pos < source.length () && charValidator.isNameChar (source.charAt (pos)))
                pos ++;
        }
        
        if (pos == startPos)
        {
            throw new XMLParseException ("Expected valid XML name for "+objectName+lookAheadForErrorMessage ("but found", startPos, 20), source, startPos);
        }
    }
    
    /** Read a piece of text.
     * 
     *  <p>The resulting token will contain the text as is with all
     *  the entity and numeric character references. 
     */
    protected void parseText (Token token)
    {
        token.setType (Type.TEXT);
        int errorPos = pos - 1;
        boolean insideEntity = false;
        
        while (pos < source.length ())
        {
            char c = source.charAt (pos);
            if (c == '<')
                break;
            
            if (c == '&')
            {
                if (!treatEntitiesAsText)
                    break;
                
                errorPos = pos;
                insideEntity = true;
            }
            else if (c == ';')
            {
                if (insideEntity)
                {
                    verifyEntity (errorPos, pos + 1);
                }
                
                insideEntity = false;
            }
            else if (c == ']' && pos + 2 < source.length ())
            {
                if (source.charAt (pos+1) == ']' && source.charAt (pos+2) == '>')
                    throw new XMLParseException ("Please replace the '>' of ']]>' in character data with '&gt;'", source, pos+2);
            }
            
            String msg = charValidator.isValid (source, pos);
            if (msg != null)
                throw new XMLParseException ("Illegal character found in text. "+msg, source, pos);
            
            skipChar (c);
        }
        
        if (insideEntity)
        {
            throw new XMLParseException ("Missing ';' after '&': "+lookAheadForErrorMessage (null, errorPos, 20), source, errorPos);
        }
    }

    /**
     * Advance one or two positions, depending on whether the current character if
     * the high part of a surrogate pair.
     */
    protected void skipChar (char c)
    {
        pos ++;
        if (Character.isHighSurrogate (c))
            pos ++;
    }

    /**
     * Verify an entity. If no entityResolver is installed, this does nothing.
     */
    protected void verifyEntity (int start, int end)
    {
        if (entityResolver == null)
            return;
        
        String entity = source.substring (start, end);

        try
        {
            entityResolver.validateEntity (entity);
        }
        catch (IllegalArgumentException e)
        {
            throw new XMLParseException (e.getMessage (), e).setSource (source, start);
        }
    }
    
    protected void parseEntity (Token token)
    {
        token.setType (Type.ENTITY);
        
        char c;
        if (pos < source.length ())
        {
            c = source.charAt (pos);
            if (c == '#')
                pos ++;
        }
        
        while (pos < source.length ())
        {
            c = source.charAt (pos);
            if (c == ';')
                break;
            
            if (!charValidator.isNameChar (c))
                throw new XMLParseException ("Illegal character in entity: ["+c+"] ("+Integer.toHexString (c)+")", source, pos);
            
            pos ++;
        }
        
        expect (';');
        
        verifyEntity (token.getStartOffset (), pos);
    }

    protected void nextChars (String expected, int startPos, String errorMessage)
    {
        int len = expected.length () - (pos - startPos);
        if (pos + len > source.length ())
            throw new XMLParseException (errorMessage, source, startPos);
        
        String s = source.substring (startPos, startPos + expected.length ());
        if (!expected.equals (s))
            throw new XMLParseException (errorMessage, source, startPos);
        
        pos += len;
    }

    protected char nextChar (String errorMessage)
    {
        if (pos >= source.length ())
            throw new XMLParseException (errorMessage, source, pos);
        return source.charAt (pos ++);
    }

    /**
     * Check that the next character is {@code expected} and skip it
     */
    protected void expect (char expected)
    {
        if (pos >= source.length () || source.charAt (pos) != expected)
        {
            throw new XMLParseException ("Expected '"+expected+"'"+lookAheadForErrorMessage ("but found", pos, 20), source, pos);
        }
        pos ++;
    }

    protected String lookAheadForErrorMessage (String conditionalPrefix, int pos, int len)
    {
        String found = "";
        if (pos < source.length ())
        {
            int len2 = source.length () - pos;
            len = Math.min (len, len2);
            String s = source.substring (pos, pos+len);
            if (len != len2)
                s += "...";
            if (conditionalPrefix == null)
                found = TextUtils.escapeJavaString (s);
            else
                found = " " + conditionalPrefix + " " + TextUtils.escapeJavaString (s);
        }
        return found;
    }

    /** Advance the current position past any whitespace in the input */
    protected void skipWhiteSpace ()
    {
        while (pos < source.length () && charValidator.isWhitespace (source.charAt (pos)))
            pos ++;
    }

}
