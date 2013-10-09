
public class EmailData {
	private String email;
	private String displayName;
	private String groupName;
	
	public EmailData(){
		email = "";
		displayName = "";
		groupName = "";
	}
	
	public EmailData(String email){
		this();
		this.email = email;
	}
	
	public EmailData(String email, String displayName){
		this(email);
		this.displayName = displayName;
	}
	
	public EmailData(String email, String displayName, String groupName){
		this(email,displayName);
		this.groupName = groupName;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	public void setGroupName(String groupName){
		this.groupName = groupName;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public String getGroupName(){
		return groupName;
	}
	
	@Override
	public String toString(){
		return "\nEmail: "+ email 
				+"\nDisplayName: " + displayName
				+"\nGroupName: " + groupName;
	}
	
	public boolean isEmpty(){
		return email.isEmpty() && displayName.isEmpty() && groupName.isEmpty();
	}
}
