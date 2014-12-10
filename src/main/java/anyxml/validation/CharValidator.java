package anyxml.validation;

import anyxml.XMLSource;

public class CharValidator
{
    /**
     * Verify the character at
     * @param source
     * @param offset
     * @return
     */
    public String isValid (XMLSource source, int offset)
    {
        char c = source.charAt (offset);
        if (Character.isHighSurrogate (c))
        {
            if (offset + 1 >= source.length ())
                return "Unexpected end of input";
            
            char c2 = source.charAt (offset + 1);
            if (Character.isLowSurrogate (c2))
                return isValid (Character.toCodePoint (c, c2));
                
            return "Character after first in surrogate pair is not between 0xDC00 and 0xDFFF: "+Integer.toHexString (c2);
        }
        
        return isValid (c);
    }
    
    /** Is the character whitespace as defined by the W3C? */
    public boolean isWhitespace (char c)
    {
        switch (c)
        {
        case ' ':
        case '\t':
        case '\n':
        case '\r':
            return true;
        }
        
        return false;
    }
    
    /** Is this a valid unicode character as defined by the W3C? */
    public String isValid (int codePoint)
    {
        if (
               codePoint == '\t'
            || codePoint == '\r'
            || codePoint == '\n'
            || (0x0020 <= codePoint && codePoint <= 0xd7ff)
            || (0xe000 <= codePoint && codePoint <= 0xfffd)
            || (0x10000 <= codePoint && codePoint <= 0x10ffff)
        )
            return null;
        
        return "Allowed values are #x09 | #x0a | #x0d | [#x0020-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]: [#x"+Integer.toHexString (codePoint)+"]";
    }

    /** Return true if the character is valid as the first one of an XML name */
    public boolean isNameStartChar (char c)
    {
        return isLetter (c) || c == ':' || c == '_';
    }
    
    /** Return true if the character is valid inside of an XML name */
    public boolean isNameChar (char c)
    {
        return isLetter (c)
            || isDigit (c)
            || c == '.'
            || c == '-'
            || c == '_'
            || c == ':'
            || isCombiningChar (c)
            || isExtender (c)
        ;
    }
    
    public boolean isLetter (char c)
    {
        return isBaseChar (c) || isIdeographic (c);
    }

