package tech.ailef.dbadmin.annotations;

/**
 * An interface that includes all the configuration methods that
 * the user has to implement in order to integrate DbAdmin. 
 *
 */
public interface DbAdminAppConfiguration {
	public String getModelsPackage();
}
