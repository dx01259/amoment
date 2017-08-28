package com.amoment.logging.Event;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by xudeng on 2017/6/8.
 */
public class UserEvent extends BaseEvent{

    private String m_RequestNo;
    private String m_UserName;
    private String m_UserID;

    @Override
    protected final String serialize() {
        try{
            Class thisClass = this.getClass();
            Gson gson = new GsonBuilder().registerTypeAdapter(thisClass, thisClass.newInstance())
                    .serializeNulls().create();
            return gson.toJson(this);
        }catch (Exception e){

        }
        return null;
    }

    public JsonElement serialize(BaseEvent src, Type typeOfSrc, JsonSerializationContext context) {

        m_JsonObject.add("RequestNo", context.serialize(m_RequestNo));
        m_JsonObject.add("UserName", context.serialize(m_UserName));
        m_JsonObject.add("UserID", context.serialize(m_UserID));

        return m_JsonObject;
    }
}
