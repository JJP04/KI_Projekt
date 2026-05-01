package tablut.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.MoveFactory;
import tablut.game.Perft;

public class UnitTests {

    //Unsere Stellungen
    String f_1 = "9/5r3/6rR1/5RK2/6R1r/9/1R8/9/1r7 w 23 1";
    String f_2 = "4r4/9/9/4R4/3rKr3/4r4/9/9/9 s 20 1";


    String a_1 = "9/9/9/9/9/9/3r5/1K7/9 w 0 1";
    String a_2 = "9/9/9/2r6/2R6/r8/9/9/9 s 0 1";
    String aa_1 = "";
    String aa_2 = "";
    String ab_1 = "";
    String ab_2 = "";
    String ac_1 = "9/9/7K1/9/3r5/9/9/9/9 s 0 1";
    String ac_2 = "9/4r4/K8/4R4/9/9/4r4/9/9 s 0 1";
    String ad_1 = "7K1/6r1r/2R3r2/9/3r5/9/1r1r5/9/9 w 20 10";
    String ad_2 = "7K1/6r1r/2R3r2/9/3r5/9/1r1r5/9/9 s 21 10";
    String af_1 = "9/9/9/9/2rK5/3r5/9/9/9 s 0 1";
    String af_2 = "9/9/3r1r3/4R4/7K1/9/9/9/9 w 0 1";
    String ai_1 = "3rrr3/4r4/4R4/r3R4/rrRRKR1r1/r3R3r/4R4/4r4/3rrr3 s 5 10";
    String ai_2 = "3r5/9/9/4R4/r4R2r/7K1/9/2r6/5r3 w 3 24";
    String al_1 = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/7R1/4r4/4rK3 s 1 12";
    String al_2 = "4rr3/4rK2r/4r4/5r1R1/r5R2/r6R1/2r5r/6r2/9 w 2 27";
    String am_1 = "";
    String am_2 = "";
    String b_1 = "";
    String b_2 = "";
    String b1_1 = "3rrr3/9/4R4/r3R3r/rrR1KRR1r/r3R3r/3R5/4r4/3rrr3 s 4 7";
    String b1_2 = "9/9/4r4/3r1r3/4K4/3R5/9/9/9 w 12 24";
    String b2_1 = "9/2R3r2/9/9/9/r8/Rr1rR4/4rK3/9 w 1 25";
    String b2_2 = "9/9/9/8r/8K/9/9/9/5r3 s 5 50";
    String b5_1 = "";
    String c_1 = "5R3/rr3r3/1KR6/9/9/9/9/9/9 w 0 30";
    String c_2 = "2r6/rK7/9/9/9/9/9/9/9 w 0 48";
    String e_1 = "";
    String e_2 = "";
    String g_1 = "";
    String g_2 = "";
    String h_1 = "";
    String j_1 = "1r7/9/rKRr1r3/1RR6/1r7/9/9/1r7/9 w 1 1";
    String j_2 = "9/9/4r4/3rRr3/2rrKRr2/3RrR3/4r4/9/9 w 1 1";
    String k_1 = "9/4Rr3/9/4r4/3rKR2r/4rr3/9/9/9 w 0 42";
    String k_2 = "7r1/R8/9/9/9/K8/9/1r7/9 s 0 39";
    String l_1 = "3rr4/4r4/4R4/r2R1R2r/rr2K2rr/2rR1R3/9/4r4/4rr3 w 0 8";
    String l_2 = "3rr4/4rr3/1r2K1r2/4RRr2/rrRR3r1/1r1RRr3/4R4/9/3rrr3 s 0 19";
    String m_1 = "2r1rr3/2RRr4/2R6/r3R3r/rr2KRRrr/r3R3r/4R4/3rrr3/4r4/ s 0 5";
    String m_2 = "2r1rr3/2RRr4/R8/4R3r/r2rKRRrr/r3R3r/4R4/3rrr3/4r4 w 08";
    String n_1 = "1K7/9/9/9/9/9/9/9/4r4 w 0 15";
    String n_2 = "3r5/9/9/4R4/2r1K2R1/4R4/9/9/4r4 s 0 5 ";
    String o_1 = "3r5/9/6R2/r5r2/r2R1R1R1/4rKr2/4rrr2/4R1R2/9 s 0 12";
    String o_2 = "9/r8/4R4/9/4K4/9/9/8r/9 s 0 14";
    String p_1 = "3r2r2/9/4rrr2/4rKr2/6R2/6R2/9/2R4R1/6R2 w 0 5";
    String p_2 = "9/4R1R2/4rrr2/4rKr2/r2R1R1R1/r5r2/6R2/9/3r5 w 0 7";
    String q_1 = "9/9/9/9/9/9/5RRRR/5RRKR/4RrR1r s 0 5";
    String q_2 = "4K4/4r4/9/9/9/9/9/9/9 s 0 7";
    String r_1 = "";
    String s_1 = "2s2s3/2w6/9/w2ww3s/s2sKw1ss/s3w4/9/9/5s3 w 0 9";
    String s_2 = "4s4/9/4w4/8s/s1sK1w1ss/s1wsw4/9/9/9 s 0 8";
    String t_1 = "9/9/9/1Rr5R/r1K6/9/1r7/9/9 w 0 6";
    String t_2 = "1w5s1/4k4/s7s/s7s/9/3w5/sws6/9/9 w 8 7";
    String u_1 = "3aa4/2a6/4d4/a3da2a/a2d2d1a/1d1d1a3/3d3a1/1ak3d2/2a3a2 w 0 22";
    String u_2 = "4aa3/6a2/3a1d3/2a1dka2/2ad1a3/1ad1d1a2/2adda2a/4a4/3aa4 s 0 30";
    String v_1 = "9/2R2r3/2KRr1r2/2R6/9/9/5R3/9/9 s 2 31";
    String v_2 = "3R5/3r5/1rK2r3/R2r5/9/9/9/9/9 w 6 41";
    String w_1 = "";
    String w_2 = "";
    String z_1 = "3rr4/4r4/5R3/r1R1Rr2r/rr1RKR1Rr/4R3r/5R3/4r4/3rr4 w 0 10";
    String z_2 = "3rrr3/9/3R5/8r/rr7/r1R1RR3/4R3r/4rKRr1/5rr2 s 0 21";
    String trainer_1 = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
    String trainer_2 = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
    String x_1 = "4rr3/3rr3r/4R4/r3R4/rrRRKRRrr/r3R3r/6R2/4r4/3rrr3 w 0 1";
    String x_2 = "4rrR2/3rr3r/4R4/rr2R4/r1RRKR1rr/r3R3r/2R6/7r1/3rrr3 w 0 1";
    String aj_1 = "9/r2r5/9/5RR2/9/1r7/1Kr6/5R3/7r1 w 0 11";
    String aj_2 = "1r7/9/5r3/9/9/9/3R5/2K5r/1r2r4 s 0 34";
    String b4_1 = "";
    String b4_2 = "";


