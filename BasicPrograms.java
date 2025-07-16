import java.util.Scanner;
public class BasicPrograms {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose an option:");
        System.out.println("1.Grade Evaluator");
        System.out.println("2.Calculator");
        System.out.println("3.Even or Odd");
        System.out.println("4.Factorial");
        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                gradeEvaluator();
                break;
            case 2:
                calculator();
                break;
            case 3:
                evenOddChecker();
                break;
            case 4:
                Factorial();
            default:
                System.out.println("Exiting the program. Invalid choice");
        }
        sc.close();
    }
    static void gradeEvaluator(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your marks:");
        int marks = sc.nextInt();
        if (marks >= 90)
            System.out.println("Grade A");
        else if (marks >= 80)
            System.out.println("Grade B");
        else if (marks >= 70)
            System.out.println("Grade C");
        else if (marks >= 60)
            System.out.println("Grade D");
        else
            System.out.println("Grade F");
        sc.close();
    }

    static void calculator(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter first number:");
        double a = sc.nextDouble();
        System.out.println("Enter second number:");
        double b = sc.nextDouble();
        System.out.println("Choose the operation : + , - , * , /");
        char op = sc.next().charAt(0);
        switch(op){
            case '+':
                System.out.println("Sum is:" + (a+b));
                break;
            case '-':
                System.out.println("Difference is:" + (a-b));
                break;
            case '*':
                System.out.println("Product is:" + (a*b));
                break;
            case '/':
                if(b != 0)
                    System.out.println("Result is:" + (a/b));
                else
                    System.out.println("Cannot divide by 0");
                break;
            default:
                System.out.println("Invalid operation");
        }
        sc.close();
    }

    static void evenOddChecker(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a number:");
        int num = sc.nextInt();
        if(num %2 == 0)
            System.out.println("Number is even");
        else
            System.out.println("Number is odd");
        sc.close();
    }

    static void Factorial(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a number:");
        int n = sc.nextInt();
        long fact = 1;
        for(int i = 1 ; i <= n ; i++){
            fact = fact*i;
        }
        System.out.println("Factorial of " + n + " is " + fact);
        sc.close();
    }
}




