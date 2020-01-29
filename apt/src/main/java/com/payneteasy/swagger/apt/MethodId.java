package com.payneteasy.swagger.apt;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Method unique id within its service.<br/>
 * Determines swagger REST method path.<br/>
 * Got from {@link ExportToSwagger#value() ExportToSwagger.value()}, defaults to method name.
 *
 * @author dvponomarev, 05.02.2019
 */
public class MethodId implements Comparable<MethodId> {

    @NotNull
    public final String id;

    public MethodId(@NotNull String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MethodId methodId = (MethodId) o;
        return id.equals(methodId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(@NotNull MethodId o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "MethodId{" +
               "id='" + id + '\'' +
               '}';
    }

}
