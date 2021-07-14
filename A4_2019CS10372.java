import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map;

// java A4_2019CS10372 nodes.csv edges.csv average
// java A4_2019CS10372 nodes.csv edges.csv rank
// java A4_2019CS10372 nodes.csv edges.csv independent_storylines_dfs

public class A4_2019CS10372 {

	public static void main(String args[]) throws Exception {

		if(args.length != 3){
			println("Insufficient parameters from command line!!");
			return ;
		}


		/*File file = new File("myoutput_dfs.txt");
		PrintStream stream = new PrintStream(file);
		System.setOut(stream);*/


		String line = "";

		ArrayList<Node> nodeList = new ArrayList<Node>();
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		int V = 0;
		int E = 0;

		File nodeFile = new File(args[0]);
		File edgeFile = new File(args[1]);

		if(!nodeFile.exists()){
			println(nodeFile.getAbsolutePath() + " not found!!");
			return ;
		}

		if(!edgeFile.exists()){
			println(edgeFile.getAbsolutePath() + " not found!!");
			return ;
		}

		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		boolean headerLine = true;
		while ((line = br.readLine()) != null){
			if(headerLine == true){
				headerLine = false;
				continue;
			}
			ArrayList<String> nodeData = split(line);
			if(nodeData.size() != 2){
				println("Improper format in node file!");
				return ;
			}
			if(map.containsKey(nodeData.get(1)) == true){
				continue;
			}
			map.put(nodeData.get(1),V);
			nodeList.add(new Node(V,nodeData.get(1)));
			V++;
		}
		br.close();

		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>(V);
		for(int i=0;i<V;i++){
			adjacencyList.add(new ArrayList<Integer>());
		}

		br = new BufferedReader(new FileReader(args[1]));
		headerLine = true;  
		while ((line = br.readLine()) != null){
			if(headerLine == true){
				headerLine = false;
				continue;
			}
			ArrayList<String> edgeData = split(line);
			if(edgeData.size() != 3){
				println("Improper format in edge file!");
				return;
			}
			if(map.containsKey(edgeData.get(0)) == false){
				//println("Node corresponding to a source in edge file not found!!");
				//return;
				continue;
			}
			int x = map.get(edgeData.get(0));
			if(map.containsKey(edgeData.get(1)) == false){
				//println("Node corresponding to a target in edge file not found!!");
				//return;
				continue;
			}
			int y = map.get(edgeData.get(1));
			int u = min(x,y);
			int v = max(x,y);
			if(u!=v){
				Edge temp = new Edge(u,v);
				try{
					temp.weight = Integer.parseInt(edgeData.get(2));
				}
				catch(Exception e){
					println("Edge weight is not an integer!!");
					return ;
				}
				if(temp.weight <= 0){
					continue;
				}
				edgeList.add(temp);
			}
			//else{
				//println("Self loop exists!");
			//}
		}
		br.close();
		
		sort(edgeList);
		int n = edgeList.size();

		for(int i=0;i<n;i++){
			if(i!=0 && edgeList.get(i).equals(edgeList.get(i-1))==true){
				//if(i!=0 && edgeList.get(i).weight != edgeList.get(i-1).weight){
					//println("Multi edge detection!!");
					//return;
				//}
				continue;
			}
			adjacencyList.get(edgeList.get(i).u).add(edgeList.get(i).v);
			adjacencyList.get(edgeList.get(i).v).add(edgeList.get(i).u);
			nodeList.get(edgeList.get(i).u).rank += edgeList.get(i).weight;
			nodeList.get(edgeList.get(i).v).rank += edgeList.get(i).weight;
			E++;
		}


		if(args[2].equals("average")){
			double average = 2*E;
			if(V==0){
				average = 0.00;
			}
			else{
				average = average/V;
			}
			System.out.printf("%.2f",average);
			System.out.println("");
		}
		else if(args[2].equals("rank")){
			sort(nodeList);
			for(int i=0;i<V-1;i++){
				print(nodeList.get(i).label+",");
			}
			println(nodeList.get(V-1).label);
		}
		else if(args[2].equals("independent_storylines_dfs")){
			ArrayList<Integer> visited = new ArrayList<Integer>(V);
			for(int i=0;i<V;i++){
				visited.add(0);
			}
			ArrayList<Component> storyLine = new ArrayList<Component>();
			for(int i=0;i<V;i++){
				if(visited.get(i) == 0){
					ArrayList<String> grp = new ArrayList<String>();
					dfs(i,visited,adjacencyList,grp,nodeList);
					sort(grp);
					storyLine.add(new Component(grp.size(),grp));
				}
			}
			sort(storyLine);
			for(Component x: storyLine){
				n=x.size;
				for(int j=0;j<n-1;j++){
					print(x.grp.get(j)+",");
				}
				println(x.grp.get(n-1));
			}
		}
		//else{
			//println(args[2] + "function does not exists");
		//}

	}


