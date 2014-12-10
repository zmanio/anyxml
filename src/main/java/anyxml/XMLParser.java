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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import anyxml.XMLTokenizer.Type;
import anyxml.dtd.DTDTokenizer;
import anyxml.dtd.DocType;
import anyxml.dtd.DocTypeAttributeList;
import anyxml.dtd.DocTypeElement;
import anyxml.dtd.DocTypeEntity;
import anyxml.dtd.DocTypeEntityResolver;
import anyxml.dtd.DocTypeNode;
import anyxml.dtd.DocTypeNotation;
import anyxml.dtd.DocTypeText;
import anyxml.dtd.DocType.DocTypeType;
import anyxml.validation.CharValidator;

/**
 * The class uses the <code>XMLTokenizer</code> to parse an <code>XMLSource</code>
 * into a <code>Document</code>.
 * 
 * @author digulla
 * @see anyxml.XMLSource
 * @see anyxml.XMLTokenizer
 * @see anyxml.Document
 */
public class XMLParser
{
    /** The entity resolver to use to expand entities in the input */
    private EntityResolver entityResolver;
    /** Should entities be expanded? Use this to temporarily disable entity expansion even if a resolver is registered */
    private boolean expandEntities;
    /** Should the parser return entity nodes or treat them as text? Default is true. */
    private boolean treatEntitiesAsText = true;
    /** The character validator to use */
    private CharValidator charValidator = new CharValidator ();

    public XMLParser ()
    {
        // Do nothing ...
    }

    public XMLParser setEntityResolver (EntityResolver entityResolver)
    {
        this.entityResolver = entityResolver;
        if (entityResolver != null)
            setExpandEntities (true);
        return this;
    }
    
    public EntityResolver getEntityResolver ()
    {
        return entityResolver;
    }
    
    public XMLParser setExpandEntities (boolean expandEntities)
    {
        this.expandEntities = expandEntities;
        if (expandEntities)
            setTreatEntitiesAsText (false);
        return this;
    }
    
    public boolean isExpandEntities ()
    {
        return expandEntities && entityResolver != null;
    }
    
    public XMLParser setTreatEntitiesAsText (boolean treatEntitiesAsText)
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
    
    public XMLParser setCharValidator (CharValidator charValidator)
    {
        if (charValidator == null)
            throw new IllegalArgumentException ("charValidator is null");

        this.charValidator = charValidator;
        return this;
    }
    
    /** Parse an XML source into a Document */
    public Document parse (XMLSource source)
    {
        Document doc = new Document ();
        
        XMLTokenizer tokenizer = createTokenizer (source);
        tokenizer.setCharValidator (charValidator);
        tokenizer.setEntityResolver (entityResolver);
        Token token;

        while ((token = tokenizer.next ()) != null)
        {
            if (token.getType () == Type.DOCTYPE)
            {
                XMLTokenizer dtdTokenizer = createDTDTokenizer (tokenizer.getSource (), token.getStartOffset ());
                
                DocType docType = parseDocType (dtdTokenizer);
                doc.addNode (docType);
                
                tokenizer.setOffset (dtdTokenizer.getOffset ());
                
                entityResolver = new DocTypeEntityResolver (docType, entityResolver);
                
                continue;
            }
            
            Node n = toNode (token);
            doc.addNode (n);
            
            if (token.getType() == Type.BEGIN_ELEMENT)
            {
                parseElement (tokenizer, (Element)n);
            }
        }
        
        if (doc.getRootElement () == null)
            throw new XMLParseException ("No root element found");
        
        if (entityResolver instanceof DocTypeEntityResolver)
        {
            entityResolver = entityResolver.getParent ();
        }
        
        return doc;
    }

