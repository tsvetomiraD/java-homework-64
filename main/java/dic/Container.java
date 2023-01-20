package dic;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Container {
    private final Set<Class<?>> PRIMITIVE_TYPES = Set.of(byte.class, short.class, int.class, long.class, float.class, double.class, char.class, Character.class, String.class, boolean.class, Integer.class);
    Map<String, Object> classes = new HashMap<>();
    Map<Class<?>, Class<?>> classToClass = new HashMap<>();
    List<Class<?>> visited = new ArrayList<>();
    Map<Class<?>, Object> addedProxyLazy = new HashMap<>();
    Map<Method, Object> listeners = new HashMap<>();
    Properties properties;
    ExecutorService ex = Executors.newFixedThreadPool(3);

    public Container(Properties properties) {
        this.properties = properties;
    }

    public Object getInstance(String key) {
        return classes.get(key);
    }

    public <T> T getInstance(Class<T> c) throws Exception {
        if (classes.containsKey(c.getName())) {
            return (T) classes.get(c.getName());
        }

        Lazy lazy = c.getDeclaredAnnotation(Lazy.class);
        if (lazy != null) {
            if (!addedProxyLazy.containsKey(c)) {
                Object cl = c.getDeclaredConstructor().newInstance();
                addedProxyLazy.put(c, cl);
                return (T) cl;
            }
            Object res = instanceClass(c);
            classes.put(c.getName(), res);
            return (T) res;
        }

        Class<?> classImpl = null;
        if (c.isInterface()) {
            if (!classToClass.containsKey(c)) {
                Default defaultClass = c.getDeclaredAnnotation(Default.class);
                if (defaultClass != null) {
                    classImpl = defaultClass.cl();
                    classToClass.put(c, classImpl);
                }
            }

            if (classImpl == null) {
                throw new RegistryException("");
            }
        }
        classImpl = classImpl != null ? classImpl : c;
        if (classes.containsKey(classImpl.getName())) {
            return (T) classes.get(classImpl.getName());
        }

        visited.add(c);

        Object res = instanceClass(classImpl);
        classes.put(classImpl.getName(), res);
        return (T) res;
    }

    private Object instanceClass(Class<?> classImpl) throws Exception {
        Constructor<?> con = getConstructor(classImpl);
        Object inst = null;
        if (con != null) {
            Object[] params = getParams(con);
            inst = con.newInstance(params);
        } else {
            inst = classImpl.getDeclaredConstructor().newInstance();
        }
        instanceFields(inst);

        if (Initializer.class.isAssignableFrom(classImpl)) {
            ((Initializer) inst).init();
        }
        Method[] methods = classImpl.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Async.class) != null) {
                Object finalInst = inst;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            m.invoke(finalInst, m.getParameters());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                ex.submit(r);
            }
            if (m.getAnnotation(EventListener.class) != null) {
                listeners.put(m, inst);
            }
        }

        return inst;
    }

    private Object[] getParams(Constructor<?> con) throws Exception {
        Object[] params = null;
        Class<?>[] paramsTypes = con.getParameterTypes();

        if (paramsTypes.length != 0) {
            params = new Object[paramsTypes.length];
        }

        for (int i = 0; i < paramsTypes.length; i++) {
            Class<?> paramType = paramsTypes[i];
            Object paramInst = getInstance(paramType);
            params[i] = paramInst;
        }
        return params;
    }

    private Constructor<?> getConstructor(Class<?> classImpl) {
        Constructor<?> con = null;
        for (Constructor c : classImpl.getDeclaredConstructors()) {
            Inject annotation = c.getDeclaredAnnotation(Inject.class);
            if (annotation == null) {
                continue;
            }
            c.setAccessible(true);
            con = c;
            break;
        }
        return con;
    }

    private void instanceFields(Object cl) throws Exception {
        Class<?> claasImpl = cl.getClass();
        for (Field f : claasImpl.getDeclaredFields()) {
            Object o = null;
            if (f.getType().equals(ApplicationEventPublisher.class)) {
                o = new ApplicationEventPublisher(this);
            }
            Inject annotation = f.getAnnotation(Inject.class);
            if (annotation == null) {
                continue;
            }
            f.setAccessible(true);
            Named name = f.getAnnotation(Named.class);


            if (PRIMITIVE_TYPES.contains(f.getType())) {
                o = name != null ? properties.get(name) : properties.get(f.getName());
            }
            Class<?> checkClass = visited.size() != 0 ? visited.get(0) : null;
            if (visited.contains(f.getType())) {
                throw new RegistryException("Circular dependency");
            }
            if (o == null) {
                if (name != null) {
                    o = getInstance(f.getName());
                } else {
                    o = getInstance(f.getType());
                }
            }
            f.set(cl, o);
        }
    }

    public void decorateInstance(Object o) throws Exception {
        instanceFields(o);
    }

    public void registerInstance(String key, Object instance) {
        classes.put(key, instance);
    }

    public void registerImplementation(Class c, Class subClass) throws Exception {
        classToClass.put(c, subClass);
    }

    public void registerImplementation(Class c) throws Exception {
        if (classes.containsKey(c.getName())) {
            return;
        }
        Object cl = c.getDeclaredConstructor().newInstance();
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            f.set(getInstance(f.getType()), cl);
        }
        classes.put(c.getName(), cl);
    }

    public void registerInstance(Object instance) {
        classes.put(instance.getClass().getName(), instance);
    }
}

class LazyClass implements InvocationHandler {
    Container container;
    Class<?> c;

    LazyClass(Container con, Class<?> c) {
        this.container = con;
        this.c = c;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        container.getInstance(c);
        return method.invoke(c, args);
    }
}
