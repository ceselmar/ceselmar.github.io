package p8.marcel.documentHandlers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PageIterator;
import com.filenet.api.constants.AccessRight;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.DynamicReferentialContainmentRelationship;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
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
	// createFileFromExistingPropertyTemplates(os,"FDA Properties.txt");
	// removeDeletePermissionFromDocument(os, "/Test/Test Doc 1");
	// createNewDocumentVersion(os, "TestDocSubSubClass2", "/Test/Test Doc
	// 1");
	createFileInP8WithPropertyTemplates(os,"FDA Properties","Test Outputs");
    }

    private static void createFileInP8WithPropertyTemplates(ObjectStore os, String docName, String folderName) {
	String docClass = "TestDocClass";
	String docPath = "/" + folderName + "/" + docName;
	String fileName = docName+".txt";

	// Check if folder exists
	SearchSQL sql = new SearchSQL("SELECT FolderName FROM Folder WHERE FolderName = '" + folderName + "'");
	SearchScope scope = new SearchScope(os);
	IndependentObjectSet results = scope.fetchObjects(sql, 0, null, false);

	// If not create folder
	Folder fold = null;
	if (results.isEmpty()) {
	    System.out.println("Folder " + folderName + " does not exist.");
	    fold = Factory.Folder.createInstance(os, "Folder");
	    fold.set_Parent(os.get_RootFolder());
	    fold.set_FolderName(folderName);
	    fold.save(RefreshMode.NO_REFRESH);
	    System.out.println("Folder " + folderName + " was created.");
	} else {
	    Iterator foldIter = results.iterator();
	    while (foldIter.hasNext()) {
		fold = (Folder) foldIter.next();
		break;
	    }
	}

	// Check if document exists
	Document doc = null;
	try {
	    doc = Factory.Document.fetchInstance(os, docPath, null);
	    System.out.println("Document " + docName + " exists in folder " + folderName);
	} catch (Exception ex1) {
	    EngineRuntimeException e1 = (EngineRuntimeException) ex1;
	    if (e1.getExceptionCode().equals(ExceptionCode.E_OBJECT_NOT_FOUND)) {
		// Create new document and file it in the target folder
		doc = Factory.Document.createInstance(os, docClass);
		Properties props = doc.getProperties();
		props.putValue("DocumentTitle", docName);
		doc.save(RefreshMode.REFRESH);
		System.out.println("Document " + docName + " was created.");
		fileDocumentToFolder(doc, fold);
	    } else {
		e1.printStackTrace();
	    }
	}

	// Add new document version
	if (doc.get_IsReserved() && !doc.get_MajorVersionNumber().equals(0)) {
	    Document reservationDoc = (Document) doc.cancelCheckout();
	    reservationDoc.save(RefreshMode.NO_REFRESH);
	    System.out.println("Checkout canceled on document " + docName);
	}

	if (!doc.get_MajorVersionNumber().equals(0)) {
	    doc.checkout(ReservationType.EXCLUSIVE, null, null, null);
	    doc.save(RefreshMode.REFRESH);
	}

	Document res = (Document) doc.get_Reservation();
	res.getProperties().putValue("DocumentTitle", docName);

	ContentElementList list = Factory.ContentElement.createList();
	ContentTransfer element = Factory.ContentTransfer.createInstance();

	element.set_ContentType("text/plain");
	element.set_RetrievalName(fileName);

	try {
	    createFileFromExistingPropertyTemplates(os, fileName);	    
	    element.setCaptureSource(new FileInputStream(new File(fileName)));
	    list.add(element);
	    res.set_ContentElements(list);
	    res.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
	    res.save(RefreshMode.NO_REFRESH);
	    System.out.println("New document version was created with name " + element.get_RetrievalName());
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    private static void fileDocumentToFolder(Document doc, Folder fold) {
	String containmentName = doc.get_Name();
//	System.out.println("Folder " + fold.get_FolderName() + " was found.");

	try {
	    DynamicReferentialContainmentRelationship drcr = (DynamicReferentialContainmentRelationship) fold.file(doc,
		    AutoUniqueName.NOT_AUTO_UNIQUE, containmentName, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
	    drcr.save(RefreshMode.NO_REFRESH);
	    System.out.println("Document " + containmentName + " was filed into the folder " + fold.get_FolderName());
	} catch (Exception ex2) {
	    EngineRuntimeException e2 = (EngineRuntimeException) ex2;
	    if (e2.getExceptionCode().equals(ExceptionCode.E_NOT_UNIQUE)) {
		System.err.println(
			"Document " + containmentName + " already exists in the folder " + fold.get_FolderName());
	    } else {
		e2.printStackTrace();
	    }
	}
    }

    private static void createNewDocumentVersion(ObjectStore os, String docType, String path) {
	Document doc = Factory.Document.fetchInstance(os, path, null);

	if (doc.get_IsReserved()) {
	    Document reservationDoc = (Document) doc.cancelCheckout();
	    reservationDoc.save(RefreshMode.NO_REFRESH);
	    System.out.println("Checkout canceled on document " + path);
	}
	doc.checkout(ReservationType.EXCLUSIVE, null, null, null);
	doc.save(RefreshMode.REFRESH);
	Document res = (Document) doc.get_Reservation();
	res.getProperties().putValue("DocumentTitle", "NextVersion");

	// Create content
	ContentElementList list = Factory.ContentElement.createList();
	ContentTransfer element = Factory.ContentTransfer.createInstance();
	element.set_ContentType("text/plain");
	element.set_RetrievalName("file.txt");

	try {
	    element.setCaptureSource(new FileInputStream(new File("test.csv")));
	    list.add(element);
	    res.set_ContentElements(list);
	    res.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
	    res.save(RefreshMode.NO_REFRESH);
	    System.out.println("New document version was created with name " + element.get_RetrievalName());
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    private static void removeDeletePermissionFromDocument(ObjectStore os, String path) {
	PropertyFilter filter = new PropertyFilter();
	filter.addIncludeProperty(0, null, false, PropertyNames.PERMISSIONS);
	Document doc = Factory.Document.fetchInstance(os, path, filter);
	// Document doc = Factory.Document.getInstance(os,
	// "TestDocSubSubClass2", path);
	Properties props = doc.getProperties();

	if (!props.isPropertyPresent(PropertyNames.PERMISSIONS)) {
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

	    if (!grantee.equalsIgnoreCase("Business Analysts@ecm.ibm.local")) {
		continue;
	    }
	    System.out.println(rights + " " + grantee);
	    /* Removing delete permission */
	    if ((rights & AccessRight.DELETE_AS_INT) != 0) {
		rights &= ~AccessRight.DELETE_AS_INT;
		ace.set_AccessMask(rights);
	    }

	    /* Adding delete permission */
	    // if ((rights & AccessRight.DELETE_AS_INT) == 0) {
	    // rights |= AccessRight.DELETE_AS_INT;
	    // ace.set_AccessMask(rights);
	    // }
	    System.out.println(rights + " " + grantee);
	}
	doc.save(RefreshMode.NO_REFRESH);
    }

    private static void createFileFromExistingPropertyTemplates(ObjectStore os, String name) {
	// IndependentObjectSet results = searchPropertyTemplates(os);
	IndependentObjectSet results = searchPagedPropertyTemplates(os);

	if (results.isEmpty()) {
	    System.out.println("No results returned.");
	}

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
//	    System.out.println("Property " + pT.get_SymbolicName() + " exists.");
	    writePropertyToFile(pT, writer);
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
