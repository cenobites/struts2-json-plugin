package es.cenobit.struts2.json;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import es.cenobit.struts2.json.annotations.NoJson;

public class JsonSerializerExclusionStrategy implements ExclusionStrategy {

	@SuppressWarnings("rawtypes")
	private final Set toExcludeClasses;
	private final Set<String> fieldsToExclude;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JsonSerializerExclusionStrategy(Set<String> fieldsToExclude, Class<?>... classes) {
		this.fieldsToExclude = fieldsToExclude;
		this.toExcludeClasses = new LinkedHashSet(Arrays.asList(classes));
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(NoJson.class) != null || fieldsToExclude.contains(f.getName());
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return toExcludeClasses.contains(clazz);
	}

}
