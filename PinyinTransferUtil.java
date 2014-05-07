
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 */
public class PinyinTransferUtil {
    public static final Integer MAX_TONG_YIN_NUM = 2;
    public static final Integer MAX_RETAIN_NUM = 2;

    public static HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
    static {
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public static String[] cn2Spell(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return null;
        }
        boolean flag = true;
        chinese = chinese.trim().toLowerCase();
        StringBuffer pybf = new StringBuffer();
        StringBuffer pysxbf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] r = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (r != null) {
                        Set<String> pySet = new HashSet<String>();
                        for (String py : r) {
                            pySet.add(py);
                        }
                        if (flag && pySet.size() > 1) {
                            flag = false;
                        }
                        if (r[0].equals("lu:")) {
                            r[0] = "lv";
                        }
                        pybf.append(r[0]);
                        pysxbf.append(r[0].substring(0, 1));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {

                }
            } else {
                if ((arr[i] >= 48 && arr[i] <= 57) || (arr[i] >= 65 && arr[i] <= 90)
                        || (arr[i] >= 97 && arr[i] <= 122)) {
                    pybf.append(arr[i]);
                    pysxbf.append(arr[i]);
                }

            }
        }
        String[] result = new String[3];
        result[0] = pybf.toString();
        result[1] = pysxbf.toString();
        if (flag) {
            result[2] = "1";
        } else {
            result[2] = "0";
        }
        return result;
    }

    public static List<String> getPermutationSentence(List<List<String>> termArrays,int start) {
        if (CollectionUtils.isEmpty(termArrays))
            return Collections.emptyList();

        int size = termArrays.size();
        if (start < 0 || start >= size) {
            return Collections.emptyList();
        }

        if (start == size-1) {
            return termArrays.get(start);
        }

        List<String> strings = termArrays.get(start);

        List<String> permutationSentences = getPermutationSentence(termArrays, start + 1);

        if (CollectionUtils.isEmpty(strings)) {
            return permutationSentences;
        }

        if (CollectionUtils.isEmpty(permutationSentences)) {
            return strings;
        }

        List<String> result = new ArrayList<String>();
        for (String pre : strings) {
            for (String suffix : permutationSentences) {
                result.add(pre+suffix);
            }
        }

        return result;
    }


    public static List<List<String>> preHandleList(List<List<String>> listArray){
        if (CollectionUtils.isEmpty(listArray)) {
            return listArray;
        }

        int product = 1;
        List<List<String>> temp = new ArrayList<List<String>>();

        for (List<String> item : listArray) {
            int size = 1;
            if (!CollectionUtils.isEmpty(item)) {
                if (item.size() > 1) {
                    HashSet set = new HashSet(item);
                    size = set.size();
                    temp.add(new ArrayList<String>(set));
                } else {
                    temp.add(item);
                    size = item.size();
                }
                product = product * size;
            }
        }

        if (product <= 4) {
            return temp;
        }

        int count = 0;
        List<List<String>> result = new ArrayList<List<String>>();
        for (List<String> item : temp) {
            if (count < MAX_RETAIN_NUM) {
                if (item.size() > MAX_TONG_YIN_NUM) {
                    List<String> subList = new ArrayList<String>();
                    subList.addAll(item.subList(0,MAX_TONG_YIN_NUM));
                    result.add(subList);
                    count++;
                } else {
                    result.add(item);
                }
            } else {
                List<String> subList = new ArrayList<String>();
                subList.addAll(item.subList(0,1));
                result.add(subList);
            }
        }

        return result;

    }


}
