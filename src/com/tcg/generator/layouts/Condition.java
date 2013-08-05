/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.layouts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mmain
 */
public class Condition {
    protected String variable = null;
    protected String operator = null;
    protected String value = null;
    
    Condition() {}
    
    Condition(String expression) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)\\s*(==|!=|>|<|>=|<=|contains|in)\\s*([a-zA-Z0-9\"\']+)");
        
        Matcher matcher = pattern.matcher(expression);
        
        if (matcher.matches()) {
            variable = matcher.group(1);
            operator = matcher.group(2);
            value    = matcher.group(3);
        }
    }
    
    protected boolean isValid() {
        return variable != null && operator != null && value != null;
    }
    
    public boolean test(LinkedHashMap<String, Object> map) {
        if (!isValid()) {
            return false;
        }
        
        Object object = map.get(variable);
        
        if (object == null) {
            return false;
        }
        
        return doComparison(object);
    }
    
    public boolean test(Object object) {
        if (!isValid()) {
            return false;
        }
        
        Field field;
        
        try {
            field = object.getClass().getField(variable);
            object = field.get(object);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            return false;
        }
        
        return doComparison(object);
    }
    
    private boolean doComparison(Object object) {
        switch (operator) {
            case "==":
                return object.toString().equals(value);
            case "!=":
                return !object.toString().equals(value);
            case "<=":
                return (Integer)object <= Integer.parseInt(value);
            case ">=":
                return (Integer)object >= Integer.parseInt(value);
            case "<":
                return (Integer)object < Integer.parseInt(value);
            case ">":
                return (Integer)object > Integer.parseInt(value);
            case "contains":
                if (object.getClass().getSimpleName() == "ArrayList") {
                    ArrayList list = (ArrayList)object;
                    return list.contains(value);
                } else if (object.getClass().getSimpleName().equals("String")) {
                    String string = (String)object;
                    return string.contains(value.toString());
                } else {
                    return false;
                }
            case "in":
                // Not implemented yet
                return false;
            default:
                return false;
        }
    }
}
