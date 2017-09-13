package com.amoment.logging.event;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by xudeng on 2017/6/9.
 */
public class LoginEvent extends UserEvent{

    private String m_LoginTime;

    public void Print(){
        System.out.println(serialize());
    }

    @Override
    public JsonElement serialize(BaseEvent src, Type typeOfSrc, JsonSerializationContext context) {

        super.serialize(src, typeOfSrc, context);

        m_JsonObject.add("LoginTime", context.serialize(m_LoginTime));

        return m_JsonObject;
    }
}