    protected DocType parseDocType (XMLTokenizer tokenizer)
    {
        Token startToken = tokenizer.next ();
        if (startToken == null)
            throw new XMLParseException ("Expected '<!DOCTYPE'", tokenizer.getSource (), tokenizer.getOffset ());
        if (startToken.getType () != Type.DOCTYPE)
            throw new XMLParseException ("Expected '<!DOCTYPE' but found '"+startToken.getText ()+"'", startToken);
        
        DocType docType = new DocType (startToken);
        
        Token token = expect (tokenizer, startToken, Type.DTD_WHITESPACE, "Expected whitespace after '<!DOCTYPE'");
        docType.add (toNode (token));
        
        token = expect (tokenizer, startToken, Type.TEXT, "Expected name after '<!DOCTYPE'");
        docType.add (toNode (token));
        docType.setName (token.getText ());
        
        token = skipOptionalWhitespace (tokenizer, tokenizer.next (), docType);
        
        if (token.getType () == Type.DOCTYPE_SYSTEM)
        {
            docType.add (toNode (token));
            token = parseSystemLiteral (tokenizer, token, docType);
        }
        else if (token.getType () == Type.DOCTYPE_PUBLIC)
        {
            docType.add (toNode (token));
            token = parsePublicLiteral (tokenizer, token, docType);
        }
        else if (token.getType () == Type.DOCTYPE_NDATA)
        {
            // TODO
        }

        token = skipOptionalWhitespace (tokenizer, token, docType);
        
        if (token.getType () == Type.DOCTYPE_BEGIN_SUBSET)
        {
            docType.add (toNode (token));
            token = parseDocTypeSubSet (tokenizer, token, docType);
        }

        if (token.getType () != Type.DOCTYPE_END)
            throw new XMLParseException ("Expected '>', got "+token, token);

        docType.add (toNode (token));
        
        token = tokenizer.next ();
        if (token != null)
            throw new XMLParseException ("Expected no further tokens from the DTD tokenizer: "+token, token);
        
        return docType;
    }

    protected XMLTokenizer createDTDTokenizer (XMLSource source, int startOffset)
    {
        return new DTDTokenizer (source, startOffset);
    }

    /**
     * If the next token is whitespace, skip it.
     * 
     * @param tokenizer
     * @param startToken This might be whitespace
     * @param docType
     * @return The current or the next token.
     */
    protected Token skipOptionalWhitespace (XMLTokenizer tokenizer,
            Token startToken, DocType docType)
    {
        if (startToken == null)
            throw new XMLParseException ("Unexpected EOF after '<!DOCTYPE'", tokenizer.getSource (), tokenizer.getSource ().length ());
        
        Token token = startToken;
        if (token.getType () == Type.DTD_WHITESPACE)
        {
            docType.add (toNode (token));
            
            token = tokenizer.next ();
            if (token == null)
                throw new XMLParseException ("Unexpected EOF after '<!DOCTYPE'", startToken);
        }
        return token;
    }

    protected Token parseDocTypeSubSet (XMLTokenizer tokenizer, Token startToken, DocType docType)
    {
        Token token;
        while ((token = tokenizer.next ()) != null)
        {
            //System.out.println ("parseDocTypeSubSet "+token);
            if (token.getType () == Type.DOCTYPE_ELEMENT)
            {
                parseDocTypeSubElement (tokenizer, token, docType);
            }
            else if (token.getType () == Type.DOCTYPE_ATTLIST)
            {
                parseDocTypeAttList (tokenizer, token, docType);
            }
            else if (token.getType () == Type.DOCTYPE_ENTITY)
            {
                parseDocTypeEntity (tokenizer, token, docType);
            }
            else if (token.getType () == Type.DOCTYPE_NOTATION)
            {
                parseDocTypeNotation (tokenizer, token, docType);
            }
            else
            {
                docType.add (toNode (token));
                if (token.getType () == Type.DOCTYPE_END_SUBSET)
                    break;
            }
        }
        
        docType.mapElementsAndAttributes ();
        
        return skipOptionalWhitespace (tokenizer, tokenizer.next (), docType);
    }

