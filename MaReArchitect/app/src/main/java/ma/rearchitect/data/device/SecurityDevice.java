package ma.rearchitect.data.device;

import ma.rearchitect.data.device.model.AdTracking;
import ma.rearchitect.data.device.model.Encryption;
import ma.rearchitect.data.device.model.Jailbreak;
import ma.rearchitect.data.device.model.OsUpToDate;
import ma.rearchitect.data.device.model.PasswordAndLockScreen;

/**
 * Created by LenVo on 7/13/18.
 */

public class SecurityDevice {
    private Jailbreak jaibreak;
    private PasswordAndLockScreen passwordAndLockScreen;
    private OsUpToDate osUpToDate;
    private AdTracking adTracking;
    private Encryption encryption;

    public Jailbreak getJaibreak() {
        return jaibreak;
    }

    public void setJaibreak(Jailbreak jaibreak) {
        this.jaibreak = jaibreak;
    }

    public PasswordAndLockScreen getPasswordAndLockScreen() {
        return passwordAndLockScreen;
    }

    public void setPasswordAndLockScreen(PasswordAndLockScreen passwordAndLockScreen) {
        this.passwordAndLockScreen = passwordAndLockScreen;
    }

    public OsUpToDate getOsUpToDate() {
        return osUpToDate;
    }

    public void setOsUpToDate(OsUpToDate osUpToDate) {
        this.osUpToDate = osUpToDate;
    }

    public AdTracking getAdTracking() {
        return adTracking;
    }

    public void setAdTracking(AdTracking adTracking) {
        this.adTracking = adTracking;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }
}