    @Test
    void f_1() {
        Board b = FenParser.parse(f_1);
        assertEquals(47, Perft.perft(b, 1));
    }

    @Test
    void f_2() {
        Board b = FenParser.parse(f_2);
        assertEquals(41, Perft.perft(b, 1));
    }

    @Test
    void a_1() {
        Board b = FenParser.parse(a_1);
        assertEquals(16, Perft.perft(b, 1));
    }

    @Test
    void a_2() {
        Board b = FenParser.parse(a_2);
        //assertEquals(25, Perft.perft(b, 1));
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(25, moves.size());
    }

    @Test
    void ac_1() {
        Board b = FenParser.parse(ac_1);
        assertEquals(15, Perft.perft(b, 1));
    }

    @Test
    void ac_2() {
        Board b = FenParser.parse(ac_2);
        assertEquals(21, Perft.perft(b, 1));
    }

    @Test
    void ad_1() {
        Board b = FenParser.parse(ad_1);
        assertEquals(29, Perft.perft(b, 1));
    }

    @Test
    void ad_2() {
        Board b = FenParser.parse(ad_2);
        assertEquals(57, Perft.perft(b, 1));
    }

    @Test
    void af_1() {
        Board b = FenParser.parse(af_1);
        assertEquals(21, Perft.perft(b, 1));
    }

    @Test
    void af_2() {
        Board b = FenParser.parse(af_2);
        assertEquals(30, Perft.perft(b, 1));
    }

