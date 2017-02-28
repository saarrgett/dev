package appHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by hagairevah on 2/27/17.
 */
public class menu {
    private static BufferedReader in;

    public static String[] getParameters(){
        String temp;
        String[] parameters = new String[2];
        System.out.print("Type Scrum number [1]:");
        temp = readline();
        parameters[0] = "scrum"+validateScrumParameter(temp);

        parameters[1] = "tryout_"+parameters[0];
        System.out.print("type Filename["+parameters[1]+"]: ");
        temp = readline();
        parameters[1]=validateFileName(temp);
//        System.out.println(parameters[1]+"**");
        return parameters;
    }
    public static String validateScrumParameter(String num)
    {
        try {
            Integer x = Integer.parseInt(num);
            if ((x>0) && (x<15)){
                return num;
            } else {
                System.out.println("scrum number is not valid, should be between 1-14");
                System.exit(0);
                return "1";
            }
        } catch (Exception e){
            return "1";
        }

    }
    public static String validateFileName(String name){
        if (name.equals("")) {
            return "tryout";
        } else {
            return name.trim().replace(" ","_");
        }
    }
    private static String readline(){
        String line = "";
        try{
            in = new BufferedReader(new InputStreamReader(System.in));
            line = in.readLine();
        } catch (IOException e){

            System.out.println("Cant read from keyboard... Bye Bye");
            System.exit(0);
        }
        return line;
    }

}
