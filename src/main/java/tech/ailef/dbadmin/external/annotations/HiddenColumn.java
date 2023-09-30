package tech.ailef.dbadmin.external.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Marks a column as hidden. This column and its values will not be shown in the
 * list and detail view for objects of this type. If the column is nullable, it
 * will be hidden in the create and edit forms as well (and this will result 
 * in the column always being NULL when creating/editing objects). If, instead,
 * it's not nullable column, it will be included in the create and edit forms
 * as it would otherwise prevent the creation of items.</p>
 * 
 * <p><strong>Please note that this is not meant as a security feature, </strong> but
 * rather to hide uninformative columns that clutter the interface. In fact, since
 * the create and edit form come pre-filled with all the information, these views
 * <b>will</b> show the value of the hidden column (if it's not nullable).
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HiddenColumn {
}