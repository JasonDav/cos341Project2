import java.util.LinkedList;
public class SemanticTable
{
	public static int tableID = 0;
	static int procNums = 0;
	static int varNum = 0;

	LinkedList<TableEntry> entries;
	LinkedList<TableEntry> declerations;
	public SemanticTable()
	{	
		//semantic table holds rows
		entries = new LinkedList<TableEntry>();
		declerations = new LinkedList<TableEntry>();
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
			else if(t.node.type == NodeType.NAME)//a reference to a PROC
			{
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
	}

	public TableEntry isDeclared(ParseNode node)
	{
		for(TableEntry t : declerations)
			if(t.node.data.equals(node.data))
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
		int decl_scope;

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
				decl_scope = scopes.get(0);
				declerations.add(this);

			}
			else if(node.type == NodeType.PROC)
			{
				decl_entry = "p"+procNums++;
				decl_scope = scopes.get(0);
				declerations.add(this);
			}
			

			name = node.data;
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

			return t_id + "\t|"+scopeStr + "\t|"+name+"\t|"+ nodeID + "\t|" + decl_id_str;
		}
	}

}