package hudson.plugins.logparser;
import java.util.*;

import org.apache.maven.artifact.Artifact;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import jenkins.model.ArtifactManager;
import javax.xml.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import hudson.XmlFile;

import java.io.*;

public class RootPomDiff {
	

	
	private ArrayList<Dependency> depList1 = new ArrayList<Dependency>();
	private ArrayList<Dependency> depList2 = new ArrayList<Dependency>();
	
	public static void setList(Run<?,?> build2) throws Exception
	{
		String build2path = build2.getLogFile().getAbsolutePath();
		System.out.println("\n\n=======================================================\n");
		
		int idx1 = build2path.lastIndexOf("build");
		String dir = build2path.substring(0,idx1);
		dir = dir+"workspace";
		//System.out.println(dir);
		findPomPath(build2);		
		build2path = build2path.substring(0,build2path.length()-3);
		ArrayList<String> alist = new ArrayList<String>();
		findFiles(dir,alist);
		dir = alist.get(0);
		System.out.println(dir);
		BufferedReader br = new BufferedReader(new FileReader(dir));
		BufferedWriter bw = new BufferedWriter(new FileWriter(build2path+"pom.xml"));
		String line;
		while((line = br.readLine()) != null) 
		{
			bw.write(line);
			bw.write("\n");
			System.out.println(line);
		}
		bw.flush();
		bw.close();
		br.close();
		System.out.println("\n\n=======================================================\n");

		
	}

	public static Map<String, ArrayList<Dependency>> compare(Run<?,?> build1, Run<?,?> build2) throws Exception
	{
System.out.println("\n============================================================\n");
		System.out.println("Comparing build "+build1.getNumber()+" and build "+build2.getNumber());
		
		String build1path = build1.getLogFile().getAbsolutePath();
		String build2path = build2.getLogFile().getAbsolutePath();
		
		build1path = build1path.substring(0,build1path.length()-3);
		build1path = build1path+"pom.xml";
		ArrayList<Dependency> deplist1 = parsePom(build1path);
		
		build2path = build2path.substring(0,build2path.length()-3);
		build2path = build2path+"pom.xml";
		
		ArrayList<Dependency> deplist2 = parsePom(build2path);
		System.out.println("The paths of the poms are:\n"+build2path+"\n"+build1path);
		for(Dependency dep : deplist1)
		{
			System.out.print(dep.getGroupId()+" ");
		}
		System.out.println("\n");
		for(Dependency dep : deplist2)
		{
			System.out.print(dep.getGroupId()+" ");
		}
		System.out.println();
		return diff(deplist1, deplist2);
		
	}

	public static ArrayList<Dependency> getlist(Run<?,?> build) throws Exception
	{
		String buildpath = build.getLogFile().getAbsolutePath();
		buildpath = buildpath.substring(0,buildpath.length()-3);
		buildpath = buildpath+"pom.xml";
		return parsePom(buildpath);
	}
	
		public static Map<String, ArrayList<Dependency>> diff(ArrayList<Dependency> deplist1, ArrayList<Dependency> deplist2) throws ParserConfigurationException, SAXException, IOException
		{
			ArrayList<Dependency> dellist = new ArrayList<Dependency>();
			ArrayList<Dependency> addlist = new ArrayList<Dependency>();
			ArrayList<Dependency> modlist = new ArrayList<Dependency>();
			Map<String,ArrayList<Dependency>> retlist = new HashMap<String, ArrayList<Dependency>>();
			for(int i=0;i<deplist2.size();++i)
			{
				Dependency dep2 = deplist2.get(i);
				String gid2 = dep2.getGroupId();
				String aid2 = dep2.getArtifactId();
				String ver2 = dep2.getVersion();
				boolean isinlist1 = false;
				for(Dependency dep1 : deplist1)
				{
					String gid1 = dep1.getGroupId();
					String aid1 = dep1.getArtifactId();
					String ver1 = dep1.getVersion();
					if(gid1.equals(gid2))
					{	
						isinlist1 = true;
						if(!aid1.equals(aid2) || !ver1.equals(ver2))
							modlist.add(new Dependency(gid2, aid2, ver2));
					}
				}
				if(!isinlist1)
					addlist.add(new Dependency(gid2,aid2,ver2));	
			}
			retlist.put("Modified",modlist);
	        	retlist.put("Added", addlist);
			for(int i=0;i<deplist1.size();++i)
			{
                                Dependency dep1 = deplist1.get(i);
                                String gid1 = dep1.getGroupId();
                                String aid1 = dep1.getArtifactId();
                                String ver1 = dep1.getVersion();
                                boolean isinlist2 = false;
				for(Dependency dep2 : deplist2)
				{
                                        String gid2 = dep2.getGroupId();
                                        String aid2 = dep2.getArtifactId();
                                        String ver2 = dep2.getVersion();
                                        if(gid2.equals(gid1))
                                        {
                                                isinlist2 = true;
                                        }
				}
				if(!isinlist2)
					dellist.add(new Dependency(gid1, aid1, ver1));
				
			}
		System.out.println("modified dependencies:");
                System.out.println(new Gson().toJson(modlist));
                System.out.println("added dependencies:");
                System.out.println(new Gson().toJson(addlist));
		System.out.println("deleted dependencies:");	
		System.out.println(new Gson().toJson(dellist));
			System.out.println("\n===================================================\n");      
			retlist.put("Deleted",dellist);
			return retlist;
		}
	
