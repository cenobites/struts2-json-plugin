package es.cenobit.struts2.json;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import es.cenobit.struts2.json.annotations.Json;

public interface JsonServices {

	public String[] determineExcludedFieldsNames(Class<?> actionClass, String methodName);

	public String[] determineExcludedFieldsNames(ActionConfig actionConfig);

	public Json getJsonAnnotation(Class<?> actionClass, String methodName);

	public Json getJsonAnnotation(ActionConfig actionConfig);
}