package com.payneteasy.swagger.apt.javadoc;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple javadoc parser.<br/>
 * Extracts method head, parameters descriptions and return description.
 *
 * @author dvponomarev, 31.01.2019
 */
public class JavadocParser {

    private static final String PARAM  = "@param";
    private static final String RETURN = "@return";

    private static final Set<String> SUPPORTED_TAGS = Sets.newHashSet(PARAM, RETURN);

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
    private static final Pattern TAG_PATTERN        = Pattern.compile("[^{]@\\w+");
    private static final Pattern HTML_TAG_PATTERN   = Pattern.compile("<.+?/>|<.+?>");

    /**
     * Parse class javadoc.
     *
     * @param javadoc class javadoc.
     * @return class javadoc.
     */
    @NotNull
    public static String parseClass(@NotNull String javadoc) {
        final String head = cutClassHead(javadoc);
        return HTML_TAG_PATTERN.matcher(head).replaceAll("").trim();
    }

    @NotNull
    private static String cutClassHead(@NotNull String javadoc) {
        final String input = " " + javadoc;
        final int    index = indexOfPattern(input, TAG_PATTERN);
        if (index < 0) {
            return javadoc.trim();
        } else {
            return input.substring(0, index).trim();
        }
    }

    /**
     * Parse method javadoc.
     *
     * @param javadoc method javadoc.
     * @return method javadocs.
     */
    @NotNull
    public static MethodJavadocInfo parseMethod(@NotNull String javadoc) {
        final String  input   = " " + javadoc;
        final Matcher matcher = TAG_PATTERN.matcher(input);
        if (!matcher.find()) {
            //no tags found
            return new MethodJavadocInfo(javadoc.trim(), Collections.emptyMap(), "");
        }

        final List<TagLocation> tagLocations = new ArrayList<>();
        do {
            tagLocations.add(new TagLocation(matcher.start() + 1, matcher.end()));
        } while (matcher.find());

        final String head;
        if (tagLocations.isEmpty()) {
            head = javadoc.trim();
        } else {
            head = input.substring(1, tagLocations.get(0).start).trim();
        }

        final Map<String, String> paramsJavadocs = new HashMap<>();
        String                    returnJavadoc  = "";
        for (int i = 0; i < tagLocations.size(); i++) {
            final TagLocation tagLocation = tagLocations.get(i);
            final String      tag         = input.substring(tagLocation.start, tagLocation.end);
            if (!SUPPORTED_TAGS.contains(tag)) {
                continue;
            }

            final int nextStart;
            if (i >= (tagLocations.size() - 1)) {
                //the last
                nextStart = input.length();
            } else {
                nextStart = tagLocations.get(i + 1).start;
            }
            final String tagData = input.substring(tagLocation.end, nextStart).trim();

            if (tag.equals(PARAM)) {
                final int firstSpaceIndex = indexOfPattern(tagData, WHITESPACE_PATTERN);
                if (firstSpaceIndex < 0) {
                    //@param
                    //@param a
                    continue;
                }
                final String parameterName    = tagData.substring(0, firstSpaceIndex);
                final String parameterJavadoc = tagData.substring(firstSpaceIndex).trim();
                paramsJavadocs.put(parameterName, parameterJavadoc);
            } else if (tag.equals(RETURN)) {
                returnJavadoc = tagData;
            } else {
                throw new IllegalArgumentException(String.format("Unsupported tag '%s'.", tag));
            }
        }

        return new MethodJavadocInfo(head, paramsJavadocs, returnJavadoc);
    }

    private static int indexOfPattern(String string, Pattern pattern) {
        final Matcher matcher = pattern.matcher(string);
        return matcher.find() ? matcher.start() : -1;
    }

    private static class TagLocation {
        /** Inclusive. */
        final int start;
        /** Exclusive. */
        final int end;

        TagLocation(int start, int end) {
            this.start = start;
            this.end   = end;
        }
    }

}
