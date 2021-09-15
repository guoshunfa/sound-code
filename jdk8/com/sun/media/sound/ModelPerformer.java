package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class ModelPerformer {
   private final List<ModelOscillator> oscillators = new ArrayList();
   private List<ModelConnectionBlock> connectionBlocks = new ArrayList();
   private int keyFrom = 0;
   private int keyTo = 127;
   private int velFrom = 0;
   private int velTo = 127;
   private int exclusiveClass = 0;
   private boolean releaseTrigger = false;
   private boolean selfNonExclusive = false;
   private Object userObject = null;
   private boolean addDefaultConnections = true;
   private String name = null;

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public List<ModelConnectionBlock> getConnectionBlocks() {
      return this.connectionBlocks;
   }

   public void setConnectionBlocks(List<ModelConnectionBlock> var1) {
      this.connectionBlocks = var1;
   }

   public List<ModelOscillator> getOscillators() {
      return this.oscillators;
   }

   public int getExclusiveClass() {
      return this.exclusiveClass;
   }

   public void setExclusiveClass(int var1) {
      this.exclusiveClass = var1;
   }

   public boolean isSelfNonExclusive() {
      return this.selfNonExclusive;
   }

   public void setSelfNonExclusive(boolean var1) {
      this.selfNonExclusive = var1;
   }

   public int getKeyFrom() {
      return this.keyFrom;
   }

   public void setKeyFrom(int var1) {
      this.keyFrom = var1;
   }

   public int getKeyTo() {
      return this.keyTo;
   }

   public void setKeyTo(int var1) {
      this.keyTo = var1;
   }

   public int getVelFrom() {
      return this.velFrom;
   }

   public void setVelFrom(int var1) {
      this.velFrom = var1;
   }

   public int getVelTo() {
      return this.velTo;
   }

   public void setVelTo(int var1) {
      this.velTo = var1;
   }

   public boolean isReleaseTriggered() {
      return this.releaseTrigger;
   }

   public void setReleaseTriggered(boolean var1) {
      this.releaseTrigger = var1;
   }

   public Object getUserObject() {
      return this.userObject;
   }

   public void setUserObject(Object var1) {
      this.userObject = var1;
   }

   public boolean isDefaultConnectionsEnabled() {
      return this.addDefaultConnections;
   }

   public void setDefaultConnectionsEnabled(boolean var1) {
      this.addDefaultConnections = var1;
   }
}