    protected void parseDocTypeNotation (XMLTokenizer tokenizer, Token startToken, DocType docType)
    {
        DocTypeNotation notation = new DocTypeNotation (startToken, null);
        
        Token token = startToken;
        token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after '<!NOTATION'");
        notation.addNode (toNode (token));
        token = expect (tokenizer, token, Type.TEXT, "Expected notation name");
        notation.addNode (toNode (token));
        String name = token.getText ();
        
        notation.setName (name);
        
        token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after notation name");
        notation.addNode (toNode (token));
        
        token = expect (tokenizer, token, new Type[] { Type.DOCTYPE_SYSTEM, Type.DOCTYPE_PUBLIC }, "Expected 'SYSTEM' or 'PUBLIC'");
        notation.addNode (toNode (token));
        if (token.getType () == Type.DOCTYPE_SYSTEM)
        {
            notation.setText (token.getText ());
            
            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after 'SYSTEM'");
            notation.addNode (toNode (token));
            
            token = expect (tokenizer, token, Type.DOCTYPE_QUOTED_TEXT, "Expected quoted text after 'SYSTEM'");
            notation.addNode (toNode (token));
            
            notation.setSystemLiteral (stripQuotes (token));
            token = skipWhiteSpaceAndComments (tokenizer, tokenizer.next (), notation);
        }
        else if (token.getType () == Type.DOCTYPE_PUBLIC)
        {
            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after 'PUBLIC'");
            notation.addNode (toNode (token));

            token = expect (tokenizer, token, Type.DOCTYPE_QUOTED_TEXT, "Expected public ID literal after 'PUBLIC'");
            notation.addNode (toNode (token));

            notation.setPublicIDLiteral (stripQuotes (token));

            token = tokenizer.next ();
            if (token != null
                && token.getType () != Type.DOCTYPE_END
            )
            {
                if (token.getType () != Type.DTD_WHITESPACE)
                    throw new XMLParseException ("Expected whitespace after public ID literal", token);
                
                token = expect (tokenizer, token, Type.DOCTYPE_QUOTED_TEXT, "Expected system literal after public ID literal");
                notation.addNode (toNode (token));

                notation.setSystemLiteral (stripQuotes (token));
            }
        }
        
        if (token == null)
            throw new XMLParseException ("Unexpected EOF while parsing notation declaration", tokenizer.getSource (), tokenizer.getOffset ());
        if (token.getType () != Type.DOCTYPE_END)
            throw new XMLParseException ("Expected '>' after notation declaration"+tokenizer.lookAheadForErrorMessage ("but found", token.getStartOffset (), 20), tokenizer.getSource (), tokenizer.getOffset ());

        docType.add (notation);
    }

    protected void parseDocTypeEntity (XMLTokenizer tokenizer, Token startToken, DocType docType)
    {
        DocTypeEntity entity = new DocTypeEntity (startToken, null);
        
        Token token = startToken;
        token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after '<!ENTITY'");
        entity.addNode (toNode (token));
        token = expect (tokenizer, token, new Type[] { Type.TEXT, Type.DOCTYPE_PARAMETER_ENTITY }, "Expected entity name or '%'");
        entity.addNode (toNode (token));
        String name = token.getText ();
        boolean isParameterEntity = "%".equals (name);
        if (isParameterEntity)
        {
            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after '%'");
            entity.addNode (toNode (token));
            token = expect (tokenizer, token, Type.TEXT, "Expected entity name");
            entity.addNode (toNode (token));
            name = token.getText ();
        }
        
        entity.setParameterEntity (isParameterEntity);
        entity.setName (name);
        
        token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after entity name");
        entity.addNode (toNode (token));
        
        token = expect (tokenizer, token, new Type[] { Type.DOCTYPE_SYSTEM, Type.DOCTYPE_PUBLIC, Type.DOCTYPE_QUOTED_TEXT }, "Expected 'SYSTEM', 'PUBLIC' or quoted text after entity name");
        entity.addNode (toNode (token));
        if (token.getType () == Type.DOCTYPE_SYSTEM)
        {
            entity.setText (token.getText ());
            
            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after 'SYSTEM'");
            entity.addNode (toNode (token));
            
            token = expect (tokenizer, token, Type.DOCTYPE_QUOTED_TEXT, "Expected quoted text after 'SYSTEM'");
            entity.addNode (toNode (token));
            
            entity.setSystemLiteral (stripQuotes (token));
        }
        else if (token.getType () == Type.DOCTYPE_PUBLIC)
        {
            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after 'PUBLIC'");
            entity.addNode (toNode (token));

            token = expect (tokenizer, token, Type.DOCTYPE_QUOTED_TEXT, "Expected public ID literal after 'PUBLIC'");
            entity.addNode (toNode (token));

            entity.setPublicIDLiteral (stripQuotes (token));

            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after public ID literal");
            entity.addNode (toNode (token));

            token = expect (tokenizer, token, Type.DOCTYPE_QUOTED_TEXT, "Expected system literal after public ID literal");
            entity.addNode (toNode (token));

            entity.setSystemLiteral (stripQuotes (token));
        }
        else
        {
            entity.setText (stripQuotes (token));
        }
        
        token = skipWhiteSpaceAndComments (tokenizer, tokenizer.next (), entity);
        
        if (token == null)
            throw new XMLParseException ("Unexpected EOF while parsing entity declaration", tokenizer.getSource (), tokenizer.getOffset ());
        
        if (token.getType () == Type.DOCTYPE_NDATA)
        {
            Node last = entity.getNodes ().get (entity.getNodes ().size () - 1);
            if (!XMLUtils.isText (last) || !((Text)last).isWhitespace ())
                throw new XMLParseException ("Space is required before an NDATA entity annotation", token);
            
            if (isParameterEntity)
                throw new XMLParseException ("Parameter entities are always parsed; NDATA annotations are not permitted", token);
            
            entity.addNode (toNode (token));
            
            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after 'NDATA'");
            entity.addNode (toNode (token));

            token = expect (tokenizer, token, Type.TEXT, "Expected name after 'NDATA'");
            entity.addNode (toNode (token));
            
            entity.setNotationName (token.getText ());

            token = skipWhiteSpaceAndComments (tokenizer, tokenizer.next (), entity);
            if (token == null)
                throw new XMLParseException ("Unexpected EOF while parsing entity declaration", tokenizer.getSource (), tokenizer.getOffset ());
        }
        
        if (token.getType () != Type.DOCTYPE_END)
            throw new XMLParseException ("Expected '>' after entity declaration"+tokenizer.lookAheadForErrorMessage ("but found", token.getStartOffset (), 20), tokenizer.getSource (), tokenizer.getOffset ());

        docType.add (entity);
    }
    
