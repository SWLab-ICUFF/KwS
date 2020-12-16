package uff.ic.swlab.util.StainerTree;

import java.util.Iterator;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class Teste {

    public static void main(String[] args) {
        for (int i = 1; i < 10; i++) {
            Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(700, i);
            while (iterator.hasNext()) {
                final int[] combination = iterator.next();
                for (int j = 0; j < combination.length; j++)
                    System.out.print(combination[j] + ",");
                System.out.println("");

            }
        }
    }
}
