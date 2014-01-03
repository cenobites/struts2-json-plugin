package es.cenobit.struts2.json.objects;

public class TestInnerObject {
    public String field1 = "Java";
    public String field2 = "Struts2";
    public String field3 = "Json";
    public TestInnerRelationship1Object relationship1 = new TestInnerRelationship1Object();
    public TestInnerRelationship2Object relationship2 = new TestInnerRelationship2Object();

    @SuppressWarnings("unused")
    private static class TestInnerRelationship1Object {
        public String java = "field1";
        public String field2 = "Struts2";
        public String json = "field3";
    }

    @SuppressWarnings("unused")
    private static class TestInnerRelationship2Object {
        public String java = "field1";
        public String field2 = "Struts2";
        public String json = "field3";
        public TestInnerRelationship1Object relationship1 = new TestInnerRelationship1Object();
    }
}
