package dic;


interface AI { }

class A implements AI { }


class B {
    @Inject
    A aField;
}

class C {
    @Inject B bField;
}

@Default(cl = D.class)
interface DI { }

class D implements DI { }

class E {
    A aField;

    @Inject
    public E(A afield) {
        this.aField = afield;
    }
}

class F {
    @Inject
    @Named
    A iname;
}

class FS {
    @Inject
    @Named String email;
}

class FSI implements Initializer {
    @Inject
    @Named String email;

    @Override
    public void init() throws Exception {
        email = "mailto:" + email;
    }
}

class CC { }

@Lazy
class CD {
    @Inject
    C aField;
}



class CustomSpringEvent extends ApplicationEvent {
    private String message;

    public CustomSpringEvent(Object source, String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

class CustomSpringEventPublisher {
    public boolean published = false;
    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    public void sendMsg(final String message) {
        published = true;
        System.out.println("Publishing custom event. ");
        CustomSpringEvent customSpringEvent = new CustomSpringEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}

class AnnotationDrivenEventListener {
    public boolean received = false;

    @EventListener
    public void handleContextStart(CustomSpringEvent event) {
        received = true;
        System.out.println("Received spring custom event with annotation - " + event.getMessage());
    }
}

