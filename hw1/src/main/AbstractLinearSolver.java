package main;

public abstract class AbstractLinearSolver implements LinearSolver {

    protected double[] solution ;
    @Override
    public void solve (double a[][],double abc[]){
        System.out.println("No implemented Method for AbstractLinearSolver");
    }
}
