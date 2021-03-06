package com.drin.java.clustering;

import java.util.Collection;

public abstract class Clusterable<E> {
   protected String mName;
   protected Collection<E> mData;

   protected String[] mMetaLabels;

   public Clusterable(String name, Collection<E> data) {
      mName = name;
      mData = data;
      mMetaLabels = null;
   }

   @Override
   public String toString() { return mName; }

   @Override
   public int hashCode() { return mName.hashCode(); }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Clusterable) {
         return mName.equals(((Clusterable<?>)obj).getName());
      }

      return false;
   }

   public void setMetaData(String[] metaLabels) { mMetaLabels = metaLabels; }
   public String[] getMetaData() { return mMetaLabels; }

   public int size() { return mData.size(); }
   public String getName() { return mName; }
   public Collection<E> getData() { return mData; }

   public abstract float compareTo(Clusterable<?> otherData);
   public abstract Clusterable<E> deepCopy();
}
