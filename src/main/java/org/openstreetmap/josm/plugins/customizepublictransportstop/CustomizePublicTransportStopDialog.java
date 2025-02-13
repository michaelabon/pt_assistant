// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.customizepublictransportstop;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.pt_assistant.utils.DialogUtils;
import org.openstreetmap.josm.tools.I18n;

/**
 * Dialog for setting stop area properties
 *
 * @author Rodion Scherbakov
 */
public class CustomizePublicTransportStopDialog implements ActionListener, ItemListener {
    private static final String CANCEL_COMMAND = "cancel";
    private static final String SAVE_COMMAND = "save";
    private static final String CANCEL_BUTTON_CAPTION = I18n.marktr("Cancel");
    private static final String SAVE_BUTTON_CAPTION = I18n.marktr("Save");
    private static final String AREA_CAPTION = I18n.marktr("Area");
    private static final String COVER_CAPTION = I18n.marktr("Cover");
    private static final String SHELDER_CAPTION = I18n.marktr("Shelter");
    private static final String BENCH_CAPTION = I18n.marktr("Bench");
    private static final String RAILWAY_STOP_CAPTION = I18n.marktr("Railway stop");
    private static final String RAILWAY_STATION_CAPTION = I18n.marktr("Railway station");
    private static final String TRAM_CAPTION = I18n.marktr("Tram");
    private static final String TROLLEYBUS_CAPTION = I18n.marktr("Trolleybus");
    private static final String SHARE_TAXI_CAPTION = I18n.marktr("Share taxi");
    private static final String BUS_CAPTION = I18n.marktr("Bus");
    private static final String BUS_STATION_CAPTION = I18n.marktr("Bus station");
    private static final String ASSIGN_TRANSPORT_TYPE_CAPTION = I18n.marktr("Assign transport type to platform");
    private static final String NETWORK_LEVEL_CAPTION = I18n.marktr("Network level");
    private static final String OPERATOR_CAPTION = I18n.marktr("Operator");
    private static final String NETWORK_CAPTION = I18n.marktr("Network");
    // i18n: label for the tag name:en=*
    private static final String NAME_EN_CAPTION = I18n.marktr("Name (en.)");
    // i18n: label for the tag name=*
    private static final String NAME_CAPTION = I18n.marktr("Name");
    private static final String STOP_CUSTOMIZING_DIALOG_CAPTION = I18n.marktr("Stop customizing");
    private static final String LONG_DISTANCE_NETWORK_CAPTION = I18n.marktr("Long distance");
    private static final String REGIONAL_NETWORK_CAPTION = I18n.marktr("Regional");
    private static final String COMMUTER_NETWORK_CAPTION = I18n.marktr("Commuter");
    private static final String CITY_NETWORK_CAPTION = I18n.marktr("City transport");
    private static final String HIGH_SPEED_NETWORK_CAPTION = I18n.marktr("High speed");

    private final String[] serviceCaptionStrings = {CITY_NETWORK_CAPTION, COMMUTER_NETWORK_CAPTION, REGIONAL_NETWORK_CAPTION,
            LONG_DISTANCE_NETWORK_CAPTION, HIGH_SPEED_NETWORK_CAPTION};
    private final String[] serviceStrings = {OSMTags.CITY_NETWORK_TAG_VALUE, OSMTags.COMMUTER_NETWORK_TAG_VALUE,
            OSMTags.REGIONAL_NETWORK_TAG_VALUE, OSMTags.LONG_DISTANCE_NETWORK_TAG_VALUE,
            OSMTags.HIGH_SPEED_NETWORK_TAG_VALUE};

    private JDialog jDialog;
    private JTextField textFieldName;
    private JTextField textFieldNameEn;
    private JTextField textFieldNetwork;
    private JTextField textFieldOperator;
    private JComboBox<String> comboBoxService;
    private JCheckBox checkBoxIsBus;
    private JCheckBox checkBoxIsTrolleybus;
    private JCheckBox checkBoxIsShareTaxi;
    private JCheckBox checkBoxIsBusStation;
    private JCheckBox checkBoxIsAssignTransportType;
    private JCheckBox checkBoxIsTram;
    private JCheckBox checkBoxIsTrainStation;
    private JCheckBox checkBoxIsTrainStop;
    private JCheckBox checkBoxIsBench;
    private JCheckBox checkBoxIsShelder;
    private JCheckBox checkBoxIsCover;
    private JCheckBox checkBoxIsArea;

