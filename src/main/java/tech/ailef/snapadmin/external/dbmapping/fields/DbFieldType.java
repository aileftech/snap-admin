/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps
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

package tech.ailef.snapadmin.external.dbmapping.fields;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import tech.ailef.snapadmin.external.dto.CompareOperator;
import tech.ailef.snapadmin.external.exceptions.UnsupportedFieldTypeException;
import tech.ailef.snapadmin.external.misc.Utils;

public abstract class DbFieldType {
	
	/**
	 * Returns the name of the Thymeleaf fragments in the 'inputs.html'
	 * file, used to render an input field for this specific type.
	 * For example, a fragment using a file input is used for binary fields.
	 */
	public abstract String getFragmentName();
	
	/**
	 * Parse the value received through an HTML form into a instance
	 * of an object of this specific type. This usually involves a conversion
	 * from string, but, for example, files are sent as MultipartFile instead.
	 * @param value the value to parse
	 * @return
	 */
	public abstract Object parseValue(Object value);
	
	/**
	 * Returns the Java class corresponding to this field type.
	 * @return
	 */
	public abstract Class<?> getJavaClass();
	
	/**
	 * Returns a list of compare operators that can be used to compare
	 * two values for this field type. Used in the faceted search to provide
	 * more operators than just equality (e.g. after/before for dates).
	 * @return
	 */
	public abstract List<CompareOperator> getCompareOperators();
	
	/**
	 * Returns all the possible values that this field can have. 
	 * This method is by default unsupported, and it's implemented only
	 * in subclasses where this is applicable, e.g. EnumFieldType.
	 * @return
	 */
	public List<?> getValues() {
		throw new UnsupportedOperationException("getValues only supported on Enum type: called on " + this.getClass().getSimpleName());
	}
	
	public String toString() {
		return Utils.camelToSnake(this.getClass().getSimpleName().replace("FieldType", "")).toUpperCase();
	}
	
	public boolean isRelationship() {
		return false;
	}
	
	/**
	 * Returns the corresponding {@linkplain DbFieldType} from a Class object.
	 * @param klass
	 * @return
	 */
	public static Class<? extends DbFieldType> fromClass(Class<?> klass) {
		if (klass == Boolean.class || klass == boolean.class) {
			return BooleanFieldType.class;
		} else if (klass == Long.class || klass == long.class) {
			return LongFieldType.class;
		} else if (klass == Integer.class || klass == int.class) {
			return IntegerFieldType.class;
		} else if (klass == BigInteger.class) {
			return BigIntegerFieldType.class;
		} else if (klass == Short.class || klass == short.class) {
			return ShortFieldType.class;
		} else if (klass == String.class) {
			return StringFieldType.class;
		} else if (klass == LocalDate.class) {
			return LocalDateFieldType.class;
		} else if (klass == Date.class) {
			return DateFieldType.class;
		} else if (klass == LocalDateTime.class) {
			return LocalDateTimeFieldType.class;
		} else if (klass == Instant.class) {
			return InstantFieldType.class;
		} else if (klass == Float.class || klass == float.class) {
			return FloatFieldType.class;
		} else if (klass == Double.class || klass == double.class) {
			return DoubleFieldType.class;
		} else if (klass == BigDecimal.class) {
			return BigDecimalFieldType.class;
		} else if (klass == byte[].class) {
			return ByteArrayFieldType.class;
		} else if (klass == OffsetDateTime.class) {
			return OffsetDateTimeFieldType.class;
		} else if (klass == byte.class || klass == Byte.class) {
			return ByteFieldType.class;
		} else if (klass == java.util.UUID.class) {
			return UUIDFieldType.class;
		} else if (klass == char.class || klass == Character.class) {
			return CharFieldType.class;
		} else if (Enum.class.isAssignableFrom(klass)) {
			return EnumFieldType.class;
		} else {
			throw new UnsupportedFieldTypeException("Unsupported field type: " + klass);
		}
	}

}
