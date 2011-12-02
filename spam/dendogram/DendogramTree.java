package spam.dendogram;

import spam.dendogram.TreeNode;

import spam.dataTypes.Isolate;
import spam.dataTypes.Cluster;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DendogramTree {
   private List<TreeNode> dendogramNodes;

   public DendogramTree() {
      dendogramNodes = new ArrayList<TreeNode>();
   }

   public void addIsolateList(List<Isolate> isolateList) {
      TreeNode treeNode = new TreeNode();

      for (Isolate isolate : isolateList) {
         treeNode.addIsolate(isolate);
      }

      dendogramNodes.add(treeNode);
   }

   public List<TreeNode> getTree() {
      return dendogramNodes;
   }
}
