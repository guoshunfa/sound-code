package com.sun.java.swing.plaf.windows;

import javax.swing.JMenuItem;

interface WindowsMenuItemUIAccessor {
   JMenuItem getMenuItem();

   TMSchema.State getState(JMenuItem var1);

   TMSchema.Part getPart(JMenuItem var1);
}
