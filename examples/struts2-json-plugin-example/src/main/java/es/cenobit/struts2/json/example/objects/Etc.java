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
