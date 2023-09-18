package tech.ailef.dbadmin.misc;

public interface Utils {
	public static String camelToSnake(String v) {
		if (Character.isUpperCase(v.charAt(0))) {
			v = Character.toLowerCase(v.charAt(0)) + v.substring(1);
		}
		
		return v.replaceAll("([A-Z][a-z])", "_$1").toLowerCase();
		
	}
	
	public static String snakeToCamel(String text) {
		boolean shouldConvertNextCharToLower = true;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
		    char currentChar = text.charAt(i);
		    if (currentChar == '_') {
		        shouldConvertNextCharToLower = false;
		    } else if (shouldConvertNextCharToLower) {
		        builder.append(Character.toLowerCase(currentChar));
		    } else {
		        builder.append(Character.toUpperCase(currentChar));
		        shouldConvertNextCharToLower = true;
		    }
		}
		return builder.toString();
	}
}
