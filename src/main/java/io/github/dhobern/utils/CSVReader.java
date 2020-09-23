/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @param <T>
 */
public class CSVReader<T> {
 
    private static final Logger LOG = LoggerFactory.getLogger(CSVReader.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");

    private Collection<SetterMethod> setterMethods = null;
    private BufferedReader reader = null;
    private Class<?> clazz;
    private Constructor<?> constructor;
    
    private boolean stripQuotes = false;
    private boolean suppressNulls = false;
    private final String separator;
    private final String fileName;
    
    private List<T> cachedList;
    
    private CSVReader() {
        fileName = null;
        separator = null;
    }
    
    public CSVReader(String fileName, Class c, String sep) 
            throws UnsupportedEncodingException, FileNotFoundException {
        this.fileName = fileName;
        if (sep.equals(",")) {
            sep = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        }
        separator = sep;
        clazz = c;
        initialise();
    }
    
    public CSVReader(String fileName, Class c) 
            throws UnsupportedEncodingException, FileNotFoundException {
        this(fileName, c, "\t");
    }
    
    @SuppressWarnings("unchecked")
    private void initialise()
    {
        try {
            constructor = clazz.getDeclaredConstructor();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String line = reader.readLine();
            if (line != null) {
                HashMap<String,Integer> columnIndexes = new HashMap<>();
                String[] columnHeadings = line.split(separator, -1);
                for (int i = 0; i < columnHeadings.length; i++) {
                    String columnHeading = columnHeadings[i].toLowerCase();
                    if (columnHeading.startsWith("\"") && columnHeading.endsWith("\"")) {
                        columnHeading = columnHeading.substring(1, columnHeading.length() - 1);
                    }
                    columnIndexes.put(columnHeading, i);
                }
                setterMethods = findSetterMethods(clazz, columnIndexes);
            }
        } catch (NoSuchMethodException ex) {
            LOG.error("No declared no-argument constructor found for class " + clazz.toString(), ex);
        } catch (IOException ex) {
            LOG.error("Failed to create CSVReader", ex);
        }
    }
    
    private void terminate(){
        try {
            reader.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    private T read() {
        T record = null;
        
        try {
            String line = reader.readLine();
            try {
                if (line != null) {
                    String[] fields = line.split(separator, -1);
                    
                    record = (T) constructor.newInstance();

                    for (SetterMethod m : setterMethods) {
                        if (fields.length > m.getColumn()) {
                            String field = fields[m.getColumn()];
                            if (field != null && field.length() > 0) {
                                if (field.length() >= 2 && field.startsWith("\"") && field.endsWith("\"")) {
                                    field = field.substring(1, field.length() - 1);
                                }
                                Object parameter = getParameter(m.getType(), field);
                                m.getMethod().invoke(record, parameter);
                            }
                        }
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
                LOG.error("Failed to construct record from line: " + line, ex);
            }
        } catch (IOException ex) {
            LOG.error("Failed to read line", ex);
        }
        
        return record;
    }
    
    public List<T> getList() {
        return getList(false);
    }
    
    public List<T> getList(boolean cacheList) {
        
        initialise();
        
        List<T> list = cachedList;
        
        if (list == null) {
            list = new ArrayList<>();

            T t;
            while ((t = read()) != null) {
                list.add(t);
            }
            
            if (cacheList) {
                cachedList = list;
            }
        }
        
        terminate();
        
        return list;
    }
    
    public Map<String,T> getMap(Function<T,String> method) {
        return getMap(method, false);
    }
    
    public Map<String,T> getMap(Function<T,String> method, boolean cacheList) {
        List<T> list = getList(cacheList);
        
        Map<String,T> map = new LinkedHashMap<>();
        
        for (T t : list) {
            map.put(method.apply(t), t);
        }
        
        return map;
    }

    public Map<Integer,T> getIntegerMap(Function<T,Integer> method) {
        return getIntegerMap(method, false);
    }

    public Map<Integer,T> getIntegerMap(Function<T,Integer> method, boolean cacheList) {
        List<T> list = getList(cacheList);
        
        Map<Integer,T> map = new LinkedHashMap<>();
        
        for (T t : list) {
            map.put(method.apply(t), t);
        }
        
        return map;
    }

    public Map<String,Set<T>> getKeyedSets(Function<T,String> method) {
        return getKeyedSets(method, false);
    }
    
    public Map<String,Set<T>> getKeyedSets(Function<T,String> method, boolean cacheList) {
        List<T> list = getList(cacheList);
        
        Map<String,Set<T>> map = new LinkedHashMap<>();
        
        for (T t : list) {
            String key = method.apply(t);
            Set<T> set = map.get(key);
            if (set == null) {
                set = new TreeSet<>();
                map.put(key, set);
            }
            set.add(t);
        }
        
        return map;
    }

    public Map<Integer,Set<T>> getIntegerKeyedSets(Function<T,Integer> method) {
        return getIntegerKeyedSets(method, false);
    }
    
    public Map<Integer,Set<T>> getIntegerKeyedSets(Function<T,Integer> method, boolean cacheList) {
        List<T> list = getList(cacheList);
        
        Map<Integer,Set<T>> map = new LinkedHashMap<>();
        
        for (T t : list) {
            Integer key = method.apply(t);
            Set<T> set = map.get(key);
            if (set == null) {
                set = new TreeSet<>();
                map.put(key, set);
            }
            set.add(t);
        }
        
        return map;
    }

    private Collection<SetterMethod> findSetterMethods(Class clazz, HashMap<String,Integer> columns) {
        HashMap<String, SetterMethod> map = new HashMap<>();
        
        for (Method m : clazz.getMethods()) {
            if(m.getName().startsWith("set")) {
                Type[] types = m.getParameterTypes();
                if (types.length == 1 && canSupportType(types[0])) {
                    String p = m.getName().substring(3).toLowerCase();
                    Integer column = columns.get(p);
                    if (column != null) {
                        String t = types[0].getTypeName();
                        SetterMethod setter = map.get(p);
                        if (setter == null) {
                            map.put(p, new SetterMethod(p, m, t, column));
                            LOG.debug("New SetterMethod: " + p + ", " + m + ", " + t + ", " + column);
                        } else if (t.equals("java.lang.String")) {
                            setter.setMethod(m, t);
                            LOG.debug("Updated SetterMethod: " + p + ", " + m + ", " + t + ", " + column);
                        }
                    }
                }
            }
        }
        
        return map.values();
    }
    
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            reader = null;
        }
    }

    private boolean canSupportType(Type type) {
        String name = type.getTypeName();
        boolean supported = false;
        
        if (    name.equals("java.lang.String")
             || name.equals("java.lang.CharSequence")
             || name.equals("java.lang.Integer")
             || name.equals("java.lang.Float")
             || name.equals("java.lang.Double")
             || name.equals("java.time.LocalDate")) {
            supported = true;
        }
        
        return supported;
    }

    @SuppressWarnings("UnusedAssignment")
    private Object getParameter(String type, String field) {
        Object o = null;
        
        try {
            switch(type) {
                case "java.lang.String", "java.lang.CharSequence" -> {
                    if (stripQuotes && field.matches("^\".*\"$")) {
                        field = field.substring(1, field.length() - 1);
                    }
                    if (!suppressNulls || !field.equalsIgnoreCase("\\n")) {
                        o = field;
                    }
                }
                
                case "java.lang.Integer" -> o = Integer.valueOf(field);

                case "java.lang.Float" -> o = Float.valueOf(field);

                case "java.lang.Double" -> o = Double.valueOf(field);

                case "java.time.LocalDate" -> o = LocalDate.parse(field, DATE_FORMAT);
            }
        } catch (NumberFormatException e) {
            LOG.error("Could not create " + type + " from " + field);
        }
        
        return o;
    }

    public void stripQuotes() {
        stripQuotes = true;
    }

    public void suppressNulls() {
        suppressNulls = true;
    }

    private static class SetterMethod {

        String property;
        Method method;
        String type;
        Integer column;

        public SetterMethod(String p, Method m, String t, Integer c) {
            property = p;
            method = m;
            type = t;
            column = c;
        }
        
        public void setMethod(Method m, String t) {
            method = m;
            type = t;
        }

        public String getProperty() {
            return property;
        }

        public Method getMethod() {
            return method;
        }

        public String getType() {
            return type;
        }

        public Integer getColumn() {
            return column;
        }  
    }
}