    protected String stripQuotes (Token token)
    {
        String text = token.getText ();
        if (text == null || text.length () < 2)
            return text;
        
        return text.substring (1, text.length () - 1);
    }

    protected void parseDocTypeAttList (XMLTokenizer tokenizer, Token startToken, DocType docType)
    {
        Token token = startToken;
        token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after '<!ATTLIST'");
        token = expect (tokenizer, token, Type.TEXT, "Expected name of element that this '<!ATTLIST' is for");
        String elementName = token.getText ();
        if (!isValidName (tokenizer, elementName))
            throw new XMLParseException ("Attribute name is no valid XML name", token);
        
        DocTypeAttributeList attList = new DocTypeAttributeList (startToken, elementName);
        //System.out.println ("elementName="+elementName);
        
        while (true)
        {
            token = tokenizer.next ();
            if (token == null)
                break;
            
            token = skipWhiteSpaceAndComments (tokenizer, token, attList);
            
            if (token.getType() == Type.DOCTYPE_END)
                break;
            
            if (token.getType () != Type.TEXT)
                throw new XMLParseException ("Expected attribute name", token);
            
            attList.addNode (toNode (token));
            if (!isValidName (tokenizer, token.getText ()))
                throw new XMLParseException ("Attribute name is no valid XML name", token);
            
            //String attributeName = token.getText ();
            //System.out.println ("attributeName: "+token.getText ());
        
            token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after attribute name");
            attList.addNode (toNode (token));

            token = expect (tokenizer, token, new Type[] { Type.TEXT, Type.DOCTYPE_BEGIN_GROUP }, "Expected attribute type");
            attList.addNode (toNode (token));
            
            if (token.getType () == Type.TEXT)
            {
                String type = token.getText ();
                //System.out.println ("type: "+token);
    
                token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after attribute type");
                attList.addNode (toNode (token));
                
                if ("NOTATION".equals (type))
                {
                    token = tokenizer.next ();
                    if (token == null)
                        break;
                    
                    if (token.getType () == Type.DOCTYPE_BEGIN_GROUP)
                    {
                        token = parseAttListTypeGroup (tokenizer, token, attList);
                    }
    
                    token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after list of notation types");
                    attList.addNode (toNode (token));
                }
            }
            else
            {
                token = parseAttListNameTokens (tokenizer, token, attList);
                
                token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after list of alternatives");
                attList.addNode (toNode (token));
            }
            
            token = tokenizer.next ();
            if (token == null)
                break;

            if (token.getType () != Type.DOCTYPE_IMPLIED
                && token.getType () != Type.DOCTYPE_REQUIRED
                && token.getType () != Type.DOCTYPE_FIXED
                && token.getType () != Type.DOCTYPE_QUOTED_TEXT
            )
                throw new XMLParseException ("Expected #IMPLIED or quoted text: "+token, token);
            
            attList.addNode (toNode (token));
            
            if (token.getType () == Type.DOCTYPE_FIXED)
            {
                token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after '#FIXED'");
                attList.addNode (toNode (token));
                
                token = expect (tokenizer, token, Type.DOCTYPE_QUOTED_TEXT, "Expected quoted text after '#FIXED'");
                attList.addNode (toNode (token));
            }
        }

        if (token == null)
            throw new XMLParseException ("Unexpected EOF while parsing attribute list declaration", tokenizer.getSource (), tokenizer.getOffset ());
        
        docType.add (attList);
    }
    
