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

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;

public class JsonActionProxyFactory extends DefaultActionProxyFactory implements ActionProxyFactory {

    public JsonActionProxyFactory() {
        super();
    }

    public ActionProxy createActionProxy(String namespace, String actionName, String methodName,
            Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {

        ActionInvocation inv = new JsonActionInvocation(extraContext, true);
        container.inject(inv);
        return createActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
    }

    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName,
            boolean executeResult, boolean cleanupContext) {

        JsonActionProxy proxy = new JsonActionProxy(inv, namespace, actionName, methodName, executeResult,
                cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }

}
