package PomWalker;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;


public class PomParser {
    String modDate;
    int skip;
    int anaPOM;
    ReadWriter errlog = new ReadWriter("log.txt");

    //get the POM file contents
    @SuppressWarnings("finally")
    public String walkParse(String log) {
	String contents = "";			
		
	//Remove the pom path from the list of paths. Now may only contain at most jar paths.
	LinkedList<String> pathList = new LinkedList<String>(Arrays.asList(log.split(",")));
	String pomPath = (pathList.size() > 0) ? pathList.remove(0) : "" ;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	final File pomXmlFile = new File(pomPath.trim());
	Date date = new Date(pomXmlFile.lastModified());
	modDate = sdf.format(date);
		
        try {
            final Reader reader = new FileReader(pomXmlFile);
            final Model model;
            try {
                final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
                model = xpp3Reader.read(reader);
         
		/*
		  if(model.getDependencyManagement()!=null){
		  contents = printManagedModel(model, pomPath);
		  }else{
		  contents = printDepAll(model, pomPath);
		  }
		*/
			
		contents = printJarInfo(model, pomPath, pathList);
                
                //contents = printDep(model, pomPath, artifact, artVersion);
            } finally {
                reader.close();
            }
        } catch (XmlPullParserException ex) {
	    System.out.println("*** ERROR ***");
	    errlog.write(pomPath+", "+ex+"\n\n");
	    throw new RuntimeException("Error parsing POM!", ex);
        } catch (final IOException ex) {
	    System.out.println("*** ERROR ***");
	    errlog.write(pomPath+", "+ex+"\n\n");
	    throw new RuntimeException("Error reading POM!", ex);
        } finally {
	    return contents;
        }
       
    }

    public String printJarInfo(Model m, String pomPath, LinkedList<String> jarList){
	System.out.println("Extracting From Pom: " + pomPath);
	for (int i = 0; i < jarList.size(); i++ ) {
	    String jarPath = jarList.get(i);
	    System.out.println("Related jar: "+jarPath);
	    jarList.set(i, jarPath.substring(jarPath.lastIndexOf('/')+1));
	}
	StringBuilder info = new StringBuilder();
	Organization org = m.getOrganization();

	String pomName = pomPath.substring(pomPath.lastIndexOf('/')+1);
	String artifact = modelGetterWrapper("getArtifactId", m);
	String version = modelGetterWrapper("getVersion", m);
	String groupId = modelGetterWrapper("getGroupId", m);
	String orgName = orgGetterWrapper("getName", org);
 
	info.append(artifact+";"+version+";"+groupId+";"+orgName+";");
	info.append(pomName);
	for (String jar : jarList) { info.append(";"+jar); }
	info.append("\n");

	return info.toString();
    }

    public String printPomInfo(Model m, String pomPath){
	System.out.println("Extracting From: "+pomPath);
	StringBuilder pomContents = new StringBuilder();

	String artifact = modelGetterWrapper("getArtifactId", m);
	String version = modelGetterWrapper("getVersion", m);
	String groupId = modelGetterWrapper("getGroupId", m);
	String description = modelGetterWrapper("getDescription", m);
	
	pomContents.append(artifact+";"+version+";"+groupId+";"+description+"\n");
	return pomContents.toString();
    }

    @SuppressWarnings("unchecked")
    public String printManagedModel(Model m, String pomPath){
	System.out.println("Extracting From: "+pomPath);
	System.out.print("Managed Dependancies: ");
	StringBuilder pomContents = new StringBuilder();
				
	DependencyManagement dm = m.getDependencyManagement();
			
	final List<Dependency> dependencies = dm.getDependencies();
	       			
	//update counters
	if (dependencies.size() == 0){
	    System.out.println("none");
	    skip++;
	}else{
	    System.out.println(dependencies.size());
	    anaPOM++;
	}
			
	System.out.println("Parent: SuperPom");
	String parent = "superPom";
			
	for (int i = 0; i < dependencies.size(); i++) {
	    final Dependency dependency = dependencies.get(i);
	    //write to file
	    pomContents.append (dependency.getArtifactId() + ","
				+ dependency.getVersion() +","+modDate+","
				+ parent+","
				+ m.getGroupId()+","+m.getArtifactId()+","+m.getVersion()+","
				+ dependency.getGroupId() + "," + dependency.getScope()+","+pomPath);
	    pomContents.append("\n");
			    	
	}
	String pContents = pomContents.toString();
	return pContents;
    }
		
