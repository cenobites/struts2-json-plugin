package es.cenobit.struts2.json.example.objects;

public class Foo {

    private Long id;
    private String name;
    private Bar bar;

    public Foo(Long id, String name, Bar bar) {
        super();
        this.id = id;
        this.name = name;
        this.bar = bar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

}
