package PomWalker;
import java.io.File;

//This class is used to extract the POM files and locations within
public class FileScan{
	
    private String indexPath;

    public FileScan(String index){
	indexPath = index;
    }
    
    public void walkPoms(String path) {
        ReadWriter wr = new ReadWriter(indexPath);
        
    	File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

	for ( File f : list ) {
            if ( f.isDirectory() ) {
                walkPoms( f.getAbsolutePath() );
            } else {
            	
            	String lookFile = f.getAbsoluteFile().toString();
            	
            	if (getExtension(lookFile).equalsIgnoreCase("pom")){
		    File pomF = new File(lookFile);
		    wr.write(pomF.getAbsolutePath()+"\n");
            	}
            }
        }
    }
    
    public void walkPomsAndJars(String path) {
        ReadWriter wr = new ReadWriter(indexPath);
    
    	File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

	for ( File f : list ) {
            if ( f.isDirectory() ) {
                walkPomsAndJars( f.getAbsolutePath() );
            } else {
		String lookFile = f.getAbsoluteFile().toString();
            	if (getExtension(lookFile).equalsIgnoreCase("pom")){
		    File pomF = new File(lookFile);
		    File jarF = new File(lookFile.replace(".pom", ".jar"));
		    if (jarF.exists()) {
			wr.write(pomF.getAbsolutePath()+","+jarF.getAbsolutePath()+"\n");
		    } else {
			wr.write(pomF.getAbsolutePath()+","+"<NOJAR>"+"\n");
		    }	
            	}
            }
        }
    }

    protected String getExtension(String name) {
        String[] str = name.split("\\.");
        if(str.length > 1) {
            return str[str.length - 1];
        }
        return ""; 
    }
}
