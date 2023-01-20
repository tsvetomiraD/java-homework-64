package dic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ApplicationEventPublisher {
    Container container;
    protected ApplicationEventPublisher(Container container) {
        this.container = container;
    }
    public void publishEvent(ApplicationEvent event) {
        for (Map.Entry<Method, Object> l : container.listeners.entrySet()) {
            try {
                l.getKey().invoke(l.getValue(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