    protected boolean isValidName (XMLTokenizer tokenizer, String name)
    {
        return name != null && name.length () > 0 && charValidator.isNameStartChar (name.charAt (0));
    }

    protected Token parseAttListNameTokens (XMLTokenizer tokenizer, Token token, DocTypeAttributeList attList)
    {
        while ((token = tokenizer.next ()) != null)
        {
            attList.addNode (toNode (token));
            
            if (token.getType () == Type.DTD_WHITESPACE
                || token.getType () == Type.TEXT
                || token.getType () == Type.DOCTYPE_ALTERNATIVE
            )
                continue;
            
            if (token.getType () == Type.DOCTYPE_END_GROUP)
                break;
            
            throw new XMLParseException ("Expected whitespace, '|' or a name token", token);
        }
        
        return token;
    }

    protected Token parseAttListTypeGroup (XMLTokenizer tokenizer, Token token, DocTypeAttributeList attList)
    {
        attList.addNode (toNode (token));
        Token startGroup = token;
        int subLevel = 0;
        
        while ((token = tokenizer.next ()) != null)
        {
            attList.addNode (toNode (token));
            
            if (token.getType () == Type.DOCTYPE_END_GROUP)
            {
                if (subLevel == 0)
                    break;
                subLevel --;
            }
            else if (token.getType () == Type.DOCTYPE_BEGIN_GROUP)
            {
                subLevel ++;
            }
        }
        
        if (token == null)
            throw new XMLParseException ("Expected end of group"+tokenizer.lookAheadForErrorMessage ("but found", startGroup.getStartOffset (), 20), startGroup);
        return token;
    }

    protected Token skipWhiteSpaceAndComments (XMLTokenizer tokenizer, Token token, DocTypeNode n)
    {
        while (token != null)
        {
            if (token.getType () == Type.DTD_WHITESPACE)
            {
                n.addNode (toNode (token));
                token = tokenizer.next ();
            }
            else if (token.getType () == Type.DOCTYPE_COMMENT)
            {
                n.addNode (toNode (token));
                token = tokenizer.next ();
            }
            else
                break;
        }
        
        return token;
    }

    protected void parseDocTypeSubElement (XMLTokenizer tokenizer, Token startToken,
            DocType docType)
    {
        Token token = startToken;
        token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after '<!ELEMENT'");
        token = expect (tokenizer, token, Type.TEXT, "Expected element name");
        String name = token.getText ();
        
        token = expect (tokenizer, token, Type.DTD_WHITESPACE, "Expected whitespace after element name");
        Token beforeContent = token;
        
        while ((token = tokenizer.next ()) != null)
        {
            //System.out.println ("parseDocTypeSubElement "+token);
            if (token.getType() == Type.DOCTYPE_END)
                break;
            
            // TODO Check EMPTY, ANY, #PCDATA, (|), ?, *, +
        }

        if (token == null)
            throw new XMLParseException ("Unexpected EOF while parsing element content", tokenizer.getSource (), tokenizer.getOffset ());
        
        String content = tokenizer.getSource ().substring (beforeContent.getEndOffset (), token.getStartOffset ());
        startToken.setEndOffset (token.getEndOffset ());
        DocTypeElement element = new DocTypeElement (startToken, name, content);
        docType.add (element);
    }

