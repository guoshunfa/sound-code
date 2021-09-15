package javax.swing;

import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.Set;

final class DelegatingDefaultFocusManager extends DefaultFocusManager {
   private final KeyboardFocusManager delegate;

   DelegatingDefaultFocusManager(KeyboardFocusManager var1) {
      this.delegate = var1;
      this.setDefaultFocusTraversalPolicy(this.gluePolicy);
   }

   KeyboardFocusManager getDelegate() {
      return this.delegate;
   }

   public void processKeyEvent(Component var1, KeyEvent var2) {
      this.delegate.processKeyEvent(var1, var2);
   }

   public void focusNextComponent(Component var1) {
      this.delegate.focusNextComponent(var1);
   }

   public void focusPreviousComponent(Component var1) {
      this.delegate.focusPreviousComponent(var1);
   }

   public Component getFocusOwner() {
      return this.delegate.getFocusOwner();
   }

   public void clearGlobalFocusOwner() {
      this.delegate.clearGlobalFocusOwner();
   }

   public Component getPermanentFocusOwner() {
      return this.delegate.getPermanentFocusOwner();
   }

   public Window getFocusedWindow() {
      return this.delegate.getFocusedWindow();
   }

   public Window getActiveWindow() {
      return this.delegate.getActiveWindow();
   }

   public FocusTraversalPolicy getDefaultFocusTraversalPolicy() {
      return this.delegate.getDefaultFocusTraversalPolicy();
   }

   public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy var1) {
      if (this.delegate != null) {
         this.delegate.setDefaultFocusTraversalPolicy(var1);
      }

   }

   public void setDefaultFocusTraversalKeys(int var1, Set<? extends AWTKeyStroke> var2) {
      this.delegate.setDefaultFocusTraversalKeys(var1, var2);
   }

   public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int var1) {
      return this.delegate.getDefaultFocusTraversalKeys(var1);
   }

   public Container getCurrentFocusCycleRoot() {
      return this.delegate.getCurrentFocusCycleRoot();
   }

   public void setGlobalCurrentFocusCycleRoot(Container var1) {
      this.delegate.setGlobalCurrentFocusCycleRoot(var1);
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      this.delegate.addPropertyChangeListener(var1);
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      this.delegate.removePropertyChangeListener(var1);
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.delegate.addPropertyChangeListener(var1, var2);
   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.delegate.removePropertyChangeListener(var1, var2);
   }

   public void addVetoableChangeListener(VetoableChangeListener var1) {
      this.delegate.addVetoableChangeListener(var1);
   }

   public void removeVetoableChangeListener(VetoableChangeListener var1) {
      this.delegate.removeVetoableChangeListener(var1);
   }

   public void addVetoableChangeListener(String var1, VetoableChangeListener var2) {
      this.delegate.addVetoableChangeListener(var1, var2);
   }

   public void removeVetoableChangeListener(String var1, VetoableChangeListener var2) {
      this.delegate.removeVetoableChangeListener(var1, var2);
   }

   public void addKeyEventDispatcher(KeyEventDispatcher var1) {
      this.delegate.addKeyEventDispatcher(var1);
   }

   public void removeKeyEventDispatcher(KeyEventDispatcher var1) {
      this.delegate.removeKeyEventDispatcher(var1);
   }

   public boolean dispatchEvent(AWTEvent var1) {
      return this.delegate.dispatchEvent(var1);
   }

   public boolean dispatchKeyEvent(KeyEvent var1) {
      return this.delegate.dispatchKeyEvent(var1);
   }

   public void upFocusCycle(Component var1) {
      this.delegate.upFocusCycle(var1);
   }

   public void downFocusCycle(Container var1) {
      this.delegate.downFocusCycle(var1);
   }
}
