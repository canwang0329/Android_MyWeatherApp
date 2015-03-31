package com.example.wangcan.myweather.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import demo.Pinyin4jAppletDemo;

/**
 * Created by wangcan on 2015/3/31.
 */
public class PinYin {

    /**
     * 汉字转换为汉语拼音，英文字符不变
     * @param  chinese
     * @return pinyinName
     */

    public static String converterToSpell(String chinese){
        String pinyinName="";
        char[] nameChar=chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormate=new HanyuPinyinOutputFormat();
        defaultFormate.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormate.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for(int i=0;i<nameChar.length;i++){
            if(nameChar[i]>128){
                try{
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i],defaultFormate)[0];
                }catch (BadHanyuPinyinOutputFormatCombination e){
                    e.printStackTrace();
                }
            }else{
                pinyinName +=nameChar[i];
            }
        }

        return pinyinName;
    }
}
