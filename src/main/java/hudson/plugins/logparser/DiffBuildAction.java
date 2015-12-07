package hudson.plugins.logparser;

import java.util.ArrayList;
import java.util.List;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import org.kohsuke.stapler.Stapler;

/**
 * @since FIXME
 * 
 *        DiffBuildAction is the entry point of the functionality of diff build.
 */
public class DiffBuildAction implements Action, Describable<DiffBuildAction> {

    /**
     * The current build
     */
    private final Run<?, ?> build;

    /**
     * The descriptor of DiffBuildAction class
     */
    private static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * The constructor for initializing fields and also get all build numbers in
     * the current job
     */
    public DiffBuildAction(final Run<?, ?> build, final Launcher launcher,
            final FilePath workspace) {
        this.build = build;
        DescriptorImpl.launcher = launcher;
        DescriptorImpl.workspace = workspace;

        DescriptorImpl.allBuildNum = new ArrayList<>();
        int lastBuildNum = build.getParent().getLastBuild().number;
        int firstBuildNum = build.getParent().getFirstBuild().number;
        for (int i = lastBuildNum; i >= firstBuildNum; i--) {
            DescriptorImpl.allBuildNum.add(i);
        }
    }

    /**
     * Get current build
     */
    public Run<?, ?> getOwner() {
        return this.build;
    }

    /**
     * To invoke Console Line Diff output page
     */
    public ConsoleLineDiffDisplay getConsoleLineDiffDisplay() {
        return new ConsoleLineDiffDisplay(build);
    }

    /**
     * To invoke Log Section Diff output page
     */
    public LogSectionDiffAction getLogSectionDiffAction() {
        return new LogSectionDiffAction(build);
    }
    /**
     * To invoke Dependency Diff output page
     */
    public DependencyDiffAction getPomDepDiffAction() throws Exception{
   	return new DependencyDiffAction(build);
    }  
    /**
     * To invoke source code diff output page
     */
    public SourceCodeDiffAction getSourceCodeDiffAction() throws Exception {
        Job job = build.getParent();
        int build1 = Integer.parseInt(Stapler.getCurrentRequest().getParameter("prevBuild"));
        return new SourceCodeDiffAction(job, build1, build.getNumber(), DescriptorImpl.launcher,
                DescriptorImpl.workspace);
    }

    /** * {@inheritDoc} */
    @Override
    public String getIconFileName() {
        return "document.gif";
    }

    /** * {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return "Diff Against Other Build";
    }

    /** * {@inheritDoc} */
    @Override
    public String getUrlName() {
        return "diffbuild";
    }

    /** * {@inheritDoc} */
    @Override
    public Descriptor<DiffBuildAction> getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * The descriptor class for DiffBuildAction class
     * 
     * @author chanon
     *
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<DiffBuildAction> {

        private static List<Integer> allBuildNum;
        private static Launcher launcher;
        private static FilePath workspace;

        /**
         * Display name for Choose Type of Diff dropdown
         */
        private static final String TYPE_DIFF_DISPLAY[] = { "Console Output (By Line)",
                "Console Output (By Section)", "Source Code", "Maven Phase", "POM Dependency" };

        /**
         * Value for Choose Type of Diff dropdown, and these values have to
         * match with the url or each output page
         */
        private static final String TYPE_DIFF_VALUE[] = { "consoleLineDiffDisplay",
                "logSectionDiffAction", "sourceCodeDiffAction", "mavenPhaseDiffAction",
                "pomDepDiffAction" };

        /**
         * Fill data in Choose Another Build dropdown
         */
        public ListBoxModel doFillAllBuildItems() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < allBuildNum.size(); i++) {
                if (i == 0) {
                    items.add(new Option("build" + " " + allBuildNum.get(i),
                            allBuildNum.get(i) + "", true));
                } else {
                    items.add(new Option("build" + " " + allBuildNum.get(i),
                            allBuildNum.get(i) + "", false));
                }
            }
            return items;
        }

        /**
         * Fill data in Choose Type of Diff dropdown
         */
        public ListBoxModel doFillTypeDiffItems() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < TYPE_DIFF_DISPLAY.length; i++) {
                if (i == 0) {
                    items.add(new Option(TYPE_DIFF_DISPLAY[i], TYPE_DIFF_VALUE[i], true));
                } else {
                    items.add(new Option(TYPE_DIFF_DISPLAY[i], TYPE_DIFF_VALUE[i], false));
                }
            }
            return items;
        }

        /** * {@inheritDoc} */
        @Override
        public String getDisplayName() {
            return "Diff Build Action";
        }
    }
}
