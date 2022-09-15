package p8.marcel;

import java.util.Iterator;

import com.filenet.api.admin.LocalizedString;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.admin.PropertyTemplateBoolean;
import com.filenet.api.admin.PropertyTemplateDateTime;
import com.filenet.api.admin.PropertyTemplateInteger32;
import com.filenet.api.admin.PropertyTemplateString;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.LocalizedStringList;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;

public class CommandProcessor {
	public static void ExecuteChanges(ObjectStore os) {
		createNewPropertyTemplate(os, "FDA_Test1", "STRING", Cardinality.SINGLE, "FDA Test");
		createNewPropertyTemplate(os, "FDA_Test2", "STRING", Cardinality.SINGLE, "FDA Test 2");
		createNewPropertyTemplate(os, "FDA_Test2", "STRING", Cardinality.SINGLE, "FDA Test 2");
		createNewPropertyTemplate(os, "FDA_Test3", "INTEGER", Cardinality.SINGLE, "FDA Test 3");
		createNewPropertyTemplate(os, "FDA_Test4", "BOOLEAN", Cardinality.SINGLE, "FDA Test 4");
		// DeletePropertyTemplate(os, "FDA_Test1");
		// DeletePropertyTemplate(os, "FDA_Test2");
		// DeletePropertyTemplate(os, "FDA_Test3");
		// DeletePropertyTemplate(os, "FDA_Test4");
		// Id propID = new Id("{97811EE8-4AEA-4A58-B6FE-CFAE90A7C90B}");
		// FetchPropertyTemplate(os, propID);
	}

	private static void DeletePropertyTemplate(ObjectStore os, String propSymbolicName) {
		String sqlCondition = "SymbolicName = '" + propSymbolicName + "'";
		SearchSQL sql = new SearchSQL(
				"SELECT " + "SymbolicName" + " FROM " + "PropertyTemplate" + " WHERE " + sqlCondition);
		SearchScope scope = new SearchScope(os);
		IndependentObjectSet results = scope.fetchObjects(sql, 1, null, false);

		if (results.isEmpty()) {
			System.out.println("The property " + propSymbolicName + " doesn't exist.");
			return;
		}

		Iterator resultsIter = results.iterator();
		while (resultsIter.hasNext()) {
			PropertyTemplate pT = (PropertyTemplate) resultsIter.next();
			try {
				System.out.println("Trying to delete property " + pT.get_SymbolicName());
				pT.delete();
				pT.save(RefreshMode.NO_REFRESH);
				System.out.println(propSymbolicName + " was deleted.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	private static void createNewPropertyTemplate(ObjectStore os, String propSymbolicName, String propType,
			Cardinality cardin, String propDisplayName) {
		// Check if the property already exists
		String sqlCondition = "SymbolicName = '" + propSymbolicName + "'";
		SearchSQL sql = new SearchSQL(
				"SELECT " + "SymbolicName" + " FROM " + "PropertyTemplate" + " WHERE " + sqlCondition);
		SearchScope scope = new SearchScope(os);
		IndependentObjectSet results = scope.fetchObjects(sql, 1, null, false);
		if (!results.isEmpty()) {
			System.out.println("Property " + propSymbolicName + " already exists.");
			return;
		}

		if (propType.equals("STRING")) {
			PropertyTemplateString pTString = Factory.PropertyTemplateString.createInstance(os);
			createPropertyTemplate(os, propSymbolicName, cardin, propDisplayName, pTString);
		} else if (propType.equals("INTEGER")) {
			PropertyTemplateInteger32 pTInteger = Factory.PropertyTemplateInteger32.createInstance(os);
			createPropertyTemplate(os, propSymbolicName, cardin, propDisplayName, pTInteger);
		} else if (propType.equals("BOOLEAN")) {
			PropertyTemplateBoolean pTBool = Factory.PropertyTemplateBoolean.createInstance(os);
			createPropertyTemplate(os, propSymbolicName, cardin, propDisplayName, pTBool);
		} else if (propType.equals("DATE")) {
			PropertyTemplateDateTime pTDate = Factory.PropertyTemplateDateTime.createInstance(os);
			createPropertyTemplate(os, propSymbolicName, cardin, propDisplayName, pTDate);
		} else {
			System.out.println("Property type " + propType + " is not yet implemented.");
		}
	}

	private static void createPropertyTemplate(ObjectStore os, String propSymbolicName, Cardinality cardin,
			String propDisplayName, PropertyTemplate pT) {
		try {
			updatePropertyValues(os, propSymbolicName, cardin, propDisplayName, pT);
			pT.save(RefreshMode.NO_REFRESH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Property " + propSymbolicName + " was created.");

	}

	private static void updatePropertyValues(ObjectStore os, String propSymbolicName, Cardinality cardin,
			String propDisplayName, PropertyTemplate pT) {
		LocalizedString localDisplayName = Factory.LocalizedString.createInstance();
		localDisplayName.set_LocalizedText(propDisplayName);
		localDisplayName.set_LocaleName(os.get_LocaleName());

		pT.set_SymbolicName(propSymbolicName);
		pT.set_Cardinality(cardin);
		pT.set_DisplayNames(Factory.LocalizedString.createList());
		pT.get_DisplayNames().add(localDisplayName);
	}
}
