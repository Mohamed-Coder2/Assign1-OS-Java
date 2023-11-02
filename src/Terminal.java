import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

//13 commands were implemented
//echo --- pwd --- ls & ls-r --- mkdir --- rmdir --- cd
//touch --- rm --- history --- cp & cp -r --- cat

public class Terminal {  
    
    private static File currentDirectory = new File(System.getProperty("user.dir"));
    private static List<String> commandHistory = new ArrayList<>();
    public static String TerminalString = new String();

    Parser parser;

    public static void printFileContent(String filePath) {
        File file = new File(filePath);
        
        if (file.exists() && file.isFile()) {
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } 

            catch (IOException e) {
                e.printStackTrace();
            }
        } 

        else {
            System.err.println("File not found or not a regular file: " + filePath);
        }
    }

    public static void concatenateAndPrintFiles(String filePath1, String filePath2) {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);

        if (file1.exists() && file1.isFile() && file2.exists() && file2.isFile()) {
            try (BufferedReader reader1 = new BufferedReader(new FileReader(file1));
                 BufferedReader reader2 = new BufferedReader(new FileReader(file2))) {

                String line;
                while ((line = reader1.readLine()) != null) {
                    System.out.println(line);
                }

                System.out.println(); // Add a separator between files

                while ((line = reader2.readLine()) != null) {
                    System.out.println(line);
                }
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        } 
        
        else {
            System.err.println("One or both files not found or not regular files: " + filePath1 + ", " + filePath2);
        }
    }

    public static void copyDirectory(String sourcePath, String destinationPath) {
        Path sourceDir = Paths.get(sourcePath);
        Path destinationDir = Paths.get(destinationPath);

        try {
            if (!Files.exists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = destinationDir.resolve(sourceDir.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = destinationDir.resolve(sourceDir.relativize(dir));
                    if (!Files.exists(targetDir)) {
                        Files.createDirectory(targetDir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            System.out.println("Directory copied successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String sourceFile, String destinationFile) {    
        
        try {
            Files.copy(Paths.get(sourceFile), Paths.get(destinationFile));
        } 
        
        catch (IOException e) {
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
            case "ls":  //ls & ls -r
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
            case "cp":  //cp & cp-r
                if(arg[0].equals("-r")){
                    
                    if(arg.length == 3){
                        copyDirectory(arg[1], arg[2]);
                        addCommandToHistory(TerminalString);
                        break;
                    }

                    System.out.println("Please specify directories");

                    break;
                }
                copyFile(arg[0], arg[1]);
                addCommandToHistory(TerminalString);
                break;
            
            case "cat": 
                if(arg.length == 1){
                    printFileContent(arg[0]);
                    addCommandToHistory(TerminalString);
                    break;
                }

                else if(arg.length == 2){
                    
                    concatenateAndPrintFiles(arg[0], arg[1]);

                    addCommandToHistory(TerminalString);
                    break;
                }

                System.out.println("Please refer to the manual to learn how to use our handmade Terminal :)");  
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

