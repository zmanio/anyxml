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

/**
 * Predefined HTML entities.
 * 
 * <p>Source: http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
 * 
 * @author DIGULAA
 *
 */
public class HTMLEntityResolver extends EntityResolver
{
    public HTMLEntityResolver ()
    {
        super ();
    }
    
    public HTMLEntityResolver (EntityResolver parent)
    {
        super (parent);
    }
    
    @Override
    public void clear ()
    {
        super.clear ();
        
        // TODO maybe I should split them by HTML standard? (2.0, 3.2, 4.0, XHTML 1.0, ...) 
        add ("nbsp", "\u00a0"); // no-break space (= non-breaking space)
        add ("iexcl", "\u00a1"); // inverted exclamation mark
        add ("cent", "\u00a2"); // cent sign
        add ("pound", "\u00a3"); // pound sign
        add ("curren", "\u00a4"); // currency sign
        add ("yen", "\u00a5"); // yen sign (= yuan sign)
        add ("brvbar", "\u00a6"); // broken bar (= broken vertical bar)
        add ("sect", "\u00a7"); // section sign
        add ("uml", "\u00a8"); // diaeresis (= spacing diaeresis); see German umlaut
        add ("copy", "\u00a9"); // copyright sign
        add ("ordf", "\u00aa"); // feminine ordinal indicator
        add ("laquo", "\u00ab"); // left-pointing double angle quotation mark (= left pointing guillemet)
        add ("not", "\u00ac"); // not sign
        add ("shy", "\u00ad"); // soft hyphen (= discretionary hyphen)
        add ("reg", "\u00ae"); // registered sign ( = registered trade mark sign)
        add ("macr", "\u00af"); // macron (= spacing macron = overline = APL overbar)
        add ("deg", "\u00b0"); // degree sign
        add ("plusmn", "\u00b1"); // plus-minus sign (= plus-or-minus sign)
        add ("sup2", "\u00b2"); // superscript two (= superscript digit two = squared)
        add ("sup3", "\u00b3"); // superscript three (= superscript digit three = cubed)
        add ("acute", "\u00b4"); // acute accent (= spacing acute)
        add ("micro", "\u00b5"); // micro sign
        add ("para", "\u00b6"); // pilcrow sign ( = paragraph sign)
        add ("middot", "\u00b7"); // middle dot (= Georgian comma = Greek middle dot)
        add ("cedil", "\u00b8"); // cedilla (= spacing cedilla)
        add ("sup1", "\u00b9"); // superscript one (= superscript digit one)
        add ("ordm", "\u00ba"); // masculine ordinal indicator
        add ("raquo", "\u00bb"); // right-pointing double angle quotation mark (= right pointing guillemet)
        add ("frac14", "\u00bc"); // vulgar fraction one quarter (= fraction one quarter)
        add ("frac12", "\u00bd"); // vulgar fraction one half (= fraction one half)
        add ("frac34", "\u00be"); // vulgar fraction three quarters (= fraction three quarters)
        add ("iquest", "\u00bf"); // inverted question mark (= turned question mark)
        add ("Agrave", "\u00c0"); // Latin capital letter A with grave (= Latin capital letter A grave)
        add ("Aacute", "\u00c1"); // Latin capital letter A with acute
        add ("Acirc", "\u00c2"); // Latin capital letter A with circumflex
        add ("Atilde", "\u00c3"); // Latin capital letter A with tilde
        add ("Auml", "\u00c4"); // Latin capital letter A with diaeresis
        add ("Aring", "\u00c5"); // Latin capital letter A with ring above (= Latin capital letter A ring)
        add ("AElig", "\u00c6"); // Latin capital letter AE (= Latin capital ligature AE)
        add ("Ccedil", "\u00c7"); // Latin capital letter C with cedilla
        add ("Egrave", "\u00c8"); // Latin capital letter E with grave
        add ("Eacute", "\u00c9"); // Latin capital letter E with acute
        add ("Ecirc", "\u00ca"); // Latin capital letter E with circumflex
        add ("Euml", "\u00cb"); // Latin capital letter E with diaeresis
        add ("Igrave", "\u00cc"); // Latin capital letter I with grave
        add ("Iacute", "\u00cd"); // Latin capital letter I with acute
        add ("Icirc", "\u00ce"); // Latin capital letter I with circumflex
        add ("Iuml", "\u00cf"); // Latin capital letter I with diaeresis
        add ("ETH", "\u00d0"); // Latin capital letter ETH
        add ("Ntilde", "\u00d1"); // Latin capital letter N with tilde
        add ("Ograve", "\u00d2"); // Latin capital letter O with grave
        add ("Oacute", "\u00d3"); // Latin capital letter O with acute
        add ("Ocirc", "\u00d4"); // Latin capital letter O with circumflex
        add ("Otilde", "\u00d5"); // Latin capital letter O with tilde
        add ("Ouml", "\u00d6"); // Latin capital letter O with diaeresis
        add ("times", "\u00d7"); // multiplication sign
        add ("Oslash", "\u00d8"); // Latin capital letter O with stroke (= Latin capital letter O slash)
        add ("Ugrave", "\u00d9"); // Latin capital letter U with grave
        add ("Uacute", "\u00da"); // Latin capital letter U with acute
        add ("Ucirc", "\u00db"); // Latin capital letter U with circumflex
        add ("Uuml", "\u00dc"); // Latin capital letter U with diaeresis
        add ("Yacute", "\u00dd"); // Latin capital letter Y with acute
        add ("THORN", "\u00de"); // Latin capital letter THORN
        add ("szlig", "\u00df"); // Latin small letter sharp s (= ess-zed); see German Eszett
        add ("agrave", "\u00e0"); // Latin small letter a with grave
        add ("aacute", "\u00e1"); // Latin small letter a with acute
        add ("acirc", "\u00e2"); // Latin small letter a with circumflex
        add ("atilde", "\u00e3"); // Latin small letter a with tilde
        add ("auml", "\u00e4"); // Latin small letter a with diaeresis
        add ("aring", "\u00e5"); // Latin small letter a with ring above
        add ("aelig", "\u00e6"); // Latin small letter ae (= Latin small ligature ae)
        add ("ccedil", "\u00e7"); // Latin small letter c with cedilla
        add ("egrave", "\u00e8"); // Latin small letter e with grave
        add ("eacute", "\u00e9"); // Latin small letter e with acute
        add ("ecirc", "\u00ea"); // Latin small letter e with circumflex
        add ("euml", "\u00eb"); // Latin small letter e with diaeresis
        add ("igrave", "\u00ec"); // Latin small letter i with grave
        add ("iacute", "\u00ed"); // Latin small letter i with acute
        add ("icirc", "\u00ee"); // Latin small letter i with circumflex
        add ("iuml", "\u00ef"); // Latin small letter i with diaeresis
        add ("eth", "\u00f0"); // Latin small letter eth
        add ("ntilde", "\u00f1"); // Latin small letter n with tilde
        add ("ograve", "\u00f2"); // Latin small letter o with grave
        add ("oacute", "\u00f3"); // Latin small letter o with acute
        add ("ocirc", "\u00f4"); // Latin small letter o with circumflex
        add ("otilde", "\u00f5"); // Latin small letter o with tilde
        add ("ouml", "\u00f6"); // Latin small letter o with diaeresis
        add ("divide", "\u00f7"); // division sign
        add ("oslash", "\u00f8"); // Latin small letter o with stroke (= Latin small letter o slash)
        add ("ugrave", "\u00f9"); // Latin small letter u with grave
        add ("uacute", "\u00fa"); // Latin small letter u with acute
        add ("ucirc", "\u00fb"); // Latin small letter u with circumflex
        add ("uuml", "\u00fc"); // Latin small letter u with diaeresis
        add ("yacute", "\u00fd"); // Latin small letter y with acute
        add ("thorn", "\u00fe"); // Latin small letter thorn
        add ("yuml", "\u00ff"); // Latin small letter y with diaeresis
        add ("OElig", "\u0152"); // Latin capital ligature oe
        add ("oelig", "\u0153"); // Latin small ligature oe
        add ("Scaron", "\u0160"); // Latin capital letter s with caron
        add ("scaron", "\u0161"); // Latin small letter s with caron
        add ("Yuml", "\u0178"); // Latin capital letter y with diaeresis
        add ("fnof", "\u0192"); // Latin small letter f with hook (= function = florin)
        add ("circ", "\u02c6"); // modifier letter circumflex accent
        add ("tilde", "\u02dc"); // small tilde
        add ("Alpha", "\u0391"); // Greek capital letter Alpha
        add ("Beta", "\u0392"); // Greek capital letter Beta
        add ("Gamma", "\u0393"); // Greek capital letter Gamma
        add ("Delta", "\u0394"); // Greek capital letter Delta
        add ("Epsilon", "\u0395"); // Greek capital letter Epsilon
        add ("Zeta", "\u0396"); // Greek capital letter Zeta
        add ("Eta", "\u0397"); // Greek capital letter Eta
        add ("Theta", "\u0398"); // Greek capital letter Theta
        add ("Iota", "\u0399"); // Greek capital letter Iota
        add ("Kappa", "\u039a"); // Greek capital letter Kappa
        add ("Lambda", "\u039b"); // Greek capital letter Lambda
        add ("Mu", "\u039c"); // Greek capital letter Mu
        add ("Nu", "\u039d"); // Greek capital letter Nu
        add ("Xi", "\u039e"); // Greek capital letter Xi
        add ("Omicron", "\u039f"); // Greek capital letter Omicron
        add ("Pi", "\u03a0"); // Greek capital letter Pi
        add ("Rho", "\u03a1"); // Greek capital letter Rho
        add ("Sigma", "\u03a3"); // Greek capital letter Sigma
        add ("Tau", "\u03a4"); // Greek capital letter Tau
        add ("Upsilon", "\u03a5"); // Greek capital letter Upsilon
        add ("Phi", "\u03a6"); // Greek capital letter Phi
        add ("Chi", "\u03a7"); // Greek capital letter Chi
        add ("Psi", "\u03a8"); // Greek capital letter Psi
        add ("Omega", "\u03a9"); // Greek capital letter Omega
        add ("alpha", "\u03b1"); // Greek small letter alpha
        add ("beta", "\u03b2"); // Greek small letter beta
        add ("gamma", "\u03b3"); // Greek small letter gamma
        add ("delta", "\u03b4"); // Greek small letter delta
        add ("epsilon", "\u03b5"); // Greek small letter epsilon
        add ("zeta", "\u03b6"); // Greek small letter zeta
        add ("eta", "\u03b7"); // Greek small letter eta
        add ("theta", "\u03b8"); // Greek small letter theta
        add ("iota", "\u03b9"); // Greek small letter iota
        add ("kappa", "\u03ba"); // Greek small letter kappa
        add ("lambda", "\u03bb"); // Greek small letter lambda
        add ("mu", "\u03bc"); // Greek small letter mu
        add ("nu", "\u03bd"); // Greek small letter nu
        add ("xi", "\u03be"); // Greek small letter xi
        add ("omicron", "\u03bf"); // Greek small letter omicron
        add ("pi", "\u03c0"); // Greek small letter pi
        add ("rho", "\u03c1"); // Greek small letter rho
        add ("sigmaf", "\u03c2"); // Greek small letter final sigma
        add ("sigma", "\u03c3"); // Greek small letter sigma
        add ("tau", "\u03c4"); // Greek small letter tau
        add ("upsilon", "\u03c5"); // Greek small letter upsilon
        add ("phi", "\u03c6"); // Greek small letter phi
        add ("chi", "\u03c7"); // Greek small letter chi
        add ("psi", "\u03c8"); // Greek small letter psi
        add ("omega", "\u03c9"); // Greek small letter omega
        add ("thetasym", "\u03d1"); // Greek theta symbol
        add ("upsih", "\u03d2"); // Greek Upsilon with hook symbol
        add ("piv", "\u03d6"); // Greek pi symbol
        add ("ensp", "\u2002"); // en space
        add ("emsp", "\u2003"); // em space
        add ("thinsp", "\u2009"); // thin space
        add ("zwnj", "\u200c"); // zero-width non-joiner
        add ("zwj", "\u200d"); // zero-width joiner
        add ("lrm", "\u200e"); // left-to-right mark
        add ("rlm", "\u200f"); // right-to-left mark
        add ("ndash", "\u2013"); // en dash
        add ("mdash", "\u2014"); // em dash
        add ("lsquo", "\u2018"); // left single quotation mark
        add ("rsquo", "\u2019"); // right single quotation mark
        add ("sbquo", "\u201a"); // single low-9 quotation mark
        add ("ldquo", "\u201c"); // left double quotation mark
        add ("rdquo", "\u201d"); // right double quotation mark
        add ("bdquo", "\u201e"); // double low-9 quotation mark
        add ("dagger", "\u2020"); // dagger
        add ("Dagger", "\u2021"); // double dagger
        add ("bull", "\u2022"); // bullet (= black small circle)
        add ("hellip", "\u2026"); // horizontal ellipsis (= three dot leader)
        add ("permil", "\u2030"); // per mille sign
        add ("prime", "\u2032"); // prime (= minutes = feet)
        add ("Prime", "\u2033"); // double prime (= seconds = inches)
        add ("lsaquo", "\u2039"); // single left-pointing angle quotation mark
        add ("rsaquo", "\u203a"); // single right-pointing angle quotation mark
        add ("oline", "\u203e"); // overline (= spacing overscore)
        add ("frasl", "\u2044"); // fraction slash (= Solidus (punctuation)|solidus)
        add ("euro", "\u20ac"); // euro sign
        add ("image", "\u2111"); // black-letter capital I (= imaginary part)
        add ("weierp", "\u2118"); // script capital P (= power set = Weierstrass p)
        add ("real", "\u211c"); // black-letter capital R (= real part symbol)
        add ("trade", "\u2122"); // trademark sign
        add ("alefsym", "\u2135"); // alef symbol (= first transfinite cardinal)
        add ("larr", "\u2190"); // leftwards arrow
        add ("uarr", "\u2191"); // upwards arrow
        add ("rarr", "\u2192"); // rightwards arrow
        add ("darr", "\u2193"); // downwards arrow
        add ("harr", "\u2194"); // left right arrow
        add ("crarr", "\u21b5"); // downwards arrow with corner leftwards (= carriage return)
        add ("lArr", "\u21d0"); // leftwards double arrow
        add ("uArr", "\u21d1"); // upwards double arrow
        add ("rArr", "\u21d2"); // rightwards double arrow
        add ("dArr", "\u21d3"); // downwards double arrow
        add ("hArr", "\u21d4"); // left right double arrow
        add ("forall", "\u2200"); // for all
        add ("part", "\u2202"); // partial differential
        add ("exist", "\u2203"); // there exists
        add ("empty", "\u2205"); // empty set (= null set = diameter)
        add ("nabla", "\u2207"); // nabla (= backward difference)
        add ("isin", "\u2208"); // element of
        add ("notin", "\u2209"); // not an element of
        add ("ni", "\u220b"); // contains as member
        add ("prod", "\u220f"); // n-ary product (= product sign)
        add ("sum", "\u2211"); // n-ary summation
        add ("minus", "\u2212"); // minus sign
        add ("lowast", "\u2217"); // asterisk operator
        add ("radic", "\u221a"); // square root (= radical sign)
        add ("prop", "\u221d"); // proportional to
        add ("infin", "\u221e"); // infinity
        add ("ang", "\u2220"); // angle
        add ("and", "\u2227"); // logical and (= wedge)
        add ("or", "\u2228"); // logical or (= vee)
        add ("cap", "\u2229"); // intersection (= cap)
        add ("cup", "\u222a"); // union (= cup)
        add ("int", "\u222b"); // integral
        add ("there4", "\u2234"); // therefore
        add ("sim", "\u223c"); // tilde operator (= varies with = similar to)
        add ("cong", "\u2245"); // congruent to
        add ("asymp", "\u2248"); // almost equal to (= asymptotic to)
        add ("ne", "\u2260"); // not equal to
        add ("equiv", "\u2261"); // identical to; sometimes used for 'equivalent to'
        add ("le", "\u2264"); // less-than or equal to
        add ("ge", "\u2265"); // greater-than or equal to
        add ("sub", "\u2282"); // subset of
        add ("sup", "\u2283"); // superset of
        add ("nsub", "\u2284"); // not a subset of
        add ("sube", "\u2286"); // subset of or equal to
        add ("supe", "\u2287"); // superset of or equal to
        add ("oplus", "\u2295"); // circled plus (= direct sum)
        add ("otimes", "\u2297"); // circled times (= vector product)
        add ("perp", "\u22a5"); // up tack (= orthogonal to = perpendicular)
        add ("sdot", "\u22c5"); // dot operator
        add ("lceil", "\u2308"); // left ceiling (= APL upstile)
        add ("rceil", "\u2309"); // right ceiling
        add ("lfloor", "\u230a"); // left floor (= APL downstile)
        add ("rfloor", "\u230b"); // right floor
        add ("lang", "\u2329"); // left-pointing angle bracket (= bra)
        add ("rang", "\u232a"); // right-pointing angle bracket (= ket)
        add ("loz", "\u25ca"); // lozenge
        add ("spades", "\u2660"); // black spade suit
        add ("clubs", "\u2663"); // black club suit (= shamrock)
        add ("hearts", "\u2665"); // black heart suit (= valentine)
        add ("diams", "\u2666"); // black diamond suit
    }
}
