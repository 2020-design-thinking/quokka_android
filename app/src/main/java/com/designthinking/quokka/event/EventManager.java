package com.designthinking.quokka.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private static EventManager instance;

    public List<IEventListener> listeners = new ArrayList<>();

    public EventManager(){
        instance = this;
    }

    public static EventManager getInstance(){
        if(instance == null)
            instance = new EventManager();
        return instance;
    }

    public void addListener(IEventListener listener){
        if(listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void removeListener(IEventListener listener){
        listeners.remove(listener);
    }

    public void invoke(Event message){
        for (IEventListener listener : listeners){
            for(Method method : listener.getClass().getMethods()){
                Class<?>[] types = method.getParameterTypes();
                if(types.length == 1 && message.getClass().equals(types[0])){
                    try {
                        method.invoke(listener, message);
                        break;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