    @SuppressWarnings("unchecked")
    public String printDep(Model m, String pomPath, String art, String artID){
	StringBuilder pomContents = new StringBuilder();
	final List<Dependency> dependencies = m.getDependencies();
	System.out.println("Extracting from: "+pomPath);
	System.out.print("Dependencies: ");

	//update counters
	if (dependencies.size() == 0){
	    System.out.println("none");
	    skip++;
	}else{
	    System.out.println(dependencies.size());
	    anaPOM++;
	}
		
	System.out.println("Parent: SuperPom");
	String parent = "superPom";
		
	for (int i = 0; i < dependencies.size(); i++) {
	    final Dependency dependency = dependencies.get(i);
		    
	    if (art.equalsIgnoreCase(dependency.getArtifactId())){
		if (artID.equalsIgnoreCase(dependency.getVersion())){
		   
		    //write to file
		    pomContents.append (dependency.getArtifactId() + ","
					+ dependency.getVersion() +","+modDate+","
					+ parent+","
					+ m.getGroupId()+","+m.getArtifactId()+","+m.getVersion()+","
					+ dependency.getGroupId() + "," + dependency.getScope()+","+pomPath);
		    pomContents.append("\n");
		}
	    }
	}
	String pContents = pomContents.toString();
	return pContents;
    }
	
    public String printDepAll(Model m, String pomPath){
	StringBuilder pomContents = new StringBuilder();
	final List<Dependency> dependencies = m.getDependencies();
	System.out.println("Extracting from: "+pomPath);
	System.out.print("Dependencies: ");
	//update counters
	if (dependencies.size() == 0){
	    System.out.println("none");
	    skip++;
	}else{
	    System.out.println(dependencies.size());
	    anaPOM++;
	}
		
	//if this has a parent
	String parent = "null";
		
	if(m.getParent()!=null){
	    parent = m.getParent().getArtifactId();
	    System.out.println("Parent: "+parent);
	}else{
	    parent = "none";
	    System.out.println("Parent: "+parent);
	}
		
	for (int i = 0; i < dependencies.size(); i++) {
	    final Dependency dependency = dependencies.get(i);
		   
	    //write to file
	    pomContents.append (dependency.getArtifactId() + ","
				+ dependency.getVersion() +","+modDate+","
				+ parent+","
				+ m.getGroupId()+","+m.getArtifactId()+","+m.getVersion()+","
				+ dependency.getGroupId() +"," + dependency.getScope()+","+pomPath);
	    pomContents.append("\n");
		    	
	}
	String pContents = pomContents.toString();
	return pContents;
    }

    //Wraps getter functions from maven's Model class to return the fields in a special format.
    private String modelGetterWrapper(String func, Model m){
	try {
	    Method modelFunc = m.getClass().getMethod(func);
	    Object value =  modelFunc.invoke(m);
	    String field = (String)value;
	    return field.replace(";","<SEMI>");
	} catch (ReflectiveOperationException e) { 
	    errlog.write("Error in using reflection in model wrapper on function "+func+": "+e+"\n");
	    return "<NONE>";
	} catch (NullPointerException e) {
	    //Tried to use replace on a field that had no value. Will be common, so don't write to log
	    return "<NONE>";
	} catch (Exception e) {
	    errlog.write("Error running model wrapper on function "+func+": "+e+"\n");
	    return "<NONE>";
	}

    }

    //Wraps getter functions from maven's Organiztion class to return the fields in a special format.
    private String orgGetterWrapper(String func, Organization org){
	try {
	    Method modelFunc = org.getClass().getMethod(func);
	    Object value =  modelFunc.invoke(org);
	    String field = (String)value;
	    return field.replace(";","<SEMI>");
	} catch (ReflectiveOperationException e) { 
	    errlog.write("Error in using reflection in model wrapper on function "+func+": "+e+"\n");
	    return "<NONE>";
	} catch (NullPointerException e) {
	    //Tried to use replace on a field that had no value. Will be common, so don't write to log
	    return "<NONE>";
	} catch (Exception e) {
	    errlog.write("Error running model wrapper on function "+func+": "+e+"\n");
	    return "<NONE>";
	}

    }
      
}
