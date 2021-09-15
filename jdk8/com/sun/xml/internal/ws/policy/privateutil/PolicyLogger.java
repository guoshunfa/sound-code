package com.sun.xml.internal.ws.policy.privateutil;

import com.sun.istack.internal.logging.Logger;
import java.lang.reflect.Field;

public final class PolicyLogger extends Logger {
   private static final String POLICY_PACKAGE_ROOT = "com.sun.xml.internal.ws.policy";

   private PolicyLogger(String policyLoggerName, String className) {
      super(policyLoggerName, className);
   }

   public static PolicyLogger getLogger(Class<?> componentClass) {
      String componentClassName = componentClass.getName();
      return componentClassName.startsWith("com.sun.xml.internal.ws.policy") ? new PolicyLogger(getLoggingSubsystemName() + componentClassName.substring("com.sun.xml.internal.ws.policy".length()), componentClassName) : new PolicyLogger(getLoggingSubsystemName() + "." + componentClassName, componentClassName);
   }

   private static String getLoggingSubsystemName() {
      String loggingSubsystemName = "wspolicy";

      try {
         Class jaxwsConstants = Class.forName("com.sun.xml.internal.ws.util.Constants");
         Field loggingDomainField = jaxwsConstants.getField("LoggingDomain");
         Object loggingDomain = loggingDomainField.get((Object)null);
         loggingSubsystemName = loggingDomain.toString().concat(".wspolicy");
      } catch (RuntimeException var4) {
      } catch (Exception var5) {
      }

      return loggingSubsystemName;
   }
}
