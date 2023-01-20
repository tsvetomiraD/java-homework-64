package values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;



@Component
@PropertySource("props.properties")
class A {

    @Value("string value")
    private String stringValue;

    @Value("${value.from.file}")
    private String valueFromFile;

    @Value("${unknown.param:some default}")
    private String someDefault;

    @Value("#{36 / 2}")
    private int spel;
    @Value("#{someBean.someValue}")
    private int intValue;

    @Bean(name = "someBean")
    void SomeBean() {
        int someValue = 10;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public String getValueFromFile() {
        return this.valueFromFile;
    }

    public String getSomeDefault() {
        return this.someDefault;
    }

    public int getSpel() {
        return this.spel;
    }

    public int getSomeBeanValue() {
        return this.intValue;
   }
}

@Component
class PriorityProvider {

    private String priority;

    @Autowired
    public PriorityProvider(@Value("Some String") String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return this.priority;
    }
}

@Component
class Setter {

    private String value;

    @Autowired
    public void setValue(@Value("Some Value for setter") String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}