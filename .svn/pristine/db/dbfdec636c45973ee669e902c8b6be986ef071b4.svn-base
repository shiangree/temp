package hudson.plugins.logparser;

import org.kohsuke.stapler.Stapler;

import hudson.model.Action;
import hudson.model.Run;

public class TestResultPage implements Action{
	
	final private Run<?, ?> build;
	private String prevBuild;
	
	public TestResultPage(Run<?, ?> build){
		this.build = build;
		this.prevBuild = Stapler.getCurrentRequest().getParameter("prevBuild");
	}
	
	public Run<?, ?> getOwner() {
        return this.build;
    }
	
	public String getPrevBuild(){
		return this.prevBuild;
	}

	@Override
	public String getIconFileName() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Test Result Page";
	}

	@Override
	public String getUrlName() {
		// TODO Auto-generated method stub
		return "testResultPage";
	}

}
