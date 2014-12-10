package anyxml.dtd;

import anyxml.EntityResolver;
import anyxml.XMLParseException;

public class DocTypeEntityResolver extends EntityResolver
{
    private final DocType docType;

    public DocTypeEntityResolver (DocType docType)
    {
        super ();
        this.docType = docType;
    }

    public DocTypeEntityResolver (DocType docType, EntityResolver parent)
    {
        super (parent);
        this.docType = docType;
    }
    
    public DocType getDocType ()
    {
        return docType;
    }
    
    @Override
    public boolean isDefined (String name)
    {
        name = stripName (name);
        
        DocTypeEntity e = docType.getEntity (name);
        if (e == null)
            return super.isDefined (name);
        
        return true;
    }
    
    @Override
    public String resolve (String name)
    {
        name = stripName (name);
        
        DocTypeEntity e = docType.getEntity (name);
        if (e == null)
            return super.resolve (name);
        
        String s = e.getResolvedText ();
        if (s == null)
        {
            s = resolveLiteralValueOfEntity (name, e.getText ());
            e.setResolvedText (s);
        }
        
        return s;
    }

    /** Resolve character and parameter-entity references */
    public String resolveLiteralValueOfEntity (String name, String text)
    {
        int pos = 0;
        int N = text.length ();
        
        StringBuilder buffer = new StringBuilder (1024);
        
        while (pos < N)
        {
            int parEntityPos = text.indexOf ('%', pos);
            int charEntityPos = text.indexOf ("&#", pos);
            
            if (parEntityPos == -1 && charEntityPos == -1)
                break;
            
            if (parEntityPos == -1)
                parEntityPos = N;
            if (charEntityPos == -1)
                charEntityPos = N;
            
            if (parEntityPos < charEntityPos)
            {
                if (pos < parEntityPos)
                    buffer.append (text, pos, parEntityPos);
                
                int endPos = text.indexOf (';', parEntityPos);
                if (endPos == -1)
                    throw new XMLParseException ("Missing ';' after '%' of parameter entity name: "+text.substring (parEntityPos));
                endPos ++;
                
                String parEntityName = text.substring (parEntityPos + 1, endPos - 1);
                DocTypeEntity parEntity = docType.getParameterEntity (parEntityName);
                String s = parEntity.getResolvedText ();
                if (s == null)
                {
                    s = resolveLiteralValueOfEntity (parEntityName, s);
                    parEntity.setResolvedText (s);
                }
                buffer.append (s);

                pos = endPos;
            }
            else
            {
                if (pos < charEntityPos)
                    buffer.append (text, pos, charEntityPos);
                
                int endPos = text.indexOf (';', charEntityPos);
                if (endPos == -1)
                    throw new XMLParseException ("Missing ';' after '&#' of character entity: "+text.substring (charEntityPos));
                endPos ++;
                
                String s = text.substring (charEntityPos, endPos);
                buffer.append (expand (s));
                
                pos = endPos;
            }
        }
        
        if (pos < N)
            buffer.append (text, pos, N);
        
        return buffer.toString ();
    }
}
