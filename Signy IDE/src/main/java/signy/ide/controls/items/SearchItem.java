package signy.ide.controls.items;

public class SearchItem {
	
	private String toString;
	private int line;
	private int l1; // index
	private int l2; // end index length of string
	
	public SearchItem(int line ,String show, int l1,int l2) {

		this.line = line+1;
		toString = "Line : " + this.line + " " + show.trim();
		this.l1 = l1;
		this.l2 = l2;
	}
	
	public int getLine() {
		return line;
	}
	
	@Override
	public String toString() {
		return toString.trim();
	}
	
	public int getIndex() {
		return l1;
	}
	
	public int getLength() {
		return l2;
	}
}
