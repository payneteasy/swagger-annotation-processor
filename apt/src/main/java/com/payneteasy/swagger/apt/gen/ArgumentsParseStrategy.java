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
     *
     * Single string argument:
     * <pre>{@code
     * "some text"
     * }</pre>
     *
     * Single complex argument:
     * <pre>{@code
     * {
     *   "status": 0,
     *   "text": "some text"
     * }
     * }</pre>
     *
     * Multiple arguments:
     * <pre>{@code
     * {
     *   "anInt": 0,
     *   "aString": "some text",
     *   "aModel": {
     *     "status": 0,
     *     "text": "some text"
     *   }
     * }
     * }</pre>
     *
     * <b>Note for multi parameter methods</b>: for service survive on service method parameters rename use
     * {@link com.payneteasy.swagger.apt.annotation.MethodParam @MethodParam} annotation.
     */
    MIXED,

    /**
     * Single int argument:
     * <pre>{@code
     * {
     *   "anInt": 0
     * }
     * }</pre>
     *
     * Single string argument:
     * <pre>{@code
     * {
     *   "aString": "some text"
     * }
     * }</pre>
     *
     * Single complex argument:
     * <pre>{@code
     * {
     *   "aModel": {
     *     "status": 0,
     *     "text": "some text"
     *   }
     * }
     * }</pre>
     *
     * Multiple arguments:
     * <pre>{@code
     * {
     *   "anInt": 0,
     *   "aString": "some text"
     *   "aModel": {
     *     "status": 0,
     *     "text": "some text"
     *   }
     * }
     * }</pre>
     *
     * <b>Note</b>: for service survive on service method parameters rename use
     * {@link com.payneteasy.swagger.apt.annotation.MethodParam @MethodParam} annotation.
     */
    MAP

}
