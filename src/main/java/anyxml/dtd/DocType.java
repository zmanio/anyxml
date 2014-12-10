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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anyxml.BasicNode;
import anyxml.EntityResolver;
import anyxml.Node;
import anyxml.Token;
import anyxml.XMLWriter;


public class DocType extends BasicNode
{
    public static enum DocTypeType
    {
        INLINE,
        SYSTEM,
        PUBLIC
    };
    
    private String name;
    private DocTypeType docTypeType = DocTypeType.INLINE;
    private String publicLiteral;
    private String systemLiteral;
    private String notationDataName;
    private List<Node> nodes = new ArrayList<Node> ();
    private Map<String, DocTypeElement> nameToElement = new HashMap<String, DocTypeElement> ();
    private Map<String, List<DocTypeAttributeList>> nameToAttributeList = new HashMap<String, List<DocTypeAttributeList>> ();
    private Map<String, DocTypeEntity> parameterEntities = new HashMap<String, DocTypeEntity> ();
    private Map<String, DocTypeEntity> entities = new HashMap<String, DocTypeEntity> ();
    
    public DocType (Token token)
    {
        super (token);
    }
    
    public DocType ()
    {
        super (anyxml.XMLTokenizer.Type.DOCTYPE, "<!DOCTYPE");
    }

    public void add (Node n)
    {
        nodes.add (n);
        
        if (n.getType () == anyxml.XMLTokenizer.Type.DOCTYPE_ELEMENT)
        {
            DocTypeElement e = (DocTypeElement)n;
            nameToElement.put (e.getName (), e);
        }
        else if (n.getType () == anyxml.XMLTokenizer.Type.DOCTYPE_ATTLIST)
        {
            // There can be more than one ATTLIST per element in a DTD!
            DocTypeAttributeList alist = (DocTypeAttributeList)n;
            String key = alist.getElementName ();
            List<DocTypeAttributeList> definitions = nameToAttributeList.get (key);
            if (definitions == null)
            {
                definitions = new ArrayList<DocTypeAttributeList> ();
                nameToAttributeList.put (key, definitions);
            }
            definitions.add (alist);
        }
        else if (n.getType () == anyxml.XMLTokenizer.Type.DOCTYPE_ENTITY)
        {
            DocTypeEntity entity = (DocTypeEntity)n;
            if (entity.isParameterEntity ())
            {
                if (!parameterEntities.containsKey (entity.getName ()))
                    parameterEntities.put (entity.getName (), entity);
            }
            else
            {
                if (!entities.containsKey (entity.getName ()))
                    entities.put (entity.getName (), entity);
            }
        }
    }

    /** Map element and attlist declarations in the DTD */
    public void mapElementsAndAttributes ()
    {
        for (Map.Entry<String, List<DocTypeAttributeList>> entry: nameToAttributeList.entrySet ())
        {
            String elementName = entry.getKey ();
            DocTypeElement element = getElement (elementName);
            if (element != null)
            {
                element.setAttLists (entry.getValue ());
                
                for (DocTypeAttributeList attList: entry.getValue ())
                {
                    attList.setElement (element);
                }
            }
        }
    }
    
    public String getName ()
    {
        return name;
    }
    
    public void setName (String name)
    {
        this.name = name;
    }
    
    public DocTypeType getDocTypeType ()
    {
        return docTypeType;
    }
    
    public void setDocTypeType (DocTypeType docTypeType)
    {
        this.docTypeType = docTypeType;
    }
    
    public String getPublicLiteral ()
    {
        return publicLiteral;
    }
    
    public void setPublicLiteral (String publicLiteral)
    {
        this.publicLiteral = publicLiteral;
    }
    
    public String getSystemLiteral ()
    {
        return systemLiteral;
    }
    
    public void setSystemLiteral (String systemLiteral)
    {
        this.systemLiteral = systemLiteral;
    }
    
    public String getNotationDataName ()
    {
        return notationDataName;
    }
    
    public void setNotationDataName (String notationDataName)
    {
        this.notationDataName = notationDataName;
    }
    
    @Override
    public DocType toXML (XMLWriter writer) throws IOException
    {
        super.toXML (writer);
        
        for (Node n: nodes)
            n.toXML (writer);
        
        return this;
    }

    public DocTypeElement getElement (String name)
    {
        return nameToElement.get (name.trim ());
    }
    
    public List<DocTypeElement> getElements ()
    {
        return new ArrayList<DocTypeElement> (nameToElement.values ());
    }
    
    public List<DocTypeAttributeList> getAttributeList (String name)
    {
        return nameToAttributeList.get (name);
    }
    
    public List<List<DocTypeAttributeList>> getAttributeLists ()
    {
        return new ArrayList<List<DocTypeAttributeList>> (nameToAttributeList.values ());
    }
    
    public DocTypeEntity getEntity (String name)
    {
        return entities.get (name);
    }
    
    public EntityResolver getEntityResolver ()
    {
        return getEntityResolver (null);
    }
    
    public EntityResolver getEntityResolver (EntityResolver parent)
    {
        EntityResolver resolver = new EntityResolver (parent);
        for (DocTypeEntity e: entities.values ())
            resolver.add (e.getName (), e.getText ());
        return resolver;
    }
    
    public DocTypeEntity getParameterEntity (String name)
    {
        return parameterEntities.get (name);
    }
}
