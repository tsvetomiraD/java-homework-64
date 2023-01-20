package async;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class AsyncTests {
    AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext(SpringAsyncConfig.class);
    }

    @Test
    public void testVoidAsync() {
        SpringAsyncConfig a = context.getBean(SpringAsyncConfig.class);
        a.asyncMethodWithVoidReturnType();
    }

    @Test
    public void testReturnFuture() throws Exception {
        SpringAsyncConfig a = context.getBean(SpringAsyncConfig.class);
        Future<String> res =  a.asyncMethodWithReturnType();
        assertEquals("hello world!", res.get());
    }
    @Test
    public void testReturnFuture1() throws Exception {
        SpringAsyncConfig a = context.getBean(SpringAsyncConfig.class);
        Future<String> res =  a.asyncMethodWithReturnType();
        Future<String> res1 =  a.asyncMethodWithReturnType1();
        assertEquals("hello world!", res.get());
    }

    @Test
    public void testExecutor() throws Exception {
        SpringAsyncConfig a = context.getBean(SpringAsyncConfig.class);
        a.asyncMethodWithConfiguredExecutor();
        a.asyncMethodWithVoidReturnType();
        String myThread = a.getMyThread();

        assertEquals("myThreadPoolTaskExecutor-1", myThread);
    }
}
