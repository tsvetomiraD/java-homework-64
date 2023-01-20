package async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Configuration
@EnableAsync
class SpringAsyncConfig {
    private String normalThread = null;
    private String myThread = null;
    @Autowired
    @Async
    public void asyncMethodWithVoidReturnType() {
        this.normalThread = Thread.currentThread().getName();
        System.out.println(normalThread);
    }

    @Async
    public Future<String> asyncMethodWithReturnType() {

        try {
            Thread.sleep(5000);
            System.out.println("Execute method asynchronously - "
                    + Thread.currentThread().getName());
            return new AsyncResult<String>("hello world!");
        } catch (InterruptedException e) {
            //
        }

        return null;
    }

    @Async
    public Future<String> asyncMethodWithReturnType1() {
        System.out.println("Execute method asynchronously1 - "
                + Thread.currentThread().getName());

        return new AsyncResult<String>("hello world!1");

    }

    @Bean(name = "myThreadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Async("myThreadPoolTaskExecutor")
    public void asyncMethodWithConfiguredExecutor() {
        this.myThread = Thread.currentThread().getName();
    }
    String getMyThread() {
        return this.myThread;
    }
}

