import java.util.*;

public class Main {
  public static void main(String[] args) {
    /*
     * Examples from the Theory (Part II) slides (page 29)
     */

    // U(A,B,C,D,E)
    // FD f1 = new FD(Arrays.asList("A"), Arrays.asList("B"));
    // FD f2 = new FD(Arrays.asList("B"), Arrays.asList("D"));
    // FD f3 = new FD(Arrays.asList("C"), Arrays.asList("B"));
    // FDSet fdset = new FDSet(f1, f2, f3);
    // System.out.println(fdset);
    // Set<String> attributes = new HashSet<>();
    // attributes.add("A");
    // System.out.println("*********");
    // System.out.println(Normalizer.attribute_closure(attributes, fdset));
    FD f1 = new FD(Arrays.asList("ssn"), Arrays.asList("name")); // ssn --> name
    FD f2 = new FD(Arrays.asList("ssn", "name"), Arrays.asList("eyecolor")); // ssn,name --> eyecolor
    FDSet fdset = new FDSet(f1, f2);
   
    Set<String> people = new HashSet<>(Arrays.asList("ssn", "name", "eyecolor")); // Relation people(ssn,name,eyecolor)
    System.out.println("BCNF? " + Normalizer.isBCNF(people, fdset));
    // FD f1 = new FD(Arrays.asList("ssn"), Arrays.asList("name")); // ssn --> name
    // FD f2 = new FD(Arrays.asList("ssn"), Arrays.asList("eyecolor")); // ssn --> eyecolor
    // FDSet fdset = new FDSet(f1, f2);
   
    // Set<String> people = new HashSet<>(Arrays.asList("ssn", "name")); // Relation people(ssn, name)
    // System.out.println("Superkeys: " + Normalizer.findSuperkeys(people, fdset));
    //System.out.println("Superkeys: " + Normalizer.findSuperkeys(people, fdset));
    // Set<String> U = new HashSet<>(Arrays.asList("A", "B", "C", "D", "E"));
    // FD f1 = new FD(Arrays.asList("A", "E"), Arrays.asList("D")); // AE --> D
    // FD f2 = new FD(Arrays.asList("A", "B"), Arrays.asList("C")); // AB --> C
    // FD f3 = new FD(Arrays.asList("D"), Arrays.asList("B")); // D --> B
    // FDSet fdsetU = new FDSet(f1, f2, f3);
    // System.out.println("Final BCNF Schemas: " + Normalizer.BCNFDecompose(U, fdsetU));

    // // R(A,B,C)
    // Set<String> R = new HashSet<>(Arrays.asList("A", "xwB", "C"));
    // FD g1 = new FD(Arrays.asList("A"), Arrays.asList("B", "C")); // A --> BC
    // FD g2 = new FD(Arrays.asList("B"), Arrays.asList("C")); // B --> C
    // FD g3 = new FD(Arrays.asList("A"), Arrays.asList("B")); // A --> B
    // FD g4 = new FD(Arrays.asList("A", "B"), Arrays.asList("C")); // AB --> C
    // FDSet fdsetR = new FDSet(g1, g2, g3, g4);
    // System.out.println("Final BCNF Schemas: " + Normalizer.BCNFDecompose(R, fdsetR));
  }
}
