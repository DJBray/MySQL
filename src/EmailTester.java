import java.util.List;


public class EmailTester {
	public static void main(String args[]) throws Exception{
		EmailDBC edbc = new EmailDBC();
		
		System.out.println("Creating hello@example.com...");
		edbc.createEmail(new EmailData(
				"hello@example.com", "hello", "example"));
		
		System.out.println("Creating hi@foo.bar...");
		edbc.createEmail(new EmailData(
				"hi@foo.bar", "ho", "fu"));
		
		System.out.println("Getting email: hello@example.com");
		List<EmailData> list = 
				edbc.getEmail(new EmailData("hello@example.com"));
		for(EmailData item : list){
			System.out.println(item.toString());
		}
		
		System.out.println("Creating hello@nope.com...");
		edbc.createEmail(new EmailData(
				"hello@nope.com", "hello", "nope"));
		
		System.out.println("Getting groupName: hello");
		EmailData d = new EmailData();
		d.setDisplayName("hello");
		list = edbc.getEmail(d);
		for(EmailData item : list){
			System.out.println(item.toString());
		}
		
		System.out.println("Listing all entries...");
		list = edbc.listEmails();
		for(EmailData item : list){
			System.out.println(item.toString());
		}
		
		System.out.println("Deleting email: hello@nope.com");
		edbc.deleteEmail("hello@nope.com");
		
		System.out.println("Getting groupName: hello");
		list = edbc.getEmail(d);
		for(EmailData item : list){
			System.out.println(item.toString());
		}
		
		System.out.println("Updating hi@foo.bar");
		edbc.updateEmail("hi@foo.bar", new EmailData(
				"hi@foo.bar", "hi", "foo"));
		
		System.out.println("Getting all entries with group foo and name hello");
		d = new EmailData();
		d.setDisplayName("hello");
		d.setGroupName("foo");
		list = edbc.getEmailsLike(d);
		for(EmailData item : list){
			System.out.println(item.toString());
		}
	}
}
