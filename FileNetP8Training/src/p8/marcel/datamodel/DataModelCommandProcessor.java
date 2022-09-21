package p8.marcel.datamodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;

import com.filenet.api.admin.Choice;
import com.filenet.api.admin.ChoiceList;
import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.DocumentClassDefinition;
import com.filenet.api.admin.LocalizedString;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionString;
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
import com.filenet.api.constants.ChoiceType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.sun.xml.internal.fastinfoset.util.StringArray;

public class DataModelCommandProcessor {
    private static final String PROPERTY_CATALOG_JSON = "resources/Property Catalog.json";
    private static final String CHOICELIST_CATALOG_JSON = "resources/ChoiceList Catalog.json";

    public static void executeChanges(Domain domain, ObjectStore os) {
	UpdatingBatch batch = UpdatingBatch.createUpdatingBatchInstance(domain, RefreshMode.NO_REFRESH);
	StringList updatedProperties = Factory.StringList.createList();

	// createNewPropertyTemplate(os, "FDA_Test1", "STRING",
	// Cardinality.SINGLE, "FDA Test");
	// createNewPropertyTemplate(os, "FDA_Test2", "STRING",
	// Cardinality.SINGLE, "FDA Test 2");
	// createNewPropertyTemplate(os, "FDA_Test2", "STRING",
	// Cardinality.SINGLE, "FDA Test 2");
	// createNewPropertyTemplate(os, "FDA_Test3", "STRING",
	// Cardinality.SINGLE, "FDA Test 3");
	// createNewPropertyTemplate(os, "FDA_Test4", "BOOLEAN",
	// Cardinality.SINGLE, "FDA Test 4");
	// createNewPropertyTemplate(os, "FDA_Test5", "STRING",
	// Cardinality.SINGLE, "FDA Test 5");
	// createNewPropertyTemplate(os, "FDA_Test6", "STRING",
	// Cardinality.SINGLE, "FDA Test 6");
	// createNewPropertyTemplate(os, "FDA_Test7", "INTEGER",
	// Cardinality.SINGLE, "FDA Test 7");
	//
	// assignPropertyToDocClass(os, "FDA_Test1", "TestDocClass");
	// assignPropertyToDocClass(os, "FDA_Test2", "TestDocClass");
	// assignPropertyToDocClass(os, "FDA_Test2", "TestDocSubClass");
	// assignPropertyToDocClass(os, "FDA_Test32", "TestDocClass");
	// assignPropertyToDocClass(os, "FDA_Test2", "TestDocClass95");
	// assignPropertyToDocClass(os, "FDA_Test3", "TestDocClass");
	// assignPropertyToDocClass(os, "FDA_Test4", "TestDocClass");
	// assignPropertyToDocClass(os, "FDA_Test4", "TestDocSubClass");
	// assignPropertyToDocClass(os, "FDA_Test4", "TestDocSubSubClass1");
	//
	// deletePropertyTemplate(os, "FDA_Test1");
	// deletePropertyTemplate(os, "FDA_Test2");
	// deletePropertyTemplate(os, "FDA_Test3");
	// deletePropertyTemplate(os, "FDA_Test4");
	// deletePropertyTemplate(os, "FDA_Test5");
	//
	// createNewPropertyTemplate(os, "FDA_Test5", "STRING",
	// Cardinality.SINGLE, "FDA Test 5", "FDA German Test 5",
	// true, "FDA_Choicelist", 150, "Description EN", "Description DE");
	//
	// createNewPropertyTemplateBatch(batch, os, updatedProperties,
	// "FDA_Test1", "STRING", Cardinality.SINGLE,
	// "FDA Test");
	// createNewPropertyTemplateBatch(batch, os, updatedProperties,
	// "FDA_Test2", "STRING", Cardinality.SINGLE,
	// "FDA Test 2");
	// createNewPropertyTemplateBatch(batch, os, updatedProperties,
	// "FDA_Test2", "STRING", Cardinality.SINGLE,
	// "FDA Test 2");
	// createNewPropertyTemplateBatch(batch, os, updatedProperties,
	// "FDA_Test3", "INTEGER", Cardinality.SINGLE,
	// "FDA Test 3");
	// createNewPropertyTemplateBatch(batch, os, updatedProperties,
	// "FDA_Test4", "BOOLEAN", Cardinality.SINGLE,
	// "FDA Test 4");

//	 JSONObject fdaProperties = loadDataFromJSON(os,
//	 PROPERTY_CATALOG_JSON);
//	 JSONObject fdaChoiceLists = loadDataFromJSON(os,
//		 CHOICELIST_CATALOG_JSON);
//	 createPropertyTemplatesFromJSON(os, fdaProperties);
//	 createChoiceListFromJSON(os,fdaChoiceLists);
//	 listAllFDAProperties(os);
//	 deleteAllPropertyTemplates(os);

	try {
	    if (batch.hasPendingExecute()) {
		batch.updateBatch();
		System.out.println("Batch was executed.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static void createChoiceListFromJSON(ObjectStore os, JSONObject fdaChoiceLists) {
	try {
	    ChoiceList cl = (ChoiceList) Factory.ChoiceList.createInstance(os);
	    cl.set_DataType(TypeID.STRING);
	    cl.set_ChoiceValues(Factory.Choice.createList());
	    
	    Iterator clIterator = fdaChoiceLists.keys();
	    while (clIterator.hasNext()) {
		String clName = (String) clIterator.next();
	        JSONObject clEntries = fdaChoiceLists.getJSONObject(clName);
	        cl.set_DisplayName(clName);
//		System.out.println(clName);
	        Iterator clEntriesIter = clEntries.keys();
	        while (clEntriesIter.hasNext()) {
		    String value = (String) clEntriesIter.next();
		    Choice clItem = Factory.Choice.createInstance();
//		    System.out.println(value);
		    clItem.set_ChoiceStringValue(value);
		    clItem.set_ChoiceType(ChoiceType.STRING);
		    clItem.set_DisplayName(clEntries.getString(value));
		    cl.get_ChoiceValues().add(clItem);
		}
	    }
	    cl.save(RefreshMode.REFRESH);
	    System.out.println("Choicelist "+cl.get_DisplayName()+ " was created.");
	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }

    private static void deleteAllPropertyTemplates(ObjectStore os) {
	StringArray properties = getAllFDAProperties(os);

	if (properties.getSize() == 0) {
	    System.out.println("There are no properties in the system.");
	    return;
	}

	for (int i = 0; i < properties.getSize(); i++) {
	    deletePropertyTemplate(os, properties.get(i));
	}
    }

    private static StringArray getAllFDAProperties(ObjectStore os) {
	SearchSQL sql = new SearchSQL(
		"SELECT " + "SymbolicName" + " FROM " + "PropertyTemplate" + " WHERE " + "SymbolicName like 'FDA%'");
	SearchScope scope = new SearchScope(os);
	PropertyFilter filter = new PropertyFilter();
	filter.addIncludeProperty(0, null, false, "SymbolicName");
	IndependentObjectSet results = scope.fetchObjects(sql, 1, filter, false);

	StringArray properties = new StringArray();
	Iterator propIterator = results.iterator();
	while (propIterator.hasNext()) {
	    PropertyTemplate prop = (PropertyTemplate) propIterator.next();
	    // System.out.println(prop.get_SymbolicName());
	    properties.add(prop.get_SymbolicName());
	}
	return properties;
    }

    private static void listAllFDAProperties(ObjectStore os) {
	SearchSQL sql = new SearchSQL(
		"SELECT " + "SymbolicName" + " FROM " + "PropertyTemplate" + " WHERE " + "SymbolicName like 'FDA%'");
	SearchScope scope = new SearchScope(os);
	PropertyFilter filter = new PropertyFilter();
	filter.addIncludeProperty(0, null, false, "SymbolicName");
	IndependentObjectSet results = scope.fetchObjects(sql, 1, filter, false);

	if (results.isEmpty()) {
	    System.out.println("There are no properties in the system.");
	    return;
	}
	
	StringArray properties = new StringArray();
	Iterator propIterator = results.iterator();
	while (propIterator.hasNext()) {
	    PropertyTemplate prop = (PropertyTemplate) propIterator.next();
	    System.out.println(prop.get_SymbolicName());
	}
    }

    private static void createPropertyTemplatesFromJSON(ObjectStore os, JSONObject fdaProperties) {
	try {
	    Iterator propIter = fdaProperties.keys();
	    while (propIter.hasNext()) {
		String propSymbolicName = (String) propIter.next();
		JSONObject propertyDetails = (JSONObject) fdaProperties.get(propSymbolicName);
		String targetDocumentClass = propertyDetails.getString("TargetClass");

		createNewPropertyTemplate(os, propSymbolicName, propertyDetails);
		assignPropertyToDocClass(os, propSymbolicName, targetDocumentClass, propertyDetails);
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }

    private static void createNewPropertyTemplate(ObjectStore os, String propSymbolicName, JSONObject propertyDetails)
	    throws JSONException {
	// Check if the property already exists
	IndependentObjectSet results = searchPropertyBySymbolicName(os, propSymbolicName);
	if (!results.isEmpty()) {
	    System.out.println("Property " + propSymbolicName + " already exists.");
	    return;
	}
	String propType = propertyDetails.getString("DataType");

	if (propType.equals("STRING")) {
	    PropertyTemplateString pTString = Factory.PropertyTemplateString.createInstance(os);
	    createPropertyTemplate(os, propSymbolicName, propertyDetails, pTString);
	} else if (propType.equals("INTEGER")) {
	    PropertyTemplateInteger32 pTInteger = Factory.PropertyTemplateInteger32.createInstance(os);
	    createPropertyTemplate(os, propSymbolicName, propertyDetails, pTInteger);
	} else if (propType.equals("BOOLEAN")) {
	    PropertyTemplateBoolean pTBool = Factory.PropertyTemplateBoolean.createInstance(os);
	    createPropertyTemplate(os, propSymbolicName, propertyDetails, pTBool);
	} else if (propType.equals("DATE")) {
	    PropertyTemplateDateTime pTDate = Factory.PropertyTemplateDateTime.createInstance(os);
	    createPropertyTemplate(os, propSymbolicName, propertyDetails, pTDate);
	} else {
	    System.out.println("Property type " + propType + " is not yet implemented.");
	}

    }

    private static void createPropertyTemplate(ObjectStore os, String propSymbolicName, JSONObject propertyDetails,
	    PropertyTemplate pT) {
	try {
	    String cardinString = propertyDetails.getString("Cardinality");
	    String propDisplayName = propertyDetails.getString("DisplayName");
	    String germanDisplayName = propertyDetails.getString("DisplayNameDE");
	    String propDescription = propertyDetails.getString("Description");
	    Cardinality cardin = cardinString.equalsIgnoreCase("SINGLE") ? Cardinality.SINGLE : Cardinality.LIST;
	    
	    LocalizedString localDisplayName = Factory.LocalizedString.createInstance();
	    localDisplayName.set_LocalizedText(propDisplayName);
	    localDisplayName.set_LocaleName(os.get_LocaleName());
	    LocalizedString localDescription = Factory.LocalizedString.createInstance();
	    localDescription.set_LocalizedText(propDescription);
	    localDescription.set_LocaleName(os.get_LocaleName());
	    LocalizedString germanLocalDisplayName = Factory.LocalizedString.createInstance();
	    germanLocalDisplayName.set_LocalizedText(germanDisplayName);
	    germanLocalDisplayName.set_LocaleName("de");
	    
	    pT.set_SymbolicName(propSymbolicName);
	    pT.set_Cardinality(cardin);
	    pT.set_DisplayNames(Factory.LocalizedString.createList());
	    pT.get_DisplayNames().add(localDisplayName);
	    pT.get_DisplayNames().add(germanLocalDisplayName);
	    pT.set_DescriptiveTexts(Factory.LocalizedString.createList());
	    pT.get_DescriptiveTexts().add(localDescription);
	    pT.save(RefreshMode.NO_REFRESH);
	    System.out.println("Property "+propSymbolicName+" was created.");
	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }

    private static JSONObject loadDataFromJSON(ObjectStore os, String docName) {
	FileInputStream fis;
	JSONObject jsonObject;
	try {
	    fis = new FileInputStream(docName);
	    jsonObject = new JSONObject(fis);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return null;
	} catch (JSONException e) {
	    e.printStackTrace();
	    return null;
	}
	return jsonObject;
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

    private static void assignPropertyToDocClass(ObjectStore os, String propSymbolicName, String docClassSymbolicName, JSONObject propertyDetails) throws JSONException {
	IndependentObjectSet results = searchPropertyBySymbolicName(os, propSymbolicName);
	Integer maxPropLength = 0;
	if (propertyDetails.containsKey("MaximumLength")) {
	    maxPropLength = propertyDetails.getInt("MaximumLength");
	}
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

	    if (prop.get_DataType() == TypeID.STRING) {
		if (maxPropLength != 0) {
		    ((PropertyDefinitionString) propDef).set_MaximumLengthString(maxPropLength);
		}
	    }
	    
	    props.add(propDef);
	    System.out.println("Adding property " + propSymbolicName + " to document class " + docClassSymbolicName);
	}
	try {
	    docClass.set_PropertyDefinitions(props);
	    docClass.save(RefreshMode.NO_REFRESH);
	} catch (Exception e) {
	    EngineRuntimeException ex = (EngineRuntimeException) e;

	    if (ex.getExceptionCode() == ExceptionCode.E_NOT_UNIQUE) {
		System.err.println(ex.getLocalizedMessage());
		return;
	    } else {
		e.printStackTrace();
	    }
	}
    }

    private static void deletePropertyTemplate(ObjectStore os, String propSymbolicName) {
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
			removePropertyFromDocClasses(pT.get_SymbolicName(), usedInClass);
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

    private static void removePropertyFromDocClasses(String symbolicName, ClassDefinition usedInClass) {

	ClassDefinition parentClass = usedInClass.get_SuperclassDefinition();

	if (!parentClass.get_SymbolicName().equals("Document")) {
	    System.out.println("Accessing superclass: " + parentClass.get_SymbolicName() + " of class "
		    + usedInClass.get_SymbolicName());
	    removePropertyFromDocClasses(symbolicName, parentClass);
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

    private static void fetchPropertyTemplate(ObjectStore os, Id objectId) {
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
		"SELECT " + "SymbolicName, DataType" + " FROM " + "PropertyTemplate" + " WHERE " + sqlCondition);
	SearchScope scope = new SearchScope(os);
	PropertyFilter filter = new PropertyFilter();
	filter.addIncludeProperty(0, null, false, "DataType SymbolicName");
	IndependentObjectSet results = scope.fetchObjects(sql, 1, filter, false);
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
