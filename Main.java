import java.util.*;

public class Main {
  public static void main(String[] args) {
    /*
     * Examples from the Theory (Part II) slides (page 29)
     */
    Set<String> S = new HashSet<>(Arrays.asList("A", "B", "C", "D")); // Relation S(A,B,C,D)
    FD s1 = new FD(Arrays.asList("A"), Arrays.asList("B")); // A --> B
    FD s2 = new FD(Arrays.asList("B"), Arrays.asList("C")); // B --> C
    FDSet fdsetS = new FDSet(s1, s2);
    System.out.println(Normalizer.BCNFDecompose(S, fdsetS));
  }
}
