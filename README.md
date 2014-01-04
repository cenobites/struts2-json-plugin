Struts 2 JSON Plugin
====================

Struts 2 JSON Plugin focuses in simplicity and, therefore, all of its 
functionalities have as main goal solve the developer's problem in the less intrusive way.

Some reasons you might want to use:

* Simple, powerful, flexible.
* Serializable with [Gson](http://code.google.com/p/google-gson/ "Gson") (hey, this is great!).
* Support for [OSGi](http://www.osgi.org/Main/HomePage "OSGi") ([Struts OSGi Plugin](http://struts.apache.org/release/2.2.x/docs/osgi-plugin.html "Struts OSGi Plugin")).
* Integration for [Struts Convention Plugin](http://struts.apache.org/release/2.2.x/docs/convention-plugin.html "Struts Convention Plugin").
* Extensive documentation, and great community support.


Using it
--------

1.  In a Maven project's pom.xml file:

```xml   
<dependency>
    <groupId>es.cenobit.struts2.json</groupId>
    <artifactId>struts2-json-plugin</artifactId>
    <version>2.3.16</version> <!-- or last version Struts 2.x -->
</dependency>
```

2.  Or put struts2-json-plugin-VERSION.jar and dependencies in your `WEB-INF/lib` folder.
3.  In your project create actions.


#### Domain ####

```java
public class Bar {

    private Long id;
    private String title;
    private String description;
    private Date pubDate;
    private Boolean status;

    public Bar(Long id, String title, String description, Date pubDate, Boolean status) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
```


#### Action ####

```java
public class IndexAction extends ActionSupport {

    private static final long serialVersionUID = -1411535978306393960L;

    @Json
    public Bar showBar() {
        Bar bar = new Bar(1L, "Mussum ipsum",
                " Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo",
                new Date(), Boolean.TRUE);
        return bar;
    }
}
```


#### struts.xml ####

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 2.3//EN"
        "http://struts.apache.org/dtds/struts-2.3.dtd">
<struts>
    <constant name="struts.enable.DynamicMethodInvocation" value="false" />
    <constant name="struts.devMode" value="true" />

    <package name="default" namespace="/" extends="json-default">

        <action name="bar" class="es.cenobit.struts2.json.example.IndexAction" method="showBar" />

    </package>

</struts>
```

4.  Testing

```
$ curl -i -X GET http://localhost:8080/struts2-json-plugin-example/bar
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Server: Jetty(8.1.7.v20120910)

{"id":1,"title":"Mussum ipsum","description":" Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo","pub_date":"Jan 3, 2014 11:13:50 AM","status":true}
```

For more tests see [Examples](https://github.com/cenobites/struts2-json-plugin/tree/master/examples "Examples").


TODO
----

* Interceptor Json (validation, ...)
* Tests
* Documentation
* Examples


Project Information
-------------------

* __Author:__ Cenobit Technologies, Inc.
* __Version:__ 1.0 of 2014/01/02
* __License:__ [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html "Apache License 2.0")