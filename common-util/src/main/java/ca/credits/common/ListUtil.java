package ca.credits.common;

import java.util.List;

/**
 * Created by chenwen on 16/8/26.
 */
public class ListUtil {

    public static boolean isEmpty(List list){
        return list == null || list.size() == 0;
    }

    public static boolean isNotEmpty(List list){
        return !isEmpty(list);
    }
}
