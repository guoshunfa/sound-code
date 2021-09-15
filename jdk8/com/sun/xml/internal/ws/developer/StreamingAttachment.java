package com.sun.xml.internal.ws.developer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
@WebServiceFeatureAnnotation(
   id = "http://jax-ws.dev.java.net/features/mime",
   bean = StreamingAttachmentFeature.class
)
public @interface StreamingAttachment {
   String dir() default "";

   boolean parseEagerly() default false;

   long memoryThreshold() default 1048576L;
}
