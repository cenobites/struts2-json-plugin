package es.cenobit.struts2.json;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class JsonResult implements Result {

	private static final long serialVersionUID = 6082890994954880645L;

	private static final Logger LOG = LoggerFactory.getLogger(JsonResult.class);

	private Gson gson = JsonSerializerBuilder.create();
	private String root;

	@Override
	public void execute(ActionInvocation invocation) throws Exception {
		ActionContext actionContext = invocation.getInvocationContext();
		HttpServletResponse response = (HttpServletResponse) actionContext.get(StrutsStatics.HTTP_RESPONSE);

		try {
			response.setContentType("application/json");
			Writer writer = response.getWriter();
			writer.write(gson.toJson(findRootObject(invocation)));
			writer.flush();
		} catch (IOException exception) {
			LOG.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	protected Object findRootObject(ActionInvocation invocation) {
		Object rootObject;
		if (root != null) {
			ValueStack stack = invocation.getStack();
			rootObject = stack.findValue(root);
		} else {
			rootObject = invocation.getStack().peek(); // model overrides action
		}
		return rootObject;
	}
}
