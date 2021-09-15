package java.beans;

import com.sun.beans.finder.PrimitiveWrapperMap;
import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import sun.reflect.misc.ReflectUtil;

class MetaData {
   private static final Map<String, Field> fields = Collections.synchronizedMap(new WeakHashMap());
   private static Hashtable<String, PersistenceDelegate> internalPersistenceDelegates = new Hashtable();
   private static PersistenceDelegate nullPersistenceDelegate = new MetaData.NullPersistenceDelegate();
   private static PersistenceDelegate enumPersistenceDelegate = new MetaData.EnumPersistenceDelegate();
   private static PersistenceDelegate primitivePersistenceDelegate = new MetaData.PrimitivePersistenceDelegate();
   private static PersistenceDelegate defaultPersistenceDelegate = new DefaultPersistenceDelegate();
   private static PersistenceDelegate arrayPersistenceDelegate;
   private static PersistenceDelegate proxyPersistenceDelegate;

   public static synchronized PersistenceDelegate getPersistenceDelegate(Class var0) {
      if (var0 == null) {
         return nullPersistenceDelegate;
      } else if (Enum.class.isAssignableFrom(var0)) {
         return enumPersistenceDelegate;
      } else if (null != XMLEncoder.primitiveTypeFor(var0)) {
         return primitivePersistenceDelegate;
      } else if (var0.isArray()) {
         if (arrayPersistenceDelegate == null) {
            arrayPersistenceDelegate = new MetaData.ArrayPersistenceDelegate();
         }

         return arrayPersistenceDelegate;
      } else {
         try {
            if (Proxy.isProxyClass(var0)) {
               if (proxyPersistenceDelegate == null) {
                  proxyPersistenceDelegate = new MetaData.ProxyPersistenceDelegate();
               }

               return proxyPersistenceDelegate;
            }
         } catch (Exception var5) {
         }

         String var1 = var0.getName();
         Object var2 = (PersistenceDelegate)getBeanAttribute(var0, "persistenceDelegate");
         if (var2 == null) {
            var2 = (PersistenceDelegate)internalPersistenceDelegates.get(var1);
            if (var2 != null) {
               return (PersistenceDelegate)var2;
            }

            internalPersistenceDelegates.put(var1, defaultPersistenceDelegate);

            try {
               String var3 = var0.getName();
               Class var8 = Class.forName("java.beans.MetaData$" + var3.replace('.', '_') + "_PersistenceDelegate");
               var2 = (PersistenceDelegate)var8.newInstance();
               internalPersistenceDelegates.put(var1, var2);
            } catch (ClassNotFoundException var6) {
               String[] var4 = getConstructorProperties(var0);
               if (var4 != null) {
                  var2 = new DefaultPersistenceDelegate(var4);
                  internalPersistenceDelegates.put(var1, var2);
               }
            } catch (Exception var7) {
               System.err.println("Internal error: " + var7);
            }
         }

         return (PersistenceDelegate)(var2 != null ? var2 : defaultPersistenceDelegate);
      }
   }

   private static String[] getConstructorProperties(Class<?> var0) {
      String[] var1 = null;
      int var2 = 0;
      Constructor[] var3 = var0.getConstructors();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Constructor var6 = var3[var5];
         String[] var7 = getAnnotationValue(var6);
         if (var7 != null && var2 < var7.length && isValid(var6, var7)) {
            var1 = var7;
            var2 = var7.length;
         }
      }

