/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 *
 * @author dhobern@gmail.com
 */
public class CollectionUtils {

    public static <T> Map<String,Set<T>> getKeyedSets(Collection<T> list, Function<T,String> method) {
        
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
    
}
