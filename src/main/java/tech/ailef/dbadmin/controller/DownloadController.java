package tech.ailef.dbadmin.controller;

import java.util.Optional;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import tech.ailef.dbadmin.DbAdmin;
import tech.ailef.dbadmin.dbmapping.DbAdminRepository;
import tech.ailef.dbadmin.dbmapping.DbFieldValue;
import tech.ailef.dbadmin.dbmapping.DbObject;
import tech.ailef.dbadmin.dbmapping.DbObjectSchema;

@Controller
@RequestMapping("/dbadmin/download")
public class DownloadController {
	@Autowired
	private DbAdminRepository repository;
	
	@Autowired
	private DbAdmin dbAdmin;

	@GetMapping("/{className}/{fieldName}/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> serveFile(@PathVariable String className, 
			@PathVariable String fieldName, @PathVariable String id) {

		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		Optional<DbObject> object = repository.findById(schema, id);
		
		if (object.isPresent()) {
			DbObject dbObject = object.get();
			DbFieldValue dbFieldValue = dbObject.get(fieldName);
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
