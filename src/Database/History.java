package Database;



public class History {

	public int _id;
	public String date;
	public String from;
	public String to;
	public int peopleNum;
	public String room;
	
	public History(){
		
	}
	
	public History(int id, String date, String from, String to, int peopleNum, String room){
		this._id = id;
		this.date = date;
		this.from = from;
		this.to = to;
		this.peopleNum = peopleNum;
		this.room = room;
	}
}
