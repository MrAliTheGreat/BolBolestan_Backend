package BolBolCodes;

import java.util.Scanner;

public class main {
    public static void main(String args[]){
        Scanner scanner = new Scanner(System.in);
        BolBolInterface bolbolInterface = new BolBolInterface();

        while(true){
            bolbolInterface.runBolBolInterface(scanner.nextLine());
        }
    }
}
