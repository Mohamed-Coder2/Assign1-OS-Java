import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Terminal {

    private static File currentDirectory = new File(System.getProperty("user.dir"));
    private static List<String> commandHistory = new ArrayList<>();
    public static String TerminalString = new String();

    Parser parser;

    public static void copyFile(String sourceFile, String destinationFile) {
        
        try {
            Files.copy(Paths.get(sourceFile), Paths.get(destinationFile));
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*  
        This method uses the Files.copy() method from the java.nio.file package to copythe file.
        The Paths.get() method is used to convert the file paths from String to Path.
        Please note that if the destination file already exists,
        this method will throw a FileAlreadyExistsException.
        If you want to overwrite the existing file, you can use the REPLACE_EXISTING 

        Files.copy(Paths.get(sourceFile), 
        Paths.get(destinationFile), 
        StandardCopyOption.REPLACE_EXISTING);
    */

    public static void redirectOutputToFile(String filename) {
        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream(filename));
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void addCommandToHistory(String command){
        commandHistory.add(command);
    }

    public static void history() {
        for(int i=0; i < commandHistory.size(); i++){
            System.out.println((i + 1) + ". " + commandHistory.get(i));
        }
    }

    public static String pwd(){
        return currentDirectory.getAbsolutePath();
    }

    public static void echo(String s){
        System.out.println(s);
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

        if(dir.isDirectory()) {
            System.out.println("Number of subdirectories: " + (dir.listFiles(File::isDirectory).length));
        }

        if(allDirs != null){
            for (File file : allDirs) {
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

    public static void rm(String fileName) {
        File file = new File(currentDirectory, fileName);

        if (file.exists()) {
        
            if (file.delete()) {
                System.out.println(fileName + " has been deleted.");
            } 
            
            else {
                System.out.println("Failed to delete " + fileName);
            }

        } 
        
        else {
            System.out.println(fileName + " does not exist.");
        }
    }    

    // ...
    //This method will choose the suitable command method to be called
    public static void chooseCommandAction(String command, String[] arg){
        switch (command.toLowerCase()) {
            case "echo":
                echo(arg[0]);
                addCommandToHistory(TerminalString);
                break;
            case "pwd":
                System.out.println(pwd());
                addCommandToHistory(TerminalString);
                break;
            case "ls":
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
                    System.out.println("");
                }
                addCommandToHistory(TerminalString);

                break;
            case "mkdir":

                if(mkdir(arg[0])){
                    System.out.println("Directory created :)");
                }

                else{
                    System.out.println("Error");;
                }

                addCommandToHistory(TerminalString);
                break;
            case "rmdir":

                rmdir(arg[0]);
                addCommandToHistory(TerminalString);

                break;
            case "cd":
                if(arg.length <= 0){
                    cd();
                }
                else{
                    cd(arg[0]);
                }
                addCommandToHistory(TerminalString);
                break;
            case "touch":
                touch(arg[0]);
                addCommandToHistory(TerminalString);
                break;
            case "rm":
                rm(arg[0]);
                addCommandToHistory(TerminalString);
                break;
            case "history":
                addCommandToHistory(TerminalString);
                history();
                break;
            case "cp":
                //
                copyFile(arg[0], arg[1]);
                addCommandToHistory(TerminalString);
                break;
            default:
                break;
        }
    }

    public static void main(String[] args){
        
        Scanner scanner = new Scanner(System.in);

        System.out.print("type -exit- or 0 to terminate\n");

        boolean b = true;
        
        while(b){
            
            System.out.print("$ ");

            TerminalString = scanner.nextLine();
            
            Parser parser = new Parser();
            
            if(parser.parse(TerminalString)){
                //You can check here if it is parsed by printing anything
            }
            
            String[] arg = parser.getArgs();
            String command = parser.getCommandName();

            ///////////////////////////////////
            
            chooseCommandAction(command, arg);

            if(command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("0")){
                b = false;
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

