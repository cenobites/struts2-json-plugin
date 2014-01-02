package es.cenobit.struts2.json.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class JsonInterceptor implements Interceptor {

	private static final long serialVersionUID = -1012900438685739652L;

	@Override
	public void destroy() {

	}

	@Override
	public void init() {

	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		return invocation.invoke();
	}

}
