package p8.marcel;

import java.util.Iterator;

import javax.security.auth.Subject;

import com.filenet.api.admin.LocalizedString;
import com.filenet.api.collection.LocalizedStringList;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;

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

			ExecuteChanges(store);

		} finally {
			UserContext.get().popSubject();
		}
	}

	private static void ExecuteChanges(ObjectStore os) {
		CreateNewPropertyTemplate(os, "FDA_Test1", Cardinality.SINGLE, "FDA Test");
		Id propID = new Id("{97811EE8-4AEA-4A58-B6FE-CFAE90A7C90B}");
		// FetchPropertyTemplate(os, propID);
	}

	private static void FetchPropertyTemplate(ObjectStore os, Id objectId) {
		com.filenet.api.admin.PropertyTemplate prop = Factory.PropertyTemplate.fetchInstance(os, objectId, null);
		LocalizedStringList displNameProp = prop.get_DisplayNames();
		Iterator propIter = displNameProp.iterator();

		while (propIter.hasNext()) {
			LocalizedString propListValue = (LocalizedString) propIter.next();
			System.out.println(propListValue.get_LocaleName());
			System.out.println(propListValue.get_LocalizedText());
		}

	}

	private static void CreateNewPropertyTemplate(ObjectStore os, String propSymbolicName, Cardinality cardin,
			String propDisplayName) {
		// TODO Auto-generated method stub
		try {
			com.filenet.api.admin.PropertyTemplateString fdaTest = Factory.PropertyTemplateString.createInstance(os);
			LocalizedString localDisplayName = Factory.LocalizedString.createInstance();
			localDisplayName.set_LocalizedText(propDisplayName);
			localDisplayName.set_LocaleName(os.get_LocaleName());

			fdaTest.set_SymbolicName(propSymbolicName);
			fdaTest.set_Cardinality(cardin);
			fdaTest.set_DisplayNames(Factory.LocalizedString.createList());
			fdaTest.get_DisplayNames().add(localDisplayName);

			fdaTest.save(RefreshMode.NO_REFRESH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
