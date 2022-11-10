import java.util.HashSet;
import java.util.Set;

/**
 * This class provides static methods for performing normalization
 * 
 * @author <YOUR NAME>
 * @version <DATE>
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
    //first test if the given relation is already in BCNF
    if(isBCNF(rel, fdset)){
      Set<Set<String>> result = new HashSet<>();
      result.add(rel);
      return result;
    }
    // TODO - Identify a nontrivial FD that violates BCNF. Split the relation's
    // attributes using that FD, as seen in class.
    FDSet copy = new FDSet(fdset);
    Set<Set<String>> superkeys = findSuperkeys(rel, fdset);
    System.out.print("BCNF START \n" + "Current schema: " + rel + "\n" + "Current schema's superkeys = " + superkeys + "\n");
    
    for(FD fd : copy){
      if(!fd.isTrivial()){
        Set<String> alpha = fd.getLeft();
        Set<String> beta = fd.getRight();
        if(!superkeys.contains(alpha)){
          System.out.println(alpha);
        }
      }
    }



 
    // TODO - Redistribute the FDs in the closure of fdset to the two new
    // relations (R_Left and R_Right) as follows:
    //
    // Iterate through closure of the given set of FDs, then union all attributes
    // appearing in the FD, and test if the union is a subset of the R_Left (or
    // R_Right) relation. If so, then the FD gets added to the R_Left's (or R_Right's) FD
    // set. If the union is not a subset of either new relation, then the FD is
    // discarded

    // Repeat the above until all relations are in BCNF
    return null;
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
    FDSet copy = new FDSet(fdset);
    Set<Set<String>> superkeys = findSuperkeys(rel, fdset); 
    for(FD fd: copy){
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
    Set<String> result = new HashSet<>(attribute);
    int last_size;
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