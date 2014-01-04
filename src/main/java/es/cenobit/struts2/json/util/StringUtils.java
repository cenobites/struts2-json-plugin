/**
 * Copyright 2014 Cenobit Technologies Inc. http://cenobit.es/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
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
