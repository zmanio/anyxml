package anyxml;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.junit.Test;

import anyxml.Document;
import anyxml.XMLParser;
import anyxml.XMLWriter;


public class XMLWriterTest
{
    @Test
    public void testIndent () throws Exception
    {
        String source = "<a><b /><b>xxx</b><b><c></c></b><b>xxx<c/>xxx</b></a>";
        Document doc = XMLParser.parse (source);
        StringWriter buffer = new StringWriter ();
        XMLWriter writer = new XMLWriter (buffer);
        writer.setIndent ("\t");
        writer.setPadCompact (true);
        doc.toXML (writer);
        
        assertEquals (fixCRLF (
                "<a>\r\n" + 
                "\t<b />\r\n" + 
                "\t<b>xxx</b>\r\n" + 
                "\t<b>\r\n" + 
                "\t\t<c>\r\n" + 
                "\t\t</c>\r\n" + 
                "\t</b>\r\n" + 
                "\t<b>\r\n" + 
                "\t\txxx\r\n" + 
                "\t\t<c />\r\n" + 
                "\t\txxx\r\n" + 
                "\t</b>\r\n" + 
                "</a>"),
                fixCRLF (buffer.toString ())
        );
    }

    private String fixCRLF (String string)
    {
        return string.replaceAll ("\r\n", "\n").replaceAll ("\r", "\n");
    }
}
