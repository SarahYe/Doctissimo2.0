import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.lucene.queryparser.classic.ParseException;

import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.ScrollPane;
import javax.swing.JMenuBar;

public class IHM extends JFrame {

	private JPanel contentPane;
	private JTextField EnteredSymptom;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IHM frame = new IHM();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public IHM() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 600);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(72, 209, 204));
		contentPane.setBackground(new Color(255, 255, 240));
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel Symptom = new JLabel("Enter a symptom:");
		Symptom.setFont(new Font("Sylfaen", Font.BOLD | Font.ITALIC, 11));
		Symptom.setBounds(61, 50, 100, 14);
		contentPane.add(Symptom);
		
		EnteredSymptom = new JTextField();
		EnteredSymptom.setForeground(new Color(210, 180, 140));
		EnteredSymptom.setBounds(195, 46, 300, 20);
		contentPane.add(EnteredSymptom);
		EnteredSymptom.setColumns(10);
		
		JTextPane DiseasePane = new JTextPane();
		//JScrollPane dp = new JScrollPane(DiseasePane);
		DiseasePane.setBounds(37, 100, 200, 429);
		contentPane.add(DiseasePane);
		//contentPane.add( dp );
		
		JTextPane MedicinePane = new JTextPane();
		//JScrollPane mp = new JScrollPane(MedicinePane);
		MedicinePane.setBounds(242, 100, 200, 429);
		contentPane.add(MedicinePane);
		//contentPane.add( mp );
		
		JTextPane SideEffectPane = new JTextPane();
		//JScrollPane sep = new JScrollPane(SideEffectPane);
		SideEffectPane.setBounds(459, 100, 200, 429);
		contentPane.add(SideEffectPane);
		//contentPane.add( sep );
		
		JButton btnNewButton = new JButton("Search");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String symptom = EnteredSymptom.getText();
				Main m = new Main();
				MedicinePane.setText(symptom);
				SideEffectPane.setText(symptom);
			}
		});
		btnNewButton.setBounds(521, 45, 65, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblPossibleDiseases = new JLabel("Possible diseases");
		lblPossibleDiseases.setForeground(new Color(128, 0, 0));
		lblPossibleDiseases.setFont(new Font("Andalus", Font.ITALIC, 13));
		lblPossibleDiseases.setBounds(71, 79, 108, 14);
		contentPane.add(lblPossibleDiseases);
		
		JLabel lblMedecinesToCure = new JLabel("Medecines to cure");
		lblMedecinesToCure.setForeground(new Color(128, 0, 0));
		lblMedecinesToCure.setFont(new Font("Andalus", Font.ITALIC, 13));
		lblMedecinesToCure.setBounds(286, 79, 108, 14);
		contentPane.add(lblMedecinesToCure);
		
		JLabel lblSideEffectOf = new JLabel("Side effect of ...");
		lblSideEffectOf.setForeground(new Color(128, 0, 0));
		lblSideEffectOf.setFont(new Font("Andalus", Font.ITALIC, 13));
		lblSideEffectOf.setBounds(521, 79, 80, 14);
		contentPane.add(lblSideEffectOf);
		
		JLabel applicationName = new JLabel("Doctissimo2.0");
		applicationName.setBackground(new Color(169, 169, 169));
		applicationName.setForeground(new Color(0, 128, 0));
		applicationName.setFont(new Font("Vivaldi", Font.BOLD | Font.ITALIC, 32));
		applicationName.setBounds(10, 11, 181, 28);
		contentPane.add(applicationName);
	}
}
