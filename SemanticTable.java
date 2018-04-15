import java.util.LinkedList;
public class SemanticTable
{
	public static int tableID = 0;
	static int procNums = 0;
	static int varNum = 0;
	static int forVarNum = 0;

	static boolean forLoop = false;

	LinkedList<TableEntry> entries;
	LinkedList<TableEntry> declerations;
	LinkedList<TableEntry> forLoopVars;
	public SemanticTable()
	{	
		//semantic table holds rows
		entries = new LinkedList<TableEntry>();
		declerations = new LinkedList<TableEntry>();
		forLoopVars = new LinkedList<TableEntry>();
	}

	public void addEntry(ParseNode node)
	{
		entries.add(new TableEntry(node));
	}

	public void scopeTable()
	{
		for(TableEntry t : entries)
		{

			if(t.decl_entry!=null)
			{
				t.name = t.decl_entry;
				continue;
			}

			//check if type is of variable or proc
			if(t.node.type == NodeType.VAR)
			{
				//the var is inside a for check
				if(t.node.parent!=null && t.node.parent.data.equals("for"))
				{	
					//check if varibale has been declared!
					TableEntry temp = isDeclaredFor(t.node);
					if(temp!=null)//chck if var is the for counter
					{
						t.decl_id = temp.t_id;
						t.name = temp.decl_entry;
					}
					else if((temp=isDeclared(t.node))!=null && t.scopes.contains(temp.decl_scope))
					{
						t.decl_id = temp.t_id;
						t.name = temp.decl_entry;
					}
					else
					{						
						//var is undeclared
						t.name = "U";
					}
				}
				else
				{
					//check if varibale has been declared!
					TableEntry temp = isDeclared(t.node);
					if(temp!=null && t.scopes.contains(temp.decl_scope))//the var is declared at :
					{
						t.decl_id = temp.t_id;
						t.name = temp.decl_entry;
					}
					else
					{
						//var is undeclared
						t.name = "U";
					}
				}
			}
			else if(t.node.type == NodeType.NAME)//a reference to a PROC
			{
				// System.out.println("handling proc: "+t.name+" at: " + t.t_id);
				TableEntry temp = isDeclared(t.node);
				System.out.println("handling proc: "+t.name+" at: " + t.t_id+" temp: "+(temp==null));

				if(temp!=null)//the var is declared at :
				{
					t.decl_id = temp.t_id;
					t.name = temp.decl_entry;
				}
				else
				{
					//var is undeclared
					t.name = "U";
				}
			}
		}
	}

	public TableEntry isDeclared(ParseNode node)
	{
		TableEntry temp = null;
		int match = 0;
		int c = 0;
		for(TableEntry t : declerations)
			if(t.node.data.equals(node.data) && (c = countScopes(t,node))>match)
			{
				temp = t;
				match = c;
			}

		return temp;
	}

	int countScopes(TableEntry t, ParseNode n)
	{
		int temp = 0;
		for(Integer i: t.scopes)
			if(n.scopes.contains(i))
				temp++;

		return temp;
	}

	public TableEntry isDeclaredFor(ParseNode node)
	{
		for(TableEntry t : forLoopVars)
			if(t.node.data.equals(node.data) && t.node.parent==node.parent)
			{
				return t;
			}

		return null;
	}

	public void printTable()
	{
		System.out.println("-------------------\n"
						  +"  Semantic Table\n"
						  +"-------------------");

		for(TableEntry te : entries)
			System.out.println(te);
	}

	
	public class TableEntry
	{
		String name;//proc or var names 
		int nodeID;
		LinkedList<Integer> scopes;
		Integer decl_id = null; //id of entry that declares this entry
		int t_id;
		ParseNode node;
		String decl_entry = null;//name (v1) of the entry
		int decl_scope = -1;
		boolean isForVar = false;

		public TableEntry(ParseNode node)
		{
			this.nodeID = node.num;
			t_id = SemanticTable.tableID++;
			this.scopes = node.scopes;
			this.node = node;
			// type = node.type+"";

			if(node.parent!=null && node.parent.type == NodeType.DECL && node.type == NodeType.VAR)
			{
				decl_entry = "v"+varNum++;					
				decl_scope = node.scopes.get(0);
				declerations.add(this);
			}
			else if(node.type == NodeType.PROC)
			{
				decl_entry = "p"+procNums++;
				decl_scope = node.scopes.get(0);
				declerations.add(this);
			}
			else if(node.parent!=null && node.parent.data.equals("for"))
			{
				// TableEntry temp = isDeclaredFor(node);
				// System.out.println(temp);

				if(forLoop)//first var from loop to be seen
				{
					forLoop=false;
					decl_scope = -2;
					decl_entry = "f"+forVarNum++;
					forLoopVars.add(this);
				}
			}
			
			name = node.data;

			if(name.equals("for"))
				forLoop=true;
		} 

		

		public String toString()
		{
			String scopeStr = "";

			for(Integer i : this.scopes)
				scopeStr+=i+" ";

			String decl_id_str ="";
			if(decl_id!=null)
				decl_id_str = "delcared at: " + decl_id+"";
			else if(decl_entry==null && (node.type == NodeType.VAR || node.type == NodeType.PROC || node.type == NodeType.NAME))
				decl_id_str = "undeclared or out of scope";
			else if(decl_scope!=-1 && decl_entry!=null)
				decl_id_str = "declared: "+node.data;

			if(this.scopes.size()<4)
				return t_id + "\t|"+scopeStr + "\t\t|"+name+"\t|"+ nodeID + "\t|" + decl_id_str;
			else
				return t_id + "\t|"+scopeStr + "\t|"+name+"\t|"+ nodeID + "\t|" + decl_id_str;

		}
	}

}