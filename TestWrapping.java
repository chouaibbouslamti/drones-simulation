public class TestWrapping {
    public static void main(String[] args) {
        int coord = 5;
        int max = 5;
        int result = ((coord % max) + max) % max;
        System.out.println("coord=" + coord + ", max=" + max + ", result=" + result);
        
        coord = 4;
        result = ((coord % max) + max) % max;
        System.out.println("coord=" + coord + ", max=" + max + ", result=" + result);
    }
}
