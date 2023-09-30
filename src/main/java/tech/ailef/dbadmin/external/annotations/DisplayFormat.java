package tech.ailef.dbadmin.external.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a format string for a field, which will be automatically applied
 * when displaying its value.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DisplayFormat {
	/**
	 * The format to apply to the field's value
	 * @return 
	 */
	public String format() default "";
}