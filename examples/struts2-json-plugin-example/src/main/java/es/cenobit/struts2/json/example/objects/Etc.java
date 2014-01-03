package es.cenobit.struts2.json.example.objects;

public class Etc {

    private Long id;
    private String title;
    private Bar myBar;
    private Foo myFoo;

    public Etc(Long id, String title, Bar myBar, Foo myFoo) {
        super();
        this.id = id;
        this.title = title;
        this.myBar = myBar;
        this.myFoo = myFoo;
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

    public Bar getMyBar() {
        return myBar;
    }

    public void setMyBar(Bar myBar) {
        this.myBar = myBar;
    }

    public Foo getMyFoo() {
        return myFoo;
    }

    public void setMyFoo(Foo myFoo) {
        this.myFoo = myFoo;
    }

}
