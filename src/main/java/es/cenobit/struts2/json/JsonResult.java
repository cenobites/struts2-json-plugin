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
            response.setCharacterEncoding("UTF-8");
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
