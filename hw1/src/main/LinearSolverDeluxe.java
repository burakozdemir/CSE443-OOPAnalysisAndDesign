package main;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.Vector;

/**
 * Bu program GUI yardımı ile kullanıcıdan Lineer bir denklem
 * alır ve çözümünü seçilmiş metod ile ekrana yazdırır .
 */
class LinearSolverDeluxe extends JPanel {
    /**
     * Dosyadan okuyup veriyi doldurduğumuz sınıf .
     */
    private class Data {
        public Data(int size){
            this.size=size;
            coef = new double[size][size];
            equ = new double[size];
        }
        public int size=0;
        public double [][] coef;//Denklemin bulunduğu matrix
        public double [] equ;//Denklemin eşitliği
    }

    /**
     * Data Fields
     */
    static JFrame mainFrame; //Ana uygulama ekranı
    JComboBox combo = new JComboBox();//Methodların bulunduğu yer

    JPanel resultPanel;//Ana ekran içindeki sağ output paneli
    JPanel inputPanel;//Ana ekran içindeki sol output paneli

    JTextArea inputTextArea = new JTextArea();//Dosyadan alınan verilerin ekrana yazıldığı değişken
    JTextArea resultTextArea = new JTextArea();//Hesaplanmış sonucun ekrana yazdırıldığı değişken

    String choosen = "";//Seçilmiş metod bilgisi
    StringBuilder result = new StringBuilder();//

    JButton openButton ;//Dosya açma butonu bilgisi
    JButton resultButton ;//Sonucun hesaplanmasını sağlayan buton

    JFileChooser fc= new JFileChooser();//Seçilmiş file bilgisi
    File file;//Dosya identifier

    Vector<String> methodList=new Vector<>();

    /**
     * Defaul constructor
     */
    LinearSolverDeluxe() { }
    static private final String newline = "\n";

    public String solve(File file,String metod){
        Data equationData = getData(file);
        LinearSolver solver;
        String result;
        switch (metod){
            case GaussElimination.name :
                solver=new GaussElimination();
                solver.solve(equationData.coef,equationData.equ);
                result = solver.toString();
                break;
            case MatrixInversion.name :
                solver = new MatrixInversion();
                solver.solve(equationData.coef,equationData.equ);
                result= solver.toString();
                break;
            default : result="No implemented solve method";break;
        }
        return result;
    }

    private Data getData(File file){
        Data res = new Data(0);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int matrixSize = 0,index=0;
            if((line = (br.readLine())) != null)
                matrixSize=Integer.parseInt(line);
            res = new Data(matrixSize);
            inputTextArea.getText();
            while ((line = br.readLine()) != null) {
                String [] buffer = line.split(",");
                for (int i =0;i<buffer.length-1;i++)
                    res.coef[index][i]=Double.parseDouble(buffer[i]);
                res.equ[index]=Double.parseDouble(buffer[buffer.length-1]);
                index++ ;
            }
            inputTextArea.append(result.toString()+"\n");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace(); }
            return res;
    }

    private String fileToTextArea(File file){
        Data equationData= getData(file);
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < equationData.size; i++) {
            for (int j = 0; j < equationData.size; j++) {
                res.append(equationData.coef[i][j]+" ");
            }
            res.append(", ");
            res.append(equationData.equ[i]+" \n");
        }
        return res.toString();
    }

    private void createPanel(){
        GridLayout la = new GridLayout(1,3,10,10);
        resultPanel.setLayout(la);//Izgara sistemi
        resultPanel.setBorder(new EmptyBorder(20,20,20,20));//Pencere ve buton arası boşluk
        resultPanel.setPreferredSize(new Dimension(50,50));

        GridLayout lay = new GridLayout(2,1,10,10);
        inputPanel.setBorder(new EmptyBorder(10,10,10,10));
        inputPanel.setPreferredSize(new Dimension(50,50));

        Insets Margin = new Insets(20, 20, 20, 20);

        inputTextArea.setMargin(Margin);
        inputTextArea.setEditable(false);

        resultTextArea.setMargin(Margin);
        resultTextArea.setEditable(false);

        combo.setBounds(50,35,250,250);
        combo.setMaximumSize(new Dimension(1,1));
        combo.addItem(GaussElimination.name);
        combo.addItem(MatrixInversion.name);
        combo.setSelectedItem(null);
        combo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String val = (String) combo.getSelectedItem();
                choosen=val;
                System.out.println(choosen);
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LinearSolverDeluxe temp = new LinearSolverDeluxe();
                if (e.getSource() == openButton) {
                    inputTextArea.setText("");
                    int returnVal = fc.showOpenDialog(temp);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = fc.getSelectedFile();
                        inputTextArea.append(fileToTextArea(file));
                    } else {
                        inputTextArea.append("Open command cancelled by user." + newline);
                    }
                    inputTextArea.setCaretPosition(inputTextArea.getDocument().getLength());
                }
            }
        });

        resultButton.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            LinearSolverDeluxe temp = new LinearSolverDeluxe();
            if (e.getSource() == resultButton) {
                resultTextArea.setText("");
                resultTextArea.append(solve(file,choosen));
                resultTextArea.setCaretPosition(resultTextArea.getDocument().getLength());
                //Handle save button action.
            }
        }
        });

        inputPanel.add(combo);
        inputPanel.add(openButton);
        resultPanel.add(inputTextArea);
        resultPanel.add(resultButton);
        resultPanel.add(resultTextArea);

    }
    public void start(){
        methodList.add(GaussElimination.name);
        methodList.add(MatrixInversion.name);

        mainFrame = new JFrame("calculator");
        resultPanel = new JPanel();
        inputPanel = new JPanel();

        ImageIcon imageIcon = new ImageIcon("create.jpg"); //unscaled image
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH); // resize it here
        imageIcon = new ImageIcon(newimg);
        openButton = new JButton("Dosya Seç", imageIcon);
        openButton.setPreferredSize(new Dimension(50,50));

        ImageIcon resultIcon = new ImageIcon("result.png"); //unscaled image
        Image resultImage = resultIcon.getImage();
        Image resultNewImg = resultImage.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH); // resize it here
        resultIcon = new ImageIcon(resultNewImg);
        resultButton = new JButton("", resultIcon);
        resultButton.setPreferredSize(new Dimension(200,200));

        mainFrame.setPreferredSize(new Dimension(1200,200));
        mainFrame.setBounds(30, 30, 756, 756);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(1, 2));
        createPanel();
        mainFrame.getContentPane().add(inputPanel);
        mainFrame.getContentPane().add(resultPanel);
        mainFrame.pack();
        mainFrame.setMinimumSize(mainFrame.getSize());
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
       // mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Main function
     * @param args
     */
    public static void main(String args[]) {
        LinearSolverDeluxe a = new LinearSolverDeluxe();
        a.start();
    }



}