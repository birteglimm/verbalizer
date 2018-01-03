package org.semanticweb.cogExp.coverageEvaluator;

import org.semanticweb.cogExp.inferencerules.AdditionalDLRules;
import org.semanticweb.cogExp.inferencerules.INLG2012NguyenEtAlRules;


import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public enum DatabaseManager {
	INSTANCE;
	
	private  Connection conn = null;
	
	public void connect(String hostname){
		try {
          Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("Driver loaded!");
        } catch (Exception ex) {
        	System.out.println("Exception: " + ex.getMessage());
        }
	    try {// conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/verbalizer",
	         //                                  "java", "java444");
 
	    	
	    	Properties info = new Properties();
	    	info.setProperty("user", "java");
	    	info.setProperty("password", "java444");
	    	info.setProperty("useUnicode", "yes");
	    	info.setProperty("characterEncoding", "utf8");
	    	
	          conn = DriverManager.getConnection("jdbc:mysql://" + hostname + ":3306/verbalizer", info);
	    
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	            System.out.println("SQLState: " + ex.getSQLState());
	            System.out.println("VendorError: " + ex.getErrorCode());
	        }
	    System.out.println("connection: " + conn.toString());
	}
	
	public void insertExplanation(String subclass, 
								  String superclass, 
								  String ontologypath, 
								  String corpus,
								  boolean solved, 
								  String explanation,
								  String listing,
								  String longlisting,
								  int explanationsteps,
								  int listingsteps,
								  int longlistingsteps,
								  int time,
								  int EQUIVEXTRACT,
								  int SUBCLANDEQUIVELIM,
								  int R0,
								  int RULE1,
					    		  int RULE1neo,
					    		  int RULE2,
					    	      int RULE3,
					    	      int RULE5,
					    	      int RULE5new,
					    		  int RULE5MULTI,
					    		  int RULE6,
					    		  int RULE6neo,
					    		  int RULE12,
					    		  int RULE12new,
					    		  int RULE15,
					    		  int RULE23, // transitivity
					    	      int RULE23Repeat, // transitivity
					    	      int RULE34, 
					    	      int RULE35, 
					    		  int RULE37, // handle subpropertyof
					    		  int RULE42,
					    		  int BOTINTRO, 
					    		  int TOPINTRO,
					    		  int DEFDOMAIN,
					    		  int ELEXISTSMINUS,
					    		  int APPLRANGE,
					    		  int PROPCHAIN,
					    		  int FORALLUNION
								  
									) {
		
	Statement stmt;
		try {
			stmt = conn.createStatement();
		
         
			explanation = explanation.replace("'", "\\'");
			listing=listing.replace("'", "\\'");
			longlisting=longlisting.replace("'", "\\'");
			
         String sql = "INSERT IGNORE INTO EXPLANATIONS " +
                      "(id, subclass, superclass, ontologypath, corpus, solved, explanation, listing, longlisting," + 
        		      "verbsteps, listingsteps, longlistingsteps, time," +
        		      "equivextract, subclandequivelim, r0, r1, r1neo, r2, r3, r5, r5new, r5multi, r6, r6neo, r12, r12new, r15, r23, r23repeat," +
                      " r34, r35, r37, r42, botintro, topintro, defdomain, elexistsminus, applrange, propchain, forallunion)" + //<--24
                      " VALUES(0,"
                      +  "'" + subclass + "','" + superclass + "','" + ontologypath + "','" + corpus + "',"
                      ; 
         if (solved)
         sql += "1,";
         else 
         sql += "0,";
         
         sql += "'" + explanation + "','" + listing + "','" + longlisting + "',";
         
         sql += explanationsteps + "," + listingsteps + ","  + longlistingsteps + "," + time + ",";

         sql += EQUIVEXTRACT + "," + SUBCLANDEQUIVELIM + "," + R0 + "," + RULE1 + ",";
         sql += RULE1neo +  "," +  RULE2 + "," + RULE3 +  "," + RULE5 + "," + RULE5new + "," + RULE5MULTI + ",";
         sql += RULE6 + "," + RULE6neo + "," +RULE12 +"," +RULE12new + "," + RULE15 + "," + RULE23 + "," + RULE23Repeat + ",";
         sql += RULE34 +"," +  RULE35 +"," + RULE37 +"," + RULE42 +"," + BOTINTRO +"," + TOPINTRO + "," + DEFDOMAIN + "," + ELEXISTSMINUS +"," + APPLRANGE + "," ;
         sql += PROPCHAIN + "," + FORALLUNION + ")";
        
         
         
        
         
         // sql = sql.replace("/", "\\/");
         // sql = sql.replace("'", "\\'");
         
         System.out.println("insert statement " + sql);
         
         
         stmt.executeUpdate(sql);
         stmt.close();
         
         Statement stmt2 = conn.createStatement();
		
         
         String sql2 = "INSERT IGNORE INTO OCCURENCES(subclass, superclass,ontologypath) VALUES('" 
         + subclass + "','" + superclass + "','" + ontologypath + "');";
         
         // System.out.println(sql2);
        
         stmt2.executeUpdate(sql2);
         
         stmt2.close();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void insertBioExplanation(
			  String subclass,      // 0
			  String superclass,    // 1
			  String ontologypath,  // 2
			  String corpus,        // 3
			  boolean solved,       // 4
			  String explanation,   // 5
			  String listing,       // 6 
			  String longlisting,   // 7
			  int explanationsteps,   //8
			  int listingsteps,       //9 
			  int longlistingsteps,   // 10
			  int time,              // 11
			  int EQUIVEXTRACT,       // 12
			  int SUBCLANDEQUIVELIM,   //13
			  int R0,                 // 14
			  int RULE1,
  		  int RULE1neo,
  		  int RULE2,
  	      int RULE3,
  	      int RULE5,
  	      int RULE5new,
  		  int RULE5MULTI,
  		  int RULE6,
  		  int RULE6neo,
  		  int RULE12,
  		  int RULE12new,
  		  int RULE15,
  		  int RULE23, // transitivity
  	      int RULE23Repeat, // transitivity
  	      int RULE34, 
  	      int RULE35, 
  		  int RULE37, // handle subpropertyof
  		  int RULE42,
  		  int BOTINTRO, 
  		  int TOPINTRO,
  		  int DEFDOMAIN,
  		  int ELEXISTSMINUS,
  		  int APPLRANGE,
  		  int PROPCHAIN,
  		  int FORALLUNION,
  		  int SUBCLCHAIN
			  
				) {

Statement stmt;
try {
stmt = conn.createStatement();


explanation = explanation.replace("'", "\\'");
listing=listing.replace("'", "\\'");
longlisting=longlisting.replace("'", "\\'");

String sql = "INSERT IGNORE INTO BIOEXPLANATIONS " +
"(id, subclass, superclass, ontologypath, corpus, solved, explanation, listing, longlisting," + 
"verbsteps, listingsteps, longlistingsteps, time," +
"equivextract, subclandequivelim, r0, r1, r1neo, r2, r3, r5, r5new, r5multi, r6, r6neo, r12, r12new, r15, r23, r23repeat," +
" r34, r35, r37, r42, botintro, topintro, defdomain, elexistsminus, applrange, propchain, forallunion,subclchain)" + //<--24
" VALUES(0,"
+  "'" + subclass + "','" + superclass + "','" + ontologypath + "','" + corpus + "',"
; 
if (solved)
sql += "1,";
else 
sql += "0,";

sql += "'" + explanation + "','" + listing + "','" + longlisting + "',";

sql += explanationsteps + "," + listingsteps + ","  + longlistingsteps + "," + time + ",";

sql += EQUIVEXTRACT + "," + SUBCLANDEQUIVELIM + "," + R0 + "," + RULE1 + ",";
sql += RULE1neo +  "," +  RULE2 + "," + RULE3 +  "," + RULE5 + "," + RULE5new + "," + RULE5MULTI + ",";
sql += RULE6 + "," + RULE6neo + "," +RULE12 +"," +RULE12new + "," + RULE15 + "," + RULE23 + "," + RULE23Repeat + ",";
sql += RULE34 +"," +  RULE35 +"," + RULE37 +"," + RULE42 +"," + BOTINTRO +"," + TOPINTRO + "," + DEFDOMAIN + "," + ELEXISTSMINUS +"," + APPLRANGE + "," ;
sql += PROPCHAIN + "," + FORALLUNION + "," + SUBCLCHAIN + ")";





// sql = sql.replace("/", "\\/");
// sql = sql.replace("'", "\\'");

System.out.println("insert statement " + sql);


stmt.executeUpdate(sql);
stmt.close();

/*
Statement stmt2 = conn.createStatement();

String sql2 = "INSERT IGNORE INTO OCCURENCES(subclass, superclass,ontologypath) VALUES('" 
+ subclass + "','" + superclass + "','" + ontologypath + "');";

// System.out.println(sql2);

stmt2.executeUpdate(sql2);

stmt2.close();
*/
} catch (SQLException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}

}
	

	
	public String findOccurence(String sub, String sup){
		String query = "select ontologypath from occurences where subclass='" + sub +
				"' && superclass='"+  sup + "';";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				String ontologypath = rs.getString("ontologypath");
				rs.close();
				stmt.close();
				return ontologypath;
			}
			rs.close();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public void addOccurence(String sub, String sup, String ontologypath){
		
		Statement stmt2;
		try {
			stmt2 = conn.createStatement();
			String sql2 = "INSERT IGNORE INTO OCCURENCES(subclass, superclass,ontologypath) VALUES('" 
				        + sub + "','" + sup + "','" + ontologypath + "');";
				        
		    // System.out.println(sql2);
				       
		    stmt2.executeUpdate(sql2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    return;	
		
	}
	
	
	public List<String> getBioExplanation(String sub, String sup, String ontology){
		List<String> result = new ArrayList<String>();
		String query = "select * from bioexplanations where subclass='" + sub +
				"' && superclass='"+  sup + "' && ontologypath = '" +ontology +"';";
		// System.out.println("query string: "+ query);
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			// ResultSetMetaData rsmd = rs.getMetaData();
			// int columnsNumber = rsmd.getColumnCount();
			
			
			while (rs.next()){
				System.out.print("solved " + rs.getString("solved"));
			result.add(rs.getString("solved"));
			result.add(rs.getString("subclass"));  
			result.add(rs.getString("superclass"));   
		    result.add(rs.getString("ontologypath"));  
		    result.add(rs.getString("corpus"));
		    result.add(rs.getString("solved"));
		    result.add(rs.getString("explanation"));
		    result.add(rs.getString("listing"));
		    result.add(rs.getString("longlisting"));
		    result.add(rs.getString("verbsteps"));
		    result.add(rs.getString("listingsteps"));
		    result.add(rs.getString("longlistingsteps"));
		    result.add(rs.getString("time"));
		    result.add(rs.getString("equivextract"));
		    result.add(rs.getString("subclandequivelim"));
		    result.add(rs.getString("r0"));
		    result.add(rs.getString("r1"));
		    result.add(rs.getString("r1neo"));
		    result.add(rs.getString("r2"));
		    result.add(rs.getString("r3"));
		    result.add(rs.getString("r5"));
	        result.add(rs.getString("r5new"));
		    result.add(rs.getString("r5multi"));
		    result.add(rs.getString("r6"));
		    result.add(rs.getString("r6neo"));
		    result.add(rs.getString("r12"));
		    result.add(rs.getString("r12new"));
		    result.add(rs.getString("r15"));
		    result.add(rs.getString("r23"));
	        result.add(rs.getString("r23repeat"));
	        result.add(rs.getString("r34"));
	        result.add(rs.getString("r35"));
		    result.add(rs.getString("r37"));
		    result.add(rs.getString("r42"));
		    result.add(rs.getString("botintro")); 
		    result.add(rs.getString("topintro"));
		    result.add(rs.getString("defdomain"));
		    result.add(rs.getString("elexistsminus"));
		    result.add(rs.getString("applrange"));
		    result.add(rs.getString("propchain"));
		    result.add(rs.getString("forallunion"));
		    result.add(rs.getString("subclchain"));
			}
			stmt.close();
			rs.close();
			/*
			
			while (rs.next()){
				 for(int i = 1; i < columnsNumber+1; i++)
					 result.add(rs.getString(i));
					
				}
			}
			*/
		}catch (Exception e){
			System.out.println("ERROR WHILE ACCESSING DB");
			e.printStackTrace();
		}
		System.out.println("returning row : "+ result.toString());
		
		return result;
	}
	
	public List<String> getExplanation(String sub, String sup, String ontology){
		List<String> result = new ArrayList<String>();
		String query = "select * from explanations where subclass='" + sub +
				"' && superclass='"+  sup + "' && ontologypath = '" +ontology +"';";
		// System.out.println("query string: "+ query);
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				String solved = rs.getString("solved");
				String explanation = rs.getString("explanation");
				String listing = rs.getString("listing");
				String longlisting = rs.getString("longlisting");
				String verbsteps = rs.getString("verbsteps");
				String listingsteps = rs.getString("listingsteps");
				String longlistingsteps = rs.getString("longlistingsteps");
				String time = rs.getString("time");
				String equivextract = rs.getString("equivextract");
				result.add(solved);
				result.add(explanation);
				result.add(listing);
				result.add(longlisting);
				result.add(verbsteps);
				result.add(listingsteps);
				result.add(longlistingsteps);
				result.add(time);
				result.add(equivextract);
				
				// close resource to avoid memory leak
				stmt.close();
				rs.close();
				break;
			}
			rs.close();
			stmt.close();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		return result;	
	}
	
	public List<String> getRestrictedBioExplanation(String sub, String sup, String ontology){
		List<String> result = new ArrayList<String>();
		String query = "select * from bioexplanations where subclass='" + sub +
				"' && superclass='"+  sup + "' && ontologypath = '" +ontology +"';";
		// System.out.println("query string: "+ query);
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				String solved = rs.getString("solved");
				String explanation = rs.getString("explanation");
				String listing = rs.getString("listing");
				String longlisting = rs.getString("longlisting");
				String verbsteps = rs.getString("verbsteps");
				String listingsteps = rs.getString("listingsteps");
				String longlistingsteps = rs.getString("longlistingsteps");
				String time = rs.getString("time");
				String equivextract = rs.getString("equivextract");
				result.add(solved);
				result.add(explanation);
				result.add(listing);
				result.add(longlisting);
				result.add(verbsteps);
				result.add(listingsteps);
				result.add(longlistingsteps);
				result.add(time);
				result.add(equivextract);
				
				// close resource to avoid memory leak
				stmt.close();
				rs.close();
				break;
			}
			rs.close();
			stmt.close();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		return result;	
	}
	
	
	public List<List<String>> getExplanationsInclusive(String ontology){
		List<List<String>> result = new ArrayList<List<String>>();
		// String query = "select * from explanations;";
		
		String query = "select * from explanations, occurences where " 
				       + "explanations.subclass = occurences.subclass " 
				       + "&& explanations.superclass = occurences.superclass "
				       + "&& occurences.ontologypath = '" 
				       + ontology  
				       + "';";
		
		
		// System.out.println("query string: "+ query);
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			// System.out.println("after executing query");
			
			List<String> items = Arrays.asList("solved", "explanation", "listing", "longlisting",
					"verbsteps", "listingsteps", "longlistingsteps", "time", "equivextract", 
					"subclandequivelim", "r0", "r1", "r1neo", "r2", "r3", "r5", "r5new", 
					"r5multi", "r6", "r6neo", "r12", "r12new", "r15", "r23", "r23repeat",
					 "r34", "r35", "r37", "r42", "botintro", "topintro", "defdomain", 
					 "elexistsminus", "applrange", "propchain", "forallunion");
			
			
			while (rs.next()){
				List results = new ArrayList<String>();
				for (String item : items){
					// System.out.println(" getting item " + item + " found " + rs.getString(item));
					results.add(rs.getString(item));
				}
				result.add(results);
			}
			
			// prevent memory leak
			rs.close();
			stmt.close();
			
		} catch(Exception e){
			// System.out.println("exception" + e);
			System.out.println(e.getMessage());
		}
		return result;	
	}
	
	public List<List<String>> getExplanationsExclusive(String ontology, String table){
		List<List<String>> result = new ArrayList<List<String>>();
		// String query = "select * from explanations;";
		
		String query = "select * from " + table + " where " 
				      + " ontologypath = '" 
				       + ontology  
				       + "';";
		
		
		System.out.println("query string: "+ query);
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			// System.out.println("after executing query");
			
			List<String> items = Arrays.asList("solved", "explanation", "listing", "longlisting",
					"verbsteps", "listingsteps", "longlistingsteps", "time", "equivextract", 
					"subclandequivelim", "r0", "r1", "r1neo", "r2", "r3", "r5", "r5new", 
					"r5multi", "r6", "r6neo", "r12", "r12new", "r15", "r23", "r23repeat",
					 "r34", "r35", "r37", "r42", "botintro", "topintro", "defdomain", 
					 "elexistsminus", "applrange", "propchain", "forallunion", "subclchain");
			
			
			while (rs.next()){
				List results = new ArrayList<String>();
				for (String item : items){
					// System.out.println(" getting item " + item + " found " + rs.getString(item));
					results.add(rs.getString(item));
				}
				result.add(results);
			}
			
			// prevent memory leak
			rs.close();
			stmt.close();
			
		} catch(Exception e){
			// System.out.println("exception" + e);
			System.out.println(e.getMessage());
		}
		return result;	
	}
	
	public void deleteExplanation(
			String subclass, 
			  String superclass, 
			  String ontologypath){
		
		Statement stmt2;
		try {
			stmt2 = conn.createStatement();
			String sql2 = "DELETE FROM EXPLANATIONS WHERE subclass = ' "
					 + subclass + "' && superclass='" + superclass + "' && ontologypath='" + ontologypath + "';";
				        
		    System.out.println("Executing delete statement  " + sql2);
				       
		    stmt2.executeUpdate(sql2);
		    stmt2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("we have an exception!");
			e.printStackTrace();
		}
		return;
		
	}
	
	public void deleteBioExplanation(
			String subclass, 
			  String superclass, 
			  String ontologypath){
		
		Statement stmt2 = null;
		try {
			stmt2 = conn.createStatement();
			String sql2 = "DELETE FROM BIOEXPLANATIONS WHERE subclass = ' "
					 + subclass + "' && superclass='" + superclass + "' && ontologypath='" + ontologypath + "';";
				        
		    System.out.println("Executing delete statement  " + sql2);
				       
		    stmt2.executeUpdate(sql2);
		    stmt2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("we have an exception!");
			e.printStackTrace();
			try {stmt2.close();}
			catch (Exception ee){}
		}
		return;
		
	}
	
	public String getExplanation(List<String> strlist){
		return strlist.get(1);
	}
	
	public String getListing(List<String> strlist){
		return strlist.get(2);
	}
	
	public String getLongListing(List<String> strlist){
		return strlist.get(3);
	}
	
	public String getVerbsteps(List<String> strlist){
		return strlist.get(4);
	}
	
	public String getListingsteps(List<String> strlist){
		return strlist.get(5);
	}
	
	public String getLonglistingsteps(List<String> strlist){
		return strlist.get(6);
	}
	
	public int getTime(List<String> strlist){
		int i = -1;
		if (strlist.size()>0 && strlist.get(7).length()>0)
			i = Integer.parseInt(strlist.get(7));
		return i;
	}

	public boolean getSolved(List<String> strlist){
		if (strlist.size()>0 && strlist.get(0).length()>0 && Integer.parseInt(strlist.get(0)) ==1)
			return true;
		return false;
	}
	
	public Set<String> getUsedRules(List<String> results){
		Set<String> strings = new HashSet<String>();
		/*
		"solved", "explanation", "listing", "longlisting",
		"verbsteps", "listingsteps", "longlistingsteps", "time", "equivextract", 
		"subclandequivelim", "r0", "r1", "r1neo", "r2", "r3", "r5", "r5new", 
		"r5multi", "r6", "r6neo", "r12", "r12new", "r15", "r23", "r23repeat",
		 "r34", "r35", "r37", "r42", "botintro", "topintro", "defdomain", 
		 "elexistsminus", "applrange", "propchain", "forallunion"
		 */
		 // System.out.println("--" + results.get(13) + "--");
		 // System.out.println(!results.get(13).equals("0"));
		 if (!results.get(8).equals("0"))	
			 strings.add("equivextract");
		 if (!results.get(9).equals("0"))	
			 strings.add("subclandequivelim");
		 if (!results.get(10).equals("0"))	
			 strings.add("r0");
		 if (!results.get(11).equals("0"))	
			 strings.add("r1");
		 if (!results.get(12).equals("0"))	
			 strings.add("r1neo");
		 if (!results.get(13).equals("0"))	
			 strings.add("r2");
		 if (!results.get(14).equals("0"))	
			 strings.add("r3");
		 if (!results.get(15).equals("0"))	
			 strings.add("r5");
		 if (!results.get(16).equals("0"))	
			 strings.add("r5new");
		 if (!results.get(17).equals("0"))	
			 strings.add("r5multi");
		 if (!results.get(18).equals("0"))	
			 strings.add("r6");
		 if (!results.get(19).equals("0"))	
			 strings.add("r6neo");
		 if (!results.get(20).equals("0"))	
			 strings.add("r12");
		 if (!results.get(21).equals("0"))	
			 strings.add("r12new");
		 if (!results.get(22).equals("0"))	
			 strings.add("r15");
		 if (!results.get(23).equals("0"))	
			 strings.add("r23");
		 if (!results.get(24).equals("0"))	
			 strings.add("r23repeat");
		 if (!results.get(25).equals("0"))	
			 strings.add("r34");
		 if (!results.get(26).equals("0"))	
			 strings.add("r35");
		 if (!results.get(27).equals("0"))	
			 strings.add("r37");
		 if (!results.get(28).equals("0"))	
			 strings.add("r42");
		 if (!results.get(29).equals("0"))	
			 strings.add("botintro");
		 if (!results.get(30).equals("0"))	
			 strings.add("topintro");
		 if (!results.get(31).equals("0"))	
			 strings.add("defdomain");
		 if (!results.get(32).equals("0"))	
			 strings.add("elexistsminus");
		 if (!results.get(33).equals("0"))	
			 strings.add("applrange");
		 if (!results.get(34).equals("0"))	
			 strings.add("propchain");
		 if (!results.get(35).equals("0"))	
			 strings.add("forallunion");
		 if (!results.get(36).equals("0"))	
			 strings.add("subclchain");
		 // System.out.println("returning " + strings);
		 return strings;	
	}
	
	
	public String constructStatisticLineFromDB(String ontology, String table){
		List<List<String>> result = getExplanationsExclusive(ontology,table);
		int solved = 0;
		int unsolved = 0;
		int total = 0;
		int[] listingsteps = new int[80];
		int[] verbsteps = new int[80];
		int[] longlistingsteps = new int[80]; // <--- 50 is not sufficient!!!
		int[] timesPerListing = new int[80];
		int[] timesPerVerbs= new int[80];
		int timesSum = 0;
		Set<String> usedRules = new HashSet<String>();
		
		int[] longlistingsToVerbsCompression = new int[80];
		int[] longlistingsToListingsCompression = new int[80];
		int[] listingsToVerbsCompression = new int[80];
		
		for (List<String> line : result){
			if (line.get(0).equals("1")){
				solved +=1;
				total +=1;
			} else{
				unsolved +=1;
				total +=1;
			}
			// listingsteps
			String currentlistingsteps = getListingsteps(line);
			int listingstepsInt = Integer.parseInt(currentlistingsteps);
			listingsteps[listingstepsInt] = listingsteps[listingstepsInt] + 1;
			// System.out.println("setting position " + listingstepsInt + " to " + listingsteps[listingstepsInt]);
			// verbsteps
			String currentverbsteps = getVerbsteps(line);
			int verbstepsInt = Integer.parseInt(currentverbsteps);
			verbsteps[verbstepsInt] = verbsteps[verbstepsInt] + 1;
			// longlistingsteps
			String currentlonglistingsteps = getLonglistingsteps(line);
			int longlistingInt = Integer.parseInt(currentlonglistingsteps);
			longlistingsteps[longlistingInt] = longlistingsteps[longlistingInt] + 1;
			// time
			int time = getTime(line);
			if (line.get(0).equals("1")){  // <---- only count avg time for solved subsumptions
				timesSum = timesSum + time;}
			timesPerListing[listingstepsInt] = timesPerListing[listingstepsInt] + time;
			timesPerVerbs[verbstepsInt] = timesPerVerbs[verbstepsInt] + time;
			// compressions  longlistingsToVerbsCompression 
			int differencelonglistingsVerbs = longlistingInt - verbstepsInt;
			longlistingsToVerbsCompression[longlistingInt] = longlistingsToVerbsCompression[longlistingInt] + differencelonglistingsVerbs;
			// compressions longlistingsToListings
			int differencelonglistingsListings = longlistingInt - listingstepsInt;
			longlistingsToListingsCompression[longlistingInt] = longlistingsToListingsCompression[longlistingInt] + differencelonglistingsListings;
			// usedRules
			usedRules.addAll(getUsedRules(line));
		}
		// count listingsteps
		String resultstring = total + "," + solved + ",";
		for (int i = 1; i<=10; i++){
			resultstring = resultstring + listingsteps[i] + ",";
		}
		// count verbsteps
		for (int i = 1; i<=10; i++){
			resultstring = resultstring + verbsteps[i] + ",";
		}
		// time as per listingsteps
		for (int i = 1; i<=10; i++){
			if (listingsteps[i]==0){
				resultstring = resultstring + ",";
			}
			else{ 
			resultstring = resultstring + (timesPerListing[i]/listingsteps[i])  + ",";
			}
		}
		// time as per verbs steps
		for (int i = 1; i<=10; i++){
			if (verbsteps[i]==0){
				resultstring = resultstring + ",";
			}
			else{ 
				resultstring = resultstring + (timesPerVerbs[i]/verbsteps[i])  + ",";
				}
		}
		// avg time
		if (solved>0){
			resultstring = resultstring + (timesSum / solved) + ",";
		}else{
			resultstring = resultstring + "0,";
		}
		// overall time
		resultstring = resultstring + timesSum  + ",";
		// compressions (longlistingsToVerbsCompression )
		for (int i = 1; i<=10; i++){
			resultstring = resultstring + longlistingsToVerbsCompression[i]  + "," + longlistingsteps[i] + ",";
		}
		// compressions (longlistingsToListingsCompression)
		for (int i = 1; i<=10; i++){
			resultstring = resultstring + longlistingsToListingsCompression[i]  + "," + longlistingsteps[i] + ",";
		}
		// used rules
		for (String usedRule : usedRules){
			resultstring = resultstring + usedRule + "~";
		}
		resultstring = resultstring + ",";
		return resultstring;
	}
	
	
	
}