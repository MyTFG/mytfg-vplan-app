package de.mytfg.apps.mytfg.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class allows to specify parameters to pass to the API.
 * This is just a wrapper class for Map.
 */
public class ApiParams {
    private Map<String, String> parameters;

    /**
     * Creates a new empty Set of API-parameters
     */
    public ApiParams() {
        parameters = new HashMap<>();
        addLanguage();
    }

    public ApiParams(boolean addLang) {
        this();
        if (addLang) {
            addLanguage();
        }
    }
    /**
     * Creates a new Set of API-parameters.
     * @param params The params to create as String-Array. Always pass pairs of "key" and "value".
     */
    public ApiParams(String... params) {
        this();
        if (params != null && params.length > 0 && (params.length % 2 == 0)) {
            // There are given params and params can be interpreted as pairs of key and value
            int i = 0;
            while (i < params.length) {
                parameters.put(params[i], params[i+1]);
                i += 2;
            }
        }
    }

    /**
     * Creates a new Set of API-parameters from a given Map of key / value pairs.
     * @param params The params to create as String-String Map.
     */
    public ApiParams(Map<String, String> params) {
        this();
        this.parameters = params;
    }

    /**
     * Adds a new parameter.
     * @param key The key for the new parameter.
     * @param value The value to assign.
     */
    public void addParam(String key, String value) {
        parameters.put(key, value);
    }

    /**
     * Returns the value for a given key.
     * @param key The key to search the value for.
     * @return The value for the given key or Empty String iff key not found.
     */
    public String get(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        } else {
            return "";
        }
    }

    protected Map<String, String> getMap() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiParams)) {
            return false;
        }
        ApiParams params = (ApiParams) o;

        return (this.parameters.equals(params.parameters));
    }

    @Override
    public int hashCode() {
        return this.parameters.hashCode();
    }

    @Override
    public String toString() {
        return this.parameters.toString();
    }

    public ApiParams addLanguage() {
        this.addParam("language", Locale.getDefault().getLanguage());
        return this;
    }

    public String toUrlString() {
        return urlEncodeUTF8(this.parameters);
    }

    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    private static String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }
}
