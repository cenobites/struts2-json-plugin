package es.cenobit.struts2.json;

import java.util.Date;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class JsonSerializerBuilder {

	public static GsonBuilder builder() {
		return new GsonBuilder() //
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES) //
				.registerTypeAdapter(Date.class, new DateTypeAdapter());
	}

	public static Gson create() {
		return builder().create();
	}
}
