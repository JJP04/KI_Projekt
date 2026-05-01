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
    String ai_1 = "";
    String ai_2 = "";
    String al_1 = "";
    String al_2 = "";
    String am_1 = "";
    String am_2 = "";
    String b_1 = "";
    String b_2 = "";
    String b1_1 = "";
    String b1_2 = "";
    String b2_1 = "";
    String b2_2 = "";
    String b5_1 = "";
    String c_1 = "";
    String c_2 = "";
    String e_1 = "";
    String e_2 = "";
    String g_1 = "";
    String g_2 = "";
    String h_1 = "";
    String j_1 = "";
    String k_1 = "";
    String k_2 = "";
    String l_1 = "";
    String l_2 = "";
    String m_1 = "";
    String m_2 = "";
    String n_1 = "";
    String n_2 = "";
    String o_1 = "";
    String o_2 = "";
    String p_1 = "";
    String p_2 = "";
    String q_1 = "";
    String q_2 = "";
    String r_1 = "";
    String s_1 = "";
    String s_2 = "";
    String t_1 = "";
    String t_2 = "";
    String u_1 = "";
    String u_2 = "";
    String v_1 = "";
    String v_2 = "";
    String w_1 = "";
    String w_2 = "";
    String z_1 = "";
    String z_2 = "";
    String trainer_1 = "";
    String trainer_2 = "";
    String x_1 = "";
    String x_2 = "";
    String aj_1 = "";
    String aj_2 = "";
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
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(16, moves.size());
    }

    @Test
    void a_2() {
        Board b = FenParser.parse(a_2);
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(25, moves.size());
    }

    @Test
    void ac_1() {
        Board b = FenParser.parse(ac_1);
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(15, moves.size());
    }

    @Test
    void ac_2() {
        Board b = FenParser.parse(ac_2);
        assertEquals(21, Perft.perft(b, 1));
    }

    @Test
    void ad_1() {
        Board b = FenParser.parse(ad_1);
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(29, moves.size());
    }

    @Test
    void ad_2() {
        Board b = FenParser.parse(ad_2);
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(57, moves.size());
    }

    @Test
    void af_1() {
        Board b = FenParser.parse(af_1);
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(21, moves.size());
    }

    @Test
    void af_2() {
        Board b = FenParser.parse(af_2);
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        assertEquals(30, moves.size());
    }
}
