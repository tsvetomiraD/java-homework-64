package values;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class ValuesTests {
    AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext(A.class);
    }

    @Test
    public void testDirectlySetValue() {
        A a = context.getBean(A.class);
        assertEquals("string value", a.getStringValue());
    }

    @Test
    public void testValueFromFile() {
        A a = context.getBean(A.class);
        assertEquals("Value got from the file", a.getValueFromFile());
    }

    @Test
    public void testDefaultValue() {
        A a = context.getBean(A.class);
        assertEquals("some default", a.getSomeDefault());
    }

    @Test
    public void testBasicSpel() {
        A a = context.getBean(A.class);
        assertEquals(18, a.getSpel());
    }

    @Test
    public void testConstructorValue() {
        context.register(PriorityProvider.class);
        PriorityProvider app = context.getBean(PriorityProvider.class);
        assertEquals("Some String", app.getPriority());
    }

    @Test
    public void testSetterValue() {
        context.register(Setter.class);
        Setter app = context.getBean(Setter.class);
        assertEquals("Some Value for setter", app.getValue());
    }

    @Test
    public void testBeanValue() {
        A a = context.getBean(A.class);
        assertEquals(10, a.getSomeBeanValue());
    }
}
