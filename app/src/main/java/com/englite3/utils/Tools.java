package com.englite3.utils;

import java.util.Random;

public class Tools {
    public static int randint(int mn, int mx){
        Random random = new Random();
        return random.nextInt(mx + 1) % (mx - mn + 1) + mn;
    }

    /*
    return: a^n
     */
    public static int fastPower(int a, int n) {
        int ans = 1;
        while (n != 0) {
            if ((n & 1) != 0)
                ans *= a;
            a *= a;
            n >>= 1;
        }
        return ans;
    }
}
