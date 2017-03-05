package crixec.onbooting;

import java.util.Comparator;

/**
 * Created by crixec on 17-3-3.
 */

public class ScriptSortComparator implements Comparator<ScriptBean> {
    @Override
    public int compare(ScriptBean o1, ScriptBean o2) {
        if (o1 == o2)
            return 0;
        if (o1.isBootable() && !o2.isBootable())
            return -1;
        return 1;
    }
}
