package ghissues;

import act.data.annotation.Data;
import act.util.AdaptiveBean;
import act.util.LogSupport;
import org.osgl.mvc.annotation.GetAction;

public class Gh786 extends LogSupport {

    @Data
    public static class FooBase {
        public int n = 10;
    }

    @Data
    public static class BarBase {
        public int n = -9;
    }

    @Data(callSuper = true)
    public static class Foo extends FooBase {
    }

    @Data(callSuper = true)
    public static class Bar extends BarBase {
    }

    @GetAction("786/a")
    public int testA() {
        Foo foo = new Foo();
        Bar bar = new Bar();
        return foo.hashCode() - bar.hashCode();
    }

    @Data
    public static class MyRecord extends AdaptiveBean {}

    @GetAction("786/b")
    public int testB() {
        MyRecord r1 = new MyRecord();
        r1.putValue("foo", 1);
        MyRecord r2 = new MyRecord();
        r2.putValue("foo", 2);
        return r1.hashCode() - r2.hashCode();
    }

}
