package anyxml.mapping;

import static org.junit.Assert.*;

import org.junit.Test;

import anyxml.Document;
import anyxml.Element;
import anyxml.Text;
import anyxml.XMLParser;
import anyxml.mapping.AttributeMapping;
import anyxml.mapping.ElementName;
import anyxml.mapping.JavaMapper;
import anyxml.mapping.MappingException;
import anyxml.mapping.TextHandler;

public class JavaMappingTest
{
    public static class TextMapperWithoutParameter
    {
        public int count;
        
        /** Standard text handler */
        public void text ()
        {
            count ++;
        }
    }
    
    public static class TextMapperString
    {
        public StringBuilder buffer = new StringBuilder ();
        
        /** Standard text handler, gets the text as unicode string */
        public void text (String text)
        {
            buffer.append (text);
        }
    }
    
    public static class TextMapperText
    {
        public StringBuilder buffer = new StringBuilder ();
        public int count;
        
        /** Standard text handler, gets the text as <code>Text</code> object. It is allowed to modify this object. */
        public void text (Text text)
        {
            buffer.append (text);
            text.setText (""+count);
            count ++;
        }
    }
    
    public static class TextMapperElementString
    {
        public StringBuilder buffer = new StringBuilder ();
        
        /** Standard text handler which also gets the surrounding element. This is useful for filtering by element name. */
        public void text (Element parent, String text)
        {
            buffer.append (parent.getName ());
            buffer.append (':');
            buffer.append (text);
        }
    }
    
    public static class TextMapperElementText
    {
        public StringBuilder buffer = new StringBuilder ();
        
        /** Standard text handler which also gets the surrounding element. This is useful for modifying the <code>Text</code> objects of only certain elements. */
        public void text (Element parent, Text text)
        {
            buffer.append (parent.getName ());
            buffer.append (':');
            buffer.append (text);
        }
    }
    
    public static class TextMapperSpecialHandler
    {
        public int textCount;
        public int elementCount;
        
        public void text ()
        {
            elementCount ++;
        }
        
        /** Use this annotation if you have an element called <code>text</code> */
        @TextHandler
        public void onText ()
        {
            textCount ++;
        }
    }
    
    public static class RootElementMapper
    {
        public int count;
        
        /** Handler for <code>root</code> elements. */
        public void root ()
        {
            count ++;
        }
    }
    
    public static class ElementMapperNoParameters
    {
        public int count;
        
        /** Handler for <code>p</code> elements. */
        public void p ()
        {
            count ++;
        }
    }
    
    public static class ElementMapperWithElement
    {
        public int count;
        
        /** Handler for <code>p</code> elements which also gets the Element object */
        public void p (Element e)
        {
            assertEquals ("p", e.getName ());
            count ++;
        }
    }
    
    public static class ElementMapperWithElementAndOptionalAttribute
    {
        public String a;
        
        /** Handler for <code>p</code> elements which also gets the <code>Element</code> object and an optional parameter <code>a</code> */
        @ElementName({"p"})
        public void test (Element e, @AttributeMapping("a") String a)
        {
            assertEquals ("p", e.getName ());
            this.a = a;
        }
    }
    
    public static class ElementMapperWithSpecialElementNameAndRequiredAttributeName
    {
        public String a;
        
        /** Handler for <code>p</code> elements which also gets the <code>Element</code> object and a required parameter <code>a</code> */
        public void p (Element e, @AttributeMapping(name="a", required=true) String xxx)
        {
            assertEquals ("p", e.getName ());
            this.a = xxx;
        }
    }
    
    public static class ElementMapperInt
    {
        public int a = -1;
        
        /** Handler for <code>p</code> elements with an optional parameter <code>a</code> */
        public void p (@AttributeMapping("a") int a)
        {
            this.a = a;
        }
    }
    
    public static class ElementMapperBoolean
    {
        public Boolean a;
        
        /** Handler for <code>p</code> elements with an optional parameter <code>a</code> */
        public void p (@AttributeMapping("a") boolean a)
        {
            this.a = a;
        }
    }
    
    public static class ElementMapperBooleanTrueValue
    {
        public Boolean a;
        
        /** Handler for <code>p</code> elements with an optional parameter <code>a</code> */
        public void p (@AttributeMapping(name="a", trueValues={"a"}) boolean a)
        {
            this.a = a;
        }
    }
    
    public static class ElementMapperBooleanFalseValue
    {
        public Boolean a;
        