      return var1;
   }

   private static String[] getAnnotationValue(Constructor<?> var0) {
      ConstructorProperties var1 = (ConstructorProperties)var0.getAnnotation(ConstructorProperties.class);
      return var1 != null ? var1.value() : null;
   }

   private static boolean isValid(Constructor<?> var0, String[] var1) {
      Class[] var2 = var0.getParameterTypes();
      if (var1.length != var2.length) {
         return false;
      } else {
         String[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (var6 == null) {
               return false;
            }
         }

         return true;
      }
   }

   private static Object getBeanAttribute(Class<?> var0, String var1) {
      try {
         return Introspector.getBeanInfo(var0).getBeanDescriptor().getValue(var1);
      } catch (IntrospectionException var3) {
         return null;
      }
   }

   static Object getPrivateFieldValue(Object var0, String var1) {
      Field var2 = (Field)fields.get(var1);
      if (var2 == null) {
         int var3 = var1.lastIndexOf(46);
         final String var4 = var1.substring(0, var3);
         final String var5 = var1.substring(1 + var3);
         var2 = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
               try {
                  Field var1 = Class.forName(var4).getDeclaredField(var5);
                  var1.setAccessible(true);
                  return var1;
               } catch (ClassNotFoundException var2) {
                  throw new IllegalStateException("Could not find class", var2);
               } catch (NoSuchFieldException var3) {
                  throw new IllegalStateException("Could not find field", var3);
               }
            }
         });
         fields.put(var1, var2);
      }

      try {
         return var2.get(var0);
      } catch (IllegalAccessException var6) {
         throw new IllegalStateException("Could not get value of the field", var6);
      }
   }

   static {
      internalPersistenceDelegates.put("java.net.URI", new MetaData.PrimitivePersistenceDelegate());
      internalPersistenceDelegates.put("javax.swing.plaf.BorderUIResource$MatteBorderUIResource", new MetaData.javax_swing_border_MatteBorder_PersistenceDelegate());
      internalPersistenceDelegates.put("javax.swing.plaf.FontUIResource", new MetaData.java_awt_Font_PersistenceDelegate());
      internalPersistenceDelegates.put("javax.swing.KeyStroke", new MetaData.java_awt_AWTKeyStroke_PersistenceDelegate());
      internalPersistenceDelegates.put("java.sql.Date", new MetaData.java_util_Date_PersistenceDelegate());
      internalPersistenceDelegates.put("java.sql.Time", new MetaData.java_util_Date_PersistenceDelegate());
      internalPersistenceDelegates.put("java.util.JumboEnumSet", new MetaData.java_util_EnumSet_PersistenceDelegate());
      internalPersistenceDelegates.put("java.util.RegularEnumSet", new MetaData.java_util_EnumSet_PersistenceDelegate());
   }

   static final class sun_swing_PrintColorUIResource_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Color var3 = (Color)var1;
         Object[] var4 = new Object[]{var3.getRGB()};
         return new Expression(var3, ColorUIResource.class, "new", var4);
      }
   }

   static final class javax_swing_border_MatteBorder_PersistenceDelegate extends PersistenceDelegate {
      protected Expression instantiate(Object var1, Encoder var2) {
         MatteBorder var3 = (MatteBorder)var1;
         Insets var4 = var3.getBorderInsets();
         Object var5 = var3.getTileIcon();
         if (var5 == null) {
            var5 = var3.getMatteColor();
         }

         Object[] var6 = new Object[]{var4.top, var4.left, var4.bottom, var4.right, var5};
         return new Expression(var3, var3.getClass(), "new", var6);
      }
   }

   static final class javax_swing_JMenu_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         JMenu var5 = (JMenu)var2;
         Component[] var6 = var5.getMenuComponents();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            invokeStatement(var2, "add", new Object[]{var6[var7]}, var4);
         }

      }
   }

   static final class javax_swing_Box_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return super.mutatesTo(var1, var2) && this.getAxis(var1).equals(this.getAxis(var2));
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         return new Expression(var1, var1.getClass(), "new", new Object[]{this.getAxis(var1)});
      }

      private Integer getAxis(Object var1) {
         Box var2 = (Box)var1;
         return (Integer)MetaData.getPrivateFieldValue(var2.getLayout(), "javax.swing.BoxLayout.axis");
      }
   }

   static final class javax_swing_JTabbedPane_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         JTabbedPane var5 = (JTabbedPane)var2;

         for(int var6 = 0; var6 < var5.getTabCount(); ++var6) {
            invokeStatement(var2, "addTab", new Object[]{var5.getTitleAt(var6), var5.getIconAt(var6), var5.getComponentAt(var6)}, var4);
         }

      }
   }

   static final class javax_swing_ToolTipManager_PersistenceDelegate extends PersistenceDelegate {
      protected Expression instantiate(Object var1, Encoder var2) {
         return new Expression(var1, ToolTipManager.class, "sharedInstance", new Object[0]);
      }
   }

   static final class javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         DefaultMutableTreeNode var5 = (DefaultMutableTreeNode)var2;
         DefaultMutableTreeNode var6 = (DefaultMutableTreeNode)var3;

         for(int var7 = var6.getChildCount(); var7 < var5.getChildCount(); ++var7) {
            invokeStatement(var2, "add", new Object[]{var5.getChildAt(var7)}, var4);
         }

      }
   }

   static final class javax_swing_DefaultComboBoxModel_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         DefaultComboBoxModel var5 = (DefaultComboBoxModel)var2;

         for(int var6 = 0; var6 < var5.getSize(); ++var6) {
            invokeStatement(var2, "addElement", new Object[]{var5.getElementAt(var6)}, var4);
         }

      }
   }

   static final class javax_swing_DefaultListModel_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         DefaultListModel var5 = (DefaultListModel)var2;
         DefaultListModel var6 = (DefaultListModel)var3;

         for(int var7 = var6.getSize(); var7 < var5.getSize(); ++var7) {
            invokeStatement(var2, "add", new Object[]{var5.getElementAt(var7)}, var4);
         }

      }
   }

   static final class javax_swing_JFrame_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         Window var5 = (Window)var2;
         Window var6 = (Window)var3;
         boolean var7 = var5.isVisible();
         boolean var8 = var6.isVisible();
         if (var8 != var7) {
            boolean var9 = var4.executeStatements;
            var4.executeStatements = false;
            invokeStatement(var2, "setVisible", new Object[]{var7}, var4);
            var4.executeStatements = var9;
         }

      }
   }

   static final class java_awt_GridBagLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         if (getHashtable(var3).isEmpty()) {
            Iterator var5 = getHashtable(var2).entrySet().iterator();

            while(var5.hasNext()) {
               Map.Entry var6 = (Map.Entry)var5.next();
               Object[] var7 = new Object[]{var6.getKey(), var6.getValue()};
               invokeStatement(var2, "addLayoutComponent", var7, var4);
            }
         }

      }

      protected boolean mutatesTo(Object var1, Object var2) {
         return super.mutatesTo(var1, var2) && getHashtable(var2).isEmpty();
      }

      private static Hashtable<?, ?> getHashtable(Object var0) {
         return (Hashtable)MetaData.getPrivateFieldValue(var0, "java.awt.GridBagLayout.comptable");
      }
   }

   static final class java_awt_CardLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         if (getVector(var3).isEmpty()) {
            Iterator var5 = getVector(var2).iterator();

            while(var5.hasNext()) {
               Object var6 = var5.next();
               Object[] var7 = new Object[]{MetaData.getPrivateFieldValue(var6, "java.awt.CardLayout$Card.name"), MetaData.getPrivateFieldValue(var6, "java.awt.CardLayout$Card.comp")};
               invokeStatement(var2, "addLayoutComponent", var7, var4);
            }
         }

      }

      protected boolean mutatesTo(Object var1, Object var2) {
         return super.mutatesTo(var1, var2) && getVector(var2).isEmpty();
      }

      private static Vector<?> getVector(Object var0) {
         return (Vector)MetaData.getPrivateFieldValue(var0, "java.awt.CardLayout.vector");
      }
   }

   static final class java_awt_BorderLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
      private static final String[] CONSTRAINTS = new String[]{"North", "South", "East", "West", "Center", "First", "Last", "Before", "After"};

      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         BorderLayout var5 = (BorderLayout)var2;
         BorderLayout var6 = (BorderLayout)var3;
         String[] var7 = CONSTRAINTS;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            Component var11 = var5.getLayoutComponent(var10);
            Component var12 = var6.getLayoutComponent(var10);
            if (var11 != null && var12 == null) {
               invokeStatement(var2, "addLayoutComponent", new Object[]{var11, var10}, var4);
            }
         }

      }
   }

   static final class java_awt_List_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         List var5 = (List)var2;
         List var6 = (List)var3;

         for(int var7 = var6.getItemCount(); var7 < var5.getItemCount(); ++var7) {
            invokeStatement(var2, "add", new Object[]{var5.getItem(var7)}, var4);
         }

      }
   }

   static final class java_awt_MenuBar_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         MenuBar var5 = (MenuBar)var2;
         MenuBar var6 = (MenuBar)var3;

         for(int var7 = var6.getMenuCount(); var7 < var5.getMenuCount(); ++var7) {
            invokeStatement(var2, "add", new Object[]{var5.getMenu(var7)}, var4);
         }

      }
   }

   static final class java_awt_Menu_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         Menu var5 = (Menu)var2;
         Menu var6 = (Menu)var3;

         for(int var7 = var6.getItemCount(); var7 < var5.getItemCount(); ++var7) {
            invokeStatement(var2, "add", new Object[]{var5.getItem(var7)}, var4);
         }

      }
   }

   static final class java_awt_Choice_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         Choice var5 = (Choice)var2;
         Choice var6 = (Choice)var3;

         for(int var7 = var6.getItemCount(); var7 < var5.getItemCount(); ++var7) {
            invokeStatement(var2, "add", new Object[]{var5.getItem(var7)}, var4);
         }

      }
   }

   static final class java_awt_Container_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         if (!(var2 instanceof JScrollPane)) {
            Container var5 = (Container)var2;
            Component[] var6 = var5.getComponents();
            Container var7 = (Container)var3;
            Component[] var8 = var7 == null ? new Component[0] : var7.getComponents();
            BorderLayout var9 = var5.getLayout() instanceof BorderLayout ? (BorderLayout)var5.getLayout() : null;
            JLayeredPane var10 = var2 instanceof JLayeredPane ? (JLayeredPane)var2 : null;

            for(int var11 = var8.length; var11 < var6.length; ++var11) {
               Object[] var12 = var9 != null ? new Object[]{var6[var11], var9.getConstraints(var6[var11])} : (var10 != null ? new Object[]{var6[var11], var10.getLayer(var6[var11]), -1} : new Object[]{var6[var11]});
               invokeStatement(var2, "add", var12, var4);
            }

         }
      }
   }

   static final class java_awt_Component_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         super.initialize(var1, var2, var3, var4);
         Component var5 = (Component)var2;
         Component var6 = (Component)var3;
         if (!(var2 instanceof Window)) {
            Color var7 = var5.isBackgroundSet() ? var5.getBackground() : null;
            Color var8 = var6.isBackgroundSet() ? var6.getBackground() : null;
            if (!Objects.equals(var7, var8)) {
               invokeStatement(var2, "setBackground", new Object[]{var7}, var4);
            }

            Color var9 = var5.isForegroundSet() ? var5.getForeground() : null;
            Color var10 = var6.isForegroundSet() ? var6.getForeground() : null;
            if (!Objects.equals(var9, var10)) {
               invokeStatement(var2, "setForeground", new Object[]{var9}, var4);
            }

            Font var11 = var5.isFontSet() ? var5.getFont() : null;
            Font var12 = var6.isFontSet() ? var6.getFont() : null;
            if (!Objects.equals(var11, var12)) {
               invokeStatement(var2, "setFont", new Object[]{var11}, var4);
            }
         }

         Container var13 = var5.getParent();
         if (var13 == null || var13.getLayout() == null) {
            boolean var14 = var5.getLocation().equals(var6.getLocation());
            boolean var15 = var5.getSize().equals(var6.getSize());
            if (!var14 && !var15) {
               invokeStatement(var2, "setBounds", new Object[]{var5.getBounds()}, var4);
            } else if (!var14) {
               invokeStatement(var2, "setLocation", new Object[]{var5.getLocation()}, var4);
            } else if (!var15) {
               invokeStatement(var2, "setSize", new Object[]{var5.getSize()}, var4);
            }
         }

      }
   }

   static final class java_awt_MenuShortcut_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         MenuShortcut var3 = (MenuShortcut)var1;
         return new Expression(var1, var3.getClass(), "new", new Object[]{new Integer(var3.getKey()), var3.usesShiftModifier()});
      }
   }

   static final class java_awt_font_TextAttribute_PersistenceDelegate extends MetaData.StaticFieldsPersistenceDelegate {
   }

   static final class java_awt_SystemColor_PersistenceDelegate extends MetaData.StaticFieldsPersistenceDelegate {
   }

   static class StaticFieldsPersistenceDelegate extends PersistenceDelegate {
      protected void installFields(Encoder var1, Class<?> var2) {
         if (Modifier.isPublic(var2.getModifiers()) && ReflectUtil.isPackageAccessible(var2)) {
            Field[] var3 = var2.getFields();

            for(int var4 = 0; var4 < var3.length; ++var4) {
               Field var5 = var3[var4];
               if (Object.class.isAssignableFrom(var5.getType())) {
                  var1.writeExpression(new Expression(var5, "get", new Object[]{null}));
               }
            }
         }

      }

      protected Expression instantiate(Object var1, Encoder var2) {
         throw new RuntimeException("Unrecognized instance: " + var1);
      }

      public void writeObject(Object var1, Encoder var2) {
         if (var2.getAttribute(this) == null) {
            var2.setAttribute(this, Boolean.TRUE);
            this.installFields(var2, var1.getClass());
         }

         super.writeObject(var1, var2);
      }
   }

   static final class java_awt_AWTKeyStroke_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         AWTKeyStroke var3 = (AWTKeyStroke)var1;
         char var4 = var3.getKeyChar();
         int var5 = var3.getKeyCode();
         int var6 = var3.getModifiers();
         boolean var7 = var3.isOnKeyRelease();
         Object[] var8 = null;
         if (var4 == '\uffff') {
            var8 = !var7 ? new Object[]{var5, var6} : new Object[]{var5, var6, var7};
         } else if (var5 == 0) {
            if (!var7) {
               var8 = var6 == 0 ? new Object[]{var4} : new Object[]{var4, var6};
            } else if (var6 == 0) {
               var8 = new Object[]{var4, var7};
            }
         }

         if (var8 == null) {
            throw new IllegalStateException("Unsupported KeyStroke: " + var3);
         } else {
            Class var9 = var3.getClass();
            String var10 = var9.getName();
            int var11 = var10.lastIndexOf(46) + 1;
            if (var11 > 0) {
               var10 = var10.substring(var11);
            }

            return new Expression(var3, var9, "get" + var10, var8);
         }
      }
   }

   static final class java_awt_Font_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Font var3 = (Font)var1;
         int var4 = 0;
         String var5 = null;
         int var6 = 0;
         int var7 = 12;
         Map var8 = var3.getAttributes();
         HashMap var9 = new HashMap(var8.size());
         Iterator var10 = var8.keySet().iterator();

         while(var10.hasNext()) {
            TextAttribute var11 = (TextAttribute)var10.next();
            Object var12 = var8.get(var11);
            if (var12 != null) {
               var9.put(var11, var12);
            }

            if (var11 == TextAttribute.FAMILY) {
               if (var12 instanceof String) {
                  ++var4;
                  var5 = (String)var12;
               }
            } else if (var11 == TextAttribute.WEIGHT) {
               if (TextAttribute.WEIGHT_REGULAR.equals(var12)) {
                  ++var4;
               } else if (TextAttribute.WEIGHT_BOLD.equals(var12)) {
                  ++var4;
                  var6 |= 1;
               }
            } else if (var11 == TextAttribute.POSTURE) {
               if (TextAttribute.POSTURE_REGULAR.equals(var12)) {
                  ++var4;
               } else if (TextAttribute.POSTURE_OBLIQUE.equals(var12)) {
                  ++var4;
                  var6 |= 2;
               }
            } else if (var11 == TextAttribute.SIZE && var12 instanceof Number) {
               Number var13 = (Number)var12;
               var7 = var13.intValue();
               if ((float)var7 == var13.floatValue()) {
                  ++var4;
               }
            }
         }

         Class var14 = var3.getClass();
         if (var4 == var9.size()) {
            return new Expression(var3, var14, "new", new Object[]{var5, var6, var7});
         } else if (var14 == Font.class) {
            return new Expression(var3, var14, "getFont", new Object[]{var9});
         } else {
            return new Expression(var3, var14, "new", new Object[]{Font.getFont((Map)var9)});
         }
      }
   }

   static final class java_awt_Insets_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Insets var3 = (Insets)var1;
         Object[] var4 = new Object[]{var3.top, var3.left, var3.bottom, var3.right};
         return new Expression(var3, var3.getClass(), "new", var4);
      }
   }

   static final class java_beans_beancontext_BeanContextSupport_PersistenceDelegate extends MetaData.java_util_Collection_PersistenceDelegate {
   }

   static final class java_util_Hashtable_PersistenceDelegate extends MetaData.java_util_Map_PersistenceDelegate {
   }

   static final class java_util_AbstractMap_PersistenceDelegate extends MetaData.java_util_Map_PersistenceDelegate {
   }

   static final class java_util_AbstractList_PersistenceDelegate extends MetaData.java_util_List_PersistenceDelegate {
   }

   static final class java_util_AbstractCollection_PersistenceDelegate extends MetaData.java_util_Collection_PersistenceDelegate {
   }

   static class java_util_Map_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         Map var5 = (Map)var2;
         Map var6 = (Map)var3;
         if (var6 != null) {
            Object[] var7 = var6.keySet().toArray();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Object var10 = var7[var9];
               if (!var5.containsKey(var10)) {
                  invokeStatement(var2, "remove", new Object[]{var10}, var4);
               }
            }
         }

         Iterator var14 = var5.keySet().iterator();

         while(var14.hasNext()) {
            Object var15 = var14.next();
            Expression var16 = new Expression(var2, "get", new Object[]{var15});
            Expression var17 = new Expression(var3, "get", new Object[]{var15});

            try {
               Object var11 = var16.getValue();
               Object var12 = var17.getValue();
               var4.writeExpression(var16);
               if (!Objects.equals(var12, var4.get(var11))) {
                  invokeStatement(var2, "put", new Object[]{var15, var11}, var4);
               } else if (var12 == null && !var6.containsKey(var15)) {
                  invokeStatement(var2, "put", new Object[]{var15, var11}, var4);
               }
            } catch (Exception var13) {
               var4.getExceptionListener().exceptionThrown(var13);
            }
         }

      }
   }

   static class java_util_List_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         java.util.List var5 = (java.util.List)var2;
         java.util.List var6 = (java.util.List)var3;
         int var7 = var5.size();
         int var8 = var6 == null ? 0 : var6.size();
         if (var7 < var8) {
            invokeStatement(var2, "clear", new Object[0], var4);
            var8 = 0;
         }

         int var9;
         for(var9 = 0; var9 < var8; ++var9) {
            Integer var10 = new Integer(var9);
            Expression var11 = new Expression(var2, "get", new Object[]{var10});
            Expression var12 = new Expression(var3, "get", new Object[]{var10});

            try {
               Object var13 = var11.getValue();
               Object var14 = var12.getValue();
               var4.writeExpression(var11);
               if (!Objects.equals(var14, var4.get(var13))) {
                  invokeStatement(var2, "set", new Object[]{var10, var13}, var4);
               }
            } catch (Exception var15) {
               var4.getExceptionListener().exceptionThrown(var15);
            }
         }

         for(var9 = var8; var9 < var7; ++var9) {
            invokeStatement(var2, "add", new Object[]{var5.get(var9)}, var4);
         }

      }
   }

   static class java_util_Collection_PersistenceDelegate extends DefaultPersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         Collection var5 = (Collection)var2;
         Collection var6 = (Collection)var3;
         if (var6.size() != 0) {
            invokeStatement(var2, "clear", new Object[0], var4);
         }

         Iterator var7 = var5.iterator();

         while(var7.hasNext()) {
            invokeStatement(var2, "add", new Object[]{var7.next()}, var4);
         }

      }
   }

   static final class java_util_EnumSet_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return super.mutatesTo(var1, var2) && getType(var1) == getType(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         return new Expression(var1, EnumSet.class, "noneOf", new Object[]{getType(var1)});
      }

      private static Object getType(Object var0) {
         return MetaData.getPrivateFieldValue(var0, "java.util.EnumSet.elementType");
      }
   }

   static final class java_util_EnumMap_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return super.mutatesTo(var1, var2) && getType(var1) == getType(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         return new Expression(var1, EnumMap.class, "new", new Object[]{getType(var1)});
      }

      private static Object getType(Object var0) {
         return MetaData.getPrivateFieldValue(var0, "java.util.EnumMap.keyType");
      }
   }

   private abstract static class java_util_Collections extends PersistenceDelegate {
      private java_util_Collections() {
      }

      protected boolean mutatesTo(Object var1, Object var2) {
         if (!super.mutatesTo(var1, var2)) {
            return false;
         } else if (!(var1 instanceof java.util.List) && !(var1 instanceof Set) && !(var1 instanceof Map)) {
            Collection var3 = (Collection)var1;
            Collection var4 = (Collection)var2;
            return var3.size() == var4.size() && var3.containsAll(var4);
         } else {
            return var1.equals(var2);
         }
      }

      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
      }

      // $FF: synthetic method
      java_util_Collections(Object var1) {
         this();
      }

      static final class CheckedSortedMap_PersistenceDelegate extends MetaData.java_util_Collections {
         CheckedSortedMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Object var3 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedMap.keyType");
            Object var4 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedMap.valueType");
            TreeMap var5 = new TreeMap((SortedMap)var1);
            return new Expression(var1, Collections.class, "checkedSortedMap", new Object[]{var5, var3, var4});
         }
      }

      static final class CheckedMap_PersistenceDelegate extends MetaData.java_util_Collections {
         CheckedMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Object var3 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedMap.keyType");
            Object var4 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedMap.valueType");
            HashMap var5 = new HashMap((Map)var1);
            return new Expression(var1, Collections.class, "checkedMap", new Object[]{var5, var3, var4});
         }
      }

      static final class CheckedSortedSet_PersistenceDelegate extends MetaData.java_util_Collections {
         CheckedSortedSet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Object var3 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedCollection.type");
            TreeSet var4 = new TreeSet((SortedSet)var1);
            return new Expression(var1, Collections.class, "checkedSortedSet", new Object[]{var4, var3});
         }
      }

      static final class CheckedSet_PersistenceDelegate extends MetaData.java_util_Collections {
         CheckedSet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Object var3 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedCollection.type");
            HashSet var4 = new HashSet((Set)var1);
            return new Expression(var1, Collections.class, "checkedSet", new Object[]{var4, var3});
         }
      }

      static final class CheckedRandomAccessList_PersistenceDelegate extends MetaData.java_util_Collections {
         CheckedRandomAccessList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Object var3 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedCollection.type");
            ArrayList var4 = new ArrayList((Collection)var1);
            return new Expression(var1, Collections.class, "checkedList", new Object[]{var4, var3});
         }
      }

      static final class CheckedList_PersistenceDelegate extends MetaData.java_util_Collections {
         CheckedList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Object var3 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedCollection.type");
            LinkedList var4 = new LinkedList((Collection)var1);
            return new Expression(var1, Collections.class, "checkedList", new Object[]{var4, var3});
         }
      }

      static final class CheckedCollection_PersistenceDelegate extends MetaData.java_util_Collections {
         CheckedCollection_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Object var3 = MetaData.getPrivateFieldValue(var1, "java.util.Collections$CheckedCollection.type");
            ArrayList var4 = new ArrayList((Collection)var1);
            return new Expression(var1, Collections.class, "checkedCollection", new Object[]{var4, var3});
         }
      }

      static final class SynchronizedSortedMap_PersistenceDelegate extends MetaData.java_util_Collections {
         SynchronizedSortedMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            TreeMap var3 = new TreeMap((SortedMap)var1);
            return new Expression(var1, Collections.class, "synchronizedSortedMap", new Object[]{var3});
         }
      }

      static final class SynchronizedMap_PersistenceDelegate extends MetaData.java_util_Collections {
         SynchronizedMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            HashMap var3 = new HashMap((Map)var1);
            return new Expression(var1, Collections.class, "synchronizedMap", new Object[]{var3});
         }
      }

      static final class SynchronizedSortedSet_PersistenceDelegate extends MetaData.java_util_Collections {
         SynchronizedSortedSet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            TreeSet var3 = new TreeSet((SortedSet)var1);
            return new Expression(var1, Collections.class, "synchronizedSortedSet", new Object[]{var3});
         }
      }

      static final class SynchronizedSet_PersistenceDelegate extends MetaData.java_util_Collections {
         SynchronizedSet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            HashSet var3 = new HashSet((Set)var1);
            return new Expression(var1, Collections.class, "synchronizedSet", new Object[]{var3});
         }
      }

      static final class SynchronizedRandomAccessList_PersistenceDelegate extends MetaData.java_util_Collections {
         SynchronizedRandomAccessList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            ArrayList var3 = new ArrayList((Collection)var1);
            return new Expression(var1, Collections.class, "synchronizedList", new Object[]{var3});
         }
      }

      static final class SynchronizedList_PersistenceDelegate extends MetaData.java_util_Collections {
         SynchronizedList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            LinkedList var3 = new LinkedList((Collection)var1);
            return new Expression(var1, Collections.class, "synchronizedList", new Object[]{var3});
         }
      }

      static final class SynchronizedCollection_PersistenceDelegate extends MetaData.java_util_Collections {
         SynchronizedCollection_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            ArrayList var3 = new ArrayList((Collection)var1);
            return new Expression(var1, Collections.class, "synchronizedCollection", new Object[]{var3});
         }
      }

      static final class UnmodifiableSortedMap_PersistenceDelegate extends MetaData.java_util_Collections {
         UnmodifiableSortedMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            TreeMap var3 = new TreeMap((SortedMap)var1);
            return new Expression(var1, Collections.class, "unmodifiableSortedMap", new Object[]{var3});
         }
      }

      static final class UnmodifiableMap_PersistenceDelegate extends MetaData.java_util_Collections {
         UnmodifiableMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            HashMap var3 = new HashMap((Map)var1);
            return new Expression(var1, Collections.class, "unmodifiableMap", new Object[]{var3});
         }
      }

      static final class UnmodifiableSortedSet_PersistenceDelegate extends MetaData.java_util_Collections {
         UnmodifiableSortedSet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            TreeSet var3 = new TreeSet((SortedSet)var1);
            return new Expression(var1, Collections.class, "unmodifiableSortedSet", new Object[]{var3});
         }
      }

      static final class UnmodifiableSet_PersistenceDelegate extends MetaData.java_util_Collections {
         UnmodifiableSet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            HashSet var3 = new HashSet((Set)var1);
            return new Expression(var1, Collections.class, "unmodifiableSet", new Object[]{var3});
         }
      }

      static final class UnmodifiableRandomAccessList_PersistenceDelegate extends MetaData.java_util_Collections {
         UnmodifiableRandomAccessList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            ArrayList var3 = new ArrayList((Collection)var1);
            return new Expression(var1, Collections.class, "unmodifiableList", new Object[]{var3});
         }
      }

      static final class UnmodifiableList_PersistenceDelegate extends MetaData.java_util_Collections {
         UnmodifiableList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            LinkedList var3 = new LinkedList((Collection)var1);
            return new Expression(var1, Collections.class, "unmodifiableList", new Object[]{var3});
         }
      }

      static final class UnmodifiableCollection_PersistenceDelegate extends MetaData.java_util_Collections {
         UnmodifiableCollection_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            ArrayList var3 = new ArrayList((Collection)var1);
            return new Expression(var1, Collections.class, "unmodifiableCollection", new Object[]{var3});
         }
      }

      static final class SingletonMap_PersistenceDelegate extends MetaData.java_util_Collections {
         SingletonMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Map var3 = (Map)var1;
            Object var4 = var3.keySet().iterator().next();
            return new Expression(var1, Collections.class, "singletonMap", new Object[]{var4, var3.get(var4)});
         }
      }

      static final class SingletonSet_PersistenceDelegate extends MetaData.java_util_Collections {
         SingletonSet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            Set var3 = (Set)var1;
            return new Expression(var1, Collections.class, "singleton", new Object[]{var3.iterator().next()});
         }
      }

      static final class SingletonList_PersistenceDelegate extends MetaData.java_util_Collections {
         SingletonList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            java.util.List var3 = (java.util.List)var1;
            return new Expression(var1, Collections.class, "singletonList", new Object[]{var3.get(0)});
         }
      }

      static final class EmptyMap_PersistenceDelegate extends MetaData.java_util_Collections {
         EmptyMap_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            return new Expression(var1, Collections.class, "emptyMap", (Object[])null);
         }
      }

      static final class EmptySet_PersistenceDelegate extends MetaData.java_util_Collections {
         EmptySet_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            return new Expression(var1, Collections.class, "emptySet", (Object[])null);
         }
      }

      static final class EmptyList_PersistenceDelegate extends MetaData.java_util_Collections {
         EmptyList_PersistenceDelegate() {
            super(null);
         }

         protected Expression instantiate(Object var1, Encoder var2) {
            return new Expression(var1, Collections.class, "emptyList", (Object[])null);
         }
      }
   }

   static final class java_sql_Timestamp_PersistenceDelegate extends MetaData.java_util_Date_PersistenceDelegate {
      private static final Method getNanosMethod = getNanosMethod();

      private static Method getNanosMethod() {
         try {
            Class var0 = Class.forName("java.sql.Timestamp", true, (ClassLoader)null);
            return var0.getMethod("getNanos");
         } catch (ClassNotFoundException var1) {
            return null;
         } catch (NoSuchMethodException var2) {
            throw new AssertionError(var2);
         }
      }

      private static int getNanos(Object var0) {
         if (getNanosMethod == null) {
            throw new AssertionError("Should not get here");
         } else {
            try {
               return (Integer)getNanosMethod.invoke(var0);
            } catch (InvocationTargetException var3) {
               Throwable var2 = var3.getCause();
               if (var2 instanceof RuntimeException) {
                  throw (RuntimeException)var2;
               } else if (var2 instanceof Error) {
                  throw (Error)var2;
               } else {
                  throw new AssertionError(var3);
               }
            } catch (IllegalAccessException var4) {
               throw new AssertionError(var4);
            }
         }
      }

      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         int var5 = getNanos(var2);
         if (var5 != getNanos(var3)) {
            var4.writeStatement(new Statement(var2, "setNanos", new Object[]{var5}));
         }

      }
   }

   static class java_util_Date_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         if (!super.mutatesTo(var1, var2)) {
            return false;
         } else {
            Date var3 = (Date)var1;
            Date var4 = (Date)var2;
            return var3.getTime() == var4.getTime();
         }
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Date var3 = (Date)var1;
         return new Expression(var3, var3.getClass(), "new", new Object[]{var3.getTime()});
      }
   }

   static final class java_lang_reflect_Method_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Method var3 = (Method)var1;
         return new Expression(var1, var3.getDeclaringClass(), "getMethod", new Object[]{var3.getName(), var3.getParameterTypes()});
      }
   }

   static final class java_lang_reflect_Field_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Field var3 = (Field)var1;
         return new Expression(var1, var3.getDeclaringClass(), "getField", new Object[]{var3.getName()});
      }
   }

   static final class java_lang_Class_PersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Class var3 = (Class)var1;
         if (var3.isPrimitive()) {
            Field var7 = null;

            try {
               var7 = PrimitiveWrapperMap.getType(var3.getName()).getDeclaredField("TYPE");
            } catch (NoSuchFieldException var6) {
               System.err.println("Unknown primitive type: " + var3);
            }

            return new Expression(var1, var7, "get", new Object[]{null});
         } else if (var1 == String.class) {
            return new Expression(var1, "", "getClass", new Object[0]);
         } else if (var1 == Class.class) {
            return new Expression(var1, String.class, "getClass", new Object[0]);
         } else {
            Expression var4 = new Expression(var1, Class.class, "forName", new Object[]{var3.getName()});
            var4.loader = var3.getClassLoader();
            return var4;
         }
      }
   }

   static final class java_lang_String_PersistenceDelegate extends PersistenceDelegate {
      protected Expression instantiate(Object var1, Encoder var2) {
         return null;
      }

      public void writeObject(Object var1, Encoder var2) {
      }
   }

   static final class ProxyPersistenceDelegate extends PersistenceDelegate {
      protected Expression instantiate(Object var1, Encoder var2) {
         Class var3 = var1.getClass();
         Proxy var4 = (Proxy)var1;
         InvocationHandler var5 = Proxy.getInvocationHandler(var4);
         if (var5 instanceof EventHandler) {
            EventHandler var6 = (EventHandler)var5;
            Vector var7 = new Vector();
            var7.add(var3.getInterfaces()[0]);
            var7.add(var6.getTarget());
            var7.add(var6.getAction());
            if (var6.getEventPropertyName() != null) {
               var7.add(var6.getEventPropertyName());
            }

            if (var6.getListenerMethodName() != null) {
               var7.setSize(4);
               var7.add(var6.getListenerMethodName());
            }

            return new Expression(var1, EventHandler.class, "create", var7.toArray());
         } else {
            return new Expression(var1, Proxy.class, "newProxyInstance", new Object[]{var3.getClassLoader(), var3.getInterfaces(), var5});
         }
      }
   }

   static final class ArrayPersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var2 != null && var1.getClass() == var2.getClass() && Array.getLength(var1) == Array.getLength(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Class var3 = var1.getClass();
         return new Expression(var1, Array.class, "newInstance", new Object[]{var3.getComponentType(), new Integer(Array.getLength(var1))});
      }

      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
         int var5 = Array.getLength(var2);

         for(int var6 = 0; var6 < var5; ++var6) {
            Integer var7 = new Integer(var6);
            Expression var8 = new Expression(var2, "get", new Object[]{var7});
            Expression var9 = new Expression(var3, "get", new Object[]{var7});

            try {
               Object var10 = var8.getValue();
               Object var11 = var9.getValue();
               var4.writeExpression(var8);
               if (!Objects.equals(var11, var4.get(var10))) {
                  DefaultPersistenceDelegate.invokeStatement(var2, "set", new Object[]{var7, var10}, var4);
               }
            } catch (Exception var12) {
               var4.getExceptionListener().exceptionThrown(var12);
            }
         }

      }
   }

   static final class PrimitivePersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         return new Expression(var1, var1.getClass(), "new", new Object[]{var1.toString()});
      }
   }

   static final class EnumPersistenceDelegate extends PersistenceDelegate {
      protected boolean mutatesTo(Object var1, Object var2) {
         return var1 == var2;
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         Enum var3 = (Enum)var1;
         return new Expression(var3, Enum.class, "valueOf", new Object[]{var3.getDeclaringClass(), var3.name()});
      }
   }

   static final class NullPersistenceDelegate extends PersistenceDelegate {
      protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
      }

      protected Expression instantiate(Object var1, Encoder var2) {
         return null;
      }

      public void writeObject(Object var1, Encoder var2) {
      }
   }
}
