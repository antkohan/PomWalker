package PomWalker;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

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
	    //ReadWriter rw = new ReadWriter();
	    // Path to your local Maven repository
		
		String temp = log;
		String path = log.substring(temp.indexOf(",")+1);
		modDate = temp.substring(0,temp.indexOf(","));
		
		//modDate = temp.substring(0, temp.indexOf(",")+1);
		final File pomXmlFile = new File(path.trim());
		
        try {
            final Reader reader = new FileReader(pomXmlFile);
            final Model model;
            try {
                final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
                model = xpp3Reader.read(reader);
                              
                	
                	//contents = printDepAll(model, path);
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

            public String printInfo(Model m, String fp){
		System.out.println("Extracting From: "+fp);
		StringBuilder pomContents = new StringBuilder();
		Build b = m.getBuild();
				
		String artifact = getArtifactId(m);
		String version = getVersion(m);
		String groupId = getGroupId(m);
		String description = getDescription(m);
		String finalName = getFinalName(b);
		
		pomContents.append(
				   artifact+";"+
				   version+";"+
				   groupId+";"+
				   finalName+";"+
				   description+"\n"
				   );
		return pomContents.toString();
	    }

		// Read the model for an artifact and a given version
		//final Model model = MavenPomReader.readModel(repositoryDir, "junit", "junit-3.7", "3.7");
		// Print the dependencies on the console
                //Print the dependencies
		@SuppressWarnings("unchecked")
		public String printManagedModel(Model m, String fp){
			//ReadWriter wr = new ReadWriter();
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
			    		 //System.out.println("Writing Results");
					    //System.out.print(modDate);
					    //System.out.print(m.getGroupId()+","+m.getArtifactId()+","+m.getVersion()+",");
					    //System.out.println(dependency.getGroupId() + " , " + dependency.getArtifactId() + " , "
					        //+ dependency.getVersion() + " , " + dependency.getScope());
					    
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
		
	//Print the dependencies
	@SuppressWarnings("unchecked")
	public String printDep(Model m, String fp, String art, String artID){
		//ReadWriter wr = new ReadWriter();
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
		    		 //System.out.println("Writing Results");
				    //System.out.print(modDate);
				    //System.out.print(m.getGroupId()+","+m.getArtifactId()+","+m.getVersion()+",");
				    //System.out.println(dependency.getGroupId() + " , " + dependency.getArtifactId() + " , "
				        //+ dependency.getVersion() + " , " + dependency.getScope());
				    
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
		//ReadWriter wr = new ReadWriter();
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
		    
		    		 //System.out.println("Writing Results");
				    //System.out.print(modDate);
				    //System.out.print(m.getGroupId()+","+m.getArtifactId()+","+m.getVersion()+",");
				    //System.out.println(dependency.getGroupId() + " , " + dependency.getArtifactId() + " , "
				      //  + dependency.getVersion() + " , " + dependency.getScope());
				    
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

    private String getArtifactId(Model m){
	try {
	    return m.getArtifactId().replace(";","<SEMI>");
	} catch (RuntimeException e){
	    //Pom doesn't have the field so m.getArtifactId() throws a null pointer
	    errlog.write("Error in m.getArtifactId(): "+e+"\n");
	    return "<NONE>";
	}
    }
    private String getVersion(Model m){
	try {
	    return m.getVersion().replace(";","<SEMI>");
	} catch (RuntimeException e){
	    //Pom doesn't have the field so m.getVersion() throws a null pointer
	    errlog.write("Error in m.getVersion(): "+e+"\n");
	    return "<NONE>";
	}
    }
    private String getGroupId(Model m){
	try {
	    return m.getGroupId().replace(";","<SEMI>");
	} catch (RuntimeException e){
	    //Pom doesn't have the field so m.getGroupId() throws a null pointer
	    errlog.write("Error in m.getGroupId(): "+e+"\n");
	    return "<NONE>";
	}
    }
    private String getDescription(Model m){
	try {
	    return m.getDescription().replace(";","<SEMI>");
	} catch (RuntimeException e){
	    //Pom doesn't have the field so m.getDescription() throws a null pointer
	    errlog.write("Error in m.getDescription(): "+e+"\n");
	    return "<NONE>";
	}
    }
    private String getFinalName(Build b){
	try {
	    return b.getFinalName().replace(";","<SEMI>");
	} catch (RuntimeException e){
	    //Pom doesn't have the field so b.GetFinalName() throws a null pointer
	    errlog.write("Error in b.getFinalName(): "+e+"\n");
	    return "<NONE>";
	}
    }
	
	//public static void main(String[] args) {
        //PomParser fw = new PomParser();
        //fw.walkParse();
    //}

}
