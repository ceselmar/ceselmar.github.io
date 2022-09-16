package p8.marcel.documentHandlers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;

public class DocCommands {
	public static void ExecuteChanges(ObjectStore os) {
		// GetDocumentContent(os, "TestDocSubSubClass2", "/Test/Test Doc 1");
		// GetDocumentContentElements(os, "/Test/Test Doc 1");

		GetDocumentProperties(os, "TestDocSubSubClass2", "/Test/Test Doc 1");
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
		// Document doc = Factory.Document.getInstance(os, docClassName,
		// docPath);
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
