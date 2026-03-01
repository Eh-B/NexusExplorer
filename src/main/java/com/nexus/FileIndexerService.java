package com.nexus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * STUB CLASS: Created by Member 1 so the project compiles.
 * Member 2 will replace the logic inside these methods later.
 */
public class FileIndexerService {

    public List<NexusFile> simpleScan(File rootFolder) {

        List<NexusFile> results = new ArrayList<>();
        System.out.println("STUB: simpleScan called for " + rootFolder.getName());

        if (rootFolder != null && rootFolder.isDirectory()) {
            File[] files = rootFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) { // Only grab files, ignore sub-folders for now
                        String name = file.getName();
                        // Grab the file extension (like 'pdf' or 'java') for the category column
                        String type = "UNKNOWN";
                        if (name.contains(".")) {
                            type = name.substring(name.lastIndexOf(".") + 1).toUpperCase();
                        }
                        // Add the actual file to your list
                        results.add(new NexusFile(name, file.getAbsolutePath(), type, "#Uncategorized"));
                    }
                }
            }
        }
        return results; 
    }

    public boolean isFuzzyMatch(String query, String target) {
        // Simple placeholder logic
        return target.toLowerCase().contains(query.toLowerCase());
    }
}