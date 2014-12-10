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
@Target({ElementType.METHOD})
public @interface ElementName
{
    String[] value ();
}
