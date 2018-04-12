import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
public class ScopeAnalyzer
{
	// LinkedList<ParseNode> nodes = new LinkedList<ParseNode>();
	SemanticTable st = new SemanticTable();

	public static void main(String[] args)
	{
		ScopeAnalyzer sa = new ScopeAnalyzer();
	}

	ParseNode root;
	public ScopeAnalyzer()
	{
		buildTree();
		addScopes(root);
		printTree();
		populateTable();
		st.printTable();
		st.scopeTable();
		st.printTable();
	}

	public ParseNode getParseNodeFromString(String line)
	{
		ParseNode temp;
		int tabs;
		for (tabs = 0; line.charAt(tabs)=='\t';tabs++);

		line = line.substring(line.indexOf("|")+1,line.length());

		String[] tokens = line.split("\\|");

		temp = new ParseNode(NodeType.valueOf(tokens[0]),tokens[1]);
		temp.tabs = tabs;

		return temp;
	}

	public void buildTree()
	{
		try{

			Scanner sc = new Scanner(new File("pruned.txt"));
			String line ="";
			ParseNode current, temp;

			if(sc.hasNextLine())
			{
				line = sc.nextLine();
				root = getParseNodeFromString(line);
				// nodes.add(root);
			}

			current = root;

			while(sc.hasNext())
			{
				//check node
				line = sc.nextLine();
				temp = getParseNodeFromString(line);

				if(temp.tabs>current.tabs)
				{
					//child
					current = current.addChild(temp);
					// nodes.add(temp);

					continue;
				}
				else if (temp.tabs<current.tabs)
				{
					//uncle - go up
					while(temp.tabs<current.tabs)
						current = current.parent;

				}

				if(temp.tabs == current.tabs)
				{
					//sibling - parents child
					current = current.parent;
					current = current.addChild(temp);
					// nodes.add(temp);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found!");
		}
	}

	int currentScope = 0;
	LinkedList<ParseNode> visited = new LinkedList<ParseNode>();

	void addScopes(ParseNode head)
	{
		Stack<ParseNode> stack = new Stack<ParseNode>();
		ParseNode current;
		stack.push(head);

		while(!stack.isEmpty())
		{
			current = stack.pop();

			if(current!=head && isNewScope(current))
			{
				currentScope++;
				visited.add(current);
				addScopes(current);
			}
			else
			{
				current.addScopeToNode(currentScope);
			}

			for(int i = current.children.size()-1;i>=0;i--)
			{
				if(!visited.contains(current.children.get(i)))
					stack.push(current.children.get(i));
			}

			if(current.parent!=null)
				for(Integer i : current.parent.scopes)
					current.addScopeToNode(i);


			visited.add(current);
		}
	}

	void populateTable()
	{
		Stack<ParseNode> stack = new Stack<ParseNode>();
		ParseNode current;
		stack.push(root);

		while(!stack.isEmpty())
		{
			current = stack.pop();

			for(int i = current.children.size()-1;i>=0;i--)
			{
				stack.push(current.children.get(i));
			}

			// System.out.println(current.printWithScopes());
			st.addEntry(current);
		}
	}

	void printTree()
	{
		Stack<ParseNode> stack = new Stack<ParseNode>();
		ParseNode current;
		stack.push(root);

		while(!stack.isEmpty())
		{
			current = stack.pop();

			for(int i = current.children.size()-1;i>=0;i--)
			{
				stack.push(current.children.get(i));
			}

			System.out.println(current.printWithScopes());
		}
	}

	boolean isNewScope(ParseNode node)
	{
		return (node.type == NodeType.PROC);
	}

	// PrintWriter pw = new PrintWriter("semantic_table.txt");
	// void newSemanticEntry()
	// {
	// 	static boolean headings = false;
	// 	if(!headings)
	// 		pw.write("Node:\t")
	// }

	// public class SemanticTable()
	// {
	// 	LinkedList<Entry> entries;

	// 	public SemanticTable()
	// 	{
	// 		entries = new LinkedList<Entry>();
	// 	}

	// 	public class Entry()
	// 	{
	// 		String name;
	// 		int id;

	// 		public Entry(String n, int id)
	// 		{
	// 			name = n;
	// 			this.id = id;
	// 		}
	// 	}
	// }
}