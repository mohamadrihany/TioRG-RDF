/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import java.util.ArrayList;

/**
 *
 * @author houk
 */
public class cart {
    
    /**
     *
     * @param sets
     */
    public static  ArrayList<ArrayList<SetSetReleventFragments.ModelNode>> Cart(ArrayList<ArrayList<SetSetReleventFragments.ModelNode>> sets2) {
    ArrayList<ArrayList<SetSetReleventFragments.ModelNode>> cartisienProductResult=new  ArrayList<ArrayList<SetSetReleventFragments.ModelNode>>();
   ArrayList<ArrayList<SetSetReleventFragments.ModelNode>> sets=new ArrayList<>();
    for(ArrayList<SetSetReleventFragments.ModelNode> set:sets2){
        if(!set.isEmpty())
        {
            sets.add(set);
        }
    } 
    
    int solutions = 1;
    for(int i = 0; i < sets.size(); solutions *= sets.get(i).size(), i++);
    for(int i = 0; i < solutions; i++) {
        ArrayList<SetSetReleventFragments.ModelNode> list = new ArrayList<SetSetReleventFragments.ModelNode>();
        int j = 1;
        for(ArrayList<SetSetReleventFragments.ModelNode> set : sets) {
            list.add(set.get((i/j)%set.size()));
         //   System.out.print(set.get((i/j)%set.size()).Node.toString() + " +++");
            j *= set.size();
        }
        cartisienProductResult.add(list);
     //   System.out.println("....");
      //    System.out.println("");
    }
        return cartisienProductResult;
}
//
//public static void main(String[] args) {
//        ArrayList<Integer> x = new ArrayList();
//         ArrayList<ArrayList<Integer>> set = new ArrayList();
//         ArrayList<Integer>  y = new ArrayList();
//          ArrayList<Integer>  z = new ArrayList();
//          x.add(1);
//          x.add(20);
//          y.add(3);
//          y.add(2);
//          y.add(0);
//          z.add(5);
//          set.add(x);
//          set.add(z);
//          set.add(y);
//    Cart(set);
//}
}


//
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package fr.search;
//
//import java.util.ArrayList;
//
///**
// *
// * @author houk
// */
//public class cart {
//    
//    /**
//     *
//     * @param sets
//     */
//    public static void Cart(ArrayList<ArrayList<Integer>> sets) {
//    int solutions = 1;
//    for(int i = 0; i < sets.size().length; solutions *= sets[i].length, i++);
//    for(int i = 0; i < solutions; i++) {
//        int j = 1;
//        for(int[] set : sets) {
//            System.out.print(set[(i/j)%set.length] + " ");
//            j *= set.length;
//        }
//        System.out.println();
//    }
//}
//
//public static void main(String[] args) {
//    
//    Cart(new int[][]{{1,20,1,2,3}, {3,2,10,11}, {5}});
//}}