package anyxml.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** List the names of all elements which can be handled by this method. Also handy when
 *  the element name contains characters which are illegal in Java.
 * 
 *  <p>"*" or "" will match any element.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface AttributeMapping
{
    /** The name of the XML attribute which should me mapped to this parameter */
    String value () default "";
    /** The name of the XML attribute which should me mapped to this parameter */
    String name () default "";
    
    /** Set to true to make this a required parameter. Primitives are automatically required. */
    boolean required () default false;
    
    /** Parse format for types like numbers or dates. */
    String format () default "";
    
    /** Values which should map to <code>true</code> for boolean parameters.
     *  
     *  <p>If only this field is specified, then every other value maps to <code>false</code>.
     *  
     *  <p>If both <code>trueValues</code> and <code>falseValues</code> is specified, then
     *  all other values will throw a <code>MappingException</code>
     * 
     *  <p>Defaults: <code>"1", "on", "true", "Y", "yes"</code>
     */
    String[] trueValues () default {};
    
    /** Values which should map to <code>false</code> for boolean parameters.
     *  
     *  <p>If only this field is specified, then every other value maps to <code>true</code>.
     *  
     *  <p>If both <code>trueValues</code> and <code>falseValues</code> is specified, then
     *  all other values will throw a <code>MappingException</code>
     * 
     *  <p>Defaults: Empty.
     */
    String[] falseValues () default {};
}
