import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/*Version optimisée de l'algorithme de calcul de persistence via l'implémentation d'une 
 *d'une classe matrice sparse adaptée à la situation.
 * En outre, la réduction de la boundary BoundarySparse s'effectue en même temps que celle-ci est créée.
 * L'usage de différentes tables de hashage permet, outre le fait d'omettre de nombreux 0 
 * dans les colonnes, d'oublier des colonnes inutiles telles que celles correspondant aux sommets
 * et également de ne pas avoir à relire les intervalles de naissance et de mort 
 * pour supprimer les doublons. 
 * On retient également l'indice des pivots dans un HashMap pour améliorer (légèrement) la performance.
 */
public class SparseImplementation {
// 5 champs:
// -BoundarySparse retient pour chaque colonne(key) les indices non nuls(value).
// -IndexToSimplex permet d'accédéer à partir d'une colonne(key) au simplex correspondant(value).
// -SimplexToIndex effectue l'opération réciproque.
// -Pivots retient pour chaque colonne(key) non nulle, l'indice non nul le plus élevé. 
// -Barcodes permet de retenir les résultats de naissance(key) et de mort(value) lus sur la matrice réduite.
	
	HashMap<Integer, TreeSet<Integer>> BoundarySparse;
	HashMap<Integer, Simplex> IndexToSimplex;
	HashMap<TreeSet<Integer>, Integer> SimplexToIndex;
	HashMap<Integer, Integer> Pivots;
	HashMap<Integer, Integer> Barcodes;
	

private static void PivotGauss(TreeSet<Integer> CurrentColumn, TreeSet<Integer> PreviousColumn) {
//Modulo 2, cette fonction ajourne la valeur de CurrentColumn en ajoutant PreviousColumn.

	for (Integer i : PreviousColumn) {
		if (CurrentColumn.contains(i)) {
				CurrentColumn.remove(i);
		} else {
			CurrentColumn.add(i);
		}
	}
}
private void Reduction(TreeSet<Integer> CurrentColumn, int indexColumn) {

//Cette fonction réduit CurrentColumn via les colonnes précédentes.
//Elle s'occupe également de modifier la BoundarySparse en fonction du résultat.
		
	int pivot = CurrentColumn.last();

//Étape 1: réduire la colonne jusqu'à ce qu'elle soit vide ou que son pivot soit unique.
	
	while (Pivots.containsKey(pivot) && !CurrentColumn.isEmpty()){
		int pivotColumn = Pivots.get(pivot);
		PivotGauss(CurrentColumn, BoundarySparse.get(pivotColumn));
		if (!CurrentColumn.isEmpty()) 
			{pivot = CurrentColumn.last();}
		}
	
//Étape 2: Si la colonne résultante est vide, ouvrir un intervalle avec une mort non déterminée,
//par convention une mort "infini" est notée -1.
//Remarque : si cette colonne est "tuée" par une colonne ultérieure, cette valeur sera remplacée.
	
	if (CurrentColumn.isEmpty()) {
		Barcodes.put(indexColumn, -1);
		} 

//Étape 2bis: Si la colonne réduite présente un nouveau pivot, on retient ce pivot, on 
//ajourne la liste des barcode et on rajoute cette colonne à la BoundarySparse. 
	
	else {
		Barcodes.put(pivot, indexColumn);
		Pivots.put(pivot, indexColumn);
		BoundarySparse.put(indexColumn, CurrentColumn);
		}
	}

public SparseImplementation(Vector<Simplex> SimpComplex) {
	
// Le constructeur prend une liste de Simplex en entrée et construit la BoundarayMatrix
// colonne par colonne en réduisant celles-ci simultanément.
	
	int indexColumn = 0;
		
	BoundarySparse = new HashMap<Integer, TreeSet<Integer>>();
	Pivots = new HashMap<Integer, Integer>();
	Barcodes = new HashMap<Integer, Integer>();
	IndexToSimplex = new HashMap<Integer, Simplex>();
	SimplexToIndex = new HashMap<TreeSet<Integer>, Integer>();
	

	for (Simplex simplex : SimpComplex) {
		SimplexToIndex.put(simplex.vert, indexColumn);
		IndexToSimplex.put(indexColumn, simplex);
		
// For the boundary of a vertice is 0, we only consider the case of simplex.dim>0.
		
		if(simplex.dim==0){
			Barcodes.put(indexColumn, -1);}
		
		else{
			
//New column is the column being created using the current simplex. 
//CurrentBoundary is going trough all the available boundaries of the current simplex.
			
			TreeSet<Integer> NewColumn = new TreeSet<Integer>();
			TreeSet<Integer> CurrentBoundary;
			
			
			CurrentBoundary = new TreeSet<Integer>(simplex.vert);
			
// One should remove one by one the vertices in CurrentBoundary to find all the boundaries.
			
			for (Integer simplexInVert : simplex.vert) {
				CurrentBoundary.remove(simplexInVert);
				int IndiceBoundary = SimplexToIndex.get(CurrentBoundary);
				CurrentBoundary.add(simplexInVert);
				NewColumn.add(IndiceBoundary);
				}
			
			
			Reduction(NewColumn, indexColumn);
		}
		
		indexColumn++;
	}
}

	public void WriteBarcode(String File) {

// We iterate over Barcodes (generated by the constructor SparseImplementation
// And write the persistence in File.

		try {
			PrintWriter writer;
			writer = new PrintWriter(new FileWriter(File));
			
			for (Integer IndexCurrentBarcode : Barcodes.keySet()) {
				Simplex IndexCurrentBarcodeSimplex = IndexToSimplex.get(IndexCurrentBarcode);
				int dimension = IndexCurrentBarcodeSimplex.dim;
				Barcode CurrentBarcode;
				if (Barcodes.get(IndexCurrentBarcode)==-1){
					CurrentBarcode = new Barcode(dimension, IndexCurrentBarcodeSimplex.val, -1);
				}
				else{
					CurrentBarcode = new Barcode(IndexCurrentBarcodeSimplex.dim, IndexCurrentBarcodeSimplex.val, IndexToSimplex.get(Barcodes.get(IndexCurrentBarcode)).val);
				}
			
				if (CurrentBarcode.death!=-1){
					writer.println(CurrentBarcode.dim+" "+CurrentBarcode.birth+" "+CurrentBarcode.death);}
					else{writer.println(CurrentBarcode.dim+" "+CurrentBarcode.birth+" "+"inf");}					
				}
			
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

}





	// For testing purposes
public static void main(String[] args) {
	Vector<Simplex> F;
	long time= System.nanoTime();
	System.out.println(time);
	long time2= System.nanoTime();
	System.out.println(time2 - time);
	try {
		F = ReadFiltration.readFiltration("filtration_A.txt");   ///Choix de l'input
		F=BoundaryMatrix.sort(F);
		SparseImplementation sp = new SparseImplementation(F) ;
		sp.WriteBarcode("testSparseA.txt") ;   //Choix de l'output
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block   
		e.printStackTrace();
	}

	}

}