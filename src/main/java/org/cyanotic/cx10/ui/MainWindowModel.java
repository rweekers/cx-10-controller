package org.cyanotic.cx10.ui;

/**
 * Created by orfeo.ciano on 29/11/2016.
 */
class MainWindowModel {
    private boolean btnConnectEnabled;
    private boolean btnControlsEnabled;
    private boolean btnVideoEnabled;

    private String btnConnectText;
    private String btnControlsText;
    private String btnVideoText;
    private String lblStatusText;

    boolean isBtnConnectEnabled() {
        return btnConnectEnabled;
    }

    void setBtnConnectEnabled(boolean btnConnectEnabled) {
        this.btnConnectEnabled = btnConnectEnabled;
    }

    boolean isBtnControlsEnabled() {
        return btnControlsEnabled;
    }

    void setBtnControlsEnabled(boolean btnControlsEnabled) {
        this.btnControlsEnabled = btnControlsEnabled;
    }

    boolean isBtnVideoEnabled() {
        return btnVideoEnabled;
    }

    void setBtnVideoEnabled(boolean btnVideoEnabled) {
        this.btnVideoEnabled = btnVideoEnabled;
    }

    String getBtnConnectText() {
        return btnConnectText;
    }

    void setBtnConnectText(String btnConnectText) {
        this.btnConnectText = btnConnectText;
    }

    String getBtnControlsText() {
        return btnControlsText;
    }

    void setBtnControlsText(String btnControlsText) {
        this.btnControlsText = btnControlsText;
    }

    String getBtnVideoText() {
        return btnVideoText;
    }

    void setBtnVideoText(String btnVideoText) {
        this.btnVideoText = btnVideoText;
    }

    String getLblStatusText() {
        return lblStatusText;
    }

    void setLblStatusText(String lblStatusText) {
        this.lblStatusText = lblStatusText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MainWindowModel that = (MainWindowModel) o;

        if (btnConnectEnabled != that.btnConnectEnabled) return false;
        if (btnControlsEnabled != that.btnControlsEnabled) return false;
        if (btnVideoEnabled != that.btnVideoEnabled) return false;
        if (btnConnectText != null ? !btnConnectText.equals(that.btnConnectText) : that.btnConnectText != null)
            return false;
        if (btnControlsText != null ? !btnControlsText.equals(that.btnControlsText) : that.btnControlsText != null)
            return false;
        if (btnVideoText != null ? !btnVideoText.equals(that.btnVideoText) : that.btnVideoText != null) return false;
        return lblStatusText != null ? lblStatusText.equals(that.lblStatusText) : that.lblStatusText == null;

    }

    @Override
    public int hashCode() {
        int result = (btnConnectEnabled ? 1 : 0);
        result = 31 * result + (btnControlsEnabled ? 1 : 0);
        result = 31 * result + (btnVideoEnabled ? 1 : 0);
        result = 31 * result + (btnConnectText != null ? btnConnectText.hashCode() : 0);
        result = 31 * result + (btnControlsText != null ? btnControlsText.hashCode() : 0);
        result = 31 * result + (btnVideoText != null ? btnVideoText.hashCode() : 0);
        result = 31 * result + (lblStatusText != null ? lblStatusText.hashCode() : 0);
        return result;
    }
}