	    public static StringBuilder setupCSS()
	    {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>");
	 	sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<title></title>\n");
		sb.append("<style type=\"text/css\">\n");
		//sb.append("div { height: 150px; }\n");
		sb.append(".left { width: 30%; float: left; clear: right; background-color: #BCF5A9; }\n");
	    	sb.append(".center { width: 30%; float: left; clear: right; background-color: #81DAF5; }\n");
	    	sb.append(".right { width: 30%; float: left; clear: right; background-color: #FA5858; }\n");
		sb.append(".both { width: 100%; clear: both; background-color: #696969; }\n");
		sb.append("</style>\n");
		sb.append("</head>\n");	
		return sb;
	    }
	    public static String toHtml(ArrayList<Dependency> deplist1, ArrayList<Dependency> deplist2,Map<String, ArrayList<Dependency>> list)
	    {
	    	ArrayList<Dependency> modified = list.get("Modified");
	    	ArrayList<Dependency> added = list.get("Added");
	    	ArrayList<Dependency> deleted = list.get("Deleted");
		StringBuilder html = setupCSS();
	        
		html.append("<body>\n");
		html.append("<div>\n");
		html.append("<div class=\"left\">\n");
	    	html.append("<b>Dependency modified:</b><br />\n");
	    	for(Dependency dep : modified)
	    	{
	    		html.append("<br> groupId: " + dep.getGroupId()+"</br>\n");
	    		html.append("<br> artifactId: " + dep.getArtifactId()+"</br>\n");
	    		html.append("<br> version: " + dep.getVersion()+"</br>\n"); 
	    		html.append("<br />\n");	
	    	}	
	    	html.append("</div>\n");
		html.append("<div class=\"center\">\n");
	    	html.append("<b>Dependency added:</b><br />\n");
	    	for(Dependency dep : added)
	    	{
	    		html.append("<br> groupId: " + dep.getGroupId()+"</br>\n");
	    		html.append("<br> artifactId: " + dep.getArtifactId()+"</br>\n");     
	    		html.append("<br> version: " + dep.getVersion()+"</br>\n");
	    		html.append("<br />\n");
	    	}
	    	html.append("</div>\n");
		html.append("<div class=\"right\">\n");
	    	html.append("<b>Dependency deleted:</b><br />\n");
	    	for(Dependency dep : deleted)
	    	{
	    		html.append("<br> groupId: " + dep.getGroupId()+"</br>\n");
	    		html.append("<br> artifactId: " + dep.getArtifactId()+"</br>\n");
	    		html.append("<br> version: " + dep.getVersion()+"</br>\n");
	    		html.append("<br />\n");
	    	}
		html.append("</div>\n");
		html.append("</div>\n");
		html.append("</body>\n");
		html.append("</html>");
	    	return html.toString();		
	    } 	
	    public static void findFiles(String baseDirName, ArrayList<String> currPath) 
	    {
	        String tempName = null;
	        File baseDir = new File(baseDirName);
	        String path = null;
	        if (!baseDir.exists() || !baseDir.isDirectory()){
	            System.out.println("Can't find file: " + baseDirName + "is not a directory");
	        } else {
	        	String[] filelist = baseDir.list();
	    	    for (int i = 0; i < filelist.length; i++) {
	    	    	File readfile = new File(baseDirName + "/" + filelist[i]);
	    	        if(!readfile.isDirectory()) {
	    	        	tempName =  readfile.getName(); 
	                    if (tempName.equals("pom.xml")) {
				//System.out.println("FOUND!\n"+readfile.getAbsolutePath());
	                        currPath.add(readfile.getAbsolutePath()); 
	                    }
	    	        } else if(readfile.isDirectory()){
	    	        	findFiles(baseDirName + "/" + filelist[i],currPath);
	    	        }
	    	    }
	        }
	        
	    }
	    
	    
	    
		public static ArrayList<Dependency> parsePom(String dir) throws ParserConfigurationException, SAXException, IOException  {
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
		    Document doc = null;
			doc = dbBuilder.parse(dir);
			NodeList list = doc.getElementsByTagName("dependency");
			ArrayList<Dependency> alist = new ArrayList<Dependency>();
			System.out.println(list.getLength());
			for(int i = 0; i< list.getLength() ; i ++){
				Element element = (Element)list.item(i);
				String groupId=element.getElementsByTagName("groupId").item(0).getFirstChild().getNodeValue();
				String artifactId=element.getElementsByTagName("artifactId").item(0).getFirstChild().getNodeValue();
				String version=element.getElementsByTagName("version").item(0).getFirstChild().getNodeValue();
				alist.add(new Dependency(groupId, artifactId, version));

			}
			return alist;
	  	}
	
	    public static String findPomPath(Run<?,?> build) throws ParserConfigurationException, SAXException, IOException
	    {
	    	XmlFile configfile = build.getParent().getConfigFile();
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
		    Document doc = null;
		    doc = dbBuilder.parse(configfile.getFile());
		    System.out.println("\n\nDOC FOUND:"+doc+"\n\n");
		    NodeList list = doc.getElementsByTagName("rootPOM");
		    String pomPath = list.item(0).getNodeValue();
		    System.out.println("\n\n");
		    System.out.println("PATH FOUND:"+ pomPath+"\n\n");
		    return pomPath;
	    }
	    	
	
	
}

