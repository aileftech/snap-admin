package tech.ailef.dbadmin.external.dbmapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

/**
 * The list of supported field types 
 */
public enum DbFieldType {
	INTEGER {
		@Override
		public String getHTMLName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
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
		public String getHTMLName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
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
		public String getHTMLName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
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
		public String getHTMLName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
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
	LOCAL_DATE {
		@Override
		public String getHTMLName() {
			return "date";
		}

		@Override
		public Object parseValue(Object value) {
			if (value == null) return null;
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
		public String getHTMLName() {
			return "datetime-local";
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
		public String getHTMLName() {
			return "text";
		}

		@Override
		public Object parseValue(Object value) {
			return value;
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
		public String getHTMLName() {
			return "textarea";
		}

		@Override
		public Object parseValue(Object value) {
			return value;
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
		public String getHTMLName() {
			return "text";
		}

		@Override
		public Object parseValue(Object value) {
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
		public String getHTMLName() {
			return "number";
		}

		@Override
		public Object parseValue(Object value) {
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
	BYTE_ARRAY {
		@Override
		public String getHTMLName() {
			return "file";
		}

		@Override
		public Object parseValue(Object value) {
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
	ONE_TO_MANY {
		@Override
		public String getHTMLName() {
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
		public String getHTMLName() {
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
		public String getHTMLName() {
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
		public String getHTMLName() {
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

	public abstract String getHTMLName();
	
	public abstract Object parseValue(Object value);
	
	public abstract Class<?> getJavaClass();
	
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
		} else if (klass == String.class) {
			return STRING;
		} else if (klass == LocalDate.class) {
			return LOCAL_DATE;
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
		} else {
			throw new DbAdminException("Unsupported field type: " + klass);
		}
	}
}
