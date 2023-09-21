package tech.ailef.dbadmin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a method as a "virtual" whose value is computed by
 * using the method itself rather than retrieving it like a physical column
 * from the database.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ComputedColumn {
	public String name() default "";
}