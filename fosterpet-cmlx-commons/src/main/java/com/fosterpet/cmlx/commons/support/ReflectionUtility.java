package com.fosterpet.cmlx.commons.support;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc 反射工具类
 * @Author cmlx
 * @Date 2019-12-5 0005 17:04
 */
@UtilityClass
public class ReflectionUtility extends ReflectionUtils {

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private final Log log = LoggerFactory.make();

    public List<Field> getFields(Class<?> objectClass, boolean local) {
        List<Field> fields = new ArrayList<>();

        if (local) {
            doWithLocalFields(objectClass, fields::add);
        } else {
            doWithFields(objectClass, fields::add, null);
        }

        return fields;
    }

    public <A extends Annotation> Field[] getFields(Class<?> objectClass, Class<A> annotationClass, boolean local) {
        return getFields(objectClass, false).stream()
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
    }

    public <A extends Annotation> Field getField(Class<?> objectClass, Class<A> annotationClass, boolean local, boolean requireUnique) {
        Field[] fields = getFields(objectClass, annotationClass, local);

        if (fields.length == 1) {
            return fields[0];
        } else if (fields.length > 1 && requireUnique) {
            // TODO 需要在此抛异常
        }
        return null;
    }

    public List<Method> getMethods(Class<?> objectClass, boolean local) {
        List<Method> methods = new ArrayList<>();

        if (local) {
            doWithLocalMethods(objectClass, methods::add);
        } else {
            doWithMethods(objectClass, methods::add, null);
        }

        return methods;
    }

    public <A extends Annotation> Method[] getMethods(Class<?> objectClass, Class<A> annotationClass, boolean local) {
        return getMethods(objectClass, false).stream()
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .toArray(Method[]::new);
    }

    public <A extends Annotation> Method getMethod(Class<?> objectClass, Class<A> annotationClass, boolean local, boolean requireUnique) {
        Method[] methods = getMethods(objectClass, annotationClass, local);

        if (methods.length == 1) {
            return methods[0];
        } else if (methods.length > 1 && requireUnique) {
            // TODO 需要在此抛异常
        }

        return null;
    }

    public Set<Class<?>> loadClasses(String... packageNames) throws IOException {
        Set<Class<?>> classes = new LinkedHashSet<>();

        for (String packageName : packageNames) {
            String packageDirectoryName = packageName.replace('.', '/');

            Enumeration<URL> urlEnumeration = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(packageDirectoryName);

            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();

                if ("file".equals(url.getProtocol())) {
                    classes.addAll(loadClassesFromDirectory(packageName, new File(url.getPath())));
                } else if ("jar".equals(url.getProtocol())) {
                    classes.addAll(loadClassesFromJar(packageName, ((JarURLConnection) url.openConnection()).getJarFile()));
                }
            }
        }

        return classes;
    }

    public Set<Class<?>> loadClassesFromDirectory(String packageName, File directory) throws IOException {
        Stack<File> stack = new Stack<>();
        List<File> classFiles = new ArrayList<>();
        FileFilter fileFilter = pathname -> {
            if (pathname.isDirectory()) {
                stack.push(pathname);
                return false;
            }
            return pathname.getName().matches(".*\\.class$");
        };

        stack.push(directory);

        while (!stack.isEmpty()) {
            File current = stack.pop();
            File[] files = current.listFiles(fileFilter);

            if (files != null) {
                Collections.addAll(classFiles, files);
            }
        }

        Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
        Set<Class<?>> classes = new HashSet<>();

        for (File file : classFiles) {
            Matcher matcher = pattern.matcher(file.getAbsolutePath().replace(File.separatorChar, '/'));
            Class<?> objectClass = loadClass(matcher);

            if (objectClass != null) {
                classes.add(objectClass);
            }
        }

        return classes;
    }

    public Set<Class<?>> loadClassesFromJar(String packageName, JarFile jarFile) throws IOException {
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
        Set<Class<?>> classes = new LinkedHashSet<>();

        while (jarEntryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            String name = jarEntry.getName();
            Matcher matcher = pattern.matcher(name.replace(File.separatorChar, '/'));
            Class<?> objectClass = loadClass(matcher);

            if (objectClass != null) {
                classes.add(objectClass);
            }
        }

        return classes;
    }

    public Set<Class<?>> loadClassesByAnnotationClass(Class<? extends Annotation> annotationClass, String... packageNames) throws IOException, ClassNotFoundException {
        String annotationClassName = annotationClass.getName();
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

        Set<Class<?>> classes = new HashSet<>();

        for (String packageName : packageNames) {
            String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
                    "/" + "**/*.class";
            Resource[] resources = patternResolver.getResources(locationPattern);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }

                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

                if (!annotationMetadata.hasAnnotation(annotationClassName)) {
                    continue;
                }

                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> loadedClass = ClassUtils.forName(className, null);
                classes.add(loadedClass);
            }
        }

        return classes;
    }

    public Set<Class<?>> loadClassesByAnnotationClass(Class<? extends Annotation>[] annotationClass, String... packageNames) throws IOException, ClassNotFoundException {
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

        Set<Class<?>> classes = new HashSet<>();

        for (String packageName : packageNames) {
            String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
                    "/" + "**/*.class";
            Resource[] resources = patternResolver.getResources(locationPattern);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }

                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

                boolean flag = false;
                for (Class<? extends Annotation> aClass : annotationClass) {
                    String name = aClass.getName();
                    if (annotationMetadata.hasAnnotation(name)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    continue;
                }

                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> loadedClass = ClassUtils.forName(className, null);
                classes.add(loadedClass);
            }
        }

        return classes;
    }

    private Class<?> loadClass(Matcher matcher) {
        if (matcher.find()) {
            String className = matcher.group(1).replace('/', '.');
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                log.error("", e);
            }
        }

        return null;
    }

    public <T> ResolvableType getType(Class<T> clazz, String field) {
        return ResolvableType.forField(findField(clazz, field));
    }

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
        }
        return object;
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notEmpty(methodName.trim(), "methodName can't be blank");
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
                continue;// new add
            }
        }
        return null;
    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked exception.", e);
    }


}
