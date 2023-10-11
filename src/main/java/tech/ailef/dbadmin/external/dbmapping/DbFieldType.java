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


package tech.ailef.dbadmin.external.dbmapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;
import tech.ailef.dbadmin.external.exceptions.UnsupportedFieldTypeException;

/**
 * The enum for supported database field types. 
 */
public enum DbFieldType {
	SHORT {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return Short.parseShort(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Short.class;
		}

		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.GT, CompareOperator.EQ, CompareOperator.LT);
		}
	},
	BIG_INTEGER {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return new BigInteger(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return BigInteger.class;
		}

		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.GT, CompareOperator.EQ, CompareOperator.LT);
		}
	},
	INTEGER {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return Integer.parseInt(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Integer.class;
		}

		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.GT, CompareOperator.EQ, CompareOperator.LT);
		}
	},
	DOUBLE {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return Double.parseDouble(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Double.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.GT, CompareOperator.EQ, CompareOperator.LT);
		}
	},
	LONG {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return Long.parseLong(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Long.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.GT, CompareOperator.EQ, CompareOperator.LT);
		}
	},
	FLOAT {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return Float.parseFloat(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Float.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.GT, CompareOperator.EQ, CompareOperator.LT);
		}
	},
	OFFSET_DATE_TIME {
		@Override
		public String getFragmentName() {
			return "datetime";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return OffsetDateTime.parse(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return OffsetDateTime.class;
		}

		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
		}
		
	},
	DATE {
		@Override
		public String getFragmentName() {
			return "date";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
			try {
				return format.parse(value.toString());
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Class<?> getJavaClass() {
			return Date.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
		}
	},
	LOCAL_DATE {
		@Override
		public String getFragmentName() {
			return "date";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return LocalDate.parse(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Float.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
		}
	},
	LOCAL_DATE_TIME {
		@Override
		public String getFragmentName() {
			return "datetime";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return LocalDateTime.parse(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return LocalDateTime.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
		}
	},
	STRING {
		@Override
		public String getFragmentName() {
			return "text";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return value.toString();
		}

		@Override
		public Class<?> getJavaClass() {
			return String.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.CONTAINS, CompareOperator.STRING_EQ);
		}
	},
	TEXT {
		@Override
		public String getFragmentName() {
			return "textarea";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return value.toString();
		}

		@Override
		public Class<?> getJavaClass() {
			return String.class;
		}

		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.CONTAINS, CompareOperator.STRING_EQ);
		}
		
	},
	BOOLEAN {
		@Override
		public String getFragmentName() {
			return "text";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return Boolean.parseBoolean(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Boolean.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.EQ);
		}
	}, 
	BIG_DECIMAL {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return new BigDecimal(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return BigDecimal.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.GT, CompareOperator.EQ, CompareOperator.LT);
		}
	},
	CHAR {
		@Override
		public String getFragmentName() {
			return "char";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			if (value.toString().isBlank()) return null;
			return value.toString().charAt(0);
		}

		@Override
		public Class<?> getJavaClass() {
			return char.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.STRING_EQ);
		}
	},
	BYTE {
		@Override
		public String getFragmentName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null || value.toString().isBlank()) return null;
			return value.toString().getBytes()[0];
		}

		@Override
		public Class<?> getJavaClass() {
			return byte.class;
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			throw new DbAdminException("Binary fields are not comparable");
		}
	},
	BYTE_ARRAY {
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
	},
	UUID {

		@Override
		public String getFragmentName() {
			return "text";
		}

		@Override
		public Object parseValue(Object value) {
			return java.util.UUID.fromString(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return java.util.UUID.class;
		}

		@Override
		public List<CompareOperator> getCompareOperators() {
			return List.of(CompareOperator.STRING_EQ, CompareOperator.CONTAINS);
		}
		
	},
	ONE_TO_MANY {
		@Override
		public String getFragmentName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object parseValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getJavaClass() {
			return OneToMany.class;
		}
		
		@Override
		public boolean isRelationship() {
			return true;
		}
		
		@Override
		public String toString() {
			return "One to Many";
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			throw new DbAdminException();
		}
	},
	ONE_TO_ONE {
		@Override
		public String getFragmentName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object parseValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getJavaClass() {
			return OneToOne.class;
		}
		
		@Override
		public boolean isRelationship() {
			return true;
		}
		
		@Override
		public String toString() {
			return "One to One";
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			throw new DbAdminException();
		}
	},
	MANY_TO_MANY {
		@Override
		public String getFragmentName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object parseValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getJavaClass() {
			return ManyToMany.class;
		}
		
		@Override
		public boolean isRelationship() {
			return true;
		}
		
		@Override
		public String toString() {
			return "Many to Many";
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			throw new DbAdminException();
		}
	}, 
	COMPUTED {
		@Override
		public String getFragmentName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object parseValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getJavaClass() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public List<CompareOperator> getCompareOperators() {
			throw new DbAdminException();
		}
	};

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
	
	public boolean isRelationship() {
		return false;
	}
	
	public static DbFieldType fromClass(Class<?> klass) {
		if (klass == Boolean.class || klass == boolean.class) {
			return BOOLEAN;
		} else if (klass == Long.class || klass == long.class) {
			return LONG;
		} else if (klass == Integer.class || klass == int.class) {
			return INTEGER;
		} else if (klass == BigInteger.class) {
			return BIG_INTEGER;
		} else if (klass == Short.class || klass == short.class) {
			return SHORT;
		} else if (klass == String.class) {
			return STRING;
		} else if (klass == LocalDate.class) {
			return LOCAL_DATE;
		} else if (klass == Date.class) {
			return DATE;
		} else if (klass == LocalDateTime.class) {
			return LOCAL_DATE_TIME;
		} else if (klass == Float.class || klass == float.class) {
			return FLOAT;
		} else if (klass == Double.class || klass == double.class) {
			return DOUBLE;
		} else if (klass == BigDecimal.class) {
			return BIG_DECIMAL;
		} else if (klass == byte[].class) {
			return BYTE_ARRAY;
		} else if (klass == OffsetDateTime.class) {
			return OFFSET_DATE_TIME;
		} else if (klass == byte.class || klass == Byte.class) {
			return BYTE;
		} else if (klass == java.util.UUID.class) {
			return UUID;
		} else if (klass == char.class || klass == Character.class) {
			return CHAR;
		} else {
			throw new UnsupportedFieldTypeException("Unsupported field type: " + klass);
		}
	}
}
