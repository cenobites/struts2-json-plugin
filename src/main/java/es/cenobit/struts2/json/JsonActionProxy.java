package es.cenobit.struts2.json;

import java.io.Serializable;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxy;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

public class JsonActionProxy extends DefaultActionProxy implements ActionProxy, Serializable {

	private static final long serialVersionUID = -6594848475630141845L;

	/**
	 * This constructor is private so the builder methods (create*) should be
	 * used to create an DefaultActionProxy.
	 * <p/>
	 * The reason for the builder methods is so that you can use a subclass to
	 * create your own DefaultActionProxy instance (like a RMIActionProxy).
	 */
	protected JsonActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName,
			boolean executeResult, boolean cleanupContext) {
		super(inv, namespace, actionName, methodName, executeResult, cleanupContext);
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		ActionContext nestedContext = ActionContext.getContext();
		ActionContext.setContext(invocation.getInvocationContext());

		String retCode = null;

		String profileKey = "execute: ";
		try {
			UtilTimerStack.push(profileKey);

			retCode = invocation.invoke();
		} finally {
			if (cleanupContext) {
				ActionContext.setContext(nestedContext);
			}
			UtilTimerStack.pop(profileKey);
		}

		return retCode;
	}

	@Override
	protected void prepare() {
		super.prepare();
	}
}
