package es.cenobit.struts2.json.util;

public class StringUtils {

	public static boolean contains(String[] strings, String value, boolean ignoreCase) {
		if (strings != null) {
			for (String string : strings) {
				if (string.equals(value) || (ignoreCase && string.equalsIgnoreCase(value)))
					return true;
			}
		}

		return false;
	}

	public static String[] concat(String[] a, String[] b) {
		if (a == null && b == null) {
			return null;
		} else if (a == null) {
			return b;
		} else if (b == null) {
			return a;
		}

		int aLen = a.length;
		int bLen = b.length;
		String[] c = new String[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
}
