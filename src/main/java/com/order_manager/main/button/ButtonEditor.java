package com.order_manager.main.button;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import com.order_manager.main.view.impl.OrderManagerViewSwingImpl;

public class ButtonEditor extends DefaultCellEditor {
    private String label;
    private OrderManagerViewSwingImpl parent;
    private int row;

    public ButtonEditor(JCheckBox checkBox, String label, OrderManagerViewSwingImpl parent) {
        super(checkBox);
        this.label = label;
        this.parent = parent;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        JButton button = new JButton(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.handleButtonAction(label, row);
            }
        });
        return button;
    }
}