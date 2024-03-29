package com.uaepay.pub.csc.test.dal.es;

import java.io.*;
import java.util.Set;

import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.common.xcontent.json.JsonXContentGenerator;
import org.elasticsearch.common.xcontent.json.JsonXContentParser;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * org.elasticsearch.common.xcontent.json.JsonXContent
 */
public class JsonXContentEx implements XContent {

    public static XContentBuilder contentBuilder() throws IOException {
        return new XContentBuilder(jsonXContent, new ByteArrayOutputStream());
    }

    private static final JsonFactory jsonFactory;

    public static final JsonXContentEx jsonXContent;

    static {
        jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        jsonFactory.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        jsonFactory.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // Do not automatically close unclosed objects/arrays in
        // com.fasterxml.jackson.core.json.UTF8JsonGenerator#close() method
        jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
        jsonFactory.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);
        jsonXContent = new JsonXContentEx();
    }

//    public static void main(String[] args) {
//        System.out.println(jsonFactory.create.gen);
//    }

    private JsonXContentEx() {}

    @Override
    public XContentType type() {
        return XContentType.JSON;
    }

    @Override
    public byte streamSeparator() {
        return '\n';
    }

    @Override
    public XContentGenerator createGenerator(OutputStream os, Set<String> includes, Set<String> excludes)
        throws IOException {
        return new JsonXContentGenerator(jsonFactory.createGenerator(os, JsonEncoding.UTF8), os, includes, excludes);
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, DeprecationHandler deprecationHandler,
        String content) throws IOException {
        return new JsonXContentParser(xContentRegistry, deprecationHandler,
            jsonFactory.createParser(new StringReader(content)));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, DeprecationHandler deprecationHandler,
        InputStream is) throws IOException {
        return new JsonXContentParser(xContentRegistry, deprecationHandler, jsonFactory.createParser(is));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, DeprecationHandler deprecationHandler,
        byte[] data) throws IOException {
        return new JsonXContentParser(xContentRegistry, deprecationHandler, jsonFactory.createParser(data));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, DeprecationHandler deprecationHandler,
        byte[] data, int offset, int length) throws IOException {
        return new JsonXContentParser(xContentRegistry, deprecationHandler,
            jsonFactory.createParser(data, offset, length));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, DeprecationHandler deprecationHandler,
        Reader reader) throws IOException {
        return new JsonXContentParser(xContentRegistry, deprecationHandler, jsonFactory.createParser(reader));
    }

}
