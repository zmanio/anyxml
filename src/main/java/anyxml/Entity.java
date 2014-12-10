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

public class Entity extends BasicNode
{
    private EntityResolver resolver;
    
    public Entity (Token token, EntityResolver resolver)
    {
        super (token);
        this.resolver = resolver;
    }
    
    public Entity (String name)
    {
        super (Type.ENTITY, name.charAt (0) == '&' ? name : ("&" + name + ";"));
    }

    public static Entity createDecimalEntity (int unicode)
    {
        checkUnicode (unicode);
        
        return new Entity ("&#" + unicode + ";");
    }

    public static void checkUnicode (int unicode)
    {
        if (unicode < 0)
            throw new IllegalArgumentException ("unicode must be >= 0: "+unicode+" 0x"+Integer.toHexString (unicode));
        if (unicode > Character.MAX_VALUE)
            throw new IllegalArgumentException ("unicode must be <= "+(int)Character.MAX_VALUE+": "+unicode+" 0x"+Integer.toHexString (unicode));
    }

    public static Entity createHexEntity (int unicode)
    {
        checkUnicode (unicode);
        
        return new Entity ("&#x" + Integer.toString (unicode, 16) + ";");
    }
    
    public void setResolver (EntityResolver resolver)
    {
        this.resolver = resolver;
    }
    
    public EntityResolver getResolver ()
    {
        return resolver;
    }

    public String getName ()
    {
        String name = getValue ();
        return name.substring (1, name.length () - 1);
    }
    
    public String getText ()
    {
        return resolver == null ? getValue () : resolver.expand (getValue ());
    }
    
    public boolean isWhitespace ()
    {
        String s = getText ();
        for (int i=0; i<s.length (); i++)
        {
            if (!Character.isWhitespace (s.charAt (i)))
                return false;
        }
        
        return true;
    }

    @Override
    public Entity createClone ()
    {
        return new Entity (getValue ());
    }
    
    @Override
    public Entity copy (Node orig)
    {
        super.copy (orig);
        
        Entity other = (Entity)orig;
        
        this.resolver = other.resolver;
        
        return this;
    }
    
    @Override
    public Entity copy ()
    {
        return (Entity)super.copy ();
    }
}