    protected Token parsePublicLiteral (XMLTokenizer tokenizer, Token startToken, DocType docType)
    {
        docType.setDocTypeType (DocTypeType.PUBLIC);
        
        Token token = expect (tokenizer, startToken, Type.DTD_WHITESPACE, "Expected whitespace after 'PUBLIC'");
        docType.add (toNode (token));

        token = expect (tokenizer, startToken, Type.DOCTYPE_QUOTED_TEXT, "Expected quoted public id after 'PUBLIC'");
        docType.add (toNode (token));
        String s = token.getText ();
        docType.setPublicLiteral (s.substring (1, s.length () - 1));

        token = expect (tokenizer, startToken, Type.DTD_WHITESPACE, "Expected whitespace after public id "+docType.getPublicLiteral ());
        docType.add (toNode (token));
        
        token = expect (tokenizer, startToken, Type.DOCTYPE_QUOTED_TEXT, "Expected quoted system literal after the public id of 'PUBLIC'");
        docType.add (toNode (token));
        s = token.getText ();
        docType.setSystemLiteral (s.substring (1, s.length () - 1));
        
        return skipOptionalWhitespace (tokenizer, tokenizer.next (), docType);
    }

    protected Token parseSystemLiteral (XMLTokenizer tokenizer, Token startToken, DocType docType)
    {
        docType.setDocTypeType (DocTypeType.SYSTEM);
        
        Token token = expect (tokenizer, startToken, Type.DTD_WHITESPACE, "Expected whitespace after 'SYSTEM'");
        docType.add (toNode (token));
        token = expect (tokenizer, startToken, Type.DOCTYPE_QUOTED_TEXT, "Expected quoted system literal after 'SYSTEM'");
        docType.add (toNode (token));
        String s = token.getText ();
        docType.setSystemLiteral (s.substring (1, s.length () - 1));

        return skipOptionalWhitespace (tokenizer, tokenizer.next (), docType);
    }

    /**
     * Fetch the next token and make sure it's one of {@code expected}. If not, create an
     * {@link XMLParseException} using the {@code errorMessage}
     */
    protected Token expect (XMLTokenizer tokenizer, Token startToken, Type[] expected, String errorMessage)
    {
        Token token = tokenizer.next ();
        //System.out.println (token);
        boolean valid = token != null;
        if (valid)
        {
            valid = false;
            for (Type t: expected)
            {
                if (token.getType () == t)
                {
                    valid = true;
                    break;
                }
            }
        }
        if (!valid)
        {
            if (token == null)
                token = startToken;
            throw new XMLParseException (errorMessage + ": " + token, token);
        }
        return token;
    }
    
    /**
     * Fetch the next token and make sure it's {@code expected}. If not, create an
     * {@link XMLParseException} using the {@code errorMessage}
     */
    protected Token expect (XMLTokenizer tokenizer, Token startToken, Type expected, String errorMessage)
    {
        Token token = tokenizer.next ();
        //System.out.println (token);
        if (token == null || token.getType () != expected)
        {
            if (token == null)
                token = startToken;
            throw new XMLParseException (errorMessage + tokenizer.lookAheadForErrorMessage ("but found", token.getStartOffset (), 20) + " (" + token + ")", token);
        }
        return token;
    }

    /**
     * @param source
     * @return
     */
    protected XMLTokenizer createTokenizer (XMLSource source)
    {
        XMLTokenizer tokenizer = new XMLTokenizer (source);
        tokenizer.setTreatEntitiesAsText (treatEntitiesAsText);
        return tokenizer;
    }

