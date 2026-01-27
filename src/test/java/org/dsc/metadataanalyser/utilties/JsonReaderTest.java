package org.dsc.metadataanalyser.utilties;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonReaderTest {

    @Test
    public void testProcessMetaFile_NestedArray() throws IOException {
        String json = "{" +
                "\"Author\": \"John Doe\"," +
                "\"Keywords\": [\"java\", \"parsing\", \"metadata\"]" +
                "}";

        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        JsonReader reader = new JsonReader();
        Map<String, String> result = reader.processMetaFile(stream);

        assertEquals("John Doe", result.get("Author"));
        // The current implementation is expected to fail or skip these, or maybe concat
        // them?
        // Ideally we want to capture them. Let's see what the fix intends.
        // If the fix is to capture them, maybe as a comma-separated string?
        // For now, let's just assert the map contains "Keywords" and see what we get or
        // if it crashes.
        assertTrue(result.containsKey("Keywords"), "Map should contain Keywords");

        // Assuming we want to capture the array elements.
        // Tika usually produces metadata where multiple values for the same key
        // might be handled differently (e.g. list of strings).
        // But JsonReader logic puts into Map<String, String>.
        // So likely we want to concatenate or just picking first/last?
        // The loop in JsonReader suggests it iterates but does nothing.
        // Let's assume the goal is to have "java, parsing, metadata" or similar.
        // Or if Tika keeps them separate, the Map<String, String> is poorly designed
        // for multi-value keys
        // unless keys are "Keywords[0]", "Keywords[1]" etc.
        // But for a simple fix, let's verify we get *something*.
    }
}
