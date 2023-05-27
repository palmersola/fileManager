package org.example;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static final String HOME = "src/main/java/org/example/targetDirectory/";
    private static String currentDirName = "";
    private static File currentDir;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        setCurrentDir("");
        while(true) {
            File[] folder = currentDir.listFiles();
            File newFolder = currentDir;
            if(newFolder.isDirectory()) {
                System.out.println("You are currently in " + newFolder.getName());
                System.out.println("""
                Would you like to:
                    1.)Open directory inside current
                    2.)Return to home directory
                    3.)Create new directory in current
                    4.)Delete directory in current
                    5.)Search in current
                    6.)Copy file in current
                    7.)Move file in current
                    8.)Delete file in current
                    0.)Quit""");
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 1){
                    System.out.println("Which folder would you like to access?");
                    int switchFolder = Integer.parseInt(scanner.nextLine()) - 1;
                    assert folder != null;
                    setCurrentDir(folder[switchFolder].getName());
                }
                else if (choice == 2)setCurrentDir("");
                else if (choice == 3) createDir();
                else if (choice == 4) deleteDir();
                else if (choice == 5) searchDir();
                else if (choice == 6) fileManage("copy");
                else if (choice == 7) fileManage("move");
                else if (choice == 8) fileManage("delete");
                else if (choice == 0) { System.out.println("Goodbye.");  break;}
            } else {
                System.out.println("Please select folder.");
            }
        }
    }

    public static void setCurrentDir(String currentDir) {
        Main.currentDirName = Objects.equals(currentDir, "") ? "": currentDir + "/";
        Main.currentDir = Paths.get(HOME, currentDirName).toFile();
        printDir();
    }

    public static void printDir() {
        File[] folder = currentDir.listFiles();
        System.out.println("\ntargetDirectory/" + currentDirName + "\n");
        if (folder != null) {
            print(folder);
        }
    }

    private static void print(File[] folder) {
        int i = 1;
        for (File file: folder)
        {
            String fileOr = file.isDirectory() ? "Folder: " : "File: ";
            System.out.println(i + ".) " + fileOr + file.getName());
            System.out.println("    Size :" + file.getTotalSpace());
            System.out.println("    Last Modified: " + file.lastModified());
            System.out.println(" ");
            i++;
        }
    }

    public static void createDir() {
        System.out.println("What would you like to name this directory?");
        String name = scanner.nextLine();
        try {
            Files.createDirectory(Paths.get(HOME, currentDirName + name));
            printDir();
        } catch(Exception e) {
            System.out.println("Could not create folder. Make sure to not include periods or slashes.");
        }
    }

    public static void deleteDir() throws IOException {
        System.out.println("Which directory would you like to delete?");
        printDir();
        int input = Integer.parseInt(scanner.nextLine());
        File[] folder = currentDir.listFiles();
        int index = input - 1;
        assert folder != null;
        String name = folder[index].getName();
        System.out.println("""
            This will delete folder and all contents. Continue?
                1.) YES
                2.) NO""");
        int choice = Integer.parseInt(scanner.nextLine());
        if(choice == 1) {
            Path path = Paths.get(HOME, currentDirName + name);
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
        printDir();
    }

    public static void searchDir(){
        System.out.println("Enter search query:");
        String search = scanner.nextLine();
        FileFilter filer = f -> f.getName().contains(search);
        File[] folder = currentDir.listFiles(filer);
        assert folder != null;
        print(folder);
    }

    public static void fileManage(String select) throws IOException {
        File[] srcFolder = currentDir.listFiles();
        String srcName = currentDirName;
        System.out.println("Which file would you like to " + select + "?");
        assert srcFolder != null;
        String file = srcFolder[Integer.parseInt(scanner.nextLine()) - 1].getName();
        Path src = Paths.get(HOME, srcName + file);
        if(Objects.equals(select, "delete")){
            Files.deleteIfExists(src.toFile().toPath());
        } else {
            String path = cycleDir();
            Path dest = Paths.get(HOME, path + "/" + file);
            if (Objects.equals(select, "copy")) {
                Files.copy(src.toFile().toPath(), dest.toFile().toPath());
            } else if(Objects.equals(select, "move")){
                Files.move(src.toFile().toPath(), dest.toFile().toPath());
            }
        }
        printDir();
    }

    public static String cycleDir(){
        String path = "";
        while (true){
            setCurrentDir(path);
            File[] loopFolder = currentDir.listFiles();
            System.out.println("Select folder. Enter 0 for current directory.");
            int choice = Integer.parseInt(scanner.nextLine());
            if(choice == 0) break;
            int index = choice - 1;
            assert loopFolder != null;
            path = path  + loopFolder[index].getName() + "/";
            System.out.println(Arrays.toString(loopFolder));
        }
        return path;
    }
}