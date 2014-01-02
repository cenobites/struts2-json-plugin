package es.cenobit.struts2.json.example;

import org.apache.struts2.StrutsTestCase;

import com.opensymphony.xwork2.ActionSupport;

import es.cenobit.struts2.json.example.IndexAction;

public class IndexActionTest extends StrutsTestCase {

    public void testHelloWorld() throws Exception {
        IndexAction indexAction = new IndexAction();
        String result = indexAction.execute();
        assertTrue("Expected a success result!", ActionSupport.SUCCESS.equals(result));
    }
}
