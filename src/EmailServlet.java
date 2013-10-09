
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class EmailServlet extends HttpServlet
{
	private static final String SUCCESS = "SUCCESS";
	private static final String SQLEXCEPTION = "A database error occurred";
	private static final String NONEMPTYFIELD = "A nonempty field must be given";
	private EmailDBC edbc;

	public EmailServlet(){
		super();
		edbc = new EmailDBC();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
		PrintWriter out = resp.getWriter();

		//Assert request has at least 1 header of
		//Email-Address, Display-Name, or Group-Name
		String email = req.getHeader("Email-Address");
		String displayName = req.getHeader("Display-Name");
		String groupName = req.getHeader("Group-Name");

		//Get fields to search for
		EmailData emailSearch = new EmailData();
		if(email != null){
			emailSearch.setEmail(email);
		}
		if(displayName != null){
			emailSearch.setDisplayName(displayName);
		}
		if(groupName != null){
			emailSearch.setGroupName(groupName);
		}

		int status;
		String responseText;
		List<EmailData> emailDataList = null;
		if(!emailSearch.isEmpty()){
			//Query DB for email.
			try{
				emailDataList = edbc.getEmail(emailSearch);
				status = 200;
				responseText = SUCCESS;
			}
			catch(IOException e){
				status = 500;
				responseText = SQLEXCEPTION;
			}
			catch(Exception e){
				status = 400;
				responseText = NONEMPTYFIELD;
			}
		}
		else{
			status = 400;
			responseText = NONEMPTYFIELD;
		}

		//construct xml reponse and send
		constructXMLResponse(status, responseText, emailDataList, resp.getOutputStream());
	}
	
	private void sendXMLResponse(int status, String responseText,
			List<EmailData> dataList, OutputStream out){
		Element response = new Element("response");
		Document doc = new Document(response);
		
		Element code = new Element("code");
		code.setText(status+"");
		Element msg = new Element("msg");
		msg.setText(responseText);
		Element emailList = new Element("email-list");
		
		for(EmailData item : dataList){
			Element email = new Element("email");
			
			Element displayName = new Element("displayName");
			displayName.setText(item.getDisplayName());
			Element address = new Element("address");
			address.setText(item.getEmail());
			Element group = new Element("group");
			group.setText(item.getGroupName());
			
			email.addContent(displayName);
			email.addContent(address);
			email.addContent(group);
			
			emailList.addContent(email);
		}
		
		doc.getRootElement().addContent(code);
		doc.getRootElement().addContent(msg);
		if(emailList.getChildren() != null && emailList.getChildren().size() != 0){
			doc.getRootElement().addContent(emailList);
		}
		
		XMLOutputter xmlOut = new XMLOutputter();
		xmlOut.setFormat(Format.getRawFormat());
		xmlOut.output(doc, out);
	}
}