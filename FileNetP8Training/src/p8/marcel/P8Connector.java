package p8.marcel.datamodel;

import java.util.Iterator;

import javax.security.auth.Subject;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;;

public class P8Connector {
	public static void main(String[] args) {
		// Set connection parameters; substitute for the placeholders.
		String uri = "http://ecmdemo1.ecm.ibm.local:9080/wsi/FNCEWS40MTOM";
		String username = "p8admin";
		String password = "filenet";

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

			CommandProcessor.ExecuteChanges(store);

		} finally {
			UserContext.get().popSubject();
		}
	}
}
