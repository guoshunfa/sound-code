package java.security;

import javax.security.auth.Subject;

public interface Principal {
   boolean equals(Object var1);

   String toString();

   int hashCode();

   String getName();

   default boolean implies(Subject var1) {
      return var1 == null ? false : var1.getPrincipals().contains(this);
   }
}
