package anyxml.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Use this annotation to turn any method into a handler for text.
 * 
 *  <p>This is useful if you have an element with the name "text" in your input. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TextHandler
{
    
}