        /** Handler for <code>p</code> elements with an optional parameter <code>a</code> */
        public void p (@AttributeMapping(name="a", falseValues={"a"}) boolean a)
        {
            this.a = a;
        }
    }
    
    public static class ElementMapperBooleanTrueFalseValue
    {
        public Boolean a;
        
        /** Handler for <code>p</code> elements with an optional parameter <code>a</code> */
        public void p (@AttributeMapping(name="a", trueValues={"1"}, falseValues={"a"}) boolean a)
        {
            this.a = a;
        }
    }
    
    public static class ElementMapperInteger
    {
        public Integer a;
        
        /** Handler for <code>p</code> elements with an optional parameter <code>a</code> */
        public void p (@AttributeMapping("a") Integer a)
        {
            this.a = a;
        }
    }
    
    public static class ElementMapperBooleanObject
    {
        public Boolean a;
        
        /** Handler for <code>p</code> elements with an optional parameter <code>a</code> */
        public void p (@AttributeMapping("a") Boolean a)
        {
            this.a = a;
        }
    }
    
    public static class ElementMapperWithSeveralNames
    {
        public int count;
        
        /** Handler for <code>p</code> elements which also gets the <code>Element</code> object and an optional parameter <code>a</code> */
        @ElementName({"p", "text"})
        public void root (Element e, @AttributeMapping("a") String a)
        {
            count ++;
        }
    }
    
    // TODO Supply mapping for more types (date, double, bigdecimal)
    // TODO Supply mapping for custom types
    
    public final static String TEST_XML = "<root><p>a</p><p>b</p><text>c</text><text /></root>";
    public final static String TEST2_XML = "<root><p a='x'>text</p></root>";
    public final static String TEST3_XML = "<root><p a='5'>text</p></root>";
    public final static String TEST4_XML = "<root><p a='-'>text</p></root>";
    public final static String TEST_ON_XML = "<root><p a='on'>text</p></root>";
    
    @Test
    public void testTextWithoutParameter () throws Exception
    {
        TextMapperWithoutParameter handler = new TextMapperWithoutParameter ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals (3, handler.count);
    }
    
    @Test
    public void testTextMapperString () throws Exception
    {
        TextMapperString handler = new TextMapperString ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals ("abc", handler.buffer.toString ());
    }
    
    @Test
    public void testTextMapperText () throws Exception
    {
        TextMapperText handler = new TextMapperText ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals ("abc", handler.buffer.toString ());
    }
    
    @Test
    public void testTextMapperSpecialHandler () throws Exception
    {
        TextMapperSpecialHandler handler = new TextMapperSpecialHandler ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals (3, handler.textCount);
        assertEquals (2, handler.elementCount);
    }
    
    @Test
    public void testRootElementMapper () throws Exception
    {
        RootElementMapper handler = new RootElementMapper ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals (1, handler.count);
    }
    
    @Test
    public void testElementMapperNoParameters () throws Exception
    {
        ElementMapperNoParameters handler = new ElementMapperNoParameters ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals (2, handler.count);
    }
    
    @Test
    public void testElementMapperWithElement () throws Exception
    {
        ElementMapperWithElement handler = new ElementMapperWithElement ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals (2, handler.count);
    }
    
    @Test
    public void testElementMapperWithElementAndOptionalAttribute () throws Exception
    {
        ElementMapperWithElementAndOptionalAttribute handler = new ElementMapperWithElementAndOptionalAttribute ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertNull (handler.a);
    }
    
    @Test
    public void testElementMapperWithSpecialElementNameAndRequiredAttributeName () throws Exception
    {
        ElementMapperWithSpecialElementNameAndRequiredAttributeName handler = new ElementMapperWithSpecialElementNameAndRequiredAttributeName ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        try
        {
            mapper.setHandler (handler).apply (doc);
            fail ("No exception was thrown");
        }
        catch (MappingException e)
        {
            MappingException wrapped = (MappingException)e.getCause ();
            assertEquals ("Required attribute \"a\" is missing: <p>a</p>", wrapped.getMessage ());
        }
    }
    
