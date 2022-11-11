import java.util.HashSet;
import java.util.Set;

/**
 * This class provides static methods for performing normalization
 * 
 * @author Madison Sancehz-Forman
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
    //Identify a nontrivial FD that violates BCNF. Split the relation's attributes using that FD, as seen in class.
    FDSet copy = new FDSet(fdset);
    Set<Set<String>> superkeys = findSuperkeys(rel, fdset);
    System.out.print("BCNF START \n" + "Current schema: " + rel + "\n" + "Current schema's superkeys = " + superkeys + "\n");
    for(FD fd : copy){
      if(!fd.isTrivial()){
        if(!superkeys.contains(fd.getLeft())){
          System.out.println("*** Splitting on " + fd + " ***");

          Set<String> R_left = split_left(fd);
          Set<String> R_right = split_right(fd, rel);

          FDSet F_left = new FDSet();
          FDSet F_right = new FDSet();
          FDSet F_closure = FDUtil.fdSetClosure(fdset);
          
          for(FD redistribute : F_closure){
            Set<String> gamma = redistribute.getLeft();
            Set<String> sigma = redistribute.getRight();
            if(R_left.containsAll(gamma) && R_left.containsAll(sigma)){
              FD temp = new FD(gamma, sigma);
              F_left.add(temp);
            }
            if(R_right.containsAll(gamma) && R_right.containsAll(sigma)){
              FD temp = new FD(gamma, sigma);
              F_right.add(temp);
            }

          }
          System.out.println("Left schema: " + R_left);
          System.out.println("Left schema's superkeys: " + findSuperkeys(R_left, F_left));
          System.out.println("Right schema: " + R_right);
          System.out.println("Right schema's superkeys: " + findSuperkeys(R_right, F_right));
          
          System.out.println("\tCurrent schema: " + R_right);
          System.out.println("\tCurrent schema's superkeys: " + findSuperkeys(R_right, F_right));

          Set<Set<String>> L = BCNFDecompose(R_left, F_left);
          Set<Set<String>> R = BCNFDecompose(R_right, F_right);
          Set<Set<String>> result = new HashSet<>();
          result.addAll(L);
          result.addAll(R);
          return result;
        }
      } 
    }
    Set<Set<String>> result = new HashSet<>();
    result.add(rel);
    return result;
  }

public static Set<String> split_left(FD violater){
  Set<String> R = new HashSet<>();
  for(String str: violater.getLeft()){
    R.add(str);
  }
  for(String str: violater.getRight()){
    R.add(str);
  }
  return R;
}
public static Set<String> split_right(FD violater, Set<String> rel){
  Set<String> R = new HashSet<>();
  for(String str : violater.getLeft()){
    R.add(str);
  }
  for(String str : violater.getRight()){
    rel.remove(str);
  }
  for(String str: rel){
    R.add(str);
  }
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