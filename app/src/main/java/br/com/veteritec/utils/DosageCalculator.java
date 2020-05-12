package br.com.veteritec.utils;

public class DosageCalculator {
    public double calculate(double animalWeight, double dosage, double concentration, String dosageUnit, String concentrationUnit) {
//        animalWeight = KG
//        dose = MG e MCG
//        concentração = G, MG, MCG e %

        if (!validate(animalWeight, dosage, concentration, dosageUnit, concentrationUnit)) {
            throw new UnsupportedOperationException();
        }

        if (dosageUnit.equals("Mg") && concentrationUnit.equals("G")) {
            return (animalWeight * dosage) / (1000 / concentration);
        } else if (dosageUnit.equals("Mg") && concentrationUnit.equals("Mg")) {
            return (animalWeight * dosage) / concentration;
        } else if (dosageUnit.equals("Mg") && concentrationUnit.equals("Mcg")) {
            return (animalWeight * dosage) / (concentration * 0.001);
        } else if (dosageUnit.equals("Mg") && concentrationUnit.equals("%")) {
            return (animalWeight * dosage) / (concentration * 10);
        } else if (dosageUnit.equals("Mcg") && concentrationUnit.equals("G")) {
            return (animalWeight * (dosage * 0.001)) / (1000 / concentration);
        } else if (dosageUnit.equals("Mcg") && concentrationUnit.equals("Mg")) {
            return (animalWeight * (dosage * 0.001)) / concentration;
        } else if (dosageUnit.equals("Mcg") && concentrationUnit.equals("Mcg")) {
            return (animalWeight * (dosage * 0.001)) / (concentration * 0.001);
        } else if (dosageUnit.equals("Mcg") && concentrationUnit.equals("%")) {
            return (animalWeight * (dosage * 0.001)) / (concentration * 10);
        }

        return 0;
    }

    private boolean validate(double animalWeight, double dosage, double concentration, String dosageUnit, String concentrationUnit) {
        return !(animalWeight > 0 || dosage > 0 || concentration > 0 || dosageUnit.equals("") || concentrationUnit.equals(""));
    }
}
