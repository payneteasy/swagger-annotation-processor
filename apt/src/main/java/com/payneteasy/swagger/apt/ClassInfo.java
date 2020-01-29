package com.payneteasy.swagger.apt;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author dvponomarev, 23.01.2019
 */
class ClassInfo {

    @NotNull
    final String packageName;
    @NotNull
    final String className;

    ClassInfo(@NotNull String packageName, @NotNull String className) {
        this.packageName = packageName;
        this.className   = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClassInfo classInfo = (ClassInfo) o;
        return packageName.equals(classInfo.packageName) &&
               className.equals(classInfo.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, className);
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
               "packageName='" + packageName + '\'' +
               ", className='" + className + '\'' +
               '}';
    }

}
