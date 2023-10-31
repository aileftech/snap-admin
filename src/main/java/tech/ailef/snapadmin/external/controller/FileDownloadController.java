/* 
 * Spring Boot Database Admin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package tech.ailef.snapadmin.external.controller;

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

import tech.ailef.snapadmin.external.SnapAdmin;
import tech.ailef.snapadmin.external.dbmapping.DbAdminRepository;
import tech.ailef.snapadmin.external.dbmapping.DbFieldValue;
import tech.ailef.snapadmin.external.dbmapping.DbObject;
import tech.ailef.snapadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.snapadmin.external.exceptions.DbAdminException;

/**
 * Controller to serve file or images (`@DisplayImage`) 
 */
@Controller
@RequestMapping(value = {"/${dbadmin.baseUrl}/download", "/${dbadmin.baseUrl}/download/"})
public class FileDownloadController {
	@Autowired
	private DbAdminRepository repository;
	
	@Autowired
	private SnapAdmin dbAdmin;

	
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
