package interface_pack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is used for handling with all of the interaction of the console with the user, need for the project.
 * Created by Rachamim on 3/13/17.
 */
public class TerminalConsole {
    //Data members
    String[] args;
    BufferedReader br;

    //C'tor
    public TerminalConsole(String[] args) {
        this.args = args;
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * The function handles the input from the user for execution
     *
     * @throws Exception
     * @param scrum
     */
    public String handleUserInput(String scrum) throws Exception {
        //If the args were passed from the user
        if (args.length > 0)
        {
            scrum = args[0].toLowerCase();
            if(!validateScrumVal(scrum))
                throw new Exception("The argument passed is not valid: " + args);
        }
        else { // In case missing arguments for execution
            scrum = getScrumFromCommandLine(scrum);
        }

        return scrum;
    }

    /**
     * The function validate that the scrum input from the user is valid
     *
     * @param scrum The scrum to work on
     * @return true if the scrum input from the user is valid, false otherwise
     */
    public boolean validateScrumVal(String scrum) {
        String valToManipulate = scrum;

        if (!valToManipulate.contains("scrum"))
            return false;

        if (valToManipulate.lastIndexOf("scrum") != valToManipulate.indexOf("scrum"))
            return false;

        valToManipulate = valToManipulate.replace("scrum", "");

        if (!isValParcable(valToManipulate))
            return false;

        return true;
    }

    /**
     * the function checks if a given String is parcable or not
     *
     * @param str The String to parse
     * @return True if parcable, false otherwise
     */
    private boolean isValParcable(String str) {
        try {
            Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * The function get the scrum from the user using the terminal
     * @param scrum The scrum to update
     * @throws IOException
     */
    private String getScrumFromCommandLine(String scrum) throws IOException {
        System.out.println("Please enter scrum('scrum#'):");
        scrum = br.readLine();
        scrum = scrum.toLowerCase();
        while (!validateScrumVal(scrum)) {
            System.out.println("Invalid input, please enter scrum('scrum#'):");
            scrum = br.readLine();
        }
        return scrum;
    }
}
