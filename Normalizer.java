import java.util.HashSet;
import java.util.Set;

/**
 * This class provides static methods for performing normalization
 * 
 * @author Madison Sancehz-Forman
 * @version October 11, 2022
 */
public class Normalizer {

  /**
   * Performs BCNF decomposition
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return a set of relations (as attribute sets) that are in BCNF
   */
  public static Set<Set<String>> BCNFDecompose(Set<String> rel, FDSet fdset) {
    if(isBCNF(rel, fdset)){   //first test if the given relation is already in BCNF
      Set<Set<String>> result = new HashSet<>();
      result.add(rel);
      return result;
    }
    Set<Set<String>> superkeys = findSuperkeys(rel, fdset);
    //System.out.print("BCNF START\nCurrent schema: " + rel + "\n" + "Current schema's superkeys = " + Normalizer.findSuperkeys(rel, fdset) + "\n"); //uncomment for printing of recursive calls
    for(FD fd : fdset){
      if(!fd.isTrivial()){     //Identify a nontrivial FD that violates BCNF
        if(!superkeys.contains(fd.getLeft())){
          System.out.println("*** Splitting on " + fd + " ***");

          Set<String> R_left = split_left(fd); //finding the sets of attributes for new relations
          Set<String> R_right = split_right(fd, rel);

          FDSet F_left = new FDSet();   //new sets of functional dependencies
          FDSet F_right = new FDSet();
          FDSet F_closure = FDUtil.fdSetClosure(fdset);

          redistribute_FDs(F_closure, F_left, F_right, R_left, R_right); //handles redistribution of FDs

          System.out.println("\tLeft schema: " + R_left);
          System.out.println("\tLeft schema's superkeys: " + findSuperkeys(R_left, F_left));
          System.out.println("\tRight schema: " + R_right);
          System.out.println("\tRight schema's superkeys: " + findSuperkeys(R_right, F_right));
          
          System.out.println("\t\tCurrent schema: " + R_right);
          System.out.println("\t\tCurrent schema's superkeys: " + findSuperkeys(R_right, F_right));

          Set<Set<String>> result = new HashSet<>();
          result.addAll(BCNFDecompose(R_left, F_left));
          result.addAll(BCNFDecompose(R_right, F_right));
          return result;
        }
      } 
    }
    //if we made it here, R was already in bcnf
    Set<Set<String>> result = new HashSet<>();
    result.add(rel);
    return result;
  }
/**
 * Handles redistribution of functional dependencies of left and right relations after splitting 
 * @param F_closure FD set closure of original fdset
 * @param F_left  new currently blank set of functional dependencies for left of splitting
 * @param F_right new currently blank set of functional dependencies for right of splitting
 * @param R_left  relation found after split_left() was called 
 * @param R_right relation found after split_right() was called
 */
public static void redistribute_FDs(FDSet F_closure, FDSet F_left, FDSet F_right, Set<String> R_left, Set<String> R_right){
  for(FD fd : F_closure){
    Set<String> gamma = fd.getLeft();
    Set<String> sigma = fd.getRight();
    if(R_left.containsAll(gamma) && R_left.containsAll(sigma)){ // is (gamma U sigma) a strict subset of R_left
      F_left.add(new FD(gamma, sigma));
    }
    if(R_right.containsAll(gamma) && R_right.containsAll(sigma)){ //is (gamma U sigma) a strict subset of R_Right
      F_right.add(new FD(gamma, sigma));
    }
  }
}
/**
 * split left takes the violating FD and returns a new relation consiting of the FD's left and right attributes
 * @param violater violating fd
 * @return <Set<String>> of the FD's left and right attributes
 */
public static Set<String> split_left(FD violater){
  Set<String> R = new HashSet<>();
  R.addAll(violater.getLeft()); //alpha U beta (R)
  R.addAll(violater.getRight());
  return R;
}
/**
 * 
 * @param violater violating fd
 * @param rel set of original attributes in the relatoin
 * @return relation of the violating FD's split attributes
 */
public static Set<String> split_right(FD violater, Set<String> rel){
  Set<String> R = new HashSet<>();
  Set<String> temp = rel; //in order to remove beta
  R.addAll(violater.getLeft()); //alpha U with the rest of the relation minus beta
  temp.removeAll(violater.getRight());
  R.addAll(temp);
  return R;
}
  /**
   * Tests whether the given relation is in BCNF. A relation is in BCNF iff the
   * left-hand attribute set of all nontrivial FDs is a super key.
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return true if the relation is in BCNF with respect to the specified FD set
   */
  public static boolean isBCNF(Set<String> rel, FDSet fdset) {
    //a relational schema is in BCNF if the left-hand side of all non-trivial FDs is a superkey.
    Set<Set<String>> superkeys = findSuperkeys(rel, fdset); 
    for(FD fd: fdset){
      if(!fd.isTrivial()){ //only want non trivial fds
        if(!superkeys.contains(fd.getLeft())){ //if the left hand side of a non trivial FD is not a superkey then rel is not in BCNF
        return false;
      }
    }
  }
    return true; //if we make it out of the loop then it is in BCNF
}

  /**
   * This method returns a set of super keys
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return a set of super keys
   */
  public static Set<Set<String>> findSuperkeys(Set<String> rel, FDSet fdset) {
    //sanity check
    Set<String> attributes = new HashSet<String>();
    for(FD fd: fdset){
      attributes.addAll(fd.getLeft());
      attributes.addAll(fd.getRight());
      for(String str: attributes){
          if(!rel.contains(str)){ //if a string in attributes is not in rel, then this fd violated the sanity check 
            throw new IllegalArgumentException("FD refers to unknown attributes: " + str);
          }
      }
    }
    //algorithm to find super keys of a relation given a set of functional dependencies
    Set<Set<String>> powerset = FDUtil.powerSet(rel);
    Set<Set<String>> superkeys = new HashSet<Set<String>>();  
    //for each attribute in the powerset 
    for(Set<String> alpha : powerset){
      if(attribute_closure(alpha, fdset).equals(rel)){  //testing attribute closure of alpha
          superkeys.add(alpha);  //we have found a superkey
        }
    }
    return superkeys;
  }
/**
 * Finds the attribute closure of a set of attributes
 * @param attribute attribute to find closure of
 * @param fdset set of functional dependencies
 * @return set representing attribute closure of input attribute
 */
  public static Set<String> attribute_closure(Set<String> attribute, FDSet fdset){
    Set<String> result = new HashSet<>(attribute); //result of attribute closure starts with the original ste of attributes
    int last_size; //to store the size of result at previous iteration
    do{
      last_size = result.size();
      for(FD fd : fdset){
        Set<String> beta = fd.getLeft();
        Set<String> gamma = fd.getRight();
        if(result.containsAll(beta)){ //if beta is a subset of the attribute
          result.addAll(gamma); //result U gamma
        }
      }
    } while(result.size() != last_size); //until the result doesn't change

    return result;
  }

}