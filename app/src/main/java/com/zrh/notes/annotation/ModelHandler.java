package com.zrh.notes.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zrh
 * @date 2023/7/17
 */
public class ModelHandler {
    public static void handle(Object target) {
        Class<?> clazz = target.getClass();
        if (!clazz.isAnnotationPresent(Model.class)) return;
        Model model = clazz.getAnnotation(Model.class);
        String modelName = model.name();
        System.out.println("find model:" + modelName);
    }

    public static List<String> getAnnotations(Object target) {
        List<String> annotations = new ArrayList<>();
        for (Annotation annotation : target.getClass().getAnnotations()) {
            annotations.add(annotation.toString());
        }
        return annotations;
    }
}
