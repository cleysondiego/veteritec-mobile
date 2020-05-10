package br.com.veteritec.utils;

public class DosageCalculator {
    public int calculate(int concentration, int dosage, int weight) {
//        try {
//            double n1 = Double.parseDouble(txbConcentracaoFarmaco_1.getText());
//            double n2 = Double.parseDouble(txbDoseFarmaco_1.getText());
//            double n3 = Double.parseDouble(txbPesoAnimal_1.getText());
//
//            double PesoAnimal = 0, DoseFarmaco = 0, ConcentracaoFarmaco = 0;
//            double Medicacao = 0;
//
//            if (n1 == 0 || n2 == 0 || n3 == 0) {
//                JOptionPane.showMessageDialog(null, "Campos Inv�lidos");
//            } else {
//                PesoAnimal = Double.parseDouble(txbPesoAnimal_1.getText());
//                DoseFarmaco = Double.parseDouble(txbDoseFarmaco_1.getText());
//                ConcentracaoFarmaco = Double.parseDouble(txbConcentracaoFarmaco_1.getText());
//
//                if (PesoAnimal <= 0 || DoseFarmaco <= 0 || ConcentracaoFarmaco <= 0)
//                    JOptionPane.showMessageDialog(null, "Imposs�vel realizar calculos com n�meros negativos");
//                else {
//                    if (cbxUnidadeDose.getSelectedItem() == "Mg" && cbxUnidadeConcentracao.getSelectedItem() == "G") {
//                        Medicacao = (PesoAnimal * DoseFarmaco) / (1000 / ConcentracaoFarmaco);
//                    } else if (cbxUnidadeDose.getSelectedItem() == "Mg" && cbxUnidadeConcentracao.getSelectedItem() == "Mg") {
//                        Medicacao = (PesoAnimal * DoseFarmaco) / ConcentracaoFarmaco;
//                    } else if (cbxUnidadeDose.getSelectedItem() == "Mg" && cbxUnidadeConcentracao.getSelectedItem() == "Mcg") {
//                        Medicacao = (PesoAnimal * DoseFarmaco) / (ConcentracaoFarmaco * 0.001);
//                    } else if (cbxUnidadeDose.getSelectedItem() == "Mg" && cbxUnidadeConcentracao.getSelectedItem() == "%") {
//                        Medicacao = (PesoAnimal * DoseFarmaco) / (ConcentracaoFarmaco * 10);
//                    } else if (cbxUnidadeDose.getSelectedItem() == "Mcg" && cbxUnidadeConcentracao.getSelectedItem() == "G") {
//                        Medicacao = (PesoAnimal * (DoseFarmaco * 0.001)) / (1000 / ConcentracaoFarmaco);
//                    } else if (cbxUnidadeDose.getSelectedItem() == "Mcg" && cbxUnidadeConcentracao.getSelectedItem() == "Mg") {
//                        Medicacao = (PesoAnimal * (DoseFarmaco * 0.001)) / ConcentracaoFarmaco;
//                    } else if (cbxUnidadeDose.getSelectedItem() == "Mcg" && cbxUnidadeConcentracao.getSelectedItem() == "Mcg") {
//                        Medicacao = (PesoAnimal * (DoseFarmaco * 0.001)) / (ConcentracaoFarmaco * 0.001);
//                    } else if (cbxUnidadeDose.getSelectedItem() == "Mcg" && cbxUnidadeConcentracao.getSelectedItem() == "%") {
//                        Medicacao = (PesoAnimal * (DoseFarmaco * 0.001)) / (ConcentracaoFarmaco * 10);
//                    }
//                    if (cbxUnidadeConcentracao.getSelectedItem() == "" || cbxUnidadeDose.getSelectedItem() == "") {
//                        JOptionPane.showMessageDialog(null, "Unidades Inv�lidas!");
//                    } else
//                        lblRes.setText(Medicacao + "ML");
//                }
//            }
//        } catch (NumberFormatException e) {
//            JOPtionPane.showMessageDialog(null, "Erro de convers�o!");
//        }
        return 1;
    }
}
