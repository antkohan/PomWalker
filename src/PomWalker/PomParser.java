package PomWalker;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.lang.reflect.Method;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Build;
import org.apache.maven.model.Parent;
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
	String contents="";
			
	String temp = log;
	String path = log.substring(temp.indexOf(",")+1);
	modDate = temp.substring(0,temp.indexOf(","));
		
	final File pomXmlFile = new File(path.trim());
		
        try {
            final Reader reader = new FileReader(pomXmlFile);
            final Model model;
            try {
                final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
                model = xpp3Reader.read(reader);
         
		/*
		  if(model.getDependencyManagement()!=null){
		  contents = printManagedModel(model, path);
		  }else{
		  contents = printDepAll(model, path);
		  }
		*/
			
		contents = printInfo(model, path);
                
                //contents = printDep(model, path, artifact, artVersion);
            } finally {
                reader.close();
            }
        } catch (XmlPullParserException ex) {
	    System.out.println("vvv ERROR vvv");
	    errlog.write(path+","+ex+"\n\n");
	    throw new RuntimeException("Error parsing POM!", ex);
        } catch (final IOException ex) {
	    System.out.println("vvv ERROR vvv");
	    errlog.write(path+","+ex+"\n\n");
	    throw new RuntimeException("Error reading POM!", ex);
        } finally {
	    return contents;
        }
       
    }

    public String printJarInfo(Model m, String fp){
	System.out.println("Extracting from jar: ");
	StringBuilder jarInfo = new StringBuilder();
	Build b = m.getBuild();
	return "test";
    }

    public String printInfo(Model m, String fp){
	System.out.println("Extracting From: "+fp);
	StringBuilder pomContents = new StringBuilder();

	String artifact = modelGetterWrapper("getArtifactId", m);
	String version = modelGetterWrapper("getVersion", m);
	String groupId = modelGetterWrapper("getGroupId", m);
	String description = modelGetterWrapper("getDescription", m);
	
	pomContents.append(artifact+";"+version+";"+groupId+";"+description+"\n");
	return pomContents.toString();
    }

    @SuppressWarnings("unchecked")
    public String printManagedModel(Model m, String fp){
	System.out.println("Extracting From: "+fp);
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
				+ dependency.getGroupId() + "," + dependency.getScope()+","+fp);
	    pomContents.append("\n");
			    	
	}
	String pContents = pomContents.toString();
	return pContents;
    }
		
    @SuppressWarnings("unchecked")
    public String printDep(Model m, String fp, String art, String artID){
	StringBuilder pomContents = new StringBuilder();
	final List<Dependency> dependencies = m.getDependencies();
	System.out.println("Extracting from: "+fp);
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
					+ dependency.getGroupId() + "," + dependency.getScope()+","+fp);
		    pomContents.append("\n");
		}
	    }
	}
	String pContents = pomContents.toString();
	return pContents;
    }
	
    public String printDepAll(Model m, String fp){
	StringBuilder pomContents = new StringBuilder();
	final List<Dependency> dependencies = m.getDependencies();
	System.out.println("Extracting from: "+fp);
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
				+ dependency.getGroupId() +"," + dependency.getScope()+","+fp);
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

    //Wraps getter functions from maven's Model class to return the fields in a special format.
    private String buildGetterWrapper(String func, Build b){
	try {
	    Method modelFunc = b.getClass().getMethod(func);
	    Object value =  modelFunc.invoke(b);
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
