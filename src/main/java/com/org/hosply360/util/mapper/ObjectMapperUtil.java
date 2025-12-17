package com.org.hosply360.util.mapper;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@FunctionalInterface
public interface ObjectMapperUtil {

    void copy(Object source, Object target);

    static void safeCopyObject(Object source, Object target) {
        if (source != null && target != null) {
            BeanUtils.copyProperties(source, target);
        }
    }


     static <T> T safeCopyObjectAndIgnore(Object source, T target, List<String> ignore) {
        if (source == null || target == null) return target;

        String[] ignoreProperties = ignore != null ? ignore.toArray(new String[0]) : new String[0];
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    static <T> T copyObject(Object source, Class<T> targetClass) {
        if (source == null) return null;

        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            safeCopyObject(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + targetClass.getName(), e);
        }
    }

    static <S, T> List<T> copyListObject(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null) return List.of();
        return sourceList.stream()
                .map(item -> copyObject(item, targetClass))
                .toList();
    }

     static <S, T> Set<T> copySetObject(Set<S> sourceSet, Class<T> targetClass) {
        if (sourceSet == null) return Set.of();
        return sourceSet.stream()
                .map(item -> copyObject(item, targetClass))
                .collect(Collectors.toSet());
    }




}
