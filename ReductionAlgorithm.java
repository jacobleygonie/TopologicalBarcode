import java.awt.List;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public  class ReductionAlgorithm {
	
	public static int low (int [] colonne) {
//Récupère le pivot de la colonne en considérant des nombres modulo 2.
		int n = colonne.length;
		int i = n-1;
		while(i>=0&& colonne[i]%2==0) {
			i=i-1;
		}
		return i;
	}
	
	public static int[] GetColumn(int j, int[][] matrix){
//Fonction utilitaire pour récupérer la j-ème colonne de la matrice matrix.
		int n=matrix.length;
		int[] res= new int[n];
		for (int i=0;i<n;i++){
			res[i]=matrix[i][j];
		}
		return res;
		}
	
	public static int[][] PivotGauss(int[][] initial){
		int n = initial[0].length;
		int l = initial.length;
		int [][] result = new int[l][n];
		
		for (int i =1;i<n;i++) {
			int [] colonnei = GetColumn(i, initial);
			int lowi=low(colonnei);
			
			if (lowi!=-1 ) { 
				int j=i-1;
				while(lowi!=-1 && j!=-1) {
					int [] colonnej = GetColumn(j, result);
				
					int lowj=low(colonnej);
					if(lowi==lowj){
						
						for (int m =0;m<n;m++) {     
							colonnei[m]=colonnei[m] + colonnej[m] ;}
						lowi=low(colonnei);
						j=i-1;
					}
					else{j=j-1;}
			}
			} 
				
			for (int ligne =0;ligne<l;ligne++) {
				result[ligne][i]= colonnei[ligne];
					}
			
				}
			
		
		
/* Fait : Nous garantissons, avec la méthode ci-dessus, une complexité en O(n^3).
 * 
 * Preuve de complexité: Il s'agit pour chaque colonne i, d'inspecter les colonnes inférieures 
 * tant que le pivot de la colonne courante est non nulle et qu'il existe une autre colonne de pivot identique.
 * Chaque colonne admet i-1<n colonnes inférieures sur lesquelles on itère. 
 * Si low(i)==low(j) avec j<i, on ajoute la colonne j à la colonne i,
 * ce qui revient modulo 2 à annuler le coefficient M(low(i),i).
 * On garantit qu'à chaque opération de réduction, la colonne diminue son pivot,
 * si bien qu'on utilise une colonne pour réduire la colonne courante au plus une fois.
 * En conséquence, il y a au plus (i-1) inspections des (i-1) colonnes précédentes. L'appel à low
 * pour trouver le pivot s'effectue en temps linéraire, ainsi que l'opération de réduction entre deux colonnes.
 * Au total, on effectue O(n^2) opérations pour réduire complètement une colonne.
 * La complexité de réduction totale de la BoundaryMatrix est donc O(n^3).
 *
 * */
				
		System.out.println(" ---------------- Reduced Matrix mod 2 --------------------");
		for(int i=0;i<l;i++){
			for(int j=0; j<n ;j++){
			 System.out.print(result[i][j]+ " ");
			}
			System.out.println(" ");
		}
		return result;
	}

	
	public static void ComputeBarcode(String Input, String Output) {
		
/*Cette fonction fait appel à toutes les précédentes:
 * 1) Lecture de Input via ReadFiltration.
 * 2) Création de la BoundaryMatrix B et tri des simplexes selon la filtration.
 * 3) Réduction de la matrice via l'appel à PivotGauss.
 * 4) Création de la liste des barcodes, -barcodes-.
 * 5) Écriture des résultats dans le fichier OutPut.
 * */

		Vector<Simplex> F=new Vector<Simplex>();
		try {
			F = ReadFiltration.readFiltration (Input);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Vector<Barcode> barcodes = new Vector<Barcode>();
		BoundaryMatrix B= new BoundaryMatrix(F);
		System.out.println("-----------------Sorted simplices-------------------");
		System.out.println(B.sortedsimplices);
		int [][] initial =  B.creatematrix ();
		int[][] matrice_finale = PivotGauss(initial);
		
		int n_colonnes = matrice_finale[0].length;
		for (int j =0;j<n_colonnes;j++) {
			int lowj=low(GetColumn(j,matrice_finale));
			if (lowj==-1){
				Simplex current=B.sortedsimplices.get(j);
				barcodes.addElement(new Barcode(current.dim,current.val,-1));   //-1 means it never dies. (inf)
			}
			else{
				Simplex current=B.sortedsimplices.get(j);
				Simplex ante =B.sortedsimplices.get(lowj);
				barcodes.addElement(new Barcode(ante.dim,ante.val,current.val));
				if (barcodes.get(lowj).death==-1){
					barcodes.get(lowj).death=-2;  // -2 means no role of the previous zero column lowj since it has been killed after by column j.	
				}
			}
			PrintWriter writer;
			try {
				writer = new PrintWriter(new FileWriter(Output));
				int n=barcodes.size();
				
				for(int i=0;i<n;i++){
					Barcode current=barcodes.get(i);
					if (current.death!=-2){
						if (current.death!=-1){
						writer.println(current.dim+" "+current.birth+" "+current.death);}
						else{writer.println(current.dim+" "+current.birth+" "+"inf");}
					}
					
				}
				writer.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	public static void main(String[] args) {
		ComputeBarcode("filtration_A.txt","output1");
		
	}
}