    @Test
    public void testElementMapperWithSpecialElementNameAndRequiredAttributeName2 () throws Exception
    {
        ElementMapperWithSpecialElementNameAndRequiredAttributeName handler = new ElementMapperWithSpecialElementNameAndRequiredAttributeName ();
        Document doc = XMLParser.parse (TEST2_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals ("x", handler.a);
    }
    
    @Test
    public void testElementMapperInt () throws Exception
    {
        ElementMapperInt handler = new ElementMapperInt ();
        Document doc = XMLParser.parse (TEST3_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals (5, handler.a);
    }
    
    @Test
    public void testElementMapperInt2 () throws Exception
    {
        ElementMapperInt handler = new ElementMapperInt ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        try
        {
            mapper.setHandler (handler).apply (doc);
            fail ("No exception was thrown");
        }
        catch (MappingException e)
        {
            MappingException wrapped = (MappingException)e.getCause ();
            assertEquals ("Required attribute \"a\" is missing: <p>a</p>", wrapped.getMessage ());
        }
    }
    
    @Test
    public void testElementMapperInt3 () throws Exception
    {
        ElementMapperInt handler = new ElementMapperInt ();
        Document doc = XMLParser.parse (TEST2_XML);
        JavaMapper mapper = new JavaMapper ();
        try
        {
            mapper.setHandler (handler).apply (doc);
            fail ("No exception was thrown");
        }
        catch (MappingException e)
        {
            MappingException wrapped = (MappingException)e.getCause ();
            assertEquals ("Failed to convert the attribute a=\"x\" to integer", wrapped.getMessage ());
        }
    }
    
    @Test
    public void testElementMapperInteger () throws Exception
    {
        ElementMapperInteger handler = new ElementMapperInteger ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertNull (handler.a);
    }
    
    @Test
    public void testElementMapperInteger2 () throws Exception
    {
        ElementMapperInteger handler = new ElementMapperInteger ();
        Document doc = XMLParser.parse (TEST2_XML);
        JavaMapper mapper = new JavaMapper ();
        try
        {
            mapper.setHandler (handler).apply (doc);
            fail ("No exception was thrown");
        }
        catch (MappingException e)
        {
            MappingException wrapped = (MappingException)e.getCause ();
            assertEquals ("Failed to convert the attribute a=\"x\" to integer", wrapped.getMessage ());
        }
    }
    
    @Test
    public void testElementMapperInteger3 () throws Exception
    {
        ElementMapperInteger handler = new ElementMapperInteger ();
        runTest (TEST3_XML, handler);
        assertEquals (new Integer (5), handler.a);
    }
    
    @Test
    public void testElementMapperBoolean () throws Exception
    {
        ElementMapperBoolean handler = new ElementMapperBoolean ();
        try
        {
            runTest (TEST_XML, handler);
            fail ("No exception was thrown");
        }
        catch (MappingException e)
        {
            MappingException wrapped = (MappingException)e.getCause ();
            assertEquals ("Required attribute \"a\" is missing: <p>a</p>", wrapped.getMessage ());
        }
    }
    
    @Test
    public void testElementMapperBoolean2 () throws Exception
    {
        ElementMapperBoolean handler = new ElementMapperBoolean ();
        runTest (TEST2_XML, handler);
        assertEquals (Boolean.FALSE, handler.a);
    }
    
    @Test
    public void testElementMapperBoolean3 () throws Exception
    {
        ElementMapperBoolean handler = new ElementMapperBoolean ();
        runTest (TEST3_XML, handler);
        assertEquals (Boolean.FALSE, handler.a);
    }
    
    @Test
    public void testElementMapperBoolean4 () throws Exception
    {
        ElementMapperBoolean handler = new ElementMapperBoolean ();
        runTest (TEST4_XML, handler);
        assertEquals (Boolean.FALSE, handler.a);
    }
    
    @Test
    public void testElementMapperBooleanOn () throws Exception
    {
        ElementMapperBoolean handler = new ElementMapperBoolean ();
        runTest ("<p a='on'/>", handler);
        assertEquals (Boolean.TRUE, handler.a);
    }
    
    @Test
    public void testElementMapperBooleanON () throws Exception
    {
        ElementMapperBoolean handler = new ElementMapperBoolean ();
        runTest ("<p a='ON'/>", handler);
        assertEquals (Boolean.TRUE, handler.a);
    }
    
    @Test
    public void testElementMapperWithSeveralNames () throws Exception
    {
        ElementMapperWithSeveralNames handler = new ElementMapperWithSeveralNames ();
        Document doc = XMLParser.parse (TEST_XML);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
        assertEquals (4, handler.count);
    }
    
    // TODO Allow to create hierarchies by returning an object that is passed to children
    
    private void runTest (String xml, Object handler)
    {
        Document doc = XMLParser.parse (xml);
        JavaMapper mapper = new JavaMapper ();
        mapper.setHandler (handler).apply (doc);
    }

}
