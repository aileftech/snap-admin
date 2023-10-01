/**
 * This is the root package for the "internal" data source, i.e. not the data source
 * that interacts with the user entities, but rather the one used internally by Spring
 * Boot Database Admin in order to save information. 
 * 
 * Due to the way Spring Boot component scanning works, it is needed to create this package and the
 * respective {@link tech.ailef.dbadmin.internal.InternalDbAdminConfiguration} in order to 
 * have the component scanning only pick the correct entities/repositories.
 */
package tech.ailef.dbadmin.internal;
