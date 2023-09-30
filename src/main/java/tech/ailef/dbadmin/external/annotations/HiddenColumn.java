package tech.ailef.dbadmin.external.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a column as hidden. This column and its values will not be shown
 * in the list and detail view for objects of this type. 
 * If the column is nullable, it will be hidden in the create and edit
 * forms as well (and this will result in the column having being NULL
 * when the objects are created/edited). If, instead, it's not a nullable
 * column, it will be included in the create and edit forms.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HiddenColumn {
}