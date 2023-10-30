import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Terminal {

    //Can u see the ned edit on gitHB ? :(
    private static File currentDirectory = new File(System.getProperty("user.dir"));

    Parser parser;
    //Implement each command in a method, for example:

    public static String pwd(){
        return currentDirectory.getAbsolutePath();
    }

    public static void echo(String s){
        System.out.println(s);//it runs successfully
    }

    public static String[] ls(){

        File file = new File(".");
        String list[] = file.list();

        return list;
    }

    public static boolean mkdir(String folderPath){
        File file = new File(folderPath);

        boolean isCreated = file.mkdir();

        return isCreated;
    }

    public static void rmdir(String st){
        String dirPath = "";
        
        if(st.equals("*")){
            dirPath = ".";  // Current directory
        }
        else{
            dirPath = st;  // Target directory
        }

        File dir = new File(dirPath);
        
        File[] allDirs;

        if(st.equals("*")){
            allDirs = dir.listFiles(File::isDirectory);
        }
        else{
            allDirs = new File[]{dir};
        }

        System.out.println("Directory: " + dir.getAbsolutePath());
        System.out.println("Exists: " + dir.exists());
        System.out.println("Is directory: " + dir.isDirectory());
        if(dir.isDirectory()) {
            System.out.println("Number of subdirectories: " + (dir.listFiles(File::isDirectory).length));
        }

        if(allDirs != null){
            for (File file : allDirs) {

                System.out.println("Trying to delete: " + file.getAbsolutePath());
                System.out.println("Exists: " + file.exists());
                System.out.println("Is directory: " + file.isDirectory());
                System.out.println("Is empty: " + (file.list().length == 0));


                if(file.list().length == 0) {
                    boolean isDeleted = file.delete();

                    if(isDeleted){
                        System.out.println("Directory " + file.getName() + " deleted successfully");
                    }
                    else{
                        System.out.println("Failed to delete directory " + file.getName());
                    }
                }
            }
        }

    }

    public static void cd(){
        currentDirectory = new File(System.getProperty("user.dir"));
    }

    public static void cd(String path){
        if(path.equals("..")){
            Path parentPath = Paths.get(currentDirectory.getAbsolutePath()).getParent();
            if(parentPath != null){
                currentDirectory = parentPath.toFile();
            }
            else {
                System.out.println("Already at root directory");
            }
        }
        else {
            File newDirectory = new File(currentDirectory, path);
            if(newDirectory.exists() && newDirectory.isDirectory()){
                currentDirectory = newDirectory;
            }
            else{
                System.out.println("Directory not found: " + path);
            }
        }
    }

    public static void touch(String path){
        File newFile = new File(currentDirectory, path);
    
        try{
            if(newFile.createNewFile()){
                System.out.println("File Created: " + newFile.getName());
            }
            else {
                System.out.println("File already exists.");
            }
        }
        catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }        


    // ...
    //This method will choose the suitable command method to be called
    public void chooseCommandAction(){
        //...
    }

    public static void main(String[] args){
        
        Scanner scanner = new Scanner(System.in);

        System.out.print("Press 0 to exit\n$ ");

        boolean b = true;
        
        while(b){
            String s = scanner.nextLine();
            
            Parser parser = new Parser();
            
            if(parser.parse(s)){
                //You can check here if it is parsed by printing anything
            }
            
            String[] arg = parser.getArgs();
            String command = parser.getCommandName();

            ///////////////////////////////////
            
            if(command.equalsIgnoreCase("echo")){                 ////ECHO
                echo(arg[0]);
                System.out.print("$ ");
            }
        
            else if(command.equalsIgnoreCase("pwd")){             ////PWD
                System.out.println(pwd());
                System.out.print("$ ");
            }

            else if(command.equalsIgnoreCase("ls")){              ////LS && LS -r

                String[] list = ls();
                String[] rList = ls();

                Collections.reverse(Arrays.asList(rList));

                if(list != null){
                    if(arg.length > 0 && arg[0].equals("-r")){
                        for (String string : rList) {
                            System.out.print(string + " --- ");
                        }
                    }
                    else{
                        for (String string : list) {
                            System.out.print(string + " --- ");
                        }
                    }
                }

                System.out.print("\n$ ");
            }

            else if(command.equalsIgnoreCase("mkdir")){           //MKDIR
                if(mkdir(arg[0])){
                    System.out.print("Directory created :)");
                }
                else{
                    System.out.print("ERROR ! :(");
                }

                System.out.print("\n$ ");
            }

            else if(command.equalsIgnoreCase("rmdir")){       //RMDIR // No spaces in folder name because the args are space separated
                rmdir(arg[0]);
                System.out.print("\n$ ");
            }     
            
            else if(command.equalsIgnoreCase("cd")){
                if(arg.length <= 0){
                    cd();
                }
                else{
                    cd(arg[0]);
                }
            }

            else if(command.equalsIgnoreCase("touch")){
                touch(arg[0]);
            }

            else if (command.equals("0") || command.equalsIgnoreCase("exit")){                        //////Exit 
                b = false;
            }

            else{                                                   //////If none are correct
                System.out.println("UnIdentified command please refer to the Docs or the Readme file");
                System.out.print("$ ");
            }
        }
        scanner.close();
    }
}

class Parser {
String commandName;
    String[] args;
    String command;
    //This method will divide the input into commandName and args
    //where "input" is the string command entered by the user

    public boolean parse(String input){
        String[] holder = input.split(" ");
        this.args = new String[holder.length - 1];

        this.command = holder[0];

        //removes the first value cause its always the command
        System.arraycopy(holder, 1, args, 0, holder.length-1);

        return true;
    }

    public String getCommandName(){
        //...
        return this.command;
    }

    public String[] getArgs(){
        //...
        return this.args;
    }

}

