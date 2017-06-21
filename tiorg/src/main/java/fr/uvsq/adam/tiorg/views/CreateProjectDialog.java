package fr.uvsq.adam.tiorg.views;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CreateProjectDialog extends JDialog
{
	private JTextPane txtProjectName = null;
	private JTextPane txtProjectLocation = null;
	private JTextPane txtFileLocation = null;
	private JRadioButton rbtMove = null;
	private JRadioButton rbtCopy = null;
	private ButtonGroup btGroup = null;
	private JFileChooser fc = null;
	
	private int dialogResult = JOptionPane.CANCEL_OPTION;
	
	public int getDialogResult()
	{
		return dialogResult;
	}
	
	public String getProjectName()
	{
		return txtProjectName.getText().trim();
	}
	
	public String getProjectLocation()
	{
		return txtProjectLocation.getText().trim();
	}
	
	public String getGraphLocation()
	{
		return txtFileLocation.getText().trim();
	}
	
	public boolean getMoveGraph()
	{
		return rbtMove.isSelected();
	}
	
	public CreateProjectDialog()
	{
		setModal(true);
		setTitle("Create project");
		setName("dlgCreateProject");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setMinimumSize(new Dimension(435, 160));
		setMaximumSize(new Dimension(435, 160));
		setPreferredSize(new Dimension(435, 160));
		setSize(new Dimension(435, 200));
		setLocationRelativeTo(null);
		setResizable(false);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.setPreferredSize(new Dimension(10, 40));
		panel.setMinimumSize(new Dimension(10, 40));
		panel.setMaximumSize(new Dimension(32767, 40));
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btOk = new JButton("Create");
		btOk.addActionListener(
				new ActionListener() 
				{
					public void actionPerformed(ActionEvent e) 
					{
						String name = txtProjectName.getText();
						if(name == null || name.trim().isEmpty())
						{
							JOptionPane.showMessageDialog(CreateProjectDialog.this.getParent(), "Project name is empty");
							return;
						}
						String txt = txtProjectLocation.getText();
						if(txt == null || txt.trim().isEmpty())
						{
							JOptionPane.showMessageDialog(CreateProjectDialog.this, "Project location is empty");
							return;
						}
						File file = new File( txt.trim() );
						if(!file.isDirectory() || !file.exists())
						{
							JOptionPane.showMessageDialog(CreateProjectDialog.this, "Project location error");
							return;
						}
						file = new File(file, name);
						if(file.exists())
						{
							JOptionPane.showMessageDialog(CreateProjectDialog.this, "Project location error");
							return;
						}
						txt = txtFileLocation.getText();
						if(txt == null || txt.trim().isEmpty())
						{
							JOptionPane.showMessageDialog(CreateProjectDialog.this, "Graph location is empty");
							return;
						}
						file = new File( txt.trim() );
						if(!file.isFile() || !file.exists())
						{
							JOptionPane.showMessageDialog(CreateProjectDialog.this, "Graph location error");
							return;
						}
						
						dialogResult = JOptionPane.OK_OPTION;
						setVisible(false);
					}
			});
		panel.add(btOk);
		
		JButton btCancel = new JButton("Cancel");
		btCancel.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					txtProjectName.setText(null);
					txtProjectLocation.setText(null);
					setVisible(false);
					dispose();
				}
		});
		panel.add(btCancel);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		
		JLabel lblProjectName = new JLabel("Project name :");
		lblProjectName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblProjectName.setPreferredSize(new Dimension(100, 26));
		lblProjectName.setMinimumSize(new Dimension(100, 26));
		lblProjectName.setMaximumSize(new Dimension(100, 26));
		lblProjectName.setSize(new Dimension(100, 26));
		lblProjectName.setBounds(8, 11, 94, 22);
		panel_1.add(lblProjectName);
		
		JLabel lblProjectLocation = new JLabel("Project location :");
		lblProjectLocation.setHorizontalAlignment(SwingConstants.RIGHT);
		lblProjectLocation.setSize(new Dimension(0, 26));
		lblProjectLocation.setBounds(8, 48, 94, 22);
		panel_1.add(lblProjectLocation);
		
		txtProjectName = new JTextPane();
		txtProjectName.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtProjectName.setBounds(104, 11, 290, 22);
		panel_1.add(txtProjectName);
		
		txtProjectLocation = new JTextPane();
		txtProjectLocation.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtProjectLocation.setBounds(104, 48, 290, 22);
		panel_1.add(txtProjectLocation);
		
		JButton btProjectLocation = new JButton("...");
		btProjectLocation.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = fc.showDialog(CreateProjectDialog.this, "Project location");
					if(returnVal == JFileChooser.APPROVE_OPTION)
						txtProjectLocation.setText( fc.getSelectedFile().getAbsolutePath() );
				}
			}
		);
		btProjectLocation.setBounds(400, 48, 22, 22);
		panel_1.add(btProjectLocation);
		
		JLabel lblFileLocation = new JLabel("Graph location :");
		lblFileLocation.setSize(new Dimension(0, 26));
		lblFileLocation.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFileLocation.setBounds(8, 81, 94, 22);
		panel_1.add(lblFileLocation);
		
		txtFileLocation = new JTextPane();
		txtFileLocation.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtFileLocation.setBounds(104, 81, 290, 22);
		panel_1.add(txtFileLocation);
		
		JButton btFileLocation = new JButton("...");
		btFileLocation.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
					int returnVal = fc.showDialog(CreateProjectDialog.this, "Graph location");
					if(returnVal == JFileChooser.APPROVE_OPTION)
						txtFileLocation.setText( fc.getSelectedFile().getAbsolutePath() );
				}
			}
		);
		btFileLocation.setBounds(400, 81, 22, 22);
		panel_1.add(btFileLocation);
		
		rbtMove = new JRadioButton("Move");
		rbtMove.setBounds(166, 101, 60, 23);
		panel_1.add(rbtMove);
		
		rbtCopy = new JRadioButton("Copy");
		rbtCopy.setSelected(true);
		rbtCopy.setBounds(104, 101, 60, 23);
		panel_1.add(rbtCopy);
		
		btGroup = new ButtonGroup();
		btGroup.add(rbtCopy);
		btGroup.add(rbtMove);
	}
	
	private JFileChooser getFileChooser()
	{
		if(fc == null)
			fc = new JFileChooser( new File(".").getAbsolutePath() );
		return fc;
	}
}
