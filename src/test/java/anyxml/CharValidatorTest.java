package anyxml;

import static org.junit.Assert.*;

import org.junit.Test;

import anyxml.XMLStringSource;
import anyxml.validation.CharValidator;

public class CharValidatorTest
{
    @Test
    public void testHighSurrogatePointOnly () throws Exception
    {
        assertEquals ("Unexpected end of input", check ("\ud800"));
    }

    @Test
    public void testSurrogatePair_D800_3C () throws Exception
    {
        assertEquals ("Character after first in surrogate pair is not between 0xDC00 and 0xDFFF: 3c", check ("\ud800<"));
    }
    
    @Test
    public void testFFFF () throws Exception
    {
        assertEquals ("Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xffff]", check ("\uffff"));
    }
    
    @Test
    public void testFFFE () throws Exception
    {
        assertEquals ("Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#xfffe]", check ("\ufffe"));
    }
    
    public String check (String string)
    {
        XMLStringSource source = new XMLStringSource (string);
        CharValidator v = new CharValidator ();
        return v.isValid (source, 0);
    }
}
