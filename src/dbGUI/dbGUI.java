/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.spi.DirStateFactory.Result;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author nico
 * references: http://1bestcsharp.blogspot.se/2015/06/java-create-login-form-window-mysql-database-in-java-netbeans.html
 *             http://1bestcsharp.blogspot.se/2016/01/java-and-mysql-insert-update-delete-display.html
 */
public class dbGUI extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public dbGUI() {
        initComponents();
        Show_Users_In_JTable();
    }
    
    public Connection getConn() {
        Connection conn;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/recitations","nico", "123");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<User> getUsersList() {
        ArrayList<User> usersList = new ArrayList<User>();
        Connection connection = getConn();
        String query = "SELECT groupl, recnum, courseid FROM room";
        
        Statement st;
        ResultSet rs;
        
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            User user;
            while(rs.next()) {
                String group = rs.getString("groupl");
                Integer recNum = rs.getInt("recnum");
                String course = rs.getString("courseid");
                String query2 = "SELECT 15-count(*) AS place FROM booking WHERE groupl='" + group + "' AND recnum=" + recNum + " AND courseid='" + course + "' ";
                // to modify 15 (places) room.place should be included
                
                Statement st2 = connection.createStatement();
                ResultSet rs2 = st2.executeQuery(query2);
                while(rs2.next()) {
                    Integer places = rs2.getInt("place");
                    user = new User(group, recNum, places, course);            
                    usersList.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersList;
    }
    
    public void showCombList(String course, Integer recNum) {        

        Connection connection = getConn();        
        String query = "SELECT problems, points FROM combination WHERE courseid='" + course + "' AND recnumber=" + recNum;
        
        Statement st;
        ResultSet rs;
        
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()) {
                String problems = rs.getString("problems");
                Integer points = rs.getInt("points");                
                DefaultTableModel model = (DefaultTableModel)jTable_comb.getModel();
                Object[] row = new Object[2];
                row[0] = problems;
                row[1] = points;
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();            
        }
    }
                            
    
    public void executeSQLQuery(String query, String message)  {
        Connection conn = getConn();
        Statement st;
        try {
            st = conn.createStatement();
            if(st.executeUpdate(query) == 1) {
                // refresh jtable data
                DefaultTableModel model = (DefaultTableModel)jTable_display.getModel();
                model.setRowCount(0);
                Show_Users_In_JTable();
                JOptionPane.showMessageDialog(null, "Data " + message + " succesfully");
                
            } else {
                JOptionPane.showMessageDialog(null, "Data Not " + message);
            }            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something went wrong!");
            // student not registered for the course
            // student already registered in on this recitation
            // student not registered for this recitation
        }
    }
    
    public void executeBookQuery(String query, String message)  {
        Connection conn = getConn();
        Statement st;
        try {
            st = conn.createStatement();
            if(st.executeUpdate(query) == 1) {
                // refresh jtable data
                DefaultTableModel model = (DefaultTableModel)jTable_display.getModel();
                model.setRowCount(0);
                Show_Users_In_JTable();                
                
                // refresh combinations data
                DefaultTableModel combModel = (DefaultTableModel)jTable_comb.getModel();
                combModel.setRowCount(0);
                String course = jTextField_Course.getText();                
                int foo = Integer.parseInt(jTextField_RecNum.getText());   
                System.out.print(course);
                System.out.print(foo);
                showCombList(course, foo);
                JOptionPane.showMessageDialog(null, "Data " + message + " succesfully");
                // show combinations data
                timer1.start();                
            } else {
                JOptionPane.showMessageDialog(null, "Data Not " + message);
            }            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something went wrong!");
            // student not registered for the course
            // student already registered on this recitation
            // student not registered for this recitation
        }
    }        
    
    public void Show_Users_In_JTable(){
        ArrayList<User> list = getUsersList();
        DefaultTableModel model = (DefaultTableModel)jTable_display.getModel();
        Object[] row = new Object[4];
        for(int i = 0; i<list.size(); i++) {
            row[0] = list.get(i).getGroup();
            row[1] = list.get(i).getRecNum();
            row[2] = list.get(i).getPlaces();
            row[3] = list.get(i).getCourse();
            model.addRow(row);
        }
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    Timer timer1 = new Timer(30, new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if(jPanel_comb.getHeight() != 150) {
                jPanel_comb.setBounds(250,0,jPanel_db.getSize().width,jPanel_comb.getHeight() + 5);
                if(jPanel_comb.getHeight() == 150) {
                    timer1.stop();
                }
            }
        }                      
    });
    
    Timer timer2 = new Timer(30, new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            if(jPanel_comb.getHeight() != 0) {
                jPanel_comb.setBounds(250,0,jPanel_db.getSize().width,jPanel_comb.getHeight() - 5);
                if(jPanel_comb.getHeight() == 0) {
                    timer2.stop();
                }
            }
        }                      
    });

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel_db = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_Group = new javax.swing.JTextField();
        jTextField_RecNum = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_display = new javax.swing.JTable();
        jButton_book = new javax.swing.JButton();
        jButton_delete = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_Course = new javax.swing.JTextField();
        jTextField_Id = new javax.swing.JTextField();
        jPanel_comb = new javax.swing.JPanel();
        jButton_submit2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_comb = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel_db.setBackground(new java.awt.Color(204, 204, 204));
        jPanel_db.setPreferredSize(new java.awt.Dimension(700, 550));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel1.setText("Group:");

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setText("RecNum:");

        jTextField_Group.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextField_Group.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_GroupActionPerformed(evt);
            }
        });

        jTextField_RecNum.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextField_RecNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_RecNumActionPerformed(evt);
            }
        });

        jTable_display.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Group", "RecNum", "Places", "Course"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_display.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_displayMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_display);
        if (jTable_display.getColumnModel().getColumnCount() > 0) {
            jTable_display.getColumnModel().getColumn(0).setResizable(false);
            jTable_display.getColumnModel().getColumn(1).setResizable(false);
            jTable_display.getColumnModel().getColumn(2).setResizable(false);
            jTable_display.getColumnModel().getColumn(3).setResizable(false);
        }

        jButton_book.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton_book.setText("Book\n");
        jButton_book.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_bookActionPerformed(evt);
            }
        });

        jButton_delete.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton_delete.setText("Delete");
        jButton_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_deleteActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel4.setText("Id:");

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel3.setText("Course:");

        jTextField_Course.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextField_Course.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_CourseActionPerformed(evt);
            }
        });

        jTextField_Id.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextField_Id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_IdActionPerformed(evt);
            }
        });

        jButton_submit2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton_submit2.setText("Submit");
        jButton_submit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_submit2ActionPerformed(evt);
            }
        });

        jTable_comb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Problem", "Points"
            }
        ));
        jScrollPane3.setViewportView(jTable_comb);

        javax.swing.GroupLayout jPanel_combLayout = new javax.swing.GroupLayout(jPanel_comb);
        jPanel_comb.setLayout(jPanel_combLayout);
        jPanel_combLayout.setHorizontalGroup(
            jPanel_combLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_combLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_submit2)
                .addGap(22, 22, 22))
        );
        jPanel_combLayout.setVerticalGroup(
            jPanel_combLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_combLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jButton_submit2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel_combLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_dbLayout = new javax.swing.GroupLayout(jPanel_db);
        jPanel_db.setLayout(jPanel_dbLayout);
        jPanel_dbLayout.setHorizontalGroup(
            jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_dbLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel_dbLayout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_Group, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_dbLayout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_Id, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_dbLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_Course, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_dbLayout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_RecNum, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_dbLayout.createSequentialGroup()
                        .addComponent(jButton_book)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_delete)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_comb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        jPanel_dbLayout.setVerticalGroup(
            jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_dbLayout.createSequentialGroup()
                .addComponent(jPanel_comb, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(171, 171, 171)
                .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_dbLayout.createSequentialGroup()
                        .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Group, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField_RecNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField_Course, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField_Id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_dbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton_book, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_db, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel_db, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_IdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_IdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_IdActionPerformed

    private void jTextField_CourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_CourseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_CourseActionPerformed

    private void jButton_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_deleteActionPerformed
        String query = "DELETE FROM booking WHERE groupl = '" + jTextField_Group.getText()
        + "' AND courseid='" + jTextField_Course.getText() + "' AND recnum="
        + jTextField_RecNum.getText()
        + " AND studentid ='" + jTextField_Id.getText() + "' ";

        executeSQLQuery(query, "Deleted");
    }//GEN-LAST:event_jButton_deleteActionPerformed

    private void jButton_bookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_bookActionPerformed

        String query = "INSERT INTO booking VALUES ('" + jTextField_Group.getText()
        + "', '" + jTextField_Course.getText() + "', "
        + jTextField_RecNum.getText()
        + ", '" + jTextField_Id.getText() + "')";

        executeBookQuery(query, "Inserted");
        jTable_display.repaint();
    }//GEN-LAST:event_jButton_bookActionPerformed

    private void jTable_displayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_displayMouseClicked
        // Display Selected Row in JTextFields
        int i = jTable_display.getSelectedRow();
        TableModel model = jTable_display.getModel();
        jTextField_Group.setText(model.getValueAt(i,0).toString());
        jTextField_RecNum.setText(model.getValueAt(i,1).toString());
        jTextField_Course.setText(model.getValueAt(i,3).toString());
    }//GEN-LAST:event_jTable_displayMouseClicked

    private void jTextField_RecNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_RecNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_RecNumActionPerformed

    private void jTextField_GroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_GroupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_GroupActionPerformed

    private void jButton_submit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_submit2ActionPerformed
        int sel = jTable_comb.getSelectedRow();
        String problem = jTable_comb.getValueAt(sel, 0).toString();
        String p = jTable_comb.getValueAt(sel, 1).toString();
        int points = Integer.parseInt(p);
        String course = jTextField_Course.getText();                
        int recNum = Integer.parseInt(jTextField_RecNum.getText());
        String student = jTextField_Id.getText();
        
        String query = "INSERT INTO result VALUES ('" + student
        + "', '" + problem + "', "
        + points + ", " + recNum
        + ", '" + course + "')";        
                
        executeSQLQuery(query, "Inserted");
        timer2.start();
    }//GEN-LAST:event_jButton_submit2ActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(dbGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dbGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dbGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dbGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new dbGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_book;
    private javax.swing.JButton jButton_delete;
    private javax.swing.JButton jButton_submit2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel_comb;
    private javax.swing.JPanel jPanel_db;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable_comb;
    private javax.swing.JTable jTable_display;
    private javax.swing.JTextField jTextField_Course;
    private javax.swing.JTextField jTextField_Group;
    private javax.swing.JTextField jTextField_Id;
    private javax.swing.JTextField jTextField_RecNum;
    // End of variables declaration//GEN-END:variables
}
