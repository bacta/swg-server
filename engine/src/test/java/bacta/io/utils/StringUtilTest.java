package bacta.io.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by crush on 5/30/2016.
 */
public class StringUtilTest {
    @Test
    public void shouldGetFirstWorld() {
        final String input1 = "      \tHouston we have a problem   ";
        final String input2 = "   \t   Houston we have a problem   ";

        final String actual1 = StringUtil.getFirstWord(input1);
        final String actual2 = StringUtil.getFirstWord(input2);

        Assert.assertEquals("Houston", actual1);
        Assert.assertEquals("Houston", actual2);
    }
}
