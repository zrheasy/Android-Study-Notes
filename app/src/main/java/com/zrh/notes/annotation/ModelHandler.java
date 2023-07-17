package com.zrh.notes.annotation;

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
}
