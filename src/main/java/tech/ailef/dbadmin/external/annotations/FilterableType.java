package tech.ailef.dbadmin.external.annotations;

/**
 * Type of filters that can be used in the faceted search.
 * 
 */
public enum FilterableType {
	/**
	 * The default filter provides a list of standard operators
	 * customized to the field type (e.g. greater than/less than/equals for numbers,
	 * after/before/equals for dates, contains/equals for strings, etc...), with,
	 * if applicable, an autocomplete form if the field references a foreign key.
	 */
	DEFAULT, 
	/**
	 * The categorical filter provides the full list of possible values
	 * for the field, rendered as a list of clickable items (that will
	 * filter for equality). This provides a better UX if the field can take
	 * a limited number of values and it's more convenient to have them all
	 * on screen rather than typing them.
	 */
	CATEGORICAL;

}
