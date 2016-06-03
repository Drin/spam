package com.drin.clustering.dendogram;

import com.drin.clustering.Clusterable;
import com.drin.clustering.dendogram.Dendogram;
import com.drin.clustering.dendogram.DendogramNode;

//TODO: toString() and pp() should print out the type of the element instead of
//just isolate
public class DendogramLeaf implements Dendogram {
   private Clusterable<?> mElement;

   public DendogramLeaf(Clusterable<?> element) { mElement = element; }

   public Clusterable<?> getData() { return mElement; }

   @Override
   public String toString() {
      return String.format("<Isolate name=\"%s\"/>\n", mElement.getName());
   }

   public String pp(String prefix) {
      return String.format("%s<Isolate name=\"%s\"/>\n", prefix,
                           mElement.getName());
   }
}