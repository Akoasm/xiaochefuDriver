package com.qf.rwxchina.xiaochefudriver.Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 小工具类
 */
public class Utils {

    /**
     * 取double数据后面两位小数的上界
     */
    public static String getValueWith2Suffix(double dbl){
        DecimalFormat decimalFormat = new DecimalFormat(".#");
        double c =Double.parseDouble(decimalFormat.format(dbl)) ;
        String disctence= String.valueOf(c);
        return disctence;
//        BigDecimal bg = new BigDecimal(dbl);
//        return bg.setScale(1, BigDecimal.ROUND_CEILING).doubleValue();
    }
}
