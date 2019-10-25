package io.bacta.shared.iff;

import com.google.common.io.ByteStreams;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by crush on 12/17/2014.
 */
public class IffTest {
    private static final int ID_SCOT = Iff.createChunkId("SCOT");
    private static final int ID_DERV = Iff.createChunkId("DERV");
    private static final int ID_XXXX = Iff.createChunkId("XXXX");
    private static final int ID_0012 = Iff.createChunkId("0012");
    private static final int ID_PCNT = Iff.createChunkId("PCNT");
    private static final int ID_STOT = Iff.createChunkId("STOT");
    private static final int ID_0007 = Iff.createChunkId("0007");
    private static final int ID_SHOT = Iff.createChunkId("SHOT");

    private static final int ID_TEST = Iff.createChunkId("TEST");

    private byte[] testBytes;

    private final String resourcesPath = IffTest.class.getResource("/").getPath();

    @Before
    public void before() {
        try {
            testBytes = ByteStreams.toByteArray(this.getClass().getResourceAsStream("/human_male.iff"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldReadWithoutSpecifyingId() {
        final Iff iff = new Iff("human_male.iff", testBytes);
        iff.enterForm();
            iff.enterForm();
                iff.enterChunk();
        Assert.assertEquals("object/creature/player/shared_human_male.iff", iff.readString());
                iff.exitChunk();
            iff.exitForm();
        iff.exitForm();
    }

    @Test
    public void shouldReadDerivedFromTemplate() {
        final Iff iff = new Iff("human_male.iff", testBytes);
        iff.enterForm(ID_SCOT);
            iff.enterForm(ID_DERV);
                iff.enterChunk(ID_XXXX);
        Assert.assertEquals("object/creature/player/shared_human_male.iff", iff.readString());
                iff.exitChunk(ID_XXXX);
            iff.exitForm(ID_DERV);
            iff.enterForm(ID_0012);
                iff.enterChunk(ID_PCNT);
                    Assert.assertEquals(0, iff.readInt());
                iff.exitChunk(ID_PCNT);
            iff.exitForm(ID_0012);
        iff.exitForm(ID_SCOT);
    }

    @Test
    public void shouldReadAllChunksWithoutException() {
        final Iff iff = new Iff("human_male.iff", testBytes);
        iff.enterForm(ID_SCOT);
            iff.enterForm(ID_DERV);
                iff.enterChunk(ID_XXXX);
                iff.exitChunk(ID_XXXX);
            iff.exitForm(ID_DERV);
            iff.enterForm(ID_0012);
                iff.enterChunk(ID_PCNT);
                iff.exitChunk(ID_PCNT);
            iff.exitForm(ID_0012);
            iff.enterForm(ID_STOT);
                iff.enterForm(ID_DERV);
                    iff.enterChunk(ID_XXXX);
                    iff.exitChunk(ID_XXXX);
                iff.exitForm(ID_DERV);
                iff.enterForm(ID_0007);
                    iff.enterChunk(ID_PCNT);
                    iff.exitChunk(ID_PCNT);
                iff.exitForm(ID_0007);
                iff.enterForm(ID_SHOT);
                    iff.enterForm(ID_DERV);
                        iff.enterChunk(ID_XXXX);
                        iff.exitChunk(ID_XXXX);
                    iff.exitForm(ID_DERV);
                    iff.enterForm(ID_0007);
                        iff.enterChunk(ID_PCNT);
                        iff.exitChunk(ID_PCNT);
                    iff.exitForm(ID_0007);
                iff.exitForm(ID_SHOT);
            iff.exitForm(ID_STOT);
        iff.exitForm(ID_SCOT);
    }

    @Test
    public void shouldWriteIff() {
        final Iff iff = new Iff(1024);
        iff.insertForm(ID_TEST, true);
        {
            iff.insertForm(ID_0007, true); //version 7 LOL
            {
                iff.insertChunk(ID_XXXX, true);
                {
                    iff.insertChunkData("Testing");
                }
                iff.exitChunk(ID_XXXX);
                iff.insertChunk(ID_XXXX, true);
                {
                    iff.insertChunkData("Testing2");
                }
                iff.exitChunk(ID_XXXX);
            }
            iff.exitForm(ID_0007);
        }
        iff.exitForm(ID_TEST);
    }
}
