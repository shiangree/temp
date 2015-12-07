package hudson.plugins.logparser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DiffToHtmlUtils is used to generate HTML for diff.
 */
public class DiffToHtmlUtils {
    private static String generateBody(int build1, int build2, String diffType,
            Map<String, List<String>> content1, Map<String, List<String>> content2,
            Map<String, String> iconLocations) {
        // Keys
        Set<String> content1Keys = content1.keySet();
        Set<String> content2Keys = content2.keySet();

        // Compute intersection
        Set<String> intersection = new HashSet<>(content1Keys);
        intersection.retainAll(content2Keys);

        // Compute content 1 uniques
        Set<String> content1Uniques = new HashSet<>(content1Keys);
        content1Uniques.removeAll(content2Keys);

        // Compute content 2 uniques
        Set<String> content2Uniques = new HashSet<>(content2Keys);
        content2Uniques.removeAll(content1Keys);

        StringBuilder sb = new StringBuilder();
        sb.append("<body>\n");
        sb.append(generateBodyTitle(build1, build2, diffType));
        for (String unique : content1Uniques) {
            sb.append(generateUniqueItem(build1, unique));
        }
        for (String unique : content2Uniques) {
            sb.append(generateUniqueItem(build2, unique));
        }
        for (String common : intersection) {
            sb.append(generateCommonItem(build1, build2, common, content1.get(common),
                    content2.get(common)));
        }
        sb.append("</body>\n");
        return sb.toString();
    }

    private static String generateBodyTitle(int build1, int build2, String diffType) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        sb.append(String.format("<font size = \"5\">%s diff between build %s and build %s</font>",
                diffType, build1, build2));
        sb.append("</div>");
        sb.append("<br>");
        return sb.toString();
    }

    private static String generateUniqueItem(int build, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        sb.append("build " + build + " unique: " + key);
        sb.append("</div>");
        return sb.toString();
    }

    private static String generateCommonItem(int build1, int build2, String key,
            List<String> content1, List<String> content2) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        sb.append("build " + build1 + " and build " + build2 + " share " + key);
        sb.append("</div>");
        sb.append(new DiffToHtmlGenerator(content1, content2).generateHtmlString(true));
        return sb.toString();
    }

    private static String generateHead() {
        StringBuilder sb = new StringBuilder();
        sb.append("<head>\n");
        sb.append("<meta charset='utf-8'>\n");
        // CSS file can not be served in this way
        // sb.append("<link rel='stylesheet' type='text/css'
        // href='style.css'>\n");
        sb.append("<style>\n");
        sb.append(new DiffToHtmlGenerator().generateCSS());
        sb.append("</style>\n");
        sb.append("</head>\n");
        return sb.toString();
    }

    /**
     * Generate the HTML for diff.
     *
     * @param build1
     *            the build number of the left side
     * @param build2
     *            the build number of the right side
     * @param diffType
     *            the type of diff
     * @param content1
     *            the content for the left side build
     * @param content2
     *            the content for the right side build
     * @param iconLocations
     *            the locations for icons for sections, if any
     * @return the HTML for the diff as a String
     */
    public static String generateDiffHTML(int build1, int build2, String diffType,
            Map<String, List<String>> content1, Map<String, List<String>> content2,
            Map<String, String> iconLocations) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>\n");
        sb.append("<html lang='en'>\n");
        sb.append(generateHead());
        sb.append(generateBody(build1, build2, diffType, content1, content2, iconLocations));
        sb.append("</html>\n");
        return sb.toString();
    }
}
