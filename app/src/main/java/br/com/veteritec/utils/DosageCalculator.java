package br.com.veteritec.utils;

public class DosageCalculator {
    private double INVALID_OPERATORS = -2;

    public double calculate(double animalWeight, double dosage, double concentration, String dosageUnit, String concentrationUnit) {
//        animalWeight = KG
//        dose = MG e MCG
//        concentração = G, MG, MCG e %
//        retorno é sempre em ML

        if (dosageUnit.equals("MG") && concentrationUnit.equals("G")) {
            return (animalWeight * dosage) / (1000 / concentration);
        } else if (dosageUnit.equals("MG") && concentrationUnit.equals("MG")) {
            return (animalWeight * dosage) / concentration;
        } else if (dosageUnit.equals("MG") && concentrationUnit.equals("MCG")) {
            return (animalWeight * dosage) / (concentration * 0.001);
        } else if (dosageUnit.equals("MG") && concentrationUnit.equals("%")) {
            return (animalWeight * dosage) / (concentration * 10);
        } else if (dosageUnit.equals("MCG") && concentrationUnit.equals("G")) {
            return (animalWeight * (dosage * 0.001)) / (1000 / concentration);
        } else if (dosageUnit.equals("MCG") && concentrationUnit.equals("MG")) {
            return (animalWeight * (dosage * 0.001)) / concentration;
        } else if (dosageUnit.equals("MCG") && concentrationUnit.equals("MCG")) {
            return (animalWeight * (dosage * 0.001)) / (concentration * 0.001);
        } else if (dosageUnit.equals("MCG") && concentrationUnit.equals("%")) {
            return (animalWeight * (dosage * 0.001)) / (concentration * 10);
        }
        return INVALID_OPERATORS;
    }
}
