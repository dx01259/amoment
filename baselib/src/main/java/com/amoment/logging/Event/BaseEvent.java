package com.amoment.logging.Event;


import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

/**
 * Created by xudeng on 2017/6/8.
 */
public abstract class BaseEvent implements JsonSerializer<BaseEvent>{

    protected JsonObject m_JsonObject = new JsonObject();

    protected abstract String serialize();
}
