package tech.ailef.dbadmin.dbmapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import tech.ailef.dbadmin.exceptions.DbAdminException;

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
	},
	LOCAL_DATE {
		@Override
		public String getHTMLName() {
			return "date";
		}

		@Override
		public Object parseValue(Object value) {
			return LocalDate.parse(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return Float.class;
		}
	},
	LOCAL_DATE_TIME {
		@Override
		public String getHTMLName() {
			return "datetime-local";
		}

		@Override
		public Object parseValue(Object value) {
			return LocalDateTime.parse(value.toString());
		}

		@Override
		public Class<?> getJavaClass() {
			return LocalDateTime.class;
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
	};

	public abstract String getHTMLName();
	
	public abstract Object parseValue(Object value);
	
	public abstract Class<?> getJavaClass();
	
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