    /** Parse all tokens up to the end tag recursively into an element. */
    protected void parseElement (XMLTokenizer tokenizer, Element parent)
    {
        // This loop reads all the attributes and the whitespace between then
        Token token = null;
        while (true)
        {
            token = tokenizer.next ();
            if (token == null || token.getType() == Type.BEGIN_ELEMENT_END)
                break;

            if (token.getType() != Type.ATTRIBUTE)
                throw new XMLParseException ("Unexpected token "+token+" while parsing attributes of element "+parent.getName (), token); //@COBEX

            if (!Character.isWhitespace (token.getSource ().charAt (token.getStartOffset ())))
                throw new XMLParseException ("Expected whitespace between attributes of element a but found "+token, token);
            
            // TODO Expand entities
            parent.addAttribute ((Attribute)toNode (token));
        }
        
        // Get rid of namespace prefix and add the namespace to the element
        int pos = parent.getName ().indexOf (':');
        if (pos == 0)
            throw new XMLParseException ("Missing namespace prefix before colon: '"+parent.getName ()+"'", parent.getStartToken ());
        if (pos > 0)
        {
            String prefix = parent.getName ().substring (0, pos);
            Namespace ns = parent.getDocument ().getNamespace (prefix);
            if (ns == null)
                throw new XMLParseException ("The namespace prefix "+prefix+" is not defined: '"+parent.getName ()+"'", parent.getStartToken ());
            
            parent.setNamespace (ns);
            
            String name = parent.getName ().substring (pos+1);
            if (name.length () == 0)
                throw new XMLParseException ("Missing element name after namespace prefix: '"+parent.getName ()+"'", parent.getStartToken ());
            
            String beginName = parent.getBeginName ();
            String endName = parent.getEndName ();
            parent.setName (name);
            parent.setBeginName (beginName);
            parent.setEndName (endName);
        }

        if (token == null)
            throw new XMLParseException ("Unexpected end-of-file while parsing attributes of element "+parent.getName (), tokenizer.getSource (), tokenizer.getOffset ());
        
        if (token.getType() == Type.BEGIN_ELEMENT_END)
        {
            String postSpace = token.getPrefixWhiteSpace();
            parent.setPostSpace (postSpace);
            if ("/>".equals (token.getText ().trim ()))
            {
                parent.setCompactEmpty (true);
                return;
            }
        }

        token = parseElementContent (tokenizer, parent, null);
        if (token == null)
            throw new XMLParseException ("Unexpected end-of-file while parsing children of element "+parent.getName (), parent.getStartToken ());
    }

    /**
     * @param tokenizer
     * @param parent
     */
    protected Token parseElementContent (XMLTokenizer tokenizer, Element parent, Set<String> recursionTrap)
    {
        // This loop goes over the element content and stops after processing the end tag
        while (true)
        {
            Token token = tokenizer.next ();
            if (token == null)
                return null;
            
            if (token.getType() == Type.END_ELEMENT)
            {
                String endName = token.getText ();
                endName = endName.substring (2, endName.length () - 1);
                String name = endName.trim ();
                
                String elementName = parent.getName ();
                if (parent.getNamespace ().getPrefix ().length () != 0)
                    elementName = parent.getNamespace ().getPrefix () + ":" + elementName;
                
                if (!name.trim ().equals (elementName))
                {
                    Location l = new Location (token);
                    throw new XMLParseException ("End element '"+name+"' at line "+l.getLine ()+", column "+l.getColumn ()+" doesn't match with '"+parent.getName ()+"'", parent.getStartToken ());
                }
                
                if (endName.length () != parent.getName ().length ())
                    parent.setEndName (endName);
                
                parent.getStartToken ().setEndOffset (token.getEndOffset ());
                
                return token;
            }
            else if (expandEntities && token.getType () == Type.ENTITY)
            {
                if (recursionTrap == null)
                    recursionTrap = new HashSet<String> ();
                expandEntity (parent, tokenizer, token, recursionTrap);
                continue;
            }
            
            Node n = toNode (token);
            parent.addNode (n);
            
            if (token.getType() == Type.BEGIN_ELEMENT)
            {
                Element child = (Element)n;
                parseElement (tokenizer, child);
            }
        }
    }

