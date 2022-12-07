package cn.holelin.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/10/20 11:04
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/10/20 11:04
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class StringUtil {

    public static boolean isPalindrome(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(c);
            }
        }

        String forward = sb.toString().toLowerCase();
        String backward = sb.reverse().toString().toLowerCase();
        return forward.equals(backward);
    }

    public static String reverseString(String s) {
        return new StringBuilder(s).reverse().toString();
    }


    public static int findLevenshteinDistance(String word1, String word2) {
        // If word2 is empty, removing
        int[][] ans = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i <= word1.length(); i++) {
            ans[i][0] = i;
        }

        // if word1 is empty, adding
        for (int i = 0; i <= word2.length(); i++) {
            ans[0][i] = i;
        }

        // None is empty
        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                int min = Math.min(Math.min(ans[i][j - 1], ans[i - 1][j]), ans[i - 1][j - 1]);
                ans[i][j] = word1.charAt(i - 1) == word2.charAt(j - 1) ? ans[i - 1][j - 1] : min + 1;
            }
        }
        return ans[word1.length()][word2.length()];
    }



}
