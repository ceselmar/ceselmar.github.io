package p8.marcel.documentHandlers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DependentObjectList;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PageIterator;
import com.filenet.api.constants.AccessRight;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.security.AccessPermission;

public class DocCommands {
	public static void ExecuteChanges(ObjectStore os) {
		// GetDocumentContent(os, "TestDocSubSubClass2", "/Test/Test Doc 1");
		// GetDocumentContentElements(os, "/Test/Test Doc 1");

		// GetDocumentProperties(os, "TestDocSubSubClass2", "/Test/Test Doc 1");
		// GetExistingPropertyTemplates(os);
		removeDeletePermissionFromDocument(os,"/Test/Test Doc 1");
	}

	private static void removeDeletePermissionFromDocument(ObjectStore os, String path) {
		PropertyFilter filter = new PropertyFilter();
		filter.addIncludeProperty(0, null, false, PropertyNames.PERMISSIONS);
		Document doc = Factory.Document.fetchInstance(os, path, filter);
//		Document doc = Factory.Document.getInstance(os, "TestDocSubSubClass2", path);
		Properties props = doc.getProperties();
		
		if (! props.isPropertyPresent(PropertyNames.PERMISSIONS)) {
			System.out.println("Document security was not retrieved.");
		}
		
		AccessPermissionList acl = (AccessPermissionList) props.getDependentObjectListValue(PropertyNames.PERMISSIONS);
		Iterator aclIter = acl.iterator();
		while (aclIter.hasNext()) {
			AccessPermission ace = (AccessPermission) aclIter.next();
			PermissionSource acePS = ace.get_PermissionSource();
			
			if (acePS.equals(PermissionSource.SOURCE_PARENT)) {
				continue;
			}
			int rights = ace.get_AccessMask().intValue();
			String grantee = ace.get_GranteeName();
			
			if (! grantee.equalsIgnoreCase("Business Analysts@ecm.ibm.local")) {
				continue;
			}
			System.out.println(rights + " " + grantee);
	 /* Removing delete permission */
			if ((rights & AccessRight.DELETE_AS_INT) != 0) {
				rights &= ~AccessRight.DELETE_AS_INT;
				ace.set_AccessMask(rights);
			}
			
	/* Adding delete permission	*/
//			if ((rights & AccessRight.DELETE_AS_INT) == 0) {
//				rights |= AccessRight.DELETE_AS_INT;
//				ace.set_AccessMask(rights);
//			}
			System.out.println(rights + " " + grantee);
		}		
		doc.save(RefreshMode.NO_REFRESH);
	}

	private static void GetExistingPropertyTemplates(ObjectStore os) {
		// IndependentObjectSet results = searchPropertyTemplates(os);
		IndependentObjectSet results = searchPagedPropertyTemplates(os);

		if (results.isEmpty()) {
			System.out.println("No results returned.");
		}
		String name = "FDA Properties.txt";

		try {
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(name));
			writePropertiesToFile(results, writer);
			// writePagedPropertiesToFile(results, writer);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writePagedPropertiesToFile(IndependentObjectSet results, BufferedOutputStream writer) {
		PageIterator iter = results.pageIterator();

		while (iter.nextPage()) {
			for (Object obj : iter.getCurrentPage()) {
				PropertyTemplate prop = (PropertyTemplate) obj;
				System.out.println(prop.get_SymbolicName());
			}

		}

	}

	private static void writePropertiesToFile(IndependentObjectSet results, BufferedOutputStream writer)
			throws IOException {
		Iterator resultsIter = results.iterator();
		while (resultsIter.hasNext()) {
			PropertyTemplate pT = (PropertyTemplate) resultsIter.next();
			System.out.println("Property " + pT.get_SymbolicName() + " exists.");
			// writePropertyToFile(pT, writer);
		}
	}

	private static IndependentObjectSet searchPropertyTemplates(ObjectStore os) {
		String queryString = "SELECT SymbolicName FROM PropertyTemplate WHERE SymbolicName like 'FDA_%'";
		SearchSQL sql = new SearchSQL(queryString);
		SearchScope scope = new SearchScope(os);
		PropertyFilter filter = new PropertyFilter();
		filter.addIncludeProperty(0, null, false, "SymbolicName");
		IndependentObjectSet results = scope.fetchObjects(sql, 1, filter, false);
		return results;
	}

	private static IndependentObjectSet searchPagedPropertyTemplates(ObjectStore os) {
		String queryString = "SELECT SymbolicName FROM PropertyTemplate WHERE SymbolicName like 'FDA_%'";
		SearchSQL sql = new SearchSQL(queryString);
		SearchScope scope = new SearchScope(os);
		PropertyFilter filter = new PropertyFilter();
		filter.addIncludeProperty(0, null, false, "SymbolicName");
		IndependentObjectSet results = scope.fetchObjects(sql, 50, filter, true);
		return results;
	}

	private static void writePropertyToFile(PropertyTemplate pT, BufferedOutputStream writer) throws IOException {
		String propName = pT.get_SymbolicName() + "\r\n";
		byte[] bytes = propName.getBytes();
		int len = bytes.length;
		writer.write(bytes, 0, len);
	}

	@SuppressWarnings("deprecation")
	private static void GetDocumentProperties(ObjectStore os, String docClass, String docPath) {
		Document doc = Factory.Document.getInstance(os, docClass, docPath);
		doc.fetchProperties(new String[] { "DocumentTitle", "DateCreated" });
		System.out.println(doc.getProperties().getStringValue("DocumentTitle"));
		System.out.println(doc.getProperties().getStringValue("Creator"));

		PropertyFilter filter = new PropertyFilter();

		filter.addIncludeProperty(new FilterElement(null, null, null, "DocumentTitle DateCreated"));

		doc = Factory.Document.fetchInstance(os, docPath, filter);
		System.out.println(doc.getProperties().getStringValue("DocumentTitle"));
		System.out.println(doc.getProperties().getStringValue("Creator"));

		doc = Factory.Document.fetchInstance(os, docPath, null);
		System.out.println(doc.getProperties().getStringValue("DocumentTitle"));
		System.out.println(doc.getProperties().getStringValue("Creator"));

	}

	private static void GetDocumentContentElements(ObjectStore os, String docPath) {
		Document doc = Factory.Document.fetchInstance(os, docPath, null);
		ContentElementList elements = doc.get_ContentElements();
		ContentTransfer element = (ContentTransfer) elements.get(0);

		String fileName = element.get_RetrievalName();
		InputStream stream = element.accessContentStream();

		System.out.println("Accessing file " + fileName);
		Double size = writeContent(stream, fileName);
		Double expected = element.get_ContentSize();

		if (!size.equals(expected)) {
			System.err.println("Invalid content size retrieved. Size is " + size.toString() + " and expected "
					+ expected.toString());
		}
	}

	private static Double writeContent(InputStream s, String fileName) {
		Double size = new Double(0);

		try {
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(fileName));
			int bufferSize;
			byte[] buffer = new byte[1024];

			while ((bufferSize = s.read(buffer)) != -1) {
				size += bufferSize;
				writer.write(buffer, 0, bufferSize);
			}

			writer.close();
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return size;
	}

	private static void GetDocumentContent(ObjectStore os, String docClassName, String docPath) {
		Document doc = Factory.Document.fetchInstance(os, docPath, null);
		InputStream stream = doc.accessContentStream(0);

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String text = "", line;

		try {
			while ((line = reader.readLine()) != null) {
				text += line;
			}
			System.out.println(text);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
