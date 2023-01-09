package org.dsc.metadataanalyser.utilties;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class JsonReader {

    JsonFactory jfactory = JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_MISSING_VALUES,
                    JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS,
                    JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
                    JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES,
                    JsonReadFeature.ALLOW_TRAILING_COMMA,
                    JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS,
                    JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
            .build();
    JsonParser jParser;

    private final Map<String, String> keyValPairs = new HashMap<>();

    public JsonReader() {
    }

    public Map<String, String> processMetaFile(InputStream stream) throws IOException {

            jParser = jfactory.createParser(stream);

            JsonToken nextToken;
            jParser.nextToken();
            while ((nextToken = jParser.nextToken()) != JsonToken.END_OBJECT && jParser.hasCurrentToken()) {
                if (nextToken == JsonToken.FIELD_NAME) {
                    String nextFieldName = jParser.currentName();
                    nextToken = jParser.nextToken();
                    if (nextToken.isStructStart()) {
                        while (jParser.nextToken() != JsonToken.END_ARRAY) {
                            //To Do This may be a bug here
                            String val = jParser.getValueAsString();

                        }
                    }
                    if (jParser.getValueAsString() != null) {
                        String val = jParser.getValueAsString();
                        keyValPairs.put(nextFieldName, val);
                    }
                }

            }

        return keyValPairs;
    }


}