    @Test
    void ai_1() {
        Board b = FenParser.parse(ai_1);
        assertEquals(73, Perft.perft(b, 1));
    }
    @Test
    void ai_2() {
        Board b = FenParser.parse(ai_2);
        assertEquals(43, Perft.perft(b, 1));
    }
    @Test
    void al_1() {
        Board b = FenParser.parse(al_1);
        assertEquals(82, Perft.perft(b, 1));
    }
    @Test
    void al_2() {
        Board b = FenParser.parse(al_2);
        assertEquals(32, Perft.perft(b, 1));
    }
    @Test
    void b1_1() {
        Board b = FenParser.parse(b1_1);
        assertEquals(59, Perft.perft(b, 1));
    }
    @Test
    void b1_2() {
        Board b = FenParser.parse(b1_2);
        assertEquals(25, Perft.perft(b, 1));
    }
    @Test
    void b2_1() {
        Board b = FenParser.parse(b2_1);
        assertEquals(34, Perft.perft(b, 1));
    }
    @Test
    void b2_2() {
        Board b = FenParser.parse(b2_2);
        assertEquals(24, Perft.perft(b, 1));
    }
    @Test
    void c_1() {
        Board b = FenParser.parse(c_1);
        assertEquals(27, Perft.perft(b, 1));
    }
    @Test
    void c_2() {
        Board b = FenParser.parse(c_2);
        assertEquals(15, Perft.perft(b, 1));
    }
    @Test
    void j_1() {
        Board b = FenParser.parse(j_1);
        assertEquals(15, Perft.perft(b, 1));
    }
    @Test
    void j_2() {
        Board b = FenParser.parse(j_2);
        assertEquals(12, Perft.perft(b, 1));
    }
    @Test
    void k_1() {
        Board b = FenParser.parse(k_1);
        assertEquals(10, Perft.perft(b, 1));
    }
    @Test
    void k_2() {
        Board b = FenParser.parse(k_2);
        assertEquals(30, Perft.perft(b, 1));
    }
    @Test
    void l_1() {
        Board b = FenParser.parse(l_1);
        assertEquals(41, Perft.perft(b, 1));
    }
    @Test
    void l_2() {
        Board b = FenParser.parse(l_2);
        assertEquals(73, Perft.perft(b, 1));
    }
    @Test
    void m_1() {
        Board b = FenParser.parse(m_1);
        assertEquals(72, Perft.perft(b, 1));
    }
    @Test
    void m_2() {
        Board b = FenParser.parse(m_2);
        assertEquals(57, Perft.perft(b, 1));
    }
    @Test
    void n_1() {
        Board b = FenParser.parse(n_1);
        assertEquals(16, Perft.perft(b, 1));
    }
    @Test
    void n_2() {
        Board b = FenParser.parse(n_2);
        assertEquals(33, Perft.perft(b, 1));
    }
    @Test
    void o_1() {
        Board b = FenParser.parse(o_1);
        assertEquals(48, Perft.perft(b, 1));
    }
    @Test
    void o_2() {
        Board b = FenParser.parse(o_2);
        assertEquals(28, Perft.perft(b, 1));
    }
    @Test
    void p_1() {
        Board b = FenParser.parse(p_1);
        assertEquals(57, Perft.perft(b, 1)); //nicht Validiert
    }
    @Test
    void p_2() {
        Board b = FenParser.parse(p_2);
        assertEquals(44, Perft.perft(b, 1)); //Nicht validert
    }
    @Test
    void q_1() {
        Board b = FenParser.parse(q_1);
        assertEquals(1, Perft.perft(b, 1));
    }
    @Test
    void q_2() {
        Board b = FenParser.parse(q_2);
        assertEquals(14, Perft.perft(b, 1));
    }
    // @Test
    // void s_1() {
    //     Board b = FenParser.parse(s_1);
    //     assertEquals(47, Perft.perft(b, 1));
    // }
    // @Test
    // void s_2() {
    //     Board b = FenParser.parse(s_2);
    //     //assertEquals(44, Perft.perft(b, 1));
    //     MoveFactory m = new MoveFactory();
    //     List<Move> moves = m.getAllMoves(b);
    //     assertEquals(25, moves.size());
    // }
   //  @Test
   //  void t_1() {
   //      Board b = FenParser.parse(t_1);
   //      assertEquals(44, Perft.perft(b, 1));
   //  }
   //  @Test
   //  void t_2() {
   //      Board b = FenParser.parse(t_2);
   //      assertEquals(44, Perft.perft(b, 1));
   //  }
    //@Test
    //void u_1() {
    //    Board b = FenParser.parse(u_1);
    //    assertEquals(63, Perft.perft(b, 1));
    //}
    //@Test
    //    Board b = FenParser.parse(u_2);
     //   assertEquals(88, Perft.perft(b, 1));
    //}
    //@Test
    void v_1() {
        Board b = FenParser.parse(v_1);
        assertEquals(29, Perft.perft(b, 1));
    }
    @Test
    void v_2() {
        Board b = FenParser.parse(v_2);
        assertEquals(24, Perft.perft(b, 1));
    }
    @Test
    void z_1() {
        Board b = FenParser.parse(z_1);
        assertEquals(59, Perft.perft(b, 1));
    }
    @Test
    void z_2() {
        Board b = FenParser.parse(z_2);
        assertEquals(69, Perft.perft(b, 1));
    }
    @Test
    void x_1() {
        Board b = FenParser.parse(x_1);
        assertEquals(57, Perft.perft(b, 1));
    }
    @Test
    void x_2() {
        Board b = FenParser.parse(x_2);
        assertEquals(60, Perft.perft(b, 1));
    }
    @Test
    void aj_1() {
        Board b = FenParser.parse(aj_1);
        assertEquals(36, Perft.perft(b, 1));
    }
    @Test
    void aj_2() {
        Board b = FenParser.parse(aj_2);
        assertEquals(61, Perft.perft(b, 1));
    }
}
