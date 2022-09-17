package p8.marcel.datamodel;

import java.util.Iterator;
import java.util.List;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.DocumentClassDefinition;
import com.filenet.api.admin.LocalizedString;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.admin.PropertyTemplateBoolean;
import com.filenet.api.admin.PropertyTemplateDateTime;
import com.filenet.api.admin.PropertyTemplateInteger32;
import com.filenet.api.admin.PropertyTemplateString;
import com.filenet.api.collection.ClassDefinitionSet;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.LocalizedStringList;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.StringList;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;

public class DataModelCommandProcessor {
	public static void ExecuteChanges(Domain domain, ObjectStore os) {
		UpdatingBatch batch = UpdatingBatch.createUpdatingBatchInstance(domain, RefreshMode.REFRESH);
		StringList updatedProperties = Factory.StringList.createList();
		// createNewPropertyTemplate(os, "FDA_Test1", "STRING",
		// Cardinality.SINGLE, "FDA Test");
		// createNewPropertyTemplate(os, "FDA_Test2", "STRING",
		// Cardinality.SINGLE, "FDA Test 2");
		// createNewPropertyTemplate(os, "FDA_Test2", "STRING",
		// Cardinality.SINGLE, "FDA Test 2");
		// createNewPropertyTemplate(os, "FDA_Test3", "INTEGER",
		// Cardinality.SINGLE, "FDA Test 3");
		// createNewPropertyTemplate(os, "FDA_Test4", "BOOLEAN",
		// Cardinality.SINGLE, "FDA Test 4");

		 AssignPropertyToDocClass(os, "FDA_Test1", "TestDocSubSubClass2");
		 AssignPropertyToDocClass(os, "FDA_Test2", "TestDocClass");
		 AssignPropertyToDocClass(os, "FDA_Test2", "TestDocSubClass");
		 AssignPropertyToDocClass(os, "FDA_Test32", "TestDocClass");
		 AssignPropertyToDocClass(os, "FDA_Test2", "TestDocClass95");
		 AssignPropertyToDocClass(os, "FDA_Test3", "TestDocSubClass");
		 AssignPropertyToDocClass(os, "FDA_Test4", "TestDocSubSubClass1");

//		DeletePropertyTemplate(os, "FDA_Test1");
//		DeletePropertyTemplate(os, "FDA_Test2");
//		DeletePropertyTemplate(os, "FDA_Test3");
//		DeletePropertyTemplate(os, "FDA_Test4");
//		DeletePropertyTemplate(os, "FDA_Test5");

		// createNewPropertyTemplate(os, "FDA_Test5", "STRING",
		// Cardinality.SINGLE, "FDA Test 5", "FDA German Test 5", true,
		// "FDA_Choicelist", 150, "Description EN", "Description DE");

//		createNewPropertyTemplateBatch(batch, os, updatedProperties, "FDA_Test1", "STRING", Cardinality.SINGLE,
//				"FDA Test");
//		createNewPropertyTemplateBatch(batch, os, updatedProperties, "FDA_Test2", "STRING", Cardinality.SINGLE,
//				"FDA Test 2");
//		createNewPropertyTemplateBatch(batch, os, updatedProperties, "FDA_Test2", "STRING", Cardinality.SINGLE,
//				"FDA Test 2");
//		createNewPropertyTemplateBatch(batch, os, updatedProperties, "FDA_Test3", "INTEGER", Cardinality.SINGLE,
//				"FDA Test 3");
//		createNewPropertyTemplateBatch(batch, os, updatedProperties, "FDA_Test4", "BOOLEAN", Cardinality.SINGLE,
//				"FDA Test 4");

		try {
			if (batch.hasPendingExecute()) {
				batch.updateBatch();
				System.out.println("Batch was executed.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createNewPropertyTemplateBatch(UpdatingBatch batch, ObjectStore os, StringList batchItems,
			String propSymbolicName, String propType, Cardinality cardin, String propDisplayName) {
		IndependentObjectSet results = searchPropertyBySymbolicName(os, propSymbolicName);
		if (!results.isEmpty()) {
			System.out.println("Property " + propSymbolicName + " already exists.");
			return;
		}
		if (batchItems.contains(propSymbolicName)) {
			System.out.println("Property " + propSymbolicName + " is already in the batch.");
			return;
		}

		if (propType.equals("STRING")) {
			PropertyTemplateString pTString = Factory.PropertyTemplateString.createInstance(os);
			createPropertyTemplateBatch(batch, os, propSymbolicName, cardin, propDisplayName, pTString);
		} else if (propType.equals("INTEGER")) {
			PropertyTemplateInteger32 pTInteger = Factory.PropertyTemplateInteger32.createInstance(os);
			createPropertyTemplateBatch(batch, os, propSymbolicName, cardin, propDisplayName, pTInteger);
		} else if (propType.equals("BOOLEAN")) {
			PropertyTemplateBoolean pTBool = Factory.PropertyTemplateBoolean.createInstance(os);
			createPropertyTemplateBatch(batch, os, propSymbolicName, cardin, propDisplayName, pTBool);
		} else if (propType.equals("DATE")) {
			PropertyTemplateDateTime pTDate = Factory.PropertyTemplateDateTime.createInstance(os);
			createPropertyTemplateBatch(batch, os, propSymbolicName, cardin, propDisplayName, pTDate);
		} else {
			System.err.println("Property type " + propType + " is not yet implemented.");
		}
		batchItems.add(propSymbolicName);
	}

	private static void createPropertyTemplateBatch(UpdatingBatch batch, ObjectStore os, String propSymbolicName,
			Cardinality cardin, String propDisplayName, PropertyTemplate pT) {
		try {
			updatePropertyValues(os, propSymbolicName, cardin, propDisplayName, pT);
			batch.add(pT, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Property " + propSymbolicName + " was added to the batch.");

	}

	// private static void createNewPropertyTemplate(ObjectStore os, String
	// propSymbolicName, String propType,
	// Cardinality cardin, String propDisplayName, String propDisplayDE, boolean
	// CBREnabled, String choiceListName, int maximumLength, String
	// descriptionEN, String descriptionDE) {
	// createNewPropertyTemplate(os, propSymbolicName, propType, cardin,
	// propDisplayName);
	//
	// IndependentObjectSet results = searchPropertyBySymbolicName(os,
	// propSymbolicName);
	// Iterator resultsIter = results.iterator();
	//
	// if (results.isEmpty()) {
	// System.out.println("The property " + propSymbolicName + " doesn't
	// exist.");
	// return;
	// }
	//
	// while (resultsIter.hasNext()) {
	// PropertyTemplate prop = (PropertyTemplate) resultsIter.next();
	// updateOptionalPropertyValues(prop, propDisplayDE, CBREnabled,
	// choiceListName, maximumLength, descriptionEN, descriptionDE);
	// }
	// }

	// private static void updateOptionalPropertyValues(PropertyTemplate prop,
	// String propDisplayDE, boolean cBREnabled,
	// String choiceListName, int maximumLength, String descriptionEN, String
	// descriptionDE) {
	// // TODO Auto-generated method stub
	// LocalizedString localDisplayName =
	// Factory.LocalizedString.createInstance();
	// localDisplayName.set_LocalizedText(propDisplayDE);
	// localDisplayName.set_LocaleName("de");
	// prop.get_DisplayNames().add(localDisplayName);
	//
	// LocalizedString localDescription =
	// Factory.LocalizedString.createInstance();
	// localDescription.set_LocalizedText(descriptionEN);
	// localDescription.set_LocaleName("en-us");
	// localDescription.set_LocalizedText(descriptionDE);
	// localDescription.set_LocaleName("de");
	//// ChoiceList propCl = Factory.ChoiceList.createInstance(os);
	// prop.set_DescriptiveTexts(Factory.LocalizedString.createList());
	// prop.get_DescriptiveTexts().add(localDescription);
	// }

	private static void AssignPropertyToDocClass(ObjectStore os, String propSymbolicName, String docClassSymbolicName) {
		IndependentObjectSet results = searchPropertyBySymbolicName(os, propSymbolicName);

		if (results.isEmpty()) {
			System.out.println("The property " + propSymbolicName + " doesn't exist.");
			return;
		}

		DocumentClassDefinition docClass;
		try {
			docClass = Factory.DocumentClassDefinition.fetchInstance(os, docClassSymbolicName, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("The document class " + docClassSymbolicName + " doesn't exist.");
			return;
		}

		PropertyDefinitionList props = docClass.get_PropertyDefinitions();
		Iterator propsIter = props.iterator();
		while (propsIter.hasNext()) {
			PropertyDefinition existingPropDef = (PropertyDefinition) propsIter.next();

			if (existingPropDef.get_SymbolicName().equalsIgnoreCase(propSymbolicName)) {
				System.out.println("Property " + propSymbolicName + " is already assigned to " + docClassSymbolicName);
				return;
			}
		}

		Iterator resultsIter = results.iterator();
		while (resultsIter.hasNext()) {
			PropertyTemplate prop = (PropertyTemplate) resultsIter.next();
			PropertyDefinition propDef = (PropertyDefinition) prop.createClassProperty();
			props.add(propDef);
			System.out.println("Adding property " + propSymbolicName + " to document class " + docClassSymbolicName);
		}
		docClass.set_PropertyDefinitions(props);
		docClass.save(RefreshMode.NO_REFRESH);
	}

	private static void DeletePropertyTemplate(ObjectStore os, String propSymbolicName) {
		IndependentObjectSet results = searchPropertyBySymbolicName(os, propSymbolicName);

		if (results.isEmpty()) {
			System.out.println("The property " + propSymbolicName + " doesn't exist.");
			return;
		}

		Iterator resultsIter = results.iterator();
		while (resultsIter.hasNext()) {
			PropertyTemplate pT = (PropertyTemplate) resultsIter.next();
			try {
				ClassDefinitionSet docClasses = (ClassDefinitionSet) pT.fetchProperty("UsedInClasses", null)
						.getIndependentObjectSetValue();

				if (!docClasses.isEmpty()) {
					Iterator docIter = docClasses.iterator();
					while (docIter.hasNext()) {
						ClassDefinition usedInClass = (ClassDefinition) docIter.next();
						usedInClass.refresh();
						removePropertyDefinitionFromList(pT.get_SymbolicName(), usedInClass);
					}
				}
				System.out.println("Deleting property " + propSymbolicName);
				pT.delete();
				pT.save(RefreshMode.NO_REFRESH);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void removePropertyDefinitionFromList(String symbolicName, ClassDefinition usedInClass) {

		ClassDefinition parentClass = usedInClass.get_SuperclassDefinition();

		if (!parentClass.get_SymbolicName().equals("Document")) {
			System.out.println("Accessing superclass: " + parentClass.get_SymbolicName() + " of class "
					+ usedInClass.get_SymbolicName());
			removePropertyDefinitionFromList(symbolicName, parentClass);
		}
		PropertyDefinitionList classProps = usedInClass.get_PropertyDefinitions();
		Iterator classPropIter = classProps.iterator();
		while (classPropIter.hasNext()) {
			PropertyDefinition prop = (PropertyDefinition) classPropIter.next();
			if (prop.get_SymbolicName().equals(symbolicName)) {
				System.out
						.println("Removing property " + symbolicName + " from class " + usedInClass.get_SymbolicName());
				classProps.remove(prop);
				usedInClass.save(RefreshMode.NO_REFRESH);
			}
		}
	}

	@SuppressWarnings("unused")
	private static void printListItems(List classProps) {
		Iterator classPropIter = classProps.iterator();
		while (classPropIter.hasNext()) {
			PropertyDefinition prop = (PropertyDefinition) classPropIter.next();
			if (prop.get_SymbolicName().contains("FDA_")) {
				System.out.println("Property " + prop.get_SymbolicName() + " is present.");
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
		IndependentObjectSet results = searchPropertyBySymbolicName(os, propSymbolicName);

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

	private static IndependentObjectSet searchPropertyBySymbolicName(ObjectStore os, String propSymbolicName) {
		String sqlCondition = "SymbolicName = '" + propSymbolicName + "'";
		SearchSQL sql = new SearchSQL(
				"SELECT " + "SymbolicName" + " FROM " + "PropertyTemplate" + " WHERE " + sqlCondition);
		SearchScope scope = new SearchScope(os);
		IndependentObjectSet results = scope.fetchObjects(sql, 1, null, false);
		return results;
	}

	// @SuppressWarnings("deprecation")
	// private static IndependentObjectSet
	// searchPropertyBySymbolicNameImproved(ObjectStore os, String
	// propSymbolicName) {
	// String sqlCondition = "SymbolicName = '" + propSymbolicName + "'";
	// SearchSQL sql = new SearchSQL(
	// "SELECT " + "SymbolicName" + " FROM " + "PropertyTemplate" + " WHERE " +
	// sqlCondition);
	// SearchScope scope = new SearchScope(os);
	// PropertyFilter filter = new PropertyFilter();
	// filter.addIncludeProperty(2, null, null, "UsedInClasses");
	// IndependentObjectSet results = scope.fetchObjects(sql, 1, filter, false);
	// return results;
	// }

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
