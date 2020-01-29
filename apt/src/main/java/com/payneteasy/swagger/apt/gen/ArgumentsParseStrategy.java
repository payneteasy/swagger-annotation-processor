package com.payneteasy.swagger.apt.gen;

/**
 * @author dvponomarev, 29.01.2020
 */
public enum ArgumentsParseStrategy {
    /**
     * Single int argument:
     * <pre>{@code
     * 1
     * }</pre>
     * Single complex argument:
     * <pre>{@code
     * {
     *   "status": 0,
     *   "text": "string"
     * }
     * }</pre>
     * Multiple arguments:
     * <pre>{@code
     * {
     *   "aModel4": {
     *     "status": 0,
     *     "text": "string"
     *   },
     *   "aString4": "string"
     * }
     * }</pre>
     */
    MIXED,
    /**
     * Single int argument:
     *
     * Multiple arguments:
     * <pre>{@code
     * {
     *   "aModel4": {
     *     "status": 0,
     *     "text": "string"
     *   },
     *   "aString4": "string"
     * }
     * }</pre>
     */
    MAP,
    ARRAY
}
