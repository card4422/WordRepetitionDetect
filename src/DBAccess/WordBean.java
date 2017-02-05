package DBAccess;

public class WordBean {
	private int w_id;
	private String wordname;
	private String relatedproj;
	
	public boolean isExist(String wordname,String characteristic){
		boolean isExist = false;
		DBAccess db = new DBAccess();
		if(db.createConn()){
			String sql = "select * from word where wordname='"
		+ wordname + "' and characteristic='" + characteristic + "'";
			db.query(sql);
			if(db.next()){
				isExist = true;
			}
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}
		return isExist;
	}
	
	public boolean isExist(int w_id){
		boolean isExist = false;
		DBAccess db = new DBAccess();
		if(db.createConn()){
			String sql = "select * from word where w_id="
		+w_id;
			db.query(sql);
			if(db.next()){
				isExist = true;
			}
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}
		return isExist;
	}
	
	public int getFrequency(String wordname,String characteristic){
		DBAccess db = new DBAccess();
		String result = new String();
		if(db.createConn()){
			String sql = "select * from word where wordname='"
				+ wordname + "' and characteristic='" + characteristic + "'";
			db.query(sql);
            if(db.next()) {
				result = db.getValue("frequency");
				if(result.equals(""))
					System.out.println(1234);
			}
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}
		if(result.equals(""))
			System.out.println(1234);
		return Integer.parseInt(result);
	}
	//Create
	public void add(String wordname,String characteristic){
		DBAccess db = new DBAccess();
		if(db.createConn()){
			String sql = "insert into word(wordname,characteristic,frequency) values('"
		+ wordname + "','" + characteristic + "',"+ 1 +")";
			db.update(sql);
			db.closeStm();
			db.closeConn();
		}
	}

	//Update
	public void updateFreq(String wordname,String characteristic,int frequency){
		DBAccess db = new DBAccess();
		if(db.createConn()){
			String sql = "update word set frequency=" + frequency
			+ " where wordname='" + wordname + "' and characteristic ='" + characteristic + "'";
			db.update(sql);
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}
	}
	
	//Delete
	/*
	public void delete(String modulename){
		DBAccess db = new DBAccess();
		if(db.createConn()){
			String sql = "delete from module where modulename= '"+modulename+"'";
			db.update(sql);
			db.closeStm();
			db.closeConn();
		}
	}
	*/

}
