<%@ page language="java" contentType="application/json; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*,flexjson.*,fr.xebia.jsondemo.*"%>

<%
	// Récupérer la chaine recherchée
	String searchKey = request.getParameter("value");

	// Construire le carnet d'adresses
	Vector<Contact> contacts = new Vector<Contact>();
	contacts.add(new Contact(0, "Jean Dupond", "jean@dupond.fr"));
	contacts.add(new Contact(1, "Christophe Dupond",
			"christophe@dupond.fr"));
	contacts
			.add(new Contact(2, "Séven Le Mesle", "slemesle@gmail.com"));
	contacts.add(new Contact(3, "John Doe", "jdoe@gmail.com"));
	contacts.add(new Contact(4, "Jack Lim", "jlim@lim.com"));
	contacts.add(new Contact(5, "Patricia Smith", "psmith@yahoo.com"));
	contacts.add(new Contact(6, "George Cohen", "gcohen@msn.com"));
%>
<%=/* Serialization des contacts trouvés en JSON */
			new JSONSerializer().serialize(contacts)%>