    /**
     * Stop area
     */
    private StopArea stopArea;
    /**
     * Customize stop action object for callback
     */
    private CustomizeStopAction customizeStopAction;

    /**
     * Map of check boxes
     */
    private final HashMap<JCheckBox, Boolean> checkBoxValues = new HashMap<>();

    /**
     * Previous stop name
     */
    private static String previousName;
    /**
     * Previous english stop name
     */
    private static String previousNameEn;
    /**
     * Network name at previous call
     */
    private static String previousNetwork;
    /**
     * Operator name at previous call
     */
    private static String previousOperator;

    /**
     * Reference to dialog object
     */
    private static CustomizePublicTransportStopDialog customizePublicTransportStopDialogInstance;

    /**
     * Construct dialog and fill controls
     *
     * @param customizeStopAction Stop area customizing action
     * @param stopArea Stop area
     * @return Reference to dialog
     */
    public static CustomizePublicTransportStopDialog showCustomizePublicTransportStopDialog(
            CustomizeStopAction customizeStopAction, StopArea stopArea) {
        if (customizePublicTransportStopDialogInstance == null) {
            customizePublicTransportStopDialogInstance = new CustomizePublicTransportStopDialog(customizeStopAction,
                    stopArea);
        } else {
            customizePublicTransportStopDialogInstance.setCustomizeStopAction(customizeStopAction);
            customizePublicTransportStopDialogInstance.setStopArea(stopArea);
        }
        customizePublicTransportStopDialogInstance.setVisible(true);
        return customizePublicTransportStopDialogInstance;
    }

    /**
     * Constructor of dialog
     */
    public CustomizePublicTransportStopDialog() {
        Frame frame = JOptionPane.getFrameForComponent(MainApplication.getMainFrame());
        jDialog = new JDialog(frame, tr(STOP_CUSTOMIZING_DIALOG_CAPTION), false);
        JPanel contentPane = createContentPane();

        jDialog.add(contentPane);

        jDialog.pack();
        jDialog.setLocationRelativeTo(frame);
    }

