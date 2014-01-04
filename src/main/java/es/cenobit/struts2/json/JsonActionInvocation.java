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
package es.cenobit.struts2.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;

import com.google.gson.Gson;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import es.cenobit.struts2.json.annotations.Json;

public class JsonActionInvocation extends DefaultActionInvocation implements ActionInvocation {

	private static final long serialVersionUID = -4888602055717841526L;

	private static final Logger LOG = LoggerFactory.getLogger(JsonActionInvocation.class);

	@SuppressWarnings("rawtypes")
	private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	private JsonServices jsonService;

	public JsonActionInvocation(final Map<String, Object> extraContext, final boolean pushAction) {
		super(extraContext, pushAction);
	}

	@Inject("jsonServices")
	public void setJsonService(JsonServices jsonService) {
		this.jsonService = jsonService;
	}

	/**
	 * If the DefaultActionInvocation has been executed before and the Result is
	 * an instance of ActionChainResult, this method will walk down the chain of
	 * ActionChainResults until it finds a non-chain result, which will be
	 * returned. If the DefaultActionInvocation's result has not been executed
	 * before, the Result instance will be created and populated with the result
	 * params.
	 * 
	 * @return a Result instance
	 * @throws Exception
	 */
	@Override
	public Result getResult() throws Exception {
		Result returnResult = result;

		// If we've chained to other Actions, we need to find the last result
		while (returnResult instanceof ActionChainResult) {
			ActionProxy aProxy = ((ActionChainResult) returnResult).getProxy();

			if (aProxy != null) {
				Result proxyResult = aProxy.getInvocation().getResult();

				if ((proxyResult != null) && (aProxy.getExecuteResult())) {
					returnResult = proxyResult;
				} else {
					break;
				}
			} else {
				break;
			}
		}

		return returnResult;
	}

	@Override
	public Result createResult() throws Exception {
		return super.createResult();
	}

	/**
	 * @throws ConfigurationException
	 *             If no result can be found with the returned code
	 */
	@Override
	public String invoke() throws Exception {
		String profileKey = "invoke: ";
		try {
			UtilTimerStack.push(profileKey);

			if (executed) {
				throw new IllegalStateException("Action has already executed");
			}

			if (interceptors.hasNext()) {
				final InterceptorMapping interceptor = interceptors.next();
				String interceptorMsg = "interceptor: " + interceptor.getName();
				UtilTimerStack.push(interceptorMsg);
				try {
					resultCode = interceptor.getInterceptor().intercept(JsonActionInvocation.this);
				} finally {
					UtilTimerStack.pop(interceptorMsg);
				}
			} else {
				resultCode = invokeActionOnly();
			}

			// this is needed because the result will be executed, then control
			// will return to the Interceptor, which will
			// return above and flow through again
			if (!executed) {
				if (preResultListeners != null) {
					for (Object preResultListener : preResultListeners) {
						PreResultListener listener = (PreResultListener) preResultListener;

						String _profileKey = "preResultListener: ";
						try {
							UtilTimerStack.push(_profileKey);
							listener.beforeResult(this, resultCode);
						} finally {
							UtilTimerStack.pop(_profileKey);
						}
					}
				}

				// now execute the result, if we're supposed to
				if (proxy.getExecuteResult()) {
					executeResult();
				}

				executed = true;
			}

			return resultCode;
		} finally {
			UtilTimerStack.pop(profileKey);
		}
	}

	@Override
	public String invokeActionOnly() throws Exception {
		return invokeAction(getAction(), proxy.getConfig());
	}

