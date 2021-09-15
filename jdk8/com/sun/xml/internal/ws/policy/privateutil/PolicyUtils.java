package com.sun.xml.internal.ws.policy.privateutil;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class PolicyUtils {
   private PolicyUtils() {
   }

   public static class Rfc2396 {
      private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtils.Reflection.class);

      public static String unquote(String quoted) {
         if (null == quoted) {
            return null;
         } else {
            byte[] unquoted = new byte[quoted.length()];
            int newLength = 0;

            for(int i = 0; i < quoted.length(); ++i) {
               char c = quoted.charAt(i);
               if ('%' == c) {
                  if (i + 2 >= quoted.length()) {
                     throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted)), false);
                  }

                  ++i;
                  int hi = Character.digit((char)quoted.charAt(i), 16);
                  ++i;
                  int lo = Character.digit((char)quoted.charAt(i), 16);
                  if (0 > hi || 0 > lo) {
                     throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted)), false);
                  }

                  unquoted[newLength++] = (byte)(hi * 16 + lo);
               } else {
                  unquoted[newLength++] = (byte)c;
               }
            }

            try {
               return new String(unquoted, 0, newLength, "utf-8");
            } catch (UnsupportedEncodingException var7) {
               throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted), var7));
            }
         }
      }
   }

   public static class ServiceProvider {
      public static <T> T[] load(Class<T> serviceClass, ClassLoader loader) {
         return ServiceFinder.find(serviceClass, loader).toArray();
      }

      public static <T> T[] load(Class<T> serviceClass) {
         return ServiceFinder.find(serviceClass).toArray();
      }
   }

   public static class ConfigFile {
      public static String generateFullName(String configFileIdentifier) throws PolicyException {
         if (configFileIdentifier != null) {
            StringBuffer buffer = new StringBuffer("wsit-");
            buffer.append(configFileIdentifier).append(".xml");
            return buffer.toString();
         } else {
            throw new PolicyException(LocalizationMessages.WSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL());
         }
      }

      public static URL loadFromContext(String configFileName, Object context) {
         return (URL)PolicyUtils.Reflection.invoke(context, "getResource", URL.class, configFileName);
      }

      public static URL loadFromClasspath(String configFileName) {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         return cl == null ? ClassLoader.getSystemResource(configFileName) : cl.getResource(configFileName);
      }
   }

   static class Reflection {
      private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtils.Reflection.class);

      static <T> T invoke(Object target, String methodName, Class<T> resultClass, Object... parameters) throws RuntimePolicyUtilsException {
         Class[] parameterTypes;
         if (parameters != null && parameters.length > 0) {
            parameterTypes = new Class[parameters.length];
            int i = 0;
            Object[] var6 = parameters;
            int var7 = parameters.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Object parameter = var6[var8];
               parameterTypes[i++] = parameter.getClass();
            }
         } else {
            parameterTypes = null;
         }

         return invoke(target, methodName, resultClass, parameters, parameterTypes);
      }

      public static <T> T invoke(Object target, String methodName, Class<T> resultClass, Object[] parameters, Class[] parameterTypes) throws RuntimePolicyUtilsException {
         try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            Object result = MethodUtil.invoke(target, method, parameters);
            return resultClass.cast(result);
         } catch (IllegalArgumentException var7) {
            throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), var7));
         } catch (InvocationTargetException var8) {
            throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), var8));
         } catch (IllegalAccessException var9) {
            throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), var9.getCause()));
         } catch (SecurityException var10) {
            throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), var10));
         } catch (NoSuchMethodException var11) {
            throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), var11));
         }
      }

      private static String createExceptionMessage(Object target, Object[] parameters, String methodName) {
         return LocalizationMessages.WSP_0061_METHOD_INVOCATION_FAILED(target.getClass().getName(), methodName, parameters == null ? null : Arrays.asList(parameters).toString());
      }
   }

   public static class Collections {
      public static <E, T extends Collection<? extends E>, U extends Collection<? extends E>> Collection<Collection<E>> combine(U initialBase, Collection<T> options, boolean ignoreEmptyOption) {
         List<Collection<E>> combinations = null;
         if (options != null && !options.isEmpty()) {
            Collection<E> base = new LinkedList();
            if (initialBase != null && !initialBase.isEmpty()) {
               base.addAll(initialBase);
            }

            int finalCombinationsSize = 1;
            Queue<T> optionProcessingQueue = new LinkedList();
            Iterator var7 = options.iterator();

            int newSemiCombinationCollectionSize;
            while(var7.hasNext()) {
               T option = (Collection)var7.next();
               newSemiCombinationCollectionSize = option.size();
               if (newSemiCombinationCollectionSize == 0) {
                  if (!ignoreEmptyOption) {
                     return null;
                  }
               } else if (newSemiCombinationCollectionSize == 1) {
                  base.addAll(option);
               } else {
                  optionProcessingQueue.offer(option);
                  finalCombinationsSize *= newSemiCombinationCollectionSize;
               }
            }

            combinations = new ArrayList(finalCombinationsSize);
            combinations.add(base);
            Collection processedOption;
            if (finalCombinationsSize > 1) {
               while((processedOption = (Collection)optionProcessingQueue.poll()) != null) {
                  int actualSemiCombinationCollectionSize = combinations.size();
                  newSemiCombinationCollectionSize = actualSemiCombinationCollectionSize * processedOption.size();
                  int semiCombinationIndex = 0;
                  Iterator var11 = processedOption.iterator();

                  while(var11.hasNext()) {
                     E optionElement = var11.next();

                     for(int i = 0; i < actualSemiCombinationCollectionSize; ++i) {
                        Collection<E> semiCombination = (Collection)combinations.get(semiCombinationIndex);
                        if (semiCombinationIndex + actualSemiCombinationCollectionSize < newSemiCombinationCollectionSize) {
                           combinations.add(new LinkedList(semiCombination));
                        }

                        semiCombination.add(optionElement);
                        ++semiCombinationIndex;
                     }
                  }
               }
            }

            return combinations;
         } else {
            if (initialBase != null) {
               combinations = new ArrayList(1);
               combinations.add(new ArrayList(initialBase));
            }

            return combinations;
         }
      }
   }

   public static class Comparison {
      public static final Comparator<QName> QNAME_COMPARATOR = new Comparator<QName>() {
         public int compare(QName qn1, QName qn2) {
            if (qn1 != qn2 && !qn1.equals(qn2)) {
               int result = qn1.getNamespaceURI().compareTo(qn2.getNamespaceURI());
               return result != 0 ? result : qn1.getLocalPart().compareTo(qn2.getLocalPart());
            } else {
               return 0;
            }
         }
      };

      public static int compareBoolean(boolean b1, boolean b2) {
         int i1 = b1 ? 1 : 0;
         int i2 = b2 ? 1 : 0;
         return i1 - i2;
      }

      public static int compareNullableStrings(String s1, String s2) {
         return s1 == null ? (s2 == null ? 0 : -1) : (s2 == null ? 1 : s1.compareTo(s2));
      }
   }

   public static class Text {
      public static final String NEW_LINE = System.getProperty("line.separator");

      public static String createIndent(int indentLevel) {
         char[] charData = new char[indentLevel * 4];
         Arrays.fill(charData, ' ');
         return String.valueOf(charData);
      }
   }

   public static class IO {
      private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtils.IO.class);

      public static void closeResource(Closeable resource) {
         if (resource != null) {
            try {
               resource.close();
            } catch (IOException var2) {
               LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(resource.toString()), var2);
            }
         }

      }

      public static void closeResource(XMLStreamReader reader) {
         if (reader != null) {
            try {
               reader.close();
            } catch (XMLStreamException var2) {
               LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(reader.toString()), var2);
            }
         }

      }
   }

   public static class Commons {
      public static String getStackMethodName(int methodIndexInStack) {
         StackTraceElement[] stack = Thread.currentThread().getStackTrace();
         String methodName;
         if (stack.length > methodIndexInStack + 1) {
            methodName = stack[methodIndexInStack].getMethodName();
         } else {
            methodName = "UNKNOWN METHOD";
         }

         return methodName;
      }

      public static String getCallerMethodName() {
         String result = getStackMethodName(5);
         if (result.equals("invoke0")) {
            result = getStackMethodName(4);
         }

         return result;
      }
   }
}
