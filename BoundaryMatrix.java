import java.util.Vector;


import java.util.Collections;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Iterator;

public class BoundaryMatrix {
	
	Vector<Simplex> sortedsimplices;
	
//On trie les simplexes selon la filtration en premier lieu.
	public static Vector<Simplex> sort(Vector<Simplex> F) {
		Collections.sort(F);
		return F;
		
	}
	
	BoundaryMatrix(Vector<Simplex> simplices){
		this.sortedsimplices=sort(simplices);		
	}
	
	public int[][] creatematrix () {
//Génère la matrice à partir de la liste des simplexes triée selon la filtration.
		int n = sortedsimplices.size();
		int[][] result = new int[n][n];
		
		HashMap<TreeSet<Integer>,Integer> indicesimplex = new HashMap<TreeSet<Integer>,Integer>();
		// Le hashmap indicesimplex permet rapidement de récupérer à quelle colonne correspond tel simplex.
		for (int i =0;i<n;i++) {
			TreeSet<Integer> vert = sortedsimplices.get(i).vert;
			indicesimplex.put(vert, i); //On alloue une nouvelle colonne, i, pour le simplexe courrant.
			int m = vert.size();
			if (m!=1){ //Si c'est un sommet, rien à faire.
				Iterator<Integer> itr=vert.iterator();
				Integer first=vert.first();
				Integer last=vert.last();
				while(itr.hasNext()){ 
				//On retire tous les vertices un à un pour trouver toutes les frontières du simplexe courant.
				    Integer jcurrent=itr.next();
				    TreeSet<Integer> current = new TreeSet<Integer>();
				    if(jcurrent!=first && jcurrent!=last){
					TreeSet<Integer> current1 = (TreeSet<Integer>) vert.subSet(first,true, jcurrent,false);
					TreeSet<Integer> current2 = (TreeSet<Integer>) vert.subSet(jcurrent,false,last,true);
					current.addAll(current1);
					current.addAll(current2);
				    }
				    //Deux cas limites: on enlève le premier ou le dernier sommet.
				    if(jcurrent==first){
				    	current= (TreeSet<Integer>) vert.subSet(jcurrent,false,last,true);
				    }
				    if(jcurrent==last){
				    	current=(TreeSet<Integer>) vert.subSet(first,true, jcurrent,false);
				    }
				    //On ajourne la valeur de l'application boundary, en ce qui concerne la colonne i
				    //en indiquant qu'une de ses frontières est "current".
					result[indicesimplex.get(current)][i] = 1;
					
				}
			}		
			
		}
		//Montrer le résultat (à examiner modulo 2).
		System.out.println(" ---------------- Boundary Matrix  --------------------");
		for(int i=0;i<n;i++){
			for(int j=0; j<n ;j++){
			 System.out.print(result[i][j]+ " ");
			}
			System.out.println(" ");
		}
				
		return result;
	}
	
}