	@Override
	protected void createAction(Map<String, Object> contextMap) {
		// load action
		String timerKey = "actionCreate: " + proxy.getActionName();
		try {
			UtilTimerStack.push(timerKey);
			action = objectFactory.buildAction(proxy.getActionName(), proxy.getNamespace(), proxy.getConfig(),
					contextMap);
		} catch (InstantiationException e) {
			throw new XWorkException("Unable to intantiate Action!", e, proxy.getConfig());
		} catch (IllegalAccessException e) {
			throw new XWorkException("Illegal access to constructor, is it public?", e, proxy.getConfig());
		} catch (Exception e) {
			String gripe;

			if (proxy == null) {
				gripe = "Whoa!  No ActionProxy instance found in current ActionInvocation.  This is bad ... very bad";
			} else if (proxy.getConfig() == null) {
				gripe = "Sheesh.  Where'd that ActionProxy get to?  I can't find it in the current ActionInvocation!?";
			} else if (proxy.getConfig().getClassName() == null) {
				gripe = "No Action defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace()
						+ "'";
			} else {
				gripe = "Unable to instantiate Action, " + proxy.getConfig().getClassName() + ",  defined for '"
						+ proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
			}

			gripe += (((" -- " + e.getMessage()) != null) ? e.getMessage() : " [no message in exception]");
			throw new XWorkException(gripe, e, proxy.getConfig());
		} finally {
			UtilTimerStack.pop(timerKey);
		}

		if (actionEventListener != null) {
			action = actionEventListener.prepare(action, stack);
		}
	}

	@Override
	protected Map<String, Object> createContextMap() {
		Map<String, Object> contextMap;

		if ((extraContext != null) && (extraContext.containsKey(ActionContext.VALUE_STACK))) {
			// In case the ValueStack was passed in
			stack = (ValueStack) extraContext.get(ActionContext.VALUE_STACK);

			if (stack == null) {
				throw new IllegalStateException("There was a null Stack set into the extra params.");
			}

			contextMap = stack.getContext();
		} else {
			// create the value stack
			// this also adds the ValueStack to its context
			stack = valueStackFactory.createValueStack();

			// create the action context
			contextMap = stack.getContext();
		}

		// put extraContext in
		if (extraContext != null) {
			contextMap.putAll(extraContext);
		}

		// put this DefaultActionInvocation into the context map
		contextMap.put(ActionContext.ACTION_INVOCATION, this);
		contextMap.put(ActionContext.CONTAINER, container);

		return contextMap;
	}

