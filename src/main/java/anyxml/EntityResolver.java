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
 */
package anyxml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anyxml.validation.CharValidator;

public class EntityResolver
{
    /** Escape for "&" (ampersand) */
    public final static String AMP_ESCAPE = "&amp;";
    /** Escape for "<" (less than) */
    public final static String LT_ESCAPE = "&lt;";
    /** Escape for ">" (greater than) */
    public final static String GT_ESCAPE = "&gt;";
    /** Escape for '"' (double quote) */
    public final static String QUOT_ESCAPE = "&quot;";
    /** Escape for "'" (single quote or apostrophe) */
    public final static String APOS_ESCAPE = "&apos;";
    
    private EntityResolver parent;
    protected Map<String, String> resolveMap = new LinkedHashMap<String, String> (256);
    private CharValidator charValidator = new CharValidator ();
    
    public EntityResolver ()
    {
        clear ();
    }
    
    public EntityResolver (EntityResolver parent)
    {
        this.parent = parent;
        clear ();
    }
    
    public EntityResolver getParent ()
    {
        return parent;
    }
    
    public CharValidator getCharValidator ()
    {
        // Inherit validator
        if (charValidator == null)
            return parent == null ? null : parent.getCharValidator ();
        
        return charValidator;
    }
    
    public EntityResolver setCharValidator (CharValidator charValidator)
    {
        this.charValidator = charValidator;
        return this;
    }
    
    /**
     * Remove all definitions from the map except the standard XML
     * entities (&amp;lt;, &amp;gt;, ...)
     */
    public void clear ()
    {
        resolveMap.clear ();

        resolveMap.put ("lt", "<");
        resolveMap.put ("gt", ">");
        resolveMap.put ("amp", "&");
        resolveMap.put ("quot", "\"");
        resolveMap.put ("apos", "'");
    }
    
    /** Add a new entity
     * 
     * @param name for example "lt"
     * @param replacementText for example "<"
     */
    public void add (String name, String replacementText)
    {
        if (name == null || name.trim().length () == 0)
            throw new IllegalArgumentException ("name is null or empty");
        if (replacementText == null) // can be empty!
            throw new IllegalArgumentException ("replacementText is null");
        
        resolveMap.put (name, replacementText);
    }
    
    /** Check if an entity is defined.
     *  
     *  @param name The name of an entity ("lt" or "&lt;").
     *  @return the value of the entity or {@code null}
     */
    public boolean isDefined (String name)
    {
        name = stripName (name);
        if (resolveMap.containsKey (name))
            return true;
        
        return parent == null ? false : parent.isDefined (name);
    }
    
    /** Resolve an entity reference.
     * 
     *  <p>This returns the text stored for this entity reference.
     *  No recursive expansion takes place.
     *  
     *  @param name The name of an entity ("lt" or "&lt;").
     *  @return the value of the entity or {@code null}
     */
    public String resolve (String name)
    {
        name = stripName (name);
        String result = resolveMap.get (name);
        if (result == null && parent != null)
            result = parent.resolve (name);
        
        return result;
    }
    
    protected String stripName (String name)
    {
        if (name.startsWith ("&") && name.endsWith (";"))
            name = name.substring (1, name.length () - 1);
        
        return name;
    }
    
    /** Expand an entity reference. If the reference is unknown, the method will {@code null}.
     * 
     * <p>Valid inputs are entity names or entity references (i.e. it will work with "lt" and "&lt;")
     */
    public String expand (String entity)
    {
        if (entity.startsWith ("&#"))
        {
            int codePoint = expandNumericEntity (entity);
            return new String (Character.toChars (codePoint));
        }
        
        String name = stripName (entity);
        if (!isDefined (name))
            return null;
        
        return resolve (name);
    }
    
