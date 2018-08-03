package org.jds.protocol;

import java.nio.ByteBuffer;

public class LineDelimiter implements Delimiter {

    private char[] end;
    public LineDelimiter(String s) {
        end = s.toCharArray();
    }
    
    @Override
    public int complete(ByteBuffer bf) {
        int n = bf.limit();
        for (int i=n-1; i>=0; --i) {
            if (bf.get(i) == end[end.length -1] ) {
                int j, k;
                for (j=i-1, k=end.length-2; j>=0 && k>=0; --j, --k) {
                    if (bf.get(j) != end[k]) break;
                }
                if (k == -1) return i+1;
            }
        }
        return -1;
    }

}
