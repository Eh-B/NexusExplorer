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
        // Return an empty list for now so the UI doesn't crash
        System.out.println("STUB: simpleScan called for " + rootFolder.getName());
        return new ArrayList<>(); 
    }

    public boolean isFuzzyMatch(String query, String target) {
        // Simple placeholder logic
        return target.toLowerCase().contains(query.toLowerCase());
    }
}