package io.apigee.buildTools.enterprise4g.dep.policy.resources;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonResourceProcessor implements ResourceProcessor {
    private final ArrayList<String> jsFiles;
    private File policyFile;

    public CommonResourceProcessor(File policy) throws IOException {
        this.policyFile = policy;
        jsFiles = processPolicyJSResources(policy);
    }

    private ArrayList<String> processPolicyJSResources(File srcFile) throws IOException {
        final List<String> lines = FileUtils.readLines(srcFile);
        final ArrayList<String> resourceFiles = new ArrayList<String>();
        for (String line : lines) {
            processResourceURLs(resourceFiles, line);
        }
        return resourceFiles;
    }

    private void processResourceURLs(ArrayList<String> resourceFiles, String line) {
        String res = getUrl(line, "ResourceURL");
        addResToList(resourceFiles, res);
        res = getUrl(line, "IncludeURL");
        addResToList(resourceFiles, res);
    }

    private void addResToList(ArrayList<String> resourceFiles, String res) {
        if (res != null) {
            resourceFiles.add(res);
        }
    }

    private String getUrl(String line, String urlTag) {
        Pattern pattern;
        String resources[] = new String[]{"jsc", "java", "py", "xsl","node"};
        for (String resource : resources) {
            pattern = Pattern.compile(String.format("<%1$s>" + resource + "://(.*)</%1$s>", urlTag));
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.find()) {
                if (resource.equals("jsc")) {
                    return "jsc/" + matcher.group(1);
                } else if (resource.equals("java")) {
                    return "java/" + matcher.group(1);
                } else if (resource.equals("py")) {
                    return "py/" + matcher.group(1);
                } else if (resource.equals("xsl")) {
                    return "xsl/" + matcher.group(1);
                }else if (resource.equals("node")) {
                    return "node/" + matcher.group(1);
                }

            }
        }
        return null;
    }

    public void actOn(File targetDir, Log log) throws IOException {
        if (jsFiles.isEmpty()) {
            return;
        }
        copyFiles(targetDir, log);

    }

    private void copyFiles(File targetDir, Log log) throws IOException {
        for (String resourceFile : jsFiles) {
            copyResourceFile(resourceFile, targetDir, log);
        }
    }

    private void copyResourceFile(String resourceFile, File targetDir, Log log) throws IOException {
        final File inFile = new File(policyFile.getParent() + "/../resources/" + resourceFile);
        if (!inFile.exists()) {
            log.warn(String.format("File %s does not exists.", inFile.getAbsolutePath()));
            return;
        }
        final File outFile = new File(targetDir.getPath() + "/" + resourceFile);
        outFile.getParentFile().mkdirs();
        log.debug(String.format("Copying file %s to %s", inFile.getAbsolutePath(), outFile.getAbsolutePath()));
        FileUtils.copyFile(inFile, outFile);
    }
}