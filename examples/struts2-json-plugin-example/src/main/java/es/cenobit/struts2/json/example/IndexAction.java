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
package es.cenobit.struts2.json.example;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import es.cenobit.struts2.json.annotations.Json;
import es.cenobit.struts2.json.example.objects.Bar;
import es.cenobit.struts2.json.example.objects.Etc;
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
    public Bar barWithExcludeFields() {
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

    @Json(exclude = { "id", "Bar.title", "Bar.status" })
    public Foo fooWithExcludeFields() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        Foo foo = new Foo(2L, "Interessantiss", bar);
        return foo;
    }

    @Json
    public Etc etc() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        Foo foo = new Foo(2L, "Interessantiss", bar);
        Etc etc = new Etc(42L, "Cevadis", bar, foo);
        return etc;
    }

    @Json(exclude = { "id", "Bar.title", "Bar.status", "Foo" })
    public Etc etcWithExcludeFields() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        Foo foo = new Foo(2L, "Interessantiss", bar);
        Etc etc = new Etc(42L, "Cevadis", bar, foo);
        return etc;
    }
}