	private static void dfs(int cur,ArrayList<Integer> visited,ArrayList<ArrayList<Integer>> adj,
		ArrayList<String> grp,ArrayList<Node> nodeList){
		visited.set(cur,1);
		grp.add(nodeList.get(cur).label);
		for(int x:adj.get(cur)){
			if(visited.get(x)==0){
				dfs(x,visited,adj,grp,nodeList);
			}
		}
	}

	private static int min(int x,int y){
		if(x<=y){
			return x;
		}
		return y;
	}

	private static int max(int x,int y){
		if(x>=y){
			return x;
		}
		return y;
	}

	private static void println(String msg){
		System.out.println(msg);
	}

	private static void print(String msg){
		System.out.print(msg);
	}

	private static ArrayList<String> split(String line){
		int open=0;
		int j=0;
		int n=line.length();
		boolean containsComma = false;
		ArrayList<String> result = new ArrayList<String>();

		for(int i=0;i<n;i++){
			if(line.charAt(i) == 34){
				if(open==0){
					open++;
				}
				else{
					open=0;
				}
			}
			else if(line.charAt(i) == ','){
				if(open==0){
					// startIndex: inclusive, endIndex: exculsive
					if(containsComma == true){
						//j+1,i-1
						result.add(line.substring(j+1,i-1));
						containsComma = false;
						j=i+1;
					}
					else{
						//j,i
						result.add(line.substring(j,i));
						j=i+1;
					}
				}
				else{
					containsComma = true;
				}
			}
		}
		if(containsComma == true){
			result.add(line.substring(j+1,n-1));
		}
		else{
			result.add(line.substring(j,n));
		}

		return result;
	}



	private static <T extends Comparable<T>> void sort(ArrayList<T> arr){
		int h=arr.size()-1;
		int l=0;
		mergeSort(arr,l,h);
	}

	private static <T extends Comparable<T>> void mergeSort(ArrayList<T> arr,int l,int h){
		if(l>=h){
			return ;
		}

		int m= l+(h-l)/2;

		mergeSort(arr,l,m);
		mergeSort(arr,m+1,h);

		merge(arr,l,m,h);
	}

	private static <T extends Comparable<T>> void merge(ArrayList<T> arr,int l,int m,int h){
		ArrayList<T> temp = new ArrayList<T>(h-l+1);
		int i=l;
		int j=m+1;
		while(i<=m && j<=h){
			if(arr.get(i).compareTo(arr.get(j)) >= 0){
				temp.add(arr.get(i));
				i++;
			}
			else{
				temp.add(arr.get(j));
				j++;
			}
		} 
		while(i<=m){
			temp.add(arr.get(i));
			i++;
		}
		while(j<=h){
			temp.add(arr.get(j));
			j++;
		}

		int k=l;
		for(T x: temp){
			arr.set(k,x);
			k++;
		}
	}



}


class Edge implements Comparable<Edge> {
	public int u;
	public int v;
	public long weight;
	public Edge(int x,int y){
		u=x;
		v=y;
	}


    public int compareTo(Edge t){
    	if(this.u==t.u && this.v==t.v){
    		return 0;
    	}
    	else if(this.u<t.u || (this.u==t.u && this.v<t.v)){
    		return -1;
    	}
    	else{
    		return 1;
    	}
    }
}

class Node implements Comparable<Node> {
	public int id;
	public String label;
	public long rank;

	public Node(int id,String label){
		this.id = id;
		this.label = label;
		rank=0;
	}

	public int compareTo(Node t){
		if(this.rank == t.rank && this.label.equals(t.label)==true){
			return 0;
		}
		if(this.rank < t.rank || (this.rank==t.rank && this.label.compareTo(t.label)<0)){
			return -1;
		}
		else{
			return 1;
		}
	}
}


class Component implements Comparable<Component> {
	public int size;
	public ArrayList<String> grp;

	public Component(int sz,ArrayList<String> comp){
		size=sz;
		grp=comp;
	}

	public int compareTo(Component p){
		if(this.size == p.size && this.grp.get(0).equals(p.grp.get(0))){
			return 0;
		}
		else if(this.size<p.size || (this.size==p.size && this.grp.get(0).compareTo(p.grp.get(0))<=0)){
			return -1;
		}
		else{
			return 1;
		}
	}
}