    public boolean isBaseChar (char c)
    {
        return
              (0x0041 <= c && c <= 0x005A)
           || (0x0061 <= c && c <= 0x007A)
           || (0x00C0 <= c && c <= 0x00D6)
           || (0x00D8 <= c && c <= 0x00F6)
           || (0x00F8 <= c && c <= 0x00FF)
           || (0x0100 <= c && c <= 0x0131)
           || (0x0134 <= c && c <= 0x013E)
           || (0x0141 <= c && c <= 0x0148)
           || (0x014A <= c && c <= 0x017E)
           || (0x0180 <= c && c <= 0x01C3)
           || (0x01CD <= c && c <= 0x01F0)
           || (0x01F4 <= c && c <= 0x01F5)
           || (0x01FA <= c && c <= 0x0217)
           || (0x0250 <= c && c <= 0x02A8)
           || (0x02BB <= c && c <= 0x02C1)
           || c == 0x0386
           || (0x0388 <= c && c <= 0x038A)
           || c == 0x038C
           || (0x038E <= c && c <= 0x03A1)
           || (0x03A3 <= c && c <= 0x03CE)
           || (0x03D0 <= c && c <= 0x03D6)
           || c == 0x03DA
           || c == 0x03DC
           || c == 0x03DE
           || c == 0x03E0
           || (0x03E2 <= c && c <= 0x03F3)
           || (0x0401 <= c && c <= 0x040C)
           || (0x040E <= c && c <= 0x044F)
           || (0x0451 <= c && c <= 0x045C)
           || (0x045E <= c && c <= 0x0481)
           || (0x0490 <= c && c <= 0x04C4)
           || (0x04C7 <= c && c <= 0x04C8)
           || (0x04CB <= c && c <= 0x04CC)
           || (0x04D0 <= c && c <= 0x04EB)
           || (0x04EE <= c && c <= 0x04F5)
           || (0x04F8 <= c && c <= 0x04F9)
           || (0x0531 <= c && c <= 0x0556)
           || c == 0x0559
           || (0x0561 <= c && c <= 0x0586)
           || (0x05D0 <= c && c <= 0x05EA)
           || (0x05F0 <= c && c <= 0x05F2)
           || (0x0621 <= c && c <= 0x063A)
           || (0x0641 <= c && c <= 0x064A)
           || (0x0671 <= c && c <= 0x06B7)
           || (0x06BA <= c && c <= 0x06BE)
           || (0x06C0 <= c && c <= 0x06CE)
           || (0x06D0 <= c && c <= 0x06D3)
           || c == 0x06D5
           || (0x06E5 <= c && c <= 0x06E6)
           || (0x0905 <= c && c <= 0x0939)
           || c == 0x093D
           || (0x0958 <= c && c <= 0x0961)
           || (0x0985 <= c && c <= 0x098C)
           || (0x098F <= c && c <= 0x0990)
           || (0x0993 <= c && c <= 0x09A8)
           || (0x09AA <= c && c <= 0x09B0)
           || c == 0x09B2
           || (0x09B6 <= c && c <= 0x09B9)
           || (0x09DC <= c && c <= 0x09DD)
           || (0x09DF <= c && c <= 0x09E1)
           || (0x09F0 <= c && c <= 0x09F1)
           || (0x0A05 <= c && c <= 0x0A0A)
           || (0x0A0F <= c && c <= 0x0A10)
           || (0x0A13 <= c && c <= 0x0A28)
           || (0x0A2A <= c && c <= 0x0A30)
           || (0x0A32 <= c && c <= 0x0A33)
           || (0x0A35 <= c && c <= 0x0A36)
           || (0x0A38 <= c && c <= 0x0A39)
           || (0x0A59 <= c && c <= 0x0A5C)
           || c == 0x0A5E
           || (0x0A72 <= c && c <= 0x0A74)
           || (0x0A85 <= c && c <= 0x0A8B)
           || c == 0x0A8D
           || (0x0A8F <= c && c <= 0x0A91)
           || (0x0A93 <= c && c <= 0x0AA8)
           || (0x0AAA <= c && c <= 0x0AB0)
           || (0x0AB2 <= c && c <= 0x0AB3)
           || (0x0AB5 <= c && c <= 0x0AB9)
           || c == 0x0ABD
           || c == 0x0AE0
           || (0x0B05 <= c && c <= 0x0B0C)
           || (0x0B0F <= c && c <= 0x0B10)
           || (0x0B13 <= c && c <= 0x0B28)
           || (0x0B2A <= c && c <= 0x0B30)
           || (0x0B32 <= c && c <= 0x0B33)
           || (0x0B36 <= c && c <= 0x0B39)
           || c == 0x0B3D
           || (0x0B5C <= c && c <= 0x0B5D)
           || (0x0B5F <= c && c <= 0x0B61)
           || (0x0B85 <= c && c <= 0x0B8A)
           || (0x0B8E <= c && c <= 0x0B90)
           || (0x0B92 <= c && c <= 0x0B95)
           || (0x0B99 <= c && c <= 0x0B9A)
           || c == 0x0B9C
           || (0x0B9E <= c && c <= 0x0B9F)
           || (0x0BA3 <= c && c <= 0x0BA4)
           || (0x0BA8 <= c && c <= 0x0BAA)
           || (0x0BAE <= c && c <= 0x0BB5)
           || (0x0BB7 <= c && c <= 0x0BB9)
           || (0x0C05 <= c && c <= 0x0C0C)
           || (0x0C0E <= c && c <= 0x0C10)
           || (0x0C12 <= c && c <= 0x0C28)
           || (0x0C2A <= c && c <= 0x0C33)
           || (0x0C35 <= c && c <= 0x0C39)
           || (0x0C60 <= c && c <= 0x0C61)
           || (0x0C85 <= c && c <= 0x0C8C)
           || (0x0C8E <= c && c <= 0x0C90)
           || (0x0C92 <= c && c <= 0x0CA8)
           || (0x0CAA <= c && c <= 0x0CB3)
           || (0x0CB5 <= c && c <= 0x0CB9)
           || c == 0x0CDE
           || (0x0CE0 <= c && c <= 0x0CE1)
           || (0x0D05 <= c && c <= 0x0D0C)
           || (0x0D0E <= c && c <= 0x0D10)
           || (0x0D12 <= c && c <= 0x0D28)
           || (0x0D2A <= c && c <= 0x0D39)
           || (0x0D60 <= c && c <= 0x0D61)
           || (0x0E01 <= c && c <= 0x0E2E)
           || c == 0x0E30
           || (0x0E32 <= c && c <= 0x0E33)
           || (0x0E40 <= c && c <= 0x0E45)
           || (0x0E81 <= c && c <= 0x0E82)
           || c == 0x0E84
           || (0x0E87 <= c && c <= 0x0E88)
           || c == 0x0E8A
           || c == 0x0E8D
           || (0x0E94 <= c && c <= 0x0E97)
           || (0x0E99 <= c && c <= 0x0E9F)
           || (0x0EA1 <= c && c <= 0x0EA3)
           || c == 0x0EA5
           || c == 0x0EA7
           || (0x0EAA <= c && c <= 0x0EAB)
           || (0x0EAD <= c && c <= 0x0EAE)
           || c == 0x0EB0
           || (0x0EB2 <= c && c <= 0x0EB3)
           || c == 0x0EBD
           || (0x0EC0 <= c && c <= 0x0EC4)
           || (0x0F40 <= c && c <= 0x0F47)
           || (0x0F49 <= c && c <= 0x0F69)
           || (0x10A0 <= c && c <= 0x10C5)
           || (0x10D0 <= c && c <= 0x10F6)
           || c == 0x1100
           || (0x1102 <= c && c <= 0x1103)
           || (0x1105 <= c && c <= 0x1107)
           || c == 0x1109
           || (0x110B <= c && c <= 0x110C)
           || (0x110E <= c && c <= 0x1112)
           || c == 0x113C
           || c == 0x113E
           || c == 0x1140
           || c == 0x114C
           || c == 0x114E
           || c == 0x1150
           || (0x1154 <= c && c <= 0x1155)
           || c == 0x1159
           || (0x115F <= c && c <= 0x1161)
           || c == 0x1163
           || c == 0x1165
           || c == 0x1167
           || c == 0x1169
           || (0x116D <= c && c <= 0x116E)
           || (0x1172 <= c && c <= 0x1173)
           || c == 0x1175
           || c == 0x119E
           || c == 0x11A8
           || c == 0x11AB
           || (0x11AE <= c && c <= 0x11AF)
           || (0x11B7 <= c && c <= 0x11B8)
           || c == 0x11BA
           || (0x11BC <= c && c <= 0x11C2)
           || c == 0x11EB
           || c == 0x11F0
           || c == 0x11F9
           || (0x1E00 <= c && c <= 0x1E9B)
           || (0x1EA0 <= c && c <= 0x1EF9)
           || (0x1F00 <= c && c <= 0x1F15)
           || (0x1F18 <= c && c <= 0x1F1D)
           || (0x1F20 <= c && c <= 0x1F45)
           || (0x1F48 <= c && c <= 0x1F4D)
           || (0x1F50 <= c && c <= 0x1F57)
           || c == 0x1F59
           || c == 0x1F5B
           || c == 0x1F5D
           || (0x1F5F <= c && c <= 0x1F7D)
           || (0x1F80 <= c && c <= 0x1FB4)
           || (0x1FB6 <= c && c <= 0x1FBC)
           || c == 0x1FBE
           || (0x1FC2 <= c && c <= 0x1FC4)
           || (0x1FC6 <= c && c <= 0x1FCC)
           || (0x1FD0 <= c && c <= 0x1FD3)
           || (0x1FD6 <= c && c <= 0x1FDB)
           || (0x1FE0 <= c && c <= 0x1FEC)
           || (0x1FF2 <= c && c <= 0x1FF4)
           || (0x1FF6 <= c && c <= 0x1FFC)
           || c == 0x2126
           || (0x212A <= c && c <= 0x212B)
           || c == 0x212E
           || (0x2180 <= c && c <= 0x2182)
           || (0x3041 <= c && c <= 0x3094)
           || (0x30A1 <= c && c <= 0x30FA)
           || (0x3105 <= c && c <= 0x312C)
           || (0xAC00 <= c && c <= 0xD7A3)
       ;
    }

