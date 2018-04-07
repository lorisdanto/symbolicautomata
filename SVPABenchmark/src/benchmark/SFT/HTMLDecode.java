package benchmark.SFT;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import benchmark.SFT.codecs.HTMLEntityCodec;

import theory.characters.CharFunc;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import transducers.sft.SFT;

public class HTMLDecode {
    private static HTMLEntityCodec htmlCodec = new HTMLEntityCodec();
    private static SFT<CharPred, CharFunc, Character> sft = null;
    private static UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

    public static void main(String args[]) {
        System.out.println(htmlCodec.decode("123&#49"));
    }

    /* Copied from https://github.com/ESAPI/esapi-java-legacy/blob/92734ea2c08705639b4377c6b7cfee70a19bd230/src/test/
    java/org/owasp/esapi/codecs/AbstractCodecTest.java
    Since it uses the BSD License and I have placed the BSD License under the folder called codecs, there is no legal
    issues or contradictions with Apache License which is used by the whole library */
    @Test
    public void testHtmlDecodeDecimalEntities()
    {
        assertEquals( "test!", htmlCodec.decode("&#116;&#101;&#115;&#116;!") );
    }

    @Test
    public void testHtmlDecodeHexEntitites()
    {
        assertEquals( "test!", htmlCodec.decode("&#x74;&#x65;&#x73;&#x74;!") );
    }

    @Test
    public void testHtmlDecodeInvalidAttribute()
    {
        assertEquals( "&jeff;", htmlCodec.decode("&jeff;") );
    }

    @Test
    public void testHtmlDecodeAmp()
    {
        assertEquals("&", htmlCodec.decode("&amp;"));
        assertEquals("&X", htmlCodec.decode("&amp;X"));
        assertEquals("&", htmlCodec.decode("&amp"));
        assertEquals("&X", htmlCodec.decode("&ampX"));
    }

    @Test
    public void testHtmlDecodeLt()
    {
        assertEquals("<", htmlCodec.decode("&lt;"));
        assertEquals("<X", htmlCodec.decode("&lt;X"));
        assertEquals("<", htmlCodec.decode("&lt"));
        assertEquals("<X", htmlCodec.decode("&ltX"));
    }

    @Test
    public void testHtmlDecodeSup1()
    {
        assertEquals("\u00B9", htmlCodec.decode("&sup1;"));
        assertEquals("\u00B9X", htmlCodec.decode("&sup1;X"));
        assertEquals("\u00B9", htmlCodec.decode("&sup1"));
        assertEquals("\u00B9X", htmlCodec.decode("&sup1X"));
    }

    @Test
    public void testHtmlDecodeSup2()
    {
        assertEquals("\u00B2", htmlCodec.decode("&sup2;"));
        assertEquals("\u00B2X", htmlCodec.decode("&sup2;X"));
        assertEquals("\u00B2", htmlCodec.decode("&sup2"));
        assertEquals("\u00B2X", htmlCodec.decode("&sup2X"));
    }

    @Test
    public void testHtmlDecodeSup3()
    {
        assertEquals("\u00B3", htmlCodec.decode("&sup3;"));
        assertEquals("\u00B3X", htmlCodec.decode("&sup3;X"));
        assertEquals("\u00B3", htmlCodec.decode("&sup3"));
        assertEquals("\u00B3X", htmlCodec.decode("&sup3X"));
    }

    @Test
    public void testHtmlDecodeSup()
    {
        assertEquals("\u2283", htmlCodec.decode("&sup;"));
        assertEquals("\u2283X", htmlCodec.decode("&sup;X"));
        assertEquals("\u2283", htmlCodec.decode("&sup"));
        assertEquals("\u2283X", htmlCodec.decode("&supX"));
    }

    @Test
    public void testHtmlDecodeSupe()
    {
        assertEquals("\u2287", htmlCodec.decode("&supe;"));
        assertEquals("\u2287X", htmlCodec.decode("&supe;X"));
        assertEquals("\u2287", htmlCodec.decode("&supe"));
        assertEquals("\u2287X", htmlCodec.decode("&supeX"));
    }

    @Test
    public void testHtmlDecodePi()
    {
        assertEquals("\u03C0", htmlCodec.decode("&pi;"));
        assertEquals("\u03C0X", htmlCodec.decode("&pi;X"));
        assertEquals("\u03C0", htmlCodec.decode("&pi"));
        assertEquals("\u03C0X", htmlCodec.decode("&piX"));
    }

    @Test
    public void testHtmlDecodePiv()
    {
        assertEquals("\u03D6", htmlCodec.decode("&piv;"));
        assertEquals("\u03D6X", htmlCodec.decode("&piv;X"));
        assertEquals("\u03D6", htmlCodec.decode("&piv"));
        assertEquals("\u03D6X", htmlCodec.decode("&pivX"));
    }

    @Test
    public void testHtmlDecodeTheta()
    {
        assertEquals("\u03B8", htmlCodec.decode("&theta;"));
        assertEquals("\u03B8X", htmlCodec.decode("&theta;X"));
        assertEquals("\u03B8", htmlCodec.decode("&theta"));
        assertEquals("\u03B8X", htmlCodec.decode("&thetaX"));
    }

    @Test
    public void testHtmlDecodeThetasym()
    {
        assertEquals("\u03D1", htmlCodec.decode("&thetasym;"));
        assertEquals("\u03D1X", htmlCodec.decode("&thetasym;X"));
        assertEquals("\u03D1", htmlCodec.decode("&thetasym"));
        assertEquals("\u03D1X", htmlCodec.decode("&thetasymX"));
    }
}

