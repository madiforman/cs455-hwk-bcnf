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
    // TODO - First test if the given relation is already in BCNF with respect to
    // the provided FD set.

    // TODO - Identify a nontrivial FD that violates BCNF. Split the relation's
    // attributes using that FD, as seen in class.

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
    //Recall that a relational schema is in BCNF if the left-hand side of all non-trivial FDs is a superkey.
   // FDSet copy = new FDSet(fdset);
    FDSet copy = new FDSet(fdset);
    Set<Set<String>> superkeys = findSuperkeys(rel, fdset);
    copy.remove_all(FDUtil.trivial(fdset)); //set difference
    for(FD fd: copy){
      Set<String> left = fd.getLeft();
      if(!superkeys.contains(left)){
        return false;
      }

    }

    return true;
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
    Set<String> attributes = new HashSet<>();
    for(FD fd: fdset){
      attributes.addAll(fd.getLeft());
      attributes.addAll(fd.getRight());
      for(String str: attributes){
        if(!rel.contains(str)){ //if a string in attributes is not in rel, then this fd violated the sanity check 
          throw new IllegalArgumentException("FD refers to unknown attributes: " + fd);
        }
      }
    }
    //algorithm to find super keys of a relation given a set of functional dependencies
    Set<Set<String>> superkeys = new HashSet<>();  
    Set<Set<String>> power_set = FDUtil.powerSet(attributes);

    for(Set<String> alpha : power_set){
      if(!alpha.isEmpty()){ //skipping empty set from power set
      Set<String> alpha_closure = attribute_closure(alpha, fdset);
        if(alpha_closure.equals(rel)){  //testing attribute closure of alpha
          superkeys.add(alpha);  //we have found a superkey

        }
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
        if(result.containsAll(beta)){ //if the left side of the fd is a subset of the attribute
          result.addAll(gamma); //then add the right size
        }
      }
    }while(result.size()!=last_size); //until the result doesn't change
    return result;
  }
}