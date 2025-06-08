package br.com.boot.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Api {
    String summary();
    Class<?> success() default Void.class;
    String[] errors()  default {};
}