    protected void expandEntity (Element parent, XMLTokenizer parentTokenizer, Token entityToken, Set<String> recursionTrap)
    {
        String entity = entityToken.getText ();

        String expandedEntity = getEntityResolver ().expand (entity);
        if (expandedEntity == null)
            throw new XMLParseException ("Entity "+entity+" is not defined", entityToken);
        
        if ("<".equals (expandedEntity) || ">".equals (expandedEntity) || "&".equals (expandedEntity))
        {
            parent.addNode (new Text (expandedEntity));
            return;
        }
        
        if (recursionTrap.contains (entity))
            throw new XMLParseException ("Expansion of "+entity+" leads to infinite recursion", entityToken);
        
        //System.out.println (expandedEntity);
        
        XMLStringSource source = new XMLStringSource (expandedEntity);
        XMLTokenizer entityTokenizer = new XMLTokenizer (source);
        entityTokenizer.setEntityResolver (parentTokenizer.getEntityResolver ());
        entityTokenizer.setTreatEntitiesAsText (parentTokenizer.isTreatEntitiesAsText ());
        entityTokenizer.setCharValidator (parentTokenizer.getCharValidator ());
        
        Token token;
        try
        {
            recursionTrap.add (entity);
            token = parseElementContent (entityTokenizer, parent, recursionTrap);
            recursionTrap.remove (entity);
        }
        catch (XMLParseException e)
        {
            throw new XMLParseException ("Error while expanding entity "+entity+": "+e.getMessage (), e)
            .setToken (entityToken);
        }
        
        if (token == null)
            return;
        
        throw new XMLParseException ("Expanded entity "+entity+" is not well-formed since it contains the end-token for '"+parent.getName ()+"'", entityToken);
    }

    /** This turns a token into a node.
     * 
     *  <p>Override this to implement custom node types. 
     */
    protected Node toNode (Token token)
    {
        switch (token.getType()) //@COBEX
        {
        case TEXT: return createText (token);
        case ENTITY: return createEntity (token);
        case ATTRIBUTE: return createAttribute (token);
        case BEGIN_ELEMENT: return createElement (token);
        case CDATA: return createCData (token);
        case COMMENT: return createComment (token);
        case DTD_WHITESPACE: return createElementWhitespace (token);
        case PROCESSING_INSTRUCTION: return createProcessingInstruction (token);
        case DOCTYPE_END:
        case DOCTYPE_SYSTEM:
        case DOCTYPE_PUBLIC:
        case DOCTYPE_NDATA:
        case DOCTYPE_QUOTED_TEXT:
        case DOCTYPE_BEGIN_SUBSET:
        case DOCTYPE_END_SUBSET:
        case DOCTYPE_BEGIN_GROUP:
        case DOCTYPE_END_GROUP:
        case DOCTYPE_ALTERNATIVE:
        case DOCTYPE_IMPLIED:
        case DOCTYPE_REQUIRED:
        case DOCTYPE_FIXED:
        case DOCTYPE_COMMENT:
        case DOCTYPE_PARAMETER_ENTITY:
        case DOCTYPE_PARAMETER_ENTITY_END:
            return createDocTypeText (token);
        }

        // Note: this code should never be executed. If it is, then there is a new type of Token
        // and the switch wasn't updated for it.
        throw new XMLParseException ("Unexpected token "+token, token); //@COBEX
    }

    protected Node createDocTypeText (Token token)
    {
        return new DocTypeText (token);
    }
    
    protected Node createProcessingInstruction (Token token)
    {
        return new ProcessingInstruction (token);
    }

    protected Node createElementWhitespace (Token token)
    {
        return new Text (token);
    }

    protected Node createComment (Token token)
    {
        return new Comment (token);
    }

    protected Node createCData (Token token)
    {
        return new Text (token);
    }

    protected Node createElement (Token token)
    {
        return new Element (token);
    }

    protected Node createAttribute (Token token)
    {
        return new Attribute (token);
    }

    protected Node createEntity (Token token)
    {
        return new Entity (token, entityResolver);
    }

    protected Node createText (Token token)
    {
        return new Text (token);
    }

    /** Convenience method to parse a String into XML.
     * 
     *  <p>In this case, the encoding is ignored; the string already has to
     *  be Unicode. After the parsing, you will still find the encoding from
     *  the XML declaration in the Document (if there was one).
     */
    public static Document parse (String xml)
    {
        return new XMLParser ().parse (new XMLStringSource (xml));
    }
    
    /** Convenience method to parse a file into XML. 
     * @throws IOException
     */
    public static Document parse (File file) throws IOException
    {
        XMLIOSource source = new XMLIOSource (file);
        XMLParser parser = new XMLParser ();
        return parser.parse (source);
    }
}