	/**
	 * Uses getResult to get the final Result and executes it
	 * 
	 * @throws ConfigurationException
	 *             If not result can be found with the returned code
	 */
	private void executeResult() throws Exception {
		result = createResult();

		String timerKey = "executeResult: " + getResultCode();
		try {
			UtilTimerStack.push(timerKey);
			if (result != null) {
				result.execute(this);
			} else if (resultCode != null && !Action.NONE.equals(resultCode)) {
				throw new ConfigurationException("No result defined for action " + getAction().getClass().getName()
						+ " and result " + getResultCode(), proxy.getConfig());
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("No result returned for action " + getAction().getClass().getName() + " at "
							+ proxy.getConfig().getLocation());
				}
			}
		} finally {
			UtilTimerStack.pop(timerKey);
		}
	}

	@Override
	public void init(ActionProxy proxy) {
		this.proxy = proxy;
		Map<String, Object> contextMap = createContextMap();

		// Setting this so that other classes, like object factories, can use
		// the ActionProxy and other
		// contextual information to operate
		ActionContext actionContext = ActionContext.getContext();

		if (actionContext != null) {
			actionContext.setActionInvocation(this);
		}

		createAction(contextMap);

		if (pushAction) {
			stack.push(action);
			contextMap.put("action", action);
		}

		invocationContext = new ActionContext(contextMap);
		invocationContext.setName(proxy.getActionName());

		// get a new List so we don't get problems with the iterator if someone
		// changes the list
		List<InterceptorMapping> interceptorList = new ArrayList<InterceptorMapping>(proxy.getConfig()
				.getInterceptors());
		interceptors = interceptorList.iterator();
	}

	@Override
	protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
		String methodName = proxy.getMethod();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Executing action method = " + actionConfig.getMethodName());
		}

		String timerKey = "invokeAction: " + proxy.getActionName();
		try {
			UtilTimerStack.push(timerKey);

			boolean methodCalled = false;
			Object methodResult = null;
			Method method = null;
			try {
				method = getAction().getClass().getMethod(methodName, EMPTY_CLASS_ARRAY);
			} catch (NoSuchMethodException e) {
				// hmm -- OK, try doXxx instead
				try {
					String altMethodName = "do" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
					method = getAction().getClass().getMethod(altMethodName, EMPTY_CLASS_ARRAY);
				} catch (NoSuchMethodException e1) {
					// well, give the unknown handler a shot
					if (unknownHandlerManager.hasUnknownHandlers()) {
						try {
							methodResult = unknownHandlerManager.handleUnknownMethod(action, methodName);
							methodCalled = true;
						} catch (NoSuchMethodException e2) {
							// throw the original one
							throw e;
						}
					} else {
						throw e;
					}
				}
			}

			if (!methodCalled) {
				methodResult = method.invoke(action, EMPTY_OBJECT_ARRAY);
			}

			// write response json
			if (containsAnnotation(actionConfig, method)) {
				return saveJsonResult(actionConfig, methodResult, method);
			}

			// backward compatibility (struts-default)
			return saveResult(actionConfig, methodResult);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("The " + methodName + "() is not defined in action "
					+ getAction().getClass() + "");
		} catch (InvocationTargetException e) {
			// We try to return the source exception.
			Throwable t = e.getTargetException();

			if (actionEventListener != null) {
				String result = actionEventListener.handleException(t, getStack());
				if (result != null) {
					return result;
				}
			}
			if (t instanceof Exception) {
				throw (Exception) t;
			} else {
				throw e;
			}
		} finally {
			UtilTimerStack.pop(timerKey);
		}
	}

	protected String saveJsonResult(ActionConfig actionConfig, Object methodResult, Method method) throws IOException,
			ClassNotFoundException {

		if (methodResult instanceof Result) {
			return super.saveResult(actionConfig, methodResult);
		}

		HttpServletResponse response = (HttpServletResponse) getInvocationContext().get(StrutsStatics.HTTP_RESPONSE);

		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			String[] excludedFieldNames = determineExcludedFieldsNames(actionConfig, method);
			String json = gson(excludedFieldNames).toJson(methodResult);

			Writer writer = response.getWriter();
			writer.write(json);
			writer.flush();
		} catch (IOException e) {
			LOG.error("Unable to serialize data width Gson API", e);
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (ClassNotFoundException e) {
			LOG.error(
					"Invalid action class configuration that references an unknown class named ["
							+ actionConfig.getClassName() + "]", e);
			LOG.error(e.getMessage(), e);
			throw e;
		}

		return null;
	}

	private Gson gson(String[] excludedFieldNames) {
		final Set<String> fieldsToExclude = new LinkedHashSet<String>(Arrays.asList(excludedFieldNames));
		return JsonSerializerBuilder.create(fieldsToExclude);
	}

	private boolean containsAnnotation(ActionConfig actionConfig, Method method) throws ClassNotFoundException {
		if (method.isAnnotationPresent(Json.class)) {
			return true;
		}

		if (jsonService.getJsonAnnotation(actionConfig) != null) {
			return true;
		}

		Annotation[] annotations = method.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			String typeName = annotation.annotationType().getName();
			if (Json.class.getName().equals(typeName)) {
				return true;
			}
		}

		return false;
	}

	public String[] determineExcludedFieldsNames(ActionConfig actionConfig, Method method)
			throws ClassNotFoundException {
		if (method.isAnnotationPresent(Json.class)) {
			return method.getAnnotation(Json.class).exclude();
		}

		return jsonService.determineExcludedFieldsNames(actionConfig);
	}

	protected Gson createJsonSerializer() {
		return JsonSerializerBuilder.create();
	}

	/**
	 * Version ready to be serialize
	 * 
	 * @return instance without reference to {@link Container}
	 */
	@Override
	public ActionInvocation serialize() {
		JsonActionInvocation that = this;
		that.container = null;
		return that;
	}

	/**
	 * Restoring Container
	 * 
	 * @param actionContext
	 *            current {@link ActionContext}
	 * @return instance which can be used to invoke action
	 */
	@Override
	public ActionInvocation deserialize(ActionContext actionContext) {
		JsonActionInvocation that = this;
		that.container = actionContext.getContainer();
		return that;
	}
}
