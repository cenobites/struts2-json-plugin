package es.cenobit.struts2.json.example;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import es.cenobit.struts2.json.annotations.Json;
import es.cenobit.struts2.json.example.objects.Bar;
import es.cenobit.struts2.json.example.objects.Foo;

public class IndexAction extends ActionSupport {

    private static final long serialVersionUID = -1411535978306393960L;

    @Override
    public String execute() {
        return SUCCESS;
    }

    @Json
    public List<String> list() {
        return Arrays.asList(new String[] { "Java", "Struts2", "Gson", "Plugin" });
    }

    @Json
    public Map<Integer, String> map() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "Java");
        map.put(2, "Struts2");
        map.put(3, "Gson");
        map.put(4, "Plugin");
        return map;
    }

    @Json
    public Bar bar() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        return bar;
    }

    @Json(exclude = { "description", "pubDate", "status" })
    public Bar barExcludes() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        return bar;
    }

    @Json
    public Foo foo() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        Foo foo = new Foo(2L, "Interessantiss", bar);
        return foo;
    }

    @Json(exclude = { "id", "bar.title", "bar.status" })
    public Foo fooExcludes() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        Foo foo = new Foo(2L, "Interessantiss", bar);
        return foo;
    }
}
