package tech.ailef.dbadmin.external.dbmapping.fields;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

public class ByteArrayFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "file";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		try {
			return ((MultipartFile)value).getBytes();
		} catch (IOException e) {
			throw new DbAdminException(e);
		}
	}

	@Override
	public Class<?> getJavaClass() {
		return byte[].class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		throw new DbAdminException("Binary fields are not comparable");
	}
}
