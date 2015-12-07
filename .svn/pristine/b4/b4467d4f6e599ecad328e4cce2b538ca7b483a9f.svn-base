package hudson.plugins.logparser;

import java.util.List;
import java.util.Map;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;

public class SourceCodeDiffAction implements Action {
    public String html;
    Run<?, ?> build1;
    Run<?, ?> build2;

    public SourceCodeDiffAction(Job<?, ?> job, int build1, int build2, Launcher launcher,
            FilePath workspace) throws Exception {
        this.build1 = job.getBuildByNumber(build1);
        this.build2 = job.getBuildByNumber(build2);

        Map<String, List<String>> content1 = SCMUtils.getFilesFromBuild("*.java",
                (AbstractProject<?, ?>) job, build1, launcher, workspace);
        Map<String, List<String>> content2 = SCMUtils.getFilesFromBuild("*.java",
                (AbstractProject<?, ?>) job, build2, launcher, workspace);

        this.html = DiffToHtmlUtils.generateDiffHTML(build1, build2, "Source Code", content1,
                content2, null);
    }

    public Run<?, ?> getOwner() {
        return this.build1;
    }

    @Override
    public String getIconFileName() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return "Source Code Diff Page";
    }

    @Override
    public String getUrlName() {
        return "sourceCodeDiffAction`";
    }
}
