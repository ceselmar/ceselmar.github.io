package p8.marcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.security.auth.Subject;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

import p8.marcel.datamodel.DataModelCommandProcessor;
import p8.marcel.documentHandlers.DocCommands;

public class P8Connector {
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
	
	Properties p8props = new Properties();
	try {
	    FileInputStream fis = new FileInputStream("resources/P8Connection.properties");
	    p8props.load(fis);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return;
	} catch (IOException e) {
	    e.printStackTrace();
	    return;
	}
	
	String uri = p8props.getProperty("uri");
	String username = p8props.getProperty("username");
	String password = p8props.getProperty("password");
	
//	System.out.println(uri + " " + username + " " + password);
	// Make connection.
	Connection conn = Factory.Connection.getConnection(uri);
	Subject subject = UserContext.createSubject(conn, username, password, "FileNetP8WSI");
	UserContext.get().pushSubject(subject);

	try {
	    // Get default domain.
	    Domain domain = Factory.Domain.fetchInstance(conn, null, null);
	    System.out.println("Domain: " + domain.get_Name());

	    // Get object stores for domain.
	    ObjectStoreSet osSet = domain.get_ObjectStores();
	    ObjectStore store = null;
	    Iterator osIter = osSet.iterator();

	    while (osIter.hasNext() == true) {
		store = (ObjectStore) osIter.next();
		if (store.get_SymbolicName().equals("ECM")) {
		    break;
		}
	    }
	    System.out.println("Object store: " + store.get_Name());
	    System.out.println("Connection to Content Platform Engine successful");

	    DataModelCommandProcessor.executeChanges(domain,store);
//	    DocCommands.executeChanges(store);
	} finally {
	    UserContext.get().popSubject();
	}
    }
}
