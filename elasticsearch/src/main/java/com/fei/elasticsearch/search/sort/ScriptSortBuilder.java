package com.fei.elasticsearch.search.sort;

import com.alibaba.fastjson.JSONObject;
import com.fei.elasticsearch.script.Script;

/**
 *
 * @author jianying9
 */
public class ScriptSortBuilder implements SortBuilder
{

    private final Script script;

    private final ScriptSortType type;

    private SortOrder order = SortOrder.ASC;

    public ScriptSortBuilder(Script script, ScriptSortType type)
    {
        this.script = script;
        this.type = type;
    }

    public ScriptSortBuilder order(SortOrder order)
    {
        this.order = order;
        return this;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject scriptJson = this.script.toJSONObject();
        scriptJson.put("order", order.toString());
        scriptJson.put("type", this.type.toString());
        //
        JSONObject sortJson = new JSONObject();
        sortJson.put("_script", scriptJson);
        return sortJson;
    }

    public enum ScriptSortType
    {
        STRING,
        NUMBER;

        @Override
        public String toString()
        {
            return name().toLowerCase();
        }
    }

}