    /** Replace text in a string with entity references */
    public String encode (String input)
    {
        StringBuilder buffer = new StringBuilder (resolveMap.size () * 8);
        String delim = "(";
        Map<String, String> reverseMap = new HashMap<String, String> (resolveMap.size ());
        for (Map.Entry<String, String> entry: resolveMap.entrySet ())
        {
            String value = entry.getValue ();
            reverseMap.put (value, entry.getKey ());
            buffer.append (delim);
            delim = "|";
            buffer.append (Pattern.quote (value));
        }
        buffer.append (")");
        Pattern p = Pattern.compile (buffer.toString ());
        
        buffer.setLength (0);
        Matcher match = p.matcher (input);
        
        int pos = 0;
        while (match.find ())
        {
            int startPos = match.start ();
            buffer.append (input.substring (pos, startPos));
            
            String reverseKey = match.group ();
            buffer.append ("&");
            buffer.append (reverseMap.get (reverseKey));
            buffer.append (";");
            
            pos = match.end ();
        }
        
        buffer.append (input.substring (pos));
        
        return buffer.toString ();
    }

    /** Returns the character value of a numeric entity.
     * 
     *  <p>NOTE: This method returns a "code point", not a character. One "code point" can
     *  map to one <b>or</b> two Java characters!
     *  
     * @throws IllegalArgumentException if the numeric entity has the wrong format, or the value is too low or high.
     */
    public int expandNumericEntity (String entity)
    {
        if (!entity.endsWith (";"))
            throw new IllegalArgumentException ("Entities must end with ';': ["+entity+"]");
        
        int value;
        boolean isHex;
        if (entity.startsWith ("&#x"))
        {
            try
            {
                value = Integer.parseInt (entity.substring (3, entity.length () - 1), 16);
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException ("Value of hexadecimal entity can't be parsed: ["+entity+"]", e);
            }
            isHex = true;
        }
        else if (entity.startsWith ("&#"))
        {
            try
            {
                value = Integer.parseInt (entity.substring (2, entity.length () - 1), 10);
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException ("Value of decimal entity can't be parsed: ["+entity+"]", e);
            }
            isHex = false;
        }
        else
            throw new IllegalArgumentException ("Entities must start with '&#' or '&#x': ["+entity+"]");
        
        if (value < 0)
            throw new IllegalArgumentException ("Value of numeric entity must be > 0: ["+entity+"]");
        
        if (charValidator != null)
        {
            String msg = charValidator.isValid (value);
            if (msg == null)
                return value;
            
            throw new IllegalArgumentException ("Illegal value for numeric entity. "+msg);
        }
        
        if (value <= 0x10ffff)
            return value;
        
        throw new IllegalArgumentException ("Value of numeric entity must be [#x0000-#x10FFFF]"
                    +": ["+entity+"]"+(isHex ? "" : " (0x"+Integer.toHexString (value)+")"));
    }

    public void validateEntity (String entity)
    {
        if (entity == null)
            throw new IllegalArgumentException ("Entity is null");
        if (entity.length () <= 2)
            throw new IllegalArgumentException ("Entity is too short");
        if (!entity.startsWith ("&"))
            throw new IllegalArgumentException ("Entity doesn't begin with '&': '"+entity+"'");
        if (!entity.endsWith (";"))
            throw new IllegalArgumentException ("Entity doesn't end with ';': '"+entity+"'");
        
        if (entity.charAt (1) == '#')
        {
            expandNumericEntity (entity);
        }
        else
        {
            CharValidator v = getCharValidator ();
            if (!v.isNameStartChar (entity.charAt (1)))
                throw new IllegalArgumentException ("Entity name doesn't begin with a valid character: '"+entity+"'");
            
            int N = entity.length ()-1;
            for (int i=2; i<N; i++)
            {
                if (!v.isNameChar (entity.charAt (i)))
                {
                    String s = "";
                    if (i+1<N)
                        s = entity.substring(i+1);
                    s = entity.substring (0, i) + "_" +  entity.charAt (i) + "_" + s;
                    throw new IllegalArgumentException ("Character "+i+" of entity name is invalid: '"+entity+"'");
                }
            }
        }
    }
}
