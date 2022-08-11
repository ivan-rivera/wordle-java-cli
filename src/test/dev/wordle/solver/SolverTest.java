package test.dev.wordle.solver;

import main.dev.wordle.solver.Solver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolverTest {

    @Test
    @DisplayName("Ensure that the solver works")
    void test() throws Exception {
        Solver solver = new Solver();
        CommandLine cmd = new CommandLine(solver);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));
        int exitCode = cmd.execute("-w", "Pl***", "-e", "ANE");
        assertEquals(0, exitCode);
    }
}
