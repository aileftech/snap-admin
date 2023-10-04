package tech.ailef.dbadmin.external.dto;


/**
 * Some fragments might need to be rendered differently depending
 * on their context. For example a TEXT field is usually rendered
 * as a text area, but if it has to fit in the faceted search right
 * bar it's rendered as a normal input type "text" field for space
 * reasons (and because the user just needs to search with a short
 * query).
 * 
 * This enum indicates the possible contexts and it is passed to the
 * getFragmentName() method which determines which actual fragment
 * to use.
 *
 */
public enum FragmentContext {
	DEFAULT,
	CREATE,
	SEARCH
}
