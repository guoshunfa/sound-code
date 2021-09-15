package java.awt;

public interface MenuContainer {
   Font getFont();

   void remove(MenuComponent var1);

   /** @deprecated */
   @Deprecated
   boolean postEvent(Event var1);
}
