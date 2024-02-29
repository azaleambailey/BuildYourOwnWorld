import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

import static com.google.common.truth.Truth.assertThat;

public class WorldGenTests {
    @Test
    public void basicTest() { // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        //write a test that uses an input like "n123swasdwasd"
    }

    @Test
    public void basicSaveTest() {
        //write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
    }

    @Test
    public void autoBud() {
//        AutograderBuddy.getWorldFromInput("n123s:q");
//        assertThat(AutograderBuddy.getWorldFromInput("lswdswasdswasawsasdswsa")).isEqualTo(AutograderBuddy.getWorldFromInput("n123swdswasdswasawsasdswsa"));
//
//        assertThat(AutograderBuddy.getWorldFromInput("n1392967723524655428sddsaawwsaddw")).isEqualTo(AutograderBuddy.getWorldFromInput("n1392967723524655428sddsaawws:qladdw"));

        AutograderBuddy.getWorldFromInput("N999SD:Q");
//        AutograderBuddy.getWorldFromInput("LWWWDDD");
        assertThat(AutograderBuddy.getWorldFromInput("N999SD")).isEqualTo(AutograderBuddy.getWorldFromInput("l:q"));

//        AutograderBuddy.getWorldFromInput("N999SDDD:Q");
//        AutograderBuddy.getWorldFromInput("LWWW:Q");
//        AutograderBuddy.getWorldFromInput("LDDD:Q");
//        assertThat(AutograderBuddy.getWorldFromInput("N999SDDDWWWDDD")).isEqualTo(AutograderBuddy.getWorldFromInput("l"));
//
//        AutograderBuddy.getWorldFromInput("N999SDDD:Q");
//        AutograderBuddy.getWorldFromInput("L:Q");
//        AutograderBuddy.getWorldFromInput("L:Q");
//        AutograderBuddy.getWorldFromInput("LWWWDDD");
//        assertThat(AutograderBuddy.getWorldFromInput("N999SDDDWWWDDD")).isEqualTo(AutograderBuddy.getWorldFromInput("l"));
    }
}
