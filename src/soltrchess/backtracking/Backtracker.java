package soltrchess.backtracking;/*
 * solitarechess.backtracking.Backtracker.java
 *
 * This file comes from the solitarechess.backtracking lab. It should be useful
 * in this project. A second method has been added that you should
 * implement.
 */

import java.util.List;
import java.util.Optional;

/**
 * This class represents the classic recursive solitarechess.backtracking algorithm.
 * It has a solver that can take a valid configuration and return a
 * solution, if one exists.
 * 
 * @author sps (Sean Strout @ RIT CS)
 * @author jeh (James Heliotis @ RIT CS)
 */
public class Backtracker {
    
    /**
     * Try find a solution, if one exists, for a given configuration.
     * 
     * @param config A valid configuration
     * @return A solution config, or null if no solution
     */
    public Optional<Configuration> solve(Configuration config) {
        if (config.isGoal()) {
            System.out.println("Returned basic sol");
            return Optional.of(config);
        } else {
            for (Configuration child : config.getSuccessors()) {
                System.out.println("Current config: ");
                System.out.println(child);
                if (child.isValid()) {
                    System.out.println("Child is valid");
                    Optional<Configuration> sol = solve(child);
                    if (sol.isPresent()) {
                        System.out.println("SOLUTION FOUND!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        return sol;
                    }
                }
            }
            // implicit solitarechess.backtracking happens here
        }
        System.out.println("Solution not found :(");
        return Optional.empty();
    }

    /**
     * Find a goal configuration if it exists, and how to get there.
     * @param current the starting configuration
     * @return a list of configurations to get to a goal configuration.
     *         If there are none, return null.
     */
    public List< Configuration > solveWithPath( Configuration current ) {
        // YOUR CODE HERE
        return null;
    }

}
