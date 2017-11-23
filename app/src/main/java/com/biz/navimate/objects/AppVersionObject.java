package com.biz.navimate.objects;

/**
 * Created by Siddharth on 08-03-2017.
 */

public class AppVersionObject
{
    public int      code = 0;
    public String   name = "0.0.0";

    public AppVersionObject(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public Boolean IsValid()
    {
        Boolean bValid = false;

        if (code != 0)
        {
            if (!name.equals("0.0.0"))
            {
                bValid = true;
            }
        }

        return bValid;
    }

    @Override
    public String toString()
    {
        return name + " : " + code;
    }
}
