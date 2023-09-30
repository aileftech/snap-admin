package tech.ailef.dbadmin.external.controller;

import java.util.Optional;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.dbmapping.DbAdminRepository;
import tech.ailef.dbadmin.external.dbmapping.DbFieldValue;
import tech.ailef.dbadmin.external.dbmapping.DbObject;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

/**
 * Controller to serve file or images (`@DisplayImage`) 
 */
@Controller
@RequestMapping(value = {"/${dbadmin.baseUrl}/download", "/${dbadmin.baseUrl}/download/"})
public class DownloadController {
	@Autowired
	private DbAdminRepository repository;
	
	@Autowired
	private DbAdmin dbAdmin;

	
	/**
	 * Serve a binary field as an image
	 * @param className
	 * @param fieldName
	 * @param id
	 * @return
	 */
	@GetMapping(value="/{className}/{fieldName}/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public ResponseEntity<byte[]> serveImage(@PathVariable String className, 
			@PathVariable String fieldName, @PathVariable String id) {

		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		Optional<DbObject> object = repository.findById(schema, id);
		
		if (object.isPresent()) {
			DbObject dbObject = object.get();
			DbFieldValue dbFieldValue = dbObject.get(fieldName);
			byte[] file = (byte[])dbFieldValue.getValue();
			return ResponseEntity.ok(file);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object with id " + id + " not found");
		}
		

	}
	
	/**
	 * Serve a binary field as a file. This tries to detect the file type using Tika
	 * in order to serve the file with a plausible extension, since we don't have
	 * any meta-data about what was originally uploaded and it is not feasible to 
	 * store it (it could be modified on another end and we wouldn't be aware of it).
	 * @param className
	 * @param fieldName
	 * @param id
	 * @return
	 */
	@GetMapping("/{className}/{fieldName}/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> serveFile(@PathVariable String className, 
			@PathVariable String fieldName, @PathVariable String id) {

		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		Optional<DbObject> object = repository.findById(schema, id);
		
		if (object.isPresent()) {
			DbObject dbObject = object.get();
			
			DbFieldValue dbFieldValue;
			try {
				dbFieldValue = dbObject.get(fieldName);
			} catch (DbAdminException e) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found", e);
			}
			
			if (dbFieldValue.getValue() == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no file attached to this item");
			}
			
			byte[] file = (byte[])dbFieldValue.getValue();
			
			String filename = schema.getClassName() + "_" + id + "_" + fieldName;
			try {
				Tika tika = new Tika();
				String detect = tika.detect(file);
				String ext = MimeTypes.getDefaultMimeTypes().forName(detect).getExtension();
				filename = filename + ext;
			} catch (MimeTypeException e) {
				// Unable to determine extension, leave as is
			}
			
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + filename + "\"").body(file);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object with id " + id + " not found");
		}
		

	}
}
