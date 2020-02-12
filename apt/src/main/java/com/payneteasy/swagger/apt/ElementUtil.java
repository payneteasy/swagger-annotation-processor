package com.payneteasy.swagger.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author dvponomarev, 12.02.2020
 */
public class ElementUtil {

    public static boolean containsAtLeastOneMethod(Collection<? extends Element> elements) {
        return elements.stream().anyMatch(element -> element.getKind() == ElementKind.METHOD);
    }

    public static Collection<Element> methods(Collection<? extends Element> elements) {
        return elements.stream().filter(element -> element.getKind() == ElementKind.METHOD).collect(Collectors.toList());
    }

}