    public boolean isIdeographic (char c)
    {
        return (0x4E00 <= c && c <= 0x9FA5)
            || c == 0x3007
            || (0x3021 <= c && c <= 0x3029)
        ;
    }

    public boolean isDigit (char c)
    {
        return (0x0030 <= c && c <= 0x0039)
            || (0x0660 <= c && c <= 0x0669)
            || (0x06F0 <= c && c <= 0x06F9)
            || (0x0966 <= c && c <= 0x096F)
            || (0x09E6 <= c && c <= 0x09EF)
            || (0x0A66 <= c && c <= 0x0A6F)
            || (0x0AE6 <= c && c <= 0x0AEF)
            || (0x0B66 <= c && c <= 0x0B6F)
            || (0x0BE7 <= c && c <= 0x0BEF)
            || (0x0C66 <= c && c <= 0x0C6F)
            || (0x0CE6 <= c && c <= 0x0CEF)
            || (0x0D66 <= c && c <= 0x0D6F)
            || (0x0E50 <= c && c <= 0x0E59)
            || (0x0ED0 <= c && c <= 0x0ED9)
            || (0x0F20 <= c && c <= 0x0F29)
        ;
    }

    public boolean isCombiningChar (char c)
    {
        return (0x0300 <= c && c <= 0x0345)
            || (0x0360 <= c && c <= 0x0361)
            || (0x0483 <= c && c <= 0x0486)
            || (0x0591 <= c && c <= 0x05A1)
            || (0x05A3 <= c && c <= 0x05B9)
            || (0x05BB <= c && c <= 0x05BD)
            || c == 0x05BF
            || (0x05C1 <= c && c <= 0x05C2)
            || c == 0x05C4
            || (0x064B <= c && c <= 0x0652)
            || c == 0x0670
            || (0x06D6 <= c && c <= 0x06DC)
            || (0x06DD <= c && c <= 0x06DF)
            || (0x06E0 <= c && c <= 0x06E4)
            || (0x06E7 <= c && c <= 0x06E8)
            || (0x06EA <= c && c <= 0x06ED)
            || (0x0901 <= c && c <= 0x0903)
            || c == 0x093C
            || (0x093E <= c && c <= 0x094C)
            || c == 0x094D
            || (0x0951 <= c && c <= 0x0954)
            || (0x0962 <= c && c <= 0x0963)
            || (0x0981 <= c && c <= 0x0983)
            || c == 0x09BC
            || c == 0x09BE
            || c == 0x09BF
            || (0x09C0 <= c && c <= 0x09C4)
            || (0x09C7 <= c && c <= 0x09C8)
            || (0x09CB <= c && c <= 0x09CD)
            || c == 0x09D7
            || (0x09E2 <= c && c <= 0x09E3)
            || c == 0x0A02
            || c == 0x0A3C
            || c == 0x0A3E
            || c == 0x0A3F
            || (0x0A40 <= c && c <= 0x0A42)
            || (0x0A47 <= c && c <= 0x0A48)
            || (0x0A4B <= c && c <= 0x0A4D)
            || (0x0A70 <= c && c <= 0x0A71)
            || (0x0A81 <= c && c <= 0x0A83)
            || c == 0x0ABC
            || (0x0ABE <= c && c <= 0x0AC5)
            || (0x0AC7 <= c && c <= 0x0AC9)
            || (0x0ACB <= c && c <= 0x0ACD)
            || (0x0B01 <= c && c <= 0x0B03)
            || c == 0x0B3C
            || (0x0B3E <= c && c <= 0x0B43)
            || (0x0B47 <= c && c <= 0x0B48)
            || (0x0B4B <= c && c <= 0x0B4D)
            || (0x0B56 <= c && c <= 0x0B57)
            || (0x0B82 <= c && c <= 0x0B83)
            || (0x0BBE <= c && c <= 0x0BC2)
            || (0x0BC6 <= c && c <= 0x0BC8)
            || (0x0BCA <= c && c <= 0x0BCD)
            || c == 0x0BD7
            || (0x0C01 <= c && c <= 0x0C03)
            || (0x0C3E <= c && c <= 0x0C44)
            || (0x0C46 <= c && c <= 0x0C48)
            || (0x0C4A <= c && c <= 0x0C4D)
            || (0x0C55 <= c && c <= 0x0C56)
            || (0x0C82 <= c && c <= 0x0C83)
            || (0x0CBE <= c && c <= 0x0CC4)
            || (0x0CC6 <= c && c <= 0x0CC8)
            || (0x0CCA <= c && c <= 0x0CCD)
            || (0x0CD5 <= c && c <= 0x0CD6)
            || (0x0D02 <= c && c <= 0x0D03)
            || (0x0D3E <= c && c <= 0x0D43)
            || (0x0D46 <= c && c <= 0x0D48)
            || (0x0D4A <= c && c <= 0x0D4D)
            || c == 0x0D57
            || c == 0x0E31
            || (0x0E34 <= c && c <= 0x0E3A)
            || (0x0E47 <= c && c <= 0x0E4E)
            || c == 0x0EB1
            || (0x0EB4 <= c && c <= 0x0EB9)
            || (0x0EBB <= c && c <= 0x0EBC)
            || (0x0EC8 <= c && c <= 0x0ECD)
            || (0x0F18 <= c && c <= 0x0F19)
            || c == 0x0F35
            || c == 0x0F37
            || c == 0x0F39
            || c == 0x0F3E
            || c == 0x0F3F
            || (0x0F71 <= c && c <= 0x0F84)
            || (0x0F86 <= c && c <= 0x0F8B)
            || (0x0F90 <= c && c <= 0x0F95)
            || c == 0x0F97
            || (0x0F99 <= c && c <= 0x0FAD)
            || (0x0FB1 <= c && c <= 0x0FB7)
            || c == 0x0FB9
            || (0x20D0 <= c && c <= 0x20DC)
            || c == 0x20E1
            || (0x302A <= c && c <= 0x302F)
            || c == 0x3099
            || c == 0x309A
        ;
    }

    public boolean isExtender (char c)
    {
        return c == 0x00B7
            || c == 0x02D0
            || c == 0x02D1
            || c == 0x0387
            || c == 0x0640
            || c == 0x0E46
            || c == 0x0EC6
            || c == 0x3005
            || (0x3031 <= c && c <= 0x3035)
            || (0x309D <= c && c <= 0x309E)
            || (0x30FC <= c && c <= 0x30FE)
        ;
    }

}
