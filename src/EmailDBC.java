import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;




public class EmailDBC {
	private Connection conn;

	public EmailDBC(){
		initialize();
	}

	public void initialize(){
		conn = null;
		try {
			conn =
					DriverManager.getConnection("jdbc:mysql://localhost/EMAIL?" +
							"user=root&password=password");
			// Do something with the Connection
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	/**
	 * getEmail
	 * 
	 * Searches for emails in the database using all the data provided in emailSearch.
	 * The data returned must match all the criteria of the search (AND not OR).
	 * 
	 * @param emailSearch - the email meta-data to search for
	 * @return A list of all the returned emails from the database query.
	 * @throws IOException if an SQLException or IOException occurs.
	 */
	public List<EmailData> getEmail(EmailData emailSearch) throws Exception, IOException{
		if(emailSearch.isEmpty()){
			throw new Exception("Search criteria cannot be empty.");
		}
		
		String params = "";
		if(!emailSearch.getEmail().isEmpty()){
			params = "EMAIL.email = \'" + emailSearch.getEmail() + "\' ";
		}
		if(!emailSearch.getDisplayName().isEmpty()){
			if(!emailSearch.getEmail().isEmpty())
				params += "AND ";
			params += "EMAIL.displayName = \'" + emailSearch.getDisplayName() + "\' ";
		}
		if(!emailSearch.getGroupName().isEmpty()){
			if(!emailSearch.getEmail().isEmpty() || !emailSearch.getDisplayName().isEmpty())
				params += "AND ";

			params += "EMAIL.groupName = \'" + emailSearch.getGroupName() + "\' ";
		}
		try{	
			PreparedStatement ps = conn.prepareStatement(
					"select * from EMAIL where " + params);
			ResultSet rs = ps.executeQuery();

			List<EmailData> emailList = new ArrayList<EmailData>();
			while(rs.next()){
				String email = rs.getString("email");
				String displayName = rs.getString("displayName");
				String groupName = rs.getString("groupName");
				emailList.add(new EmailData(email, displayName, groupName));
			}

			return emailList;
		}
		catch(SQLException e){
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * createEmail
	 * 
	 * Inserts email into the database. The email must have an email address
	 * or the insertion will fail.
	 * 
	 * @param email - the email meta-data to insert into the database.
	 * @throws Exception if an exception occurs or an email address is not provided.
	 * @throws IOException if an SQLException occurs
	 */
	public void createEmail(EmailData email) throws Exception, IOException{
		if(email.getEmail().isEmpty()){
			throw new Exception("To create a new email there MUST be an email address");
		}

		try{
			PreparedStatement ps = conn.prepareStatement(
					"insert into EMAIL values(\""+ email.getEmail() 
					+ "\",\"" + email.getDisplayName() + "\",\"" + email.getGroupName() + "\")" );
			ps.executeUpdate();
		}
		catch(SQLException e){
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * deleteEmail
	 * 
	 * Deletes an email record from the database.
	 * 
	 * @param emailAddress - the email address of the record to delete
	 * @throws Exception if an exception occurs or the emailAddress is empty.
	 * @throws IOException if an SQLException occurs.
	 */
	public void deleteEmail(String emailAddress) throws Exception, IOException{
		if(emailAddress.isEmpty()){
			throw new Exception("Email must be non-empty");
		}
		
		try{
			PreparedStatement ps = conn.prepareStatement(
					"delete from EMAIL where EMAIL.email = \'"
					+ emailAddress + "\'");
			ps.executeUpdate();
		}
		catch(SQLException e){
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * updateEmail
	 * 
	 * Updates the email address in the database with the data stored in updatedEmail
	 * 
	 * @param emailAddress - the email address of the entry to update
	 * @param updatedEmail - the meta-data for update
	 * @throws Exception if emailAddress is empty or if updatedEmail has all empty fields or if an Exception occurs
	 * @throws IOException if an SQLException occurs
	 */
	public void updateEmail(String emailAddress, EmailData updatedEmail) throws Exception, IOException{
		if(emailAddress.isEmpty()){
			throw new Exception("Email must be non-empty");
		}
		else if(updatedEmail.isEmpty()){
			throw new Exception("updatedEmail must contain at least one non-empty field to update.");
		}
		
		String fields = "";
		if(!updatedEmail.getEmail().isEmpty()){
			fields+="email = \'" + updatedEmail.getEmail() + "\'";
		}
		if(!updatedEmail.getDisplayName().isEmpty()){
			if(!updatedEmail.getEmail().isEmpty()){
				fields += ",";
			}
			fields += "displayName = \'" + updatedEmail.getDisplayName() + "\'";
		}
		if(!updatedEmail.getGroupName().isEmpty()){
			if(!updatedEmail.getEmail().isEmpty() || !updatedEmail.getDisplayName().isEmpty()){
				fields += ",";
			}
			fields += "groupName = \'" + updatedEmail.getGroupName() + "\'";
		}
		try{
			PreparedStatement ps = conn.prepareStatement(
					"update EMAIL set " + fields + " where email = \'" + emailAddress + "\'");
			ps.executeUpdate();
		}
		catch(SQLException e){
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * listEmails
	 * 
	 * Lists all the email entries in the database
	 * 
	 * @return a list of all the email entries in the database.
	 * @throws IOException if an SQLException occurs.
	 */
	public List<EmailData> listEmails() throws IOException{
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMAIL");
			ResultSet rs = ps.executeQuery();
			
			List<EmailData> list = new ArrayList<EmailData>();
			while(rs.next()){
				String email = rs.getString("email");
				String displayName = rs.getString("displayName");
				String groupName = rs.getString("groupName");
				list.add(new EmailData(email, displayName, groupName));
			}
			return list;
		}
		catch(SQLException e){
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * getEmailsLike
	 * 
	 * Gets all the emails in the database that match any one or more of the criteria
	 * specified in emailSearch
	 * 
	 * @param emailSearch - the meta-data to search for in any record in the database.
	 * @return A list of all the emails in the database that matches one or more of the criteria
	 * @throws Exception if 
	 * @throws IOException
	 */
	public List<EmailData> getEmailsLike(EmailData emailSearch) throws Exception, IOException{
		if(emailSearch.isEmpty()){
			throw new Exception("Search criteria cannot be empty");
		}
		
		String params = "";
		if(!emailSearch.getEmail().isEmpty()){
			params = "EMAIL.email = \'" + emailSearch.getEmail() + "\' ";
		}
		if(!emailSearch.getDisplayName().isEmpty()){
			if(!emailSearch.getEmail().isEmpty())
				params += "OR ";
			params += "EMAIL.displayName = \'" + emailSearch.getDisplayName() + "\' ";
		}
		if(!emailSearch.getGroupName().isEmpty()){
			if(!emailSearch.getEmail().isEmpty() || !emailSearch.getDisplayName().isEmpty())
				params += "OR ";

			params += "EMAIL.groupName = \'" + emailSearch.getGroupName() + "\' ";
		}
		try{	
			PreparedStatement ps = conn.prepareStatement(
					"select * from EMAIL where " + params);
			ResultSet rs = ps.executeQuery();

			List<EmailData> emailList = new ArrayList<EmailData>();
			while(rs.next()){
				String email = rs.getString("email");
				String displayName = rs.getString("displayName");
				String groupName = rs.getString("groupName");
				emailList.add(new EmailData(email, displayName, groupName));
			}

			return emailList;
		}
		catch(SQLException e){
			throw new IOException(e.getMessage());
		}
	}
}
