package com.haiyiyang.light.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface LightServiceAutowired {

}
