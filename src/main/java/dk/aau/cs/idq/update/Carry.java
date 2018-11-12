package dk.aau.cs.idq.update;

import java.io.*;
import java.util.Scanner;

public class Carry {

    public void carry() throws IOException {
        File readFile1 = new File(System.getProperty("user.dir") + "/tPar.txt");
        File readFile2 = new File(System.getProperty("user.dir") + "/tDoor.txt");

        File writeFile1 = new File(System.getProperty("user.dir") + "/Par.txt");
        File writeFile2 = new File(System.getProperty("user.dir") +  "/Door.txt");

        FileReader in1 = new FileReader(readFile1);
        FileReader in2 = new FileReader(readFile2);

        FileWriter fw1 = new FileWriter(writeFile1);
        FileWriter fw2 = new FileWriter(writeFile2);

        int num = 0;
        while ((num = in1.read()) != -1) {
            fw1.write(num);
        }

        while ((num = in2.read()) != -1) {
            fw2.write(num);
        }

        fw1.close();
        fw2.close();
    }
}