    private JPanel createContentPane() {
        JPanel contentPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        contentPane.setLayout(gridbag);
        GridBagConstraints layoutCons = new GridBagConstraints();
        JLabel label = new JLabel(tr(NAME_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 0;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(label, layoutCons);
        contentPane.add(label);

        textFieldName = new JTextField("", 25);
        layoutCons.gridx = 1;
        layoutCons.gridy = 0;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(textFieldName, layoutCons);
        contentPane.add(textFieldName);

        JLabel labelNameEn = new JLabel(tr(NAME_EN_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 1;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(labelNameEn, layoutCons);
        contentPane.add(labelNameEn);

        textFieldNameEn = new JTextField("", 25);
        layoutCons.gridx = 1;
        layoutCons.gridy = 1;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(textFieldNameEn, layoutCons);
        contentPane.add(textFieldNameEn);

        JLabel labelNetwork = new JLabel(tr(NETWORK_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 2;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(labelNetwork, layoutCons);
        contentPane.add(labelNetwork);

        textFieldNetwork = new JTextField("", 25);
        layoutCons.gridx = 1;
        layoutCons.gridy = 2;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(textFieldNetwork, layoutCons);
        contentPane.add(textFieldNetwork);

        JLabel labelOperator = new JLabel(tr(OPERATOR_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 3;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(labelOperator, layoutCons);
        contentPane.add(labelOperator);

        textFieldOperator = new JTextField("", 25);
        layoutCons.gridx = 1;
        layoutCons.gridy = 3;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(textFieldOperator, layoutCons);
        contentPane.add(textFieldOperator);

        JLabel labelService = new JLabel(tr(NETWORK_LEVEL_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 4;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(labelService, layoutCons);
        contentPane.add(labelService);

        String[] serviceTransStrings = new String[serviceCaptionStrings.length];
        for (int i = 0; i < serviceCaptionStrings.length; i++) {
            serviceTransStrings[i] = tr(serviceCaptionStrings[i]);
        }
        comboBoxService = new JComboBox<>(serviceTransStrings);
        comboBoxService.setSelectedIndex(0);
        layoutCons.gridx = 1;
        layoutCons.gridy = 4;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(comboBoxService, layoutCons);
        contentPane.add(comboBoxService);

        checkBoxIsBus = new JCheckBox(tr(BUS_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 5;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsBus, layoutCons);
        checkBoxIsBus.addItemListener(this);
        contentPane.add(checkBoxIsBus);

        checkBoxIsShareTaxi = new JCheckBox(tr(SHARE_TAXI_CAPTION));
        layoutCons.gridx = 1;
        layoutCons.gridy = 5;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsShareTaxi, layoutCons);
        checkBoxIsShareTaxi.addItemListener(this);
        contentPane.add(checkBoxIsShareTaxi);

        checkBoxIsTrolleybus = new JCheckBox(tr(TROLLEYBUS_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 6;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsTrolleybus, layoutCons);
        checkBoxIsTrolleybus.addItemListener(this);
        contentPane.add(checkBoxIsTrolleybus);

        checkBoxIsBusStation = new JCheckBox(tr(BUS_STATION_CAPTION));
        layoutCons.gridx = 1;
        layoutCons.gridy = 6;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsBusStation, layoutCons);
        checkBoxIsBusStation.addItemListener(this);
        contentPane.add(checkBoxIsBusStation);

        checkBoxIsTram = new JCheckBox(tr(TRAM_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 7;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsTram, layoutCons);
        checkBoxIsTram.addItemListener(this);
        contentPane.add(checkBoxIsTram);

        checkBoxIsTrainStation = new JCheckBox(tr(RAILWAY_STATION_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 8;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsTrainStation, layoutCons);
        checkBoxIsTrainStation.addItemListener(this);
        contentPane.add(checkBoxIsTrainStation);

        checkBoxIsTrainStop = new JCheckBox(tr(RAILWAY_STOP_CAPTION));
        layoutCons.gridx = 1;
        layoutCons.gridy = 8;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsTrainStop, layoutCons);
        checkBoxIsTrainStop.addItemListener(this);
        contentPane.add(checkBoxIsTrainStop);

        checkBoxIsAssignTransportType = new JCheckBox(tr(ASSIGN_TRANSPORT_TYPE_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 9;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsAssignTransportType, layoutCons);
        checkBoxIsAssignTransportType.addItemListener(this);
        contentPane.add(checkBoxIsAssignTransportType);

        checkBoxIsBench = new JCheckBox(tr(BENCH_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 10;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsBench, layoutCons);
        checkBoxIsBench.addItemListener(this);
        contentPane.add(checkBoxIsBench);

        checkBoxIsShelder = new JCheckBox(tr(SHELDER_CAPTION));
        layoutCons.gridx = 1;
        layoutCons.gridy = 10;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsShelder, layoutCons);
        checkBoxIsShelder.addItemListener(this);
        contentPane.add(checkBoxIsShelder);

        checkBoxIsCover = new JCheckBox(tr(COVER_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 11;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsCover, layoutCons);
        checkBoxIsCover.addItemListener(this);
        contentPane.add(checkBoxIsCover);

        checkBoxIsArea = new JCheckBox(tr(AREA_CAPTION));
        layoutCons.gridx = 1;
        layoutCons.gridy = 11;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(checkBoxIsArea, layoutCons);
        checkBoxIsArea.addItemListener(this);
        contentPane.add(checkBoxIsArea);

        JButton buttonSave = new JButton(tr(SAVE_BUTTON_CAPTION));
        layoutCons.gridx = 0;
        layoutCons.gridy = 12;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.HORIZONTAL;
        layoutCons.insets = new Insets(10, 0, 0, 0);
        gridbag.setConstraints(buttonSave, layoutCons);
        buttonSave.setActionCommand(SAVE_COMMAND);
        buttonSave.addActionListener(this);
        contentPane.add(buttonSave);

        JButton buttonCancel = new JButton(tr(CANCEL_BUTTON_CAPTION));
        layoutCons.gridx = 1;
        layoutCons.gridy = 12;
        layoutCons.weightx = 0.5;
        layoutCons.fill = GridBagConstraints.LINE_START;
        layoutCons.insets = new Insets(10, 0, 0, 0);
        gridbag.setConstraints(buttonCancel, layoutCons);
        buttonCancel.setActionCommand(CANCEL_COMMAND);
        buttonCancel.addActionListener(this);
        contentPane.add(buttonCancel);
        return contentPane;
    }

    /**
     * Constructor of dialog with filling of controls
     *
     * @param customizeStopAction Stop area customizing action
     * @param stopArea Stop area
     */
    public CustomizePublicTransportStopDialog(CustomizeStopAction customizeStopAction, StopArea stopArea) {
        this();
        setValues(stopArea);
        this.customizeStopAction = customizeStopAction;
        this.stopArea = stopArea;
    }

    /**
     * Return stop area
     *
     * @return Stop area
     */
    public StopArea getStopArea() {
        return stopArea;
    }

    /**
     * Set stop area and fill controls
     *
     * @param newStopArea Stop area
     */
    public void setStopArea(StopArea newStopArea) {
        setValues(newStopArea);
        this.stopArea = newStopArea;
    }

    /**
     * Returns stop area customizing action
     *
     * @return Stop area customizing action
     */
    public CustomizeStopAction getCustomizeStopAction() {
        return customizeStopAction;
    }

    /**
     * Set stop area customizing action
     *
     * @param newCustomizeStopAction Stop area customizing action
     */
    public void setCustomizeStopAction(CustomizeStopAction newCustomizeStopAction) {
        customizeStopAction = newCustomizeStopAction;
    }

    /**
     * Set value in check boxes map
     *
     * @param checkBox Check box
     * @param value Value of check box
     */
    public void setCheckBoxValue(JCheckBox checkBox, boolean value) {
        checkBoxValues.put(checkBox, value);
        checkBox.setSelected(value);
    }

    /**
     * Returns value of check box
     *
     * @param checkBox Check box
     * @return Value of check box
     */
    public boolean getCheckBoxValue(JCheckBox checkBox) {
        try {
            if (checkBoxValues.containsKey(checkBox)) {
                return checkBoxValues.get(checkBox);
            }
            return false;
        } catch (Exception ex) {
            DialogUtils.showOkWarning("Exception when getting checkbox value", ex.getMessage());
        }
        return false;
    }

    /**
     * Callback method for check boxes Set values in check boxes map
     */
    @Override
    public void itemStateChanged(ItemEvent event) {
        JCheckBox checkBox = (JCheckBox) event.getSource();
        if (event.getStateChange() == ItemEvent.DESELECTED) {
            checkBoxValues.put(checkBox, false);
        } else if (event.getStateChange() == ItemEvent.SELECTED) {
            checkBoxValues.put(checkBox, true);
        }
    }

    /**
     * Show or hide dialog
     *
     * @param isVisible Flag of dialog visibility
     */
    public void setVisible(boolean isVisible) {
        if (jDialog != null)
            jDialog.setVisible(isVisible);
    }

    /**
     * Get index of network level
     *
     * @param service Network level name
     * @return Index of network level
     */
    private int getServiceIndex(String service) {
        for (int i = 0; i < serviceStrings.length; i++) {
            if (serviceStrings[i].equals(service)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Setting values of controls from stop area
     *
     * @param stopArea Stop area
     */
    public void setValues(StopArea stopArea) {
        if (stopArea == null)
            return;
        if (stopArea.name != null)
            textFieldName.setText(stopArea.name);
        else if (previousName != null)
            textFieldName.setText(previousName);
        if (stopArea.nameEn != null)
            textFieldNameEn.setText(stopArea.nameEn);
        else if (previousNameEn != null)
            textFieldNameEn.setText(previousNameEn);
        if (stopArea.network != null)
            textFieldNetwork.setText(stopArea.network);
        else if (previousNetwork != null)
            textFieldNetwork.setText(previousNetwork);
        if (stopArea.operator != null)
            textFieldOperator.setText(stopArea.operator);
        else if (previousOperator != null)
            textFieldOperator.setText(previousOperator);
        comboBoxService.setSelectedIndex(getServiceIndex(stopArea.service));
        setCheckBoxValue(checkBoxIsBus, stopArea.isBus);
        setCheckBoxValue(checkBoxIsShareTaxi, stopArea.isShareTaxi);
        setCheckBoxValue(checkBoxIsTrolleybus, stopArea.isTrolleybus);
        setCheckBoxValue(checkBoxIsBusStation, stopArea.isBusStation);
        setCheckBoxValue(checkBoxIsAssignTransportType, stopArea.isAssignTransportType);
        setCheckBoxValue(checkBoxIsTram, stopArea.isTram);
        setCheckBoxValue(checkBoxIsTrainStation, stopArea.isTrainStation);
        setCheckBoxValue(checkBoxIsTrainStop, stopArea.isTrainStation);
        setCheckBoxValue(checkBoxIsTrainStop, stopArea.isTrainStop);
        setCheckBoxValue(checkBoxIsBench, stopArea.isBench);
        setCheckBoxValue(checkBoxIsShelder, stopArea.isShelter);
        setCheckBoxValue(checkBoxIsCover, stopArea.isCovered);
        setCheckBoxValue(checkBoxIsArea, stopArea.isArea);
    }

    /**
     * Returns text box value or null
     *
     * @param textField Text box
     * @return Text box value or null
     */
    public String getTextFromControl(JTextField textField) {
        if (textField.getText().isEmpty())
            return null;
        return textField.getText();
    }

    /**
     * Load values from controls and saving in stop area fields
     *
     * @return Stop area
     */
    public StopArea saveValues() {
        StopArea currentStopArea = this.stopArea;
        try {
            if (currentStopArea == null)
                currentStopArea = new StopArea();
            currentStopArea.name = getTextFromControl(textFieldName);
            currentStopArea.nameEn = getTextFromControl(textFieldNameEn);
            currentStopArea.network = getTextFromControl(textFieldNetwork);
            currentStopArea.operator = getTextFromControl(textFieldOperator);
            currentStopArea.service = serviceStrings[comboBoxService.getSelectedIndex()];
            currentStopArea.isBus = getCheckBoxValue(checkBoxIsBus);
            currentStopArea.isShareTaxi = getCheckBoxValue(checkBoxIsShareTaxi);
            currentStopArea.isTrolleybus = getCheckBoxValue(checkBoxIsTrolleybus);
            currentStopArea.isBusStation = getCheckBoxValue(checkBoxIsBusStation);
            currentStopArea.isAssignTransportType = getCheckBoxValue(checkBoxIsAssignTransportType);
            currentStopArea.isTram = getCheckBoxValue(checkBoxIsTram);
            currentStopArea.isTrainStation = getCheckBoxValue(checkBoxIsTrainStation);
            currentStopArea.isTrainStop = getCheckBoxValue(checkBoxIsTrainStop);
            currentStopArea.isBench = getCheckBoxValue(checkBoxIsBench);
            currentStopArea.isShelter = getCheckBoxValue(checkBoxIsShelder);
            currentStopArea.isCovered = getCheckBoxValue(checkBoxIsCover);
            currentStopArea.isArea = getCheckBoxValue(checkBoxIsArea);
            setPreviousFields(stopArea);
        } catch (Exception ex) {
            DialogUtils.showOkWarning("Exception when saving preferences!", ex.getMessage());
        }
        return currentStopArea;
    }

    private static void setPreviousFields(StopArea stopArea) {
        previousName = stopArea.name;
        previousNameEn = stopArea.nameEn;
        previousNetwork = stopArea.network;
        previousOperator = stopArea.operator;
    }

    /**
     * Callback method for buttons event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (SAVE_COMMAND.equals(event.getActionCommand())) {
            setVisible(false);
            if (customizeStopAction != null) {
                StopArea currentStopArea = saveValues();
                customizeStopAction.performCustomizing(currentStopArea);
            }
        } else {
            setVisible(false);
        }
    }

}
