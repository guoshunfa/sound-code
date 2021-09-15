package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.server.DraconianValidationErrorHandler;
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
   id = "http://jax-ws.dev.java.net/features/schema-validation",
   bean = SchemaValidationFeature.class
)
public @interface SchemaValidation {
   Class<? extends ValidationErrorHandler> handler() default DraconianValidationErrorHandler.class;

   boolean inbound() default true;

   boolean outbound() default true;
}
