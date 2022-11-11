import java.util.*;

public class Main {
  public static void main(String[] args) {
    /*
     * Examples from the Theory (Part II) slides (page 29)
     */
    Set<String> U = new HashSet<>(Arrays.asList("A", "B", "C", "D", "E"));  // Relation U(A,B,C,D,E)
    FD f1 = new FD(Arrays.asList("A", "E"), Arrays.asList("D")); // AE --> D
    FD f2 = new FD(Arrays.asList("A", "B"), Arrays.asList("C")); // AB --> C
    FD f3 = new FD(Arrays.asList("D"), Arrays.asList("B")); // D --> B
    FDSet fdsetU = new FDSet(f1, f2, f3);
    System.out.println("Final BCNF Schemas: " + Normalizer.BCNFDecompose(U, fdsetU));
  }
}
