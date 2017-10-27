import java.util.Scanner;
import java.util.TreeSet;

public class Simplex implements Comparable<Simplex>{
	float val;
	int dim;
	TreeSet<Integer> vert;

	Simplex(Scanner sc){
		val = sc.nextFloat();
		dim = sc.nextInt();
		vert = new TreeSet<Integer>();
		for (int i=0; i<=dim; i++)
			vert.add(sc.nextInt());
	}

	public String toString(){
		return "{val="+val+"; dim="+dim+"; "+vert+"}\n";
	}
	public int compareTo(Simplex comparesimplex) {
		
	   if (this==comparesimplex){return 0;}
	   
	   int compareVal= Double.compare((double)this.val, (double)comparesimplex.val);

		if (compareVal>0) {
			return 1;
		}
		if (compareVal<0) {
			return -1;
		}
		if (compareVal==0) {
			return Double.compare((double)this.dim, (double)comparesimplex.dim);
		}
			
	
		return -1;
	}
	
